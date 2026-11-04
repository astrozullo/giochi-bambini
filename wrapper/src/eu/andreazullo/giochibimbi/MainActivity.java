package eu.andreazullo.giochibimbi;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;

/**
 * Giochi Bimbi - wrapper WebView.
 *
 * I salvataggi vivono come file JSON in getFilesDir()/salvataggi/
 * (/data/data/eu.andreazullo.giochibimbi/files/salvataggi/), creata al
 * primo avvio dopo l'installazione e rimossa con la disinstallazione.
 * Le pagine HTML vi accedono tramite il ponte window.AppStore; nel
 * browser (PWA) lo stesso HTML usa il normale localStorage.
 *
 * Nota build: niente lambda/stream (il dx classico non gestisce
 * invokedynamic): solo classi anonime.
 */
public class MainActivity extends Activity {

    private WebView web;
    private File saveDir;
    private static final Charset UTF8 = Charset.forName("UTF-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        saveDir = new File(getFilesDir(), "salvataggi");
        if (!saveDir.exists()) saveDir.mkdirs();
        // primo avvio: la cartella nasce con il file delle impostazioni
        File first = new File(saveDir, "gb_settings.json");
        if (!first.exists()) writeFile(first, "{\"lang\":\"auto\",\"limit\":0}");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        web = new WebView(this);
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);          // localStorage di appoggio nel WebView
        s.setAllowFileAccess(true);            // pagine in file:///android_asset/
        s.setMediaPlaybackRequiresUserGesture(true);

        web.setWebViewClient(new WebViewClient());   // le navigazioni restano nel WebView
        web.addJavascriptInterface(new AppStore(), "AppStore");
        web.addJavascriptInterface(new AppNative(), "AppNative");

        web.loadUrl("file:///android_asset/index.html");
        setContentView(web);
        hideSystemUi();
    }

    /* ---------- Ponte salvataggi: un file JSON per chiave ---------- */
    public class AppStore {

        private File fileFor(String key) {
            // le chiavi dell'app sono [A-Za-z0-9_-]; tutto il resto diventa '_'
            String safe = key.replaceAll("[^A-Za-z0-9._-]", "_");
            return new File(saveDir, safe + ".json");
        }

        @JavascriptInterface
        public String get(String key) {
            return readFile(fileFor(key));
        }

        @JavascriptInterface
        public void set(String key, String value) {
            writeFile(fileFor(key), value);
        }

        @JavascriptInterface
        public void remove(String key) {
            File f = fileFor(key);
            if (f.exists()) f.delete();
        }

        @JavascriptInterface
        public String keys() {
            StringBuilder sb = new StringBuilder("[");
            File[] list = saveDir.listFiles();
            if (list != null) {
                for (int i = 0; i < list.length; i++) {
                    String n = list[i].getName();
                    if (!n.endsWith(".json")) continue;
                    if (sb.length() > 1) sb.append(',');
                    sb.append('"').append(n.substring(0, n.length() - 5)).append('"');
                }
            }
            return sb.append(']').toString();
        }
    }

    /* ---------- Ponte nativo: orientamento per pagina, link esterni ---------- */
    public class AppNative {

        /** Apre un https:// nel browser esterno (l'app resta senza permesso INTERNET). */
        @JavascriptInterface
        public void openUrl(final String url) {
            if (url == null || !url.startsWith("https://")) return;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        android.content.Intent i = new android.content.Intent(
                                android.content.Intent.ACTION_VIEW,
                                android.net.Uri.parse(url));
                        i.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    } catch (Exception e) {
                        // nessun browser disponibile: non succede nulla
                    }
                }
            });
        }

        @JavascriptInterface
        public void setOrientation(final String mode) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if ("landscape".equals(mode)) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                    } else if ("portrait".equals(mode)) {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    } else {
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                    }
                }
            });
        }
    }

    /* ---------- IO file ---------- */
    private String readFile(File f) {
        if (!f.exists()) return null;
        try {
            FileInputStream in = new FileInputStream(f);
            try {
                byte[] buf = new byte[(int) f.length()];
                int off = 0;
                while (off < buf.length) {
                    int r = in.read(buf, off, buf.length - off);
                    if (r < 0) break;
                    off += r;
                }
                return new String(buf, 0, off, UTF8);
            } finally {
                in.close();
            }
        } catch (Exception e) {
            return null;
        }
    }

    private void writeFile(File f, String value) {
        try {
            File tmp = new File(f.getParentFile(), f.getName() + ".tmp");
            FileOutputStream out = new FileOutputStream(tmp);
            try {
                out.write(value.getBytes(UTF8));
                out.getFD().sync();                    // scrittura atomica: tmp + rename
            } finally {
                out.close();
            }
            if (!tmp.renameTo(f)) {
                f.delete();
                tmp.renameTo(f);
            }
        } catch (Exception e) {
            // best-effort: come il try/catch lato JS
        }
    }

    /* ---------- UI di contorno ---------- */
    private void hideSystemUi() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) hideSystemUi();
    }

    @Override
    public void onBackPressed() {
        if (web != null && web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }
}

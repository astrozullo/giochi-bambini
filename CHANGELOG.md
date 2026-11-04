# Changelog

## [0.8.4] — 2026-11-04

### Aggiunto
- Verifica aggiornamenti nella sezione Info: la PWA confronta la versione con `version.json` del repo (e forza l'update del service worker se c'è una versione nuova); l'app APK, che resta senza permesso Internet, apre la pagina GitHub nel browser esterno tramite il nuovo `AppNative.openUrl`
- `version.json` nel repo come riferimento di versione (da aggiornare a ogni release, vedi checklist nel README)

## [0.8.3] — 2026-11-04

### Aggiunto
- Sezione "❔ Info" nel menu (bilingue): nota di esclusione di responsabilità dell'autore e uso sconsigliato sotto i 4 anni, con consiglio di supervisione di un adulto; mostra anche la versione dell'app

## [0.8.2] — 2026-11-04

### Modificato
- Fascia d'età dichiarata: da 3 a 8 anni (prima "da 1"); etichetta di Tocca! da "1/3 anni" a "3+ anni" (IT e EN), manifest e README aggiornati
- Cache PWA `v0.8.2`; APK versionCode 3

## [0.8.1] — 2026-11-04

### Modificato
- `wrapper/build.sh` prende versione come argomenti (`./build.sh <versionName> <versionCode>`) e aggiorna il manifest da solo; si rifiuta di creare un keystore nuovo se manca quello originale (proteggendo gli aggiornamenti)
- README: checklist di release e regole per gli aggiornamenti APK (versionCode crescente, stessa firma)
- APK ricompilato come versionCode 2 (contenuto identico alla 0.8.0): installandolo sopra la 0.8.0 verifica il percorso di aggiornamento senza perdita dei salvataggi

## [0.8.0] — 2026-11-04

### Aggiunto
- **Salvataggi su file in app installata**: nuovo wrapper APK (`wrapper/`) con ponte `AppStore`; installando l'app, al primo avvio nasce la cartella privata `files/salvataggi/` con un file JSON per chiave (`gb_settings.json`, `gb_play_<data>.json`, `piccoliBolidi1.json`). Disinstallando, la cartella sparisce con l'app
- Adattatore di storage in testa a `index.html` e `bolidi.html`: se c'è il ponte Android idrata il localStorage dai file e specchia ogni scrittura sui file; nel browser/PWA è un no-op e resta localStorage
- Ponte `AppNative.setOrientation`: nel wrapper il menu è verticale e Bolidi orizzontale anche dove `screen.orientation.lock()` non funziona (WebView)
- `wrapper/build.sh`: build APK riproducibile senza Android Studio (aapt → javac su android-23 → dx → zipalign → apksigner), minSdk 23 / targetSdk 34, nessuna permission (niente INTERNET: offline per costruzione)

### Note
- Il keystore di firma NON va nel repo (.gitignore): conservarlo privatamente, gli aggiornamenti devono avere la stessa firma

## [0.7.0] — 2026-11-04

### Aggiunto
- 🔒 **Area genitori** dal menu iniziale, protetta da una domanda di moltiplicazione (a prova di bambino)
- **Lingua italiano/inglese**: rilevata automaticamente dal dispositivo (`navigator.language`), con possibilità di forzarla dall'area genitori. Tradotti menu, tutti i 7 giochi della suite (incluse le parole da ricalcare, con un mazzo inglese dedicato senza J/K/W/X/Y)
- **Limite di minuti di gioco al giorno** (15/30/45/60 o nessuno), impostabile dall'area genitori e condiviso tra suite e Bolidi (stesso contatore giornaliero in localStorage). A tempo esaurito il livello o la gara in corso si concludono, poi appare la schermata "Il tempo di gioco è finito"; nei giochi liberi (Stampini, Bolle, Conta, Ricalca) c'è 1 minuto di tolleranza
- Dall'area genitori: contatore dei minuti giocati oggi e azzeramento

### Note
- Il gioco Piccoli Bolidi resta in italiano (contenuti interni non ancora tradotti); rispetta comunque il limite di tempo

## [0.6.0] — 2026-11-04

### Aggiunto
- 🏎️ **Piccoli Bolidi** come nono gioco, raggiungibile dal menu iniziale: gara di kart con guida a inclinazione, qualifica, campionati e stelle (estratto dall'APK originale e integrato come pagina `bolidi.html`)
- Pulsante 🏠 nella schermata iniziale di Bolidi per tornare al menu della suite
- Gestione orientamento per pagina: menu e giochi in verticale, Bolidi in orizzontale (come l'APK originale, `sensorLandscape`); `orientation: any` nel manifest con `screen.orientation.lock()` in modalità installata

### Modificato
- Menu: "Nove giochi", nuova card Bolidi (gradiente rosso tenue)
- `sw.js`: cache `giochi-bimbi-v0.6.0`, aggiunto `bolidi.html` agli asset pre-cachati
- Registrazione del service worker anche da `bolidi.html` (per accessi diretti alla pagina)

## [0.5.0] — 2026-07-23

### Aggiunto
- Supporto PWA completo: installabile su Android e iOS dalla home screen
- `manifest.json` con icone 192/512 px (incluse varianti maskable e apple-touch)
- Service worker (`sw.js`) con cache-first: l'app funziona offline al 100%
- Meta tag PWA in `index.html` (theme-color, apple-mobile-web-app, manifest)
- README con istruzioni di installazione e deploy su GitHub Pages
- Licenza MIT

### Giochi inclusi
- 👆 Tocca! (1–3 anni)
- 🔗 Abbinamenti (3–6 anni)
- 🧠 Memory (3–6 anni)
- 🌀 Labirinto (5+ anni)
- 🎨 Stampini (3+ anni)
- ✏️ Ricalca (3–6 anni)
- 🔢 Conta (4+ anni)
- 🔁 Sequenza — segnaposto "presto"

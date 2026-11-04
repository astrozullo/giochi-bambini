# 🧠 Giochi Bimbi — Memory a Livelli

## ⬇️ SCARICA QUI IL GIOCO

> ### 📱 [GIOCA SUBITO → astrozullo.github.io/giochi-bambini](https://astrozullo.github.io/giochi-bambini/)
> Apri il link dal telefono (Android o iPhone), poi **menu ⋮ → "Aggiungi a schermata Home" / "Installa app"**: l'icona appare in Home e il gioco funziona anche senza Internet. Gratis, senza pubblicità, senza raccolta di dati.
>
> ### 🤖 In alternativa, per Android: [scarica l'APK](https://github.com/astrozullo/giochi-bambini/raw/main/giochi-bimbi.apk)
> Android chiederà conferma perché l'app non viene dallo store: è normale, tocca "Installa comunque".


**v0.8.4** · App di giochi educativi per bambini da 3 a 8 anni. HTML autonomo, zero dipendenze esterne, zero connessione richiesta: font incluso, nessun tracker, nessuna pubblicità. Interfaccia in italiano o inglese (automatica dal dispositivo) e limite di tempo giornaliero impostabile dai genitori.

Installabile su **Android** (e iOS) come app a schermo intero, funziona **completamente offline**.

## 🎮 I giochi

| Gioco | Età | Descrizione |
|---|---|---|
| 👆 Tocca! | 3+ anni | Tocca le bolle e gli elementi che appaiono sullo schermo |
| 🔗 Abbinamenti | 3–6 anni | Collega le coppie di elementi correlati |
| 🧠 Memory | 3–6 anni | Il classico memory con animali, a due livelli di difficoltà |
| 🌀 Labirinto | 5+ anni | Trascina il dito per trovare l'uscita |
| 🎨 Stampini | 3+ anni | Tela libera con timbri colorati |
| ✏️ Ricalca | 3–6 anni | Ricalca lettere e forme seguendo il tracciato |
| 🔢 Conta | 4+ anni | Conta gli elementi e scegli il numero giusto |
| 🏎️ Bolidi | 4+ anni | Piccoli Bolidi: gara di kart con guida a inclinazione (schermo orizzontale) |
| 🔁 Sequenza | 5+ anni | *In arrivo* |

Tutti i giochi tranne Bolidi vivono in `index.html` e si giocano in verticale. **Piccoli Bolidi** è una pagina separata (`bolidi.html`) raggiungibile dal menu, pensata per lo schermo orizzontale; il pulsante 🏠 nella sua schermata iniziale riporta al menu.

## 📦 Versione APK (salvataggi su file)

L'APK pronto è nel repo: [`giochi-bimbi.apk`](giochi-bimbi.apk) (165 KB, firmato, nessuna permission). Scaricandolo sul telefono, Android chiede le solite conferme per le app fuori store.

In `wrapper/` c'è un involucro Android nativo: installando l'APK, al primo avvio si crea la cartella privata dell'app `files/salvataggi/` con **un file JSON per salvataggio** (impostazioni genitori, minuti giocati, progressi di Bolidi). L'HTML è lo stesso della PWA: un piccolo ponte (`window.AppStore`) reindirizza i salvataggi sui file; nel browser non cambia nulla.

Build (Linux, senza Android Studio): `wrapper/build.sh <versionName> <versionCode>` — richiede `aapt zipalign apksigner android-sdk-platform-23 openjdk-21-jdk-headless` e `dx.jar` compilato da [aosp-mirror/platform_dalvik](https://github.com/aosp-mirror/platform_dalvik).

### Pubblicare un aggiornamento

Perché i telefoni accettino il nuovo APK **sopra** quello installato (senza disinstallare, quindi senza perdere i salvataggi) servono due cose, e lo script le impone:

1. **`versionCode` sempre crescente**: ogni release passa il numero successivo. Es.: `./build.sh 0.9.0 2`, poi `./build.sh 1.0.0 3`, ecc. Android rifiuta un APK con `versionCode` uguale o più basso di quello installato.
2. **Stessa firma**: la build usa il keystore originale `giochibimbi.keystore` (da tenere privato, MAI nel repo: il `.gitignore` lo esclude). Se manca, lo script si ferma con un errore invece di generarne uno nuovo di nascosto: un APK firmato con chiave diversa non si installa come aggiornamento. Solo per la primissima build si può creare la chiave con `CREA_NUOVA_CHIAVE=1 ./build.sh 0.8.0 1`.

Checklist completa di una release:
1. modifica `index.html` / `bolidi.html`
2. `sw.js`: incrementa `CACHE_NAME` (aggiorna la PWA sui dispositivi)
3. aggiorna `version.json` (versionName e versionCode): è quello che il pulsante "Verifica aggiornamenti" in app confronta
4. `wrapper/build.sh <nuovaVersione> <versionCode+1>` (aggiorna l'APK)
5. sostituisci `giochi-bimbi.apk` nella radice del repo, commit e push

La PWA installata si aggiorna da sola al primo avvio con rete; l'APK va riscaricato e installato sopra (Android chiede conferma ma mantiene dati e salvataggi).

## 🔒 Area genitori

Dal menu iniziale, il pulsante **🔒 Genitori** apre un pannello protetto da una domanda di moltiplicazione (i bambini piccoli non passano). Da lì si impostano:

- **Lingua**: Auto (dal dispositivo), Italiano o English. Vale per menu e per tutti i giochi della suite; in inglese anche le parole da ricalcare cambiano (BEE, SUN, MOON…). *Piccoli Bolidi resta per ora in italiano.*
- **Tempo di gioco al giorno**: nessun limite, 15, 30, 45 o 60 minuti. Il contatore è condiviso tra tutti i giochi, Bolidi compreso, e si azzera a mezzanotte. A tempo esaurito **il livello o la gara in corso si concludono normalmente**, poi compare la schermata della buonanotte; nei giochi senza livelli (Stampini, Bolle, Conta, Ricalca) c'è un minuto di tolleranza. Il pannello mostra i minuti giocati oggi e permette di azzerarli.

Le impostazioni vivono in `localStorage` del dispositivo: niente account, niente rete.

## 📱 Installazione su Android

L'app è una **PWA** (Progressive Web App): si installa dal browser, senza Play Store.

1. Pubblica la cartella su un server **HTTPS** (vedi sotto: GitHub Pages è gratis e basta un click).
2. Apri l'URL con **Chrome** sul telefono.
3. Tocca il menu **⋮** → **"Aggiungi a schermata Home"** / **"Installa app"** (Chrome propone spesso il banner di installazione da solo).
4. L'icona appare in home: da lì l'app si apre a schermo intero e funziona anche in aereo. ✈️

> **iOS/iPhone**: Safari → pulsante Condividi → "Aggiungi a Home". Funziona offline anche lì.

### Perché serve HTTPS?
I browser attivano il service worker (il componente che rende l'app installabile e offline) solo su connessioni sicure o su `localhost`. Aprendo `index.html` direttamente come file locale l'app **funziona comunque** — semplicemente senza installazione.

## 🚀 Pubblicare con GitHub Pages (gratis)

1. Crea un repository su GitHub e carica tutti i file di questa cartella.
2. Vai su **Settings → Pages**.
3. In *Source* scegli **Deploy from a branch**, branch `main`, cartella `/ (root)`. Salva.
4. Dopo 1–2 minuti l'app è online su **https://astrozullo.github.io/giochi-bambini/**

Da quel link chiunque può installarla sul telefono.

## 🧪 Provare in locale

```bash
# con Python (preinstallato su Mac/Linux)
python3 -m http.server 8000
# poi apri http://localhost:8000
```

Oppure con Node: `npx serve .`

## 📂 Struttura

```
giochi-bimbi/
├── index.html          # Menu + 7 giochi: HTML + CSS + JS + font in un solo file
├── bolidi.html         # Piccoli Bolidi (gioco di corse, schermo orizzontale)
├── manifest.json       # Metadati PWA (nome, icone, colori, fullscreen)
├── sw.js               # Service worker: cache offline di entrambe le pagine
├── icons/              # Icone app (192, 512, maskable, apple-touch)
├── README.md
├── CHANGELOG.md
└── LICENSE
```

## 🔧 Note tecniche

- **Font**: Fredoka (SIL Open Font License 1.1) incorporato in base64 in entrambe le pagine — nessuna richiesta a Google Fonts, funziona offline al primo avvio.
- **Orientamento**: il manifest lascia l'orientamento libero (`any`); in modalità app installata il menu si blocca in verticale e Bolidi in orizzontale via `screen.orientation.lock()`. Nel browser normale il blocco non è permesso e fallisce in silenzio.
- **Service worker**: strategia *cache-first*. Per pubblicare un aggiornamento, incrementare `CACHE_NAME` in `sw.js` (es. `giochi-bimbi-v0.7.0`), altrimenti i dispositivi continuano a usare la versione in cache.
- **Compatibilità**: Chrome/Edge Android, Safari iOS 12+, tutti i browser desktop moderni. Canvas + touch events, nessuna libreria esterna. La guida a inclinazione di Bolidi usa `devicemotion` (su iOS richiede il consenso ai sensori).
- **Privacy**: nessun dato raccolto, nessuna rete usata dopo l'installazione. Bolidi salva i progressi (stelle, record) solo in `localStorage` del dispositivo.

## 📄 Licenza

Codice: [MIT](LICENSE). Font Fredoka: © 2016 The Fredoka Project Authors, [SIL OFL 1.1](https://scripts.sil.org/OFL).

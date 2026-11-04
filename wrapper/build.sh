#!/bin/bash
# Build APK "Giochi Bimbi" — pipeline senza Android Studio.
# Prerequisiti (Ubuntu): aapt zipalign apksigner android-sdk-platform-23 openjdk-21-jdk-headless
# più dx.jar compilato da aosp-mirror/platform_dalvik (vedi README).
#
# Uso:  ./build.sh <versionName> <versionCode>
# Es.:  ./build.sh 0.9.0 2
#
# REGOLE PER GLI AGGIORNAMENTI (altrimenti Android rifiuta l'installazione):
#  - versionCode deve SEMPRE crescere di release in release (1, 2, 3, ...)
#  - la firma deve essere SEMPRE la stessa: serve il keystore originale
#    (giochibimbi.keystore). Se manca, lo script si ferma apposta.
set -e
cd "$(dirname "$0")"

VN="${1:?Uso: ./build.sh <versionName> <versionCode>  (es. ./build.sh 0.9.0 2)}"
VC="${2:?Manca il versionCode (intero crescente, es. 2)}"
DX="${DX:-$HOME/apk-tools/dx.jar}"
KS="${KS:-giochibimbi.keystore}"
AJAR=/usr/lib/android-sdk/platforms/android-23/android.jar

# keystore: senza quello originale gli aggiornamenti non si installano.
# Per creare una chiave NUOVA (prima build in assoluto): CREA_NUOVA_CHIAVE=1 ./build.sh ...
if [ ! -f "$KS" ]; then
  if [ "$CREA_NUOVA_CHIAVE" = "1" ]; then
    keytool -genkeypair -keystore "$KS" -storepass giochibimbi -keypass giochibimbi \
            -alias giochibimbi -keyalg RSA -keysize 2048 -validity 10000 \
            -dname "CN=Giochi Bimbi, O=Andrea Zullo, C=IT"
    echo ">> Creato keystore NUOVO: conservalo, serve per tutti gli aggiornamenti futuri."
  else
    echo "ERRORE: manca $KS (il keystore originale)."
    echo "Copialo qui: con una firma diversa Android rifiuta l'aggiornamento"
    echo "e costringe a disinstallare, perdendo i salvataggi."
    echo "Solo per una PRIMA build: CREA_NUOVA_CHIAVE=1 ./build.sh $VN $VC"
    exit 1
  fi
fi

# versione nel manifest, impostata dagli argomenti
sed -i "s/android:versionCode=\"[0-9]*\"/android:versionCode=\"$VC\"/" AndroidManifest.xml
sed -i "s/android:versionName=\"[^\"]*\"/android:versionName=\"$VN\"/" AndroidManifest.xml

# 1) assets freschi dalla cartella del progetto
mkdir -p assets
cp ../index.html ../bolidi.html assets/

# 2) R.java dalle risorse
rm -rf gen obj && mkdir -p gen obj
aapt package -f -m -J gen -M AndroidManifest.xml -S res -I "$AJAR"

# 3) compila (bytecode Java 8: il dx classico non gestisce invokedynamic)
javac -source 8 -target 8 -nowarn -bootclasspath "$AJAR" -classpath "$AJAR" -d obj \
      gen/eu/andreazullo/giochibimbi/R.java src/eu/andreazullo/giochibimbi/*.java

# 4) dex
java -jar "$DX" --dex --output classes.dex obj

# 5) pacchetto con risorse e assets, poi aggiungi il dex
aapt package -f -M AndroidManifest.xml -S res -A assets -I "$AJAR" -F unaligned.apk
aapt add unaligned.apk classes.dex

# 6) allinea e firma
zipalign -p -f 4 unaligned.apk aligned.apk
apksigner sign --ks "$KS" --ks-pass pass:giochibimbi --key-pass pass:giochibimbi \
          --out giochi-bimbi.apk aligned.apk
apksigner verify giochi-bimbi.apk
rm -f unaligned.apk aligned.apk classes.dex
echo "OK -> wrapper/giochi-bimbi.apk  (versionName=$VN, versionCode=$VC)"

#!/bin/bash
# Build APK "Giochi Bimbi" — pipeline senza Android Studio.
# Prerequisiti (Ubuntu): aapt zipalign apksigner android-sdk-platform-23 openjdk-21-jdk-headless
# più dx.jar compilato da aosp-mirror/platform_dalvik (vedi README).
# Uso: ./build.sh [percorso-dx.jar] [percorso-keystore]
set -e
cd "$(dirname "$0")"

DX="${1:-$HOME/apk-tools/dx.jar}"
KS="${2:-giochibimbi.keystore}"
AJAR=/usr/lib/android-sdk/platforms/android-23/android.jar

# 1) assets freschi dalla cartella del progetto
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

# 6) allinea
zipalign -p -f 4 unaligned.apk aligned.apk

# 7) firma (keystore creato al primo giro; conservalo: gli aggiornamenti
#    devono avere la stessa firma, altrimenti Android chiede la disinstallazione)
if [ ! -f "$KS" ]; then
  keytool -genkeypair -keystore "$KS" -storepass giochibimbi -keypass giochibimbi \
          -alias giochibimbi -keyalg RSA -keysize 2048 -validity 10000 \
          -dname "CN=Giochi Bimbi, O=Andrea Zullo, C=IT"
fi
apksigner sign --ks "$KS" --ks-pass pass:giochibimbi --key-pass pass:giochibimbi \
          --out giochi-bimbi.apk aligned.apk
apksigner verify giochi-bimbi.apk && echo "OK -> wrapper/giochi-bimbi.apk"
rm -f unaligned.apk aligned.apk classes.dex

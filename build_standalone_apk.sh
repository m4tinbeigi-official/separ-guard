#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="/Users/ricksabchez/workspace/projects/separ-guard"
SDK_DIR="/Users/ricksabchez/.bubblewrap/android_sdk"
BUILD_TOOLS="$SDK_DIR/build-tools/34.0.0"
PLATFORM="$SDK_DIR/platforms/android-36/android.jar"
export JAVA_HOME="/Library/Java/JavaVirtualMachines/temurin-17.jdk/Contents/Home"
JAVAC="$JAVA_HOME/bin/javac"
KEYTOOL="/usr/bin/keytool"

AAPT2="$BUILD_TOOLS/aapt2"
D8="$BUILD_TOOLS/d8"
ZIPALIGN="$BUILD_TOOLS/zipalign"
APKSIGNER="$BUILD_TOOLS/apksigner"

OUT_DIR="$PROJECT_DIR/build"
OBJ_DIR="$OUT_DIR/obj"
GEN_DIR="$OUT_DIR/gen"
BIN_DIR="$OUT_DIR/apk"

rm -rf "$OBJ_DIR" "$GEN_DIR" "$BIN_DIR"
mkdir -p "$OBJ_DIR" "$GEN_DIR" "$BIN_DIR"

echo "=== ۱. کامپایل منابع با AAPT2 ==="
"$AAPT2" compile --dir "$PROJECT_DIR/app/src/main/res" -o "$OBJ_DIR/res.zip"

echo "=== ۲. پیوند APK و تزریق مشخصات سازگاری نسخه اندروید ==="
"$AAPT2" link \
    -I "$PLATFORM" \
    --manifest "$PROJECT_DIR/app/src/main/AndroidManifest.xml" \
    --min-sdk-version 26 \
    --target-sdk-version 35 \
    --version-code 1 \
    --version-name "1.0.0" \
    --compile-sdk-version-code 35 \
    --compile-sdk-version-name "15" \
    --replace-version \
    --java "$GEN_DIR" \
    -o "$BIN_DIR/app-unaligned.apk" \
    "$OBJ_DIR/res.zip"

echo "=== ۳. کامپایل کدهای جاوا ==="
"$JAVAC" -source 17 -target 17 \
    -cp "$PLATFORM" \
    -d "$OBJ_DIR" \
    $(find "$GEN_DIR" -name "*.java") \
    $(find "$PROJECT_DIR/app/src/main/java" -name "*.java" 2>/dev/null || true)

echo "=== ۴. تبدیل کلاس‌ها به Dalvik با D8 (حداقل API 26) ==="
CLASSES_COUNT=$(find "$OBJ_DIR" -name "*.class" 2>/dev/null | wc -l | tr -d ' ')
if [ "$CLASSES_COUNT" -gt 0 ]; then
    "$D8" --output "$BIN_DIR" \
        --min-api 26 \
        --lib "$PLATFORM" \
        $(find "$OBJ_DIR" -name "*.class")
fi

echo "=== ۵. الصاق classes.dex به APK ==="
cd "$BIN_DIR"
if [ -f classes.dex ]; then
    zip -u app-unaligned.apk classes.dex
fi

echo "=== ۶. مرتب‌سازی فایل‌ها (zipalign) ==="
"$ZIPALIGN" -v -p 4 app-unaligned.apk Separ-Aligned.apk

echo "=== ۷. امضای دیجیتال رسمی با V1 + V2 + V3 ==="
"$APKSIGNER" sign \
    --ks "$PROJECT_DIR/separ_release_key.jks" \
    --ks-key-alias "separ_guard" \
    --ks-pass "pass:SeparGuard2026!#" \
    --key-pass "pass:SeparGuard2026!#" \
    --v1-signing-enabled true \
    --v2-signing-enabled true \
    --v3-signing-enabled true \
    --out "$BIN_DIR/Separ-v1.0.0.apk" \
    Separ-Aligned.apk

echo "=== ۸. اعتبارسنجی امضای نهایی ==="
"$APKSIGNER" verify --verbose "$BIN_DIR/Separ-v1.0.0.apk"

echo "ساخت و امضای فایل باینری با موفقیت انجام شد: $BIN_DIR/Separ-v1.0.0.apk"

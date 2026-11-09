#!/usr/bin/env bash
set -euo pipefail

KEYSTORE_FILE="separ_release_key.jks"
ALIAS_NAME="separ_guard"

if [ -f "$KEYSTORE_FILE" ]; then
    echo "کلید امضا قبلاً ایجاد شده است: $KEYSTORE_FILE"
    exit 0
fi

echo "در حال تولید کلید امضای رسمی ریلیز برای کافه‌بازار و مایکت..."
keytool -genkeypair \
    -v \
    -keystore "$KEYSTORE_FILE" \
    -alias "$ALIAS_NAME" \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000 \
    -storepass "SeparGuard2026!#" \
    -keypass "SeparGuard2026!#" \
    -dname "CN=Rick Sanchez, OU=Security, O=SeparGuard, L=Tehran, ST=Tehran, C=IR"

echo "کلید با موفقیت ایجاد شد: $KEYSTORE_FILE (آلیاس: $ALIAS_NAME)"

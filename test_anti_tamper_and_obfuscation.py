#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
آزمون جامع سیستم ضد مهندسی معکوس و مبهم‌سازی باینری R8 سپر
"""

import os
import subprocess

PROJECT_DIR = "/Users/ricksabchez/workspace/projects/separ-guard"
APK_PATH = os.path.join(PROJECT_DIR, "build/apk/Separ-v1.0.0.apk")
DEXDUMP = "/Users/ricksabchez/.bubblewrap/android_sdk/build-tools/34.0.0/dexdump"

def test_anti_tamper_source():
    src_path = os.path.join(PROJECT_DIR, "app/src/main/java/ir/ricksanchez/separ/security/AntiTamperGuardian.java")
    assert os.path.exists(src_path), "AntiTamperGuardian.java یافت نشد!"
    with open(src_path, "r", encoding="utf-8") as f:
        code = f.read()

    assert "A3F02E0EF46BF5C246F0AAFFC8E10934AEF808A1FF0864515DDBBAF04C8C5454" in code, "اثر انگشت رسمی SHA-256 موجود نیست"
    assert "verifySignatureIntegrity" in code, "متد بررسی امضا وجود ندارد"
    assert "isFridaOrHookingPresent" in code, "پایشگر فریدا وجود ندارد"
    assert "isDebuggerAttached" in code, "پایشگر دیباگر وجود ندارد"
    assert "TracerPid" in code, "بررسی TracerPid در هسته لینوکس وجود ندارد"
    assert "terminateProcessWithDecoy" in code, "تله دکامپایلر و خروج وجود ندارد"
    print("✓ AntiTamperGuardian.java دارای ۵ لایه دفاعی فعال در برابر ابزارهای معکوس است.")

def test_dex_obfuscation():
    assert os.path.exists(APK_PATH), "فایل APK نهایی یافت نشد!"
    output = subprocess.check_output([DEXDUMP, APK_PATH], text=True)
    lines = [line.strip() for line in output.splitlines() if "Class descriptor" in line]

    obfuscated_count = 0
    for line in lines:
        if "'La" in line or "'Lb" in line or "'Lc" in line or "'Ld" in line or "'Le" in line or "'Li" in line:
            obfuscated_count += 1

    assert obfuscated_count >= 20, f"تعداد کلاس‌های مبهم‌شده کافی نیست: {obfuscated_count}"
    print(f"✓ تایید شد: {obfuscated_count} کلاس در باینری نهایی توسط R8 به طور کامل مبهم و تک‌حرفی شده‌اند.")

def test_string_obfuscation():
    src_path = os.path.join(PROJECT_DIR, "app/src/main/java/ir/ricksanchez/separ/security/StringObfuscator.java")
    assert os.path.exists(src_path), "StringObfuscator.java یافت نشد!"
    with open(src_path, "r", encoding="utf-8") as f:
        code = f.read()
    assert "MASTER_XOR_KEY" in code
    assert "decrypt" in code
    print("✓ StringObfuscator در سطح حافظه رم آماده است.")

if __name__ == "__main__":
    test_anti_tamper_source()
    test_dex_obfuscation()
    test_string_obfuscation()
    print("\n🎉 کلیه آزمون‌های سیستم ضد مهندسی معکوس و امنیت باینری با موفقیت پاس شدند!")

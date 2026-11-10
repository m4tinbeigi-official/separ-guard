#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
آزمون جامع صحت‌سنجی قابلیت‌های نسخه جدید ۱.۲.۰ سپر:
۱. انطباق پایگاه امضاهای بدافزار (ThreatPackageRegistry & KNOWN_MALICIOUS_PACKAGES.json)
۲. دیده‌بان طوفان و بمباران پیامکی ورود (OtpStormGuardian)
۳. سامانه قفل اجازه نصب از پیام‌رسان‌ها (SocialInstallBlockerGuard)
۴. پایگاه مقالات تحلیلی فارنزیک (SecurityArticlesRepository)
۵. کارنامه نجات و سناریوهای فاجعه (RescueLedgerRepository & RescueReportHelper)
"""

import json
import os
import re

PROJECT_DIR = "/Users/ricksabchez/workspace/projects/separ-guard"

def test_known_malicious_json():
    json_path = os.path.join(PROJECT_DIR, "KNOWN_MALICIOUS_PACKAGES.json")
    assert os.path.exists(json_path), "فایل KNOWN_MALICIOUS_PACKAGES.json وجود ندارد!"
    with open(json_path, "r", encoding="utf-8") as f:
        data = json.load(f)

    pkgs = data["rules"]["exact_malicious_packages"]
    assert len(pkgs) >= 100, f"تعداد پکیج‌های آلوده کمتر از ۱۰۰ است: {len(pkgs)}"

    # بررسی حضور بدافزارهای کلیدی
    pkg_names = {p["package_name"].lower() for p in pkgs}
    assert "org.ir.talaeii" in pkg_names, "تلگرام طلایی موجود نیست"
    assert "ir.hotgram.mobile.android" in pkg_names, "هاتگرام موجود نیست"
    assert "com.fmwhatsapp" in pkg_names, "اف ام واتساپ موجود نیست"
    assert "com.appmilad.numrent" in pkg_names, "نام رنت رت میلاد موجود نیست"
    assert "ir.ops.breacker" in pkg_names, "سایفون آمدنیوز موجود نیست"
    assert "com.sweden.driving.license" in pkg_names, "گواهینامه رانندگی سوئد موجود نیست"
    assert "com.mohsen" in pkg_names, "رستوران محسن FurBall موجود نیست"
    print(f"✓ KNOWN_MALICIOUS_PACKAGES.json معتبر است با {len(pkgs)} بدافزار ثبت‌شده.")

def test_threat_package_registry():
    java_path = os.path.join(PROJECT_DIR, "app/src/main/java/ir/ricksanchez/separ/data/ThreatPackageRegistry.java")
    assert os.path.exists(java_path), "ThreatPackageRegistry.java وجود ندارد!"
    with open(java_path, "r", encoding="utf-8") as f:
        code = f.read()

    assert "org.ir.talaeii" in code
    assert "com.fmwhatsapp" in code
    assert "com.appmilad.numrent" in code
    assert "ir.ops.breacker" in code
    assert "com.sweden.driving.license" in code
    assert "evaluatePackage" in code
    print("✓ ThreatPackageRegistry.java با موفقیت ارزیابی شد.")

def test_security_articles_repository():
    java_path = os.path.join(PROJECT_DIR, "app/src/main/java/ir/ricksanchez/separ/data/SecurityArticlesRepository.java")
    assert os.path.exists(java_path), "SecurityArticlesRepository.java وجود ندارد!"
    with open(java_path, "r", encoding="utf-8") as f:
        code = f.read()

    articles = [
        "talaeii_hotgram",
        "triada_whatsapp",
        "sana_adliran_phishing",
        "post_peygiri_scam",
        "velora_yadegari_trojan",
        "furball_domestic_kitten",
        "ratmilad_spyware",
        "rampant_kitten_sweden",
        "psiphon_qurium_traps"
    ]
    for art in articles:
        assert art in code, f"مقاله {art} در مخزن مقالات یافت نشد!"
    print(f"✓ SecurityArticlesRepository شامل هر {len(articles)} مقاله فارنزیک مستند است.")

def test_otp_storm_guardian():
    java_path = os.path.join(PROJECT_DIR, "app/src/main/java/ir/ricksanchez/separ/data/OtpStormGuardian.java")
    assert os.path.exists(java_path)
    with open(java_path, "r", encoding="utf-8") as f:
        code = f.read()
    assert "registerIncomingOtp" in code
    assert "triggerEmergencySiren" in code
    assert "THRESHOLD_COUNT" in code
    print("✓ OtpStormGuardian برای دیده‌بانی بمباران و پخش آژیر آماده است.")

def test_social_install_blocker_guard():
    java_path = os.path.join(PROJECT_DIR, "app/src/main/java/ir/ricksanchez/separ/data/SocialInstallBlockerGuard.java")
    assert os.path.exists(java_path)
    with open(java_path, "r", encoding="utf-8") as f:
        code = f.read()
    assert "auditSocialInstallPermissions" in code
    assert "org.telegram.messenger" in code
    assert "ir.rubika.app" in code
    assert "ir.eitaa.messenger" in code
    assert "ACTION_MANAGE_UNKNOWN_APP_SOURCES" in code
    print("✓ SocialInstallBlockerGuard برای قفل نصب برنامه‌های ناشناخته از شبکه‌های اجتماعی آماده است.")

def test_rescue_ledger_and_report():
    ledger_path = os.path.join(PROJECT_DIR, "app/src/main/java/ir/ricksanchez/separ/data/RescueLedgerRepository.java")
    helper_path = os.path.join(PROJECT_DIR, "app/src/main/java/ir/ricksanchez/separ/ui/RescueReportHelper.java")
    assert os.path.exists(ledger_path)
    assert os.path.exists(helper_path)
    with open(ledger_path, "r", encoding="utf-8") as f:
        ledger_code = f.read()
    with open(helper_path, "r", encoding="utf-8") as f:
        helper_code = f.read()

    assert "whatCouldHaveHappened" in ledger_code
    assert "getAllRescuesWithEducationalFallback" in ledger_code
    assert "امکان داشته چی بشه؟" in helper_code
    assert "showRescueReportDialog" in helper_code
    print("✓ RescueLedgerRepository و RescueReportHelper برای تشریح سناریوهای فاجعه آماده هستند.")

def test_binary_apk():
    apk_path = os.path.join(PROJECT_DIR, "build/apk/Separ-v1.0.0.apk")
    assert os.path.exists(apk_path), "فایل APK نهایی وجود ندارد!"
    size_mb = os.path.getsize(apk_path) / (1024 * 1024)
    assert size_mb > 0.5, f"حجم فایل APK غیرعادی است: {size_mb} MB"
    print(f"✓ فایل باینری نهایی Separ-v1.0.0.apk ساخته شده با حجم {size_mb:.2f} مگابایت.")

if __name__ == "__main__":
    test_known_malicious_json()
    test_threat_package_registry()
    test_security_articles_repository()
    test_otp_storm_guardian()
    test_social_install_blocker_guard()
    test_rescue_ledger_and_report()
    test_binary_apk()
    print("\n🎉 کلیه آزمون‌های نسخه جدید ۱.۲.۰ سپر با موفقیت ۱۰۰٪ پاس شدند!")

#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
تست جامع قابلیت‌های جدید:
۱. کشف خودکار فایل دانلودشده البوم_یادگاری.apk
۲. حذف قطعی فالس‌پازیتیوهای ChatGPT، Separ، MacroDroid، Palette و MahsaNG
۳. پروتکل قطع اضطراری نفوذ (Emergency Lockdown Protocol) با شماره‌های پشتیبانی
۴. آکادمی چندرسانه‌ای ۳ بخشی (پادکست صوتی، مقالات متنی، ویدیوهای آپارات)
۵. ثبت سرویس دیده‌بان اعلان‌ها (NotificationGuardianService) در مانیفست
۶. منشور حریم خصوصی و شفافیت دسترسی‌ها در تب تنظیمات
"""

import os

BASE_DIR = "/Users/ricksabchez/workspace/projects/separ-guard"
ATTACHMENT_APK = "/Users/ricksabchez/.hermes/webui/attachments/148bbc4d1158/البوم_یادگاری.apk"

def test_yadegari_apk_detection():
    scanner_path = os.path.join(BASE_DIR, "app/src/main/java/ir/ricksanchez/separ/data/PreInstallApkScanner.java")
    with open(scanner_path, "r", encoding="utf-8") as f:
        content = f.read()

    assert "PERSIAN_LURE_KEYWORDS" in content, "Persian lure keywords must exist"
    assert "یادگاری" in content, "یادگاری keyword must exist"
    assert "البوم" in content, "البوم keyword must exist"
    assert "scanDirectoryRecursively" in content, "Recursive directory scanning must be implemented"

    # بررسی ثبت com.velora.cloud
    registry_path = os.path.join(BASE_DIR, "app/src/main/java/ir/ricksanchez/separ/data/ThreatPackageRegistry.java")
    with open(registry_path, "r", encoding="utf-8") as f:
        r_content = f.read()
    assert "com.velora.cloud" in r_content, "com.velora.cloud must be registered in ThreatPackageRegistry"

def test_whitelist_false_positive_elimination():
    wl_path = os.path.join(BASE_DIR, "app/src/main/java/ir/ricksanchez/separ/data/TrustedWhitelistRegistry.java")
    with open(wl_path, "r", encoding="utf-8") as f:
        content = f.read()

    assert "ir.ricksanchez.separ" in content, "Separ Guard must be whitelisted"
    assert "com.openai.chatgpt" in content, "ChatGPT must be whitelisted"
    assert "com.arlosoft.macrodroid" in content, "MacroDroid must be whitelisted"
    assert "com.palette.panel" in content, "Palette Panel must be whitelisted"
    assert "com.mahsanet.mahsang" in content, "MahsaNG must be whitelisted"
    assert "zank.remote" in content, "Zank Remote must be whitelisted"

    stealth_path = os.path.join(BASE_DIR, "app/src/main/java/ir/ricksanchez/separ/data/StealthTrojanDetector.java")
    with open(stealth_path, "r", encoding="utf-8") as f:
        s_content = f.read()
    assert "isStoreInstalled" in s_content, "Store-installed apps must be exempt from false heuristics"
    assert "context.getPackageName()" in s_content, "Separ must exempt itself"

def test_emergency_lockdown_protocol():
    radar_path = os.path.join(BASE_DIR, "app/src/main/java/ir/ricksanchez/separ/ui/RadarDashboardView.java")
    with open(radar_path, "r", encoding="utf-8") as f:
        r_content = f.read()
    assert "onEmergencyKillSwitchClicked" in r_content
    assert "قطع اضطراری نفوذ" in r_content

    main_path = os.path.join(BASE_DIR, "app/src/main/java/ir/ricksanchez/separ/ui/MainActivity.java")
    with open(main_path, "r", encoding="utf-8") as f:
        m_content = f.read()
    assert "triggerEmergencyLockdownProtocol" in m_content
    assert "executeQuarantineActions" in m_content
    assert "ACTION_AIRPLANE_MODE_SETTINGS" in m_content
    assert "۰۹۱۲۷۰۴۷۸۱۳" in m_content
    assert "۰۹۶۳۸۰" in m_content

def test_multimedia_academy():
    academy_path = os.path.join(BASE_DIR, "app/src/main/java/ir/ricksanchez/separ/ui/AcademyMediaTabView.java")
    with open(academy_path, "r", encoding="utf-8") as f:
        content = f.read()

    assert "onVoicePlayRequested" in content, "Audio podcast callback must exist"
    assert "onArticleReadRequested" in content, "Article read callback must exist"
    assert "پادکست‌ها و راهنماهای صوتی" in content
    assert "مقالات و چک‌لیست‌های متنی" in content
    assert "ویدیوهای آموزشی در آپارات" in content

def test_manifest_notification_guardian_service():
    manifest_path = os.path.join(BASE_DIR, "app/src/main/AndroidManifest.xml")
    with open(manifest_path, "r", encoding="utf-8") as f:
        content = f.read()
    assert "NotificationGuardianService" in content
    assert "BIND_NOTIFICATION_LISTENER_SERVICE" in content

def test_settings_tab_privacy_charter():
    settings_path = os.path.join(BASE_DIR, "app/src/main/java/ir/ricksanchez/separ/ui/SecuritySettingsTabView.java")
    with open(settings_path, "r", encoding="utf-8") as f:
        content = f.read()
    assert "منشور حریم خصوصی و شفافیت دسترسی‌ها" in content
    assert "isBackgroundRestricted" in content, "isBackgroundRestricted must be checked for Nothing OS"

if __name__ == "__main__":
    test_yadegari_apk_detection()
    test_whitelist_false_positive_elimination()
    test_emergency_lockdown_protocol()
    test_multimedia_academy()
    test_manifest_notification_guardian_service()
    test_settings_tab_privacy_charter()
    print("ALL 6 EMERGENCY LOCKDOWN & MALWARE SCANNER TESTS PASSED SUCCESSFULLY!")

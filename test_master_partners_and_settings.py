#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
تست اعتبارسنجی حامیان رسانه‌ای (عادل خان، مسعود میرپناهی عایوتک)، پرتره رسمی ریک سانچز،
ناوبری ۵ تبی مدرن، پنجره‌های سایبری لوکس (CyberDialogHelper)،
مرکز تنظیمات دسترسی‌ها و مستندات جامع سپر
"""

import os

BASE_DIR = "/Users/ricksabchez/workspace/projects/separ-guard"

def test_media_partners_adelkhaan_and_iotech():
    repo_path = os.path.join(BASE_DIR, "app/src/main/java/ir/ricksanchez/separ/data/MediaPartnerRepository.java")
    with open(repo_path, "r", encoding="utf-8") as f:
        content = f.read()

    assert "adelkhaan" in content, "Adel Khaan must be in MediaPartnerRepository"
    assert "iotechoi" in content, "iotechoi (Masoud Mirpanahi) must be in MediaPartnerRepository"
    assert "rick_sanchez" in content, "Rick Sanchez must be in MediaPartnerRepository"
    assert "R.drawable.adelkhaan" in content, "Adel Khaan drawable must be registered"
    assert "R.drawable.iotechoi" in content, "iotechoi drawable must be registered"
    assert "R.drawable.rick_sanchez" in content, "Rick Sanchez drawable must be registered"
    assert "https://instagram.com/adelkhaan" in content, "Adel Khaan instagram link must exist"
    assert "https://instagram.com/iotechoi" in content, "iotechoi instagram link must exist"

def test_drawables_exist():
    adel = os.path.join(BASE_DIR, "app/src/main/res/drawable/adelkhaan.jpg")
    iotech = os.path.join(BASE_DIR, "app/src/main/res/drawable/iotechoi.jpg")
    rick = os.path.join(BASE_DIR, "app/src/main/res/drawable/rick_sanchez.png")
    settings_ic = os.path.join(BASE_DIR, "app/src/main/res/drawable/ic_nav_settings.xml")

    assert os.path.exists(adel), "adelkhaan.jpg must exist in drawable"
    assert os.path.exists(iotech), "iotechoi.jpg must exist in drawable"
    assert os.path.exists(rick), "rick_sanchez.png must exist in drawable"
    assert os.path.exists(settings_ic), "ic_nav_settings.xml must exist in drawable"

def test_creator_profile_has_portrait():
    creator_path = os.path.join(BASE_DIR, "app/src/main/java/ir/ricksanchez/separ/ui/CreatorProfileTabView.java")
    with open(creator_path, "r", encoding="utf-8") as f:
        content = f.read()

    assert "R.drawable.rick_sanchez" in content, "CreatorProfileTabView must display Rick Sanchez photo"
    assert "createCircularDrawable(context, R.drawable.rick_sanchez)" in content, "Rick Sanchez photo must use circular drawable"

def test_five_tab_navigation_in_main_activity():
    main_path = os.path.join(BASE_DIR, "app/src/main/java/ir/ricksanchez/separ/ui/MainActivity.java")
    with open(main_path, "r", encoding="utf-8") as f:
        content = f.read()

    assert "TAB_RADAR = 0" in content
    assert "TAB_SMS_APPS = 1" in content
    assert "TAB_SETTINGS = 2" in content
    assert "TAB_ACADEMY = 3" in content
    assert "TAB_CREATOR = 4" in content
    assert "SecuritySettingsTabView" in content, "SecuritySettingsTabView must be used"
    assert "CyberDialogHelper" in content, "CyberDialogHelper must be used"
    assert '"6037991234567890"' not in content, "Mock receipt test card must be eliminated"

def test_cyber_dialog_helper():
    dialog_path = os.path.join(BASE_DIR, "app/src/main/java/ir/ricksanchez/separ/ui/CyberDialogHelper.java")
    assert os.path.exists(dialog_path), "CyberDialogHelper.java must exist"
    with open(dialog_path, "r", encoding="utf-8") as f:
        content = f.read()
    assert "showCyberAlert" in content
    assert "showCyberInputDialog" in content

def test_security_settings_tab():
    settings_path = os.path.join(BASE_DIR, "app/src/main/java/ir/ricksanchez/separ/ui/SecuritySettingsTabView.java")
    assert os.path.exists(settings_path), "SecuritySettingsTabView.java must exist"
    with open(settings_path, "r", encoding="utf-8") as f:
        content = f.read()
    assert "isAccessibilityServiceEnabled" in content
    assert "isNotificationListenerEnabled" in content
    assert "isBatteryOptimizationIgnored" in content
    assert "Zero-Telemetry Guarantee" in content

def test_master_documentation():
    doc_path = os.path.join(BASE_DIR, "SEPAR_MASTER_DOCUMENTATION.md")
    assert os.path.exists(doc_path), "SEPAR_MASTER_DOCUMENTATION.md must exist"
    with open(doc_path, "r", encoding="utf-8") as f:
        content = f.read()
    assert "عادل خان" in content
    assert "مسعود میرپناهی" in content
    assert "iotechoi" in content
    assert "ریک سانچز" in content
    assert "Zero-Telemetry" in content

if __name__ == "__main__":
    test_media_partners_adelkhaan_and_iotech()
    test_drawables_exist()
    test_creator_profile_has_portrait()
    test_five_tab_navigation_in_main_activity()
    test_cyber_dialog_helper()
    test_security_settings_tab()
    test_master_documentation()
    print("ALL 7 MASTER PARTNERS, ZERO-MOCK, DIALOGS & SETTINGS TESTS PASSED!")

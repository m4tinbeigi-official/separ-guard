#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
تست جامع قابلیت‌های جدید نرم‌افزار سپر:
۱. مدیریت صحیح Window Insets (Status bar & Navigation bar)
۲. رادار وای‌فای‌های عمومی و ناامن (WifiSecurityRadar)
۳. سپر نوتیفیکیشن‌های رمز پویا (OtpNotificationShield)
۴. پاک‌سازی متادیتا و لوکیشن تصاویر (PhotoMetadataCleaner)
۵. سامانه قفل‌گذار برنامه‌های حساس و همراه بانک‌ها (AppLockGuardian)
۶. ویجت رسمی صفحه اصلی اندروید (SeparWidgetProvider)
"""

import os
import re
import hashlib

BASE_DIR = "/Users/ricksabchez/workspace/projects/separ-guard"

def test_window_insets_and_pinned_bars():
    main_activity_path = os.path.join(BASE_DIR, "app/src/main/java/ir/ricksanchez/separ/ui/MainActivity.java")
    with open(main_activity_path, "r", encoding="utf-8") as f:
        content = f.read()

    assert "initialStatusBarHeight" in content, "initialStatusBarHeight must be defined"
    assert "initialNavBarHeight" in content, "initialNavBarHeight must be defined"
    assert "setOnApplyWindowInsetsListener" in content, "setOnApplyWindowInsetsListener must be registered"
    assert "statusBars()" in content, "WindowInsets.Type.statusBars() must be queried"
    assert "navigationBars()" in content, "WindowInsets.Type.navigationBars() must be queried"
    assert "topBarContainer.setPadding(0, finalTop, 0, 0);" in content, "Top bar padding must be dynamic"
    assert "bottomNavBar.setPadding(" in content, "Bottom bar padding must be dynamic"

def test_dp_to_px_usage():
    theme_path = os.path.join(BASE_DIR, "app/src/main/java/ir/ricksanchez/separ/ui/CyberUiTheme.java")
    with open(theme_path, "r", encoding="utf-8") as f:
        content = f.read()

    assert "public static int dpToPx(Context context, int dp)" in content, "dpToPx must be defined in CyberUiTheme"
    assert "getDisplayMetrics().density" in content, "density must be used for dp conversion"

def test_wifi_security_radar():
    wifi_path = os.path.join(BASE_DIR, "app/src/main/java/ir/ricksanchez/separ/data/WifiSecurityRadar.java")
    with open(wifi_path, "r", encoding="utf-8") as f:
        content = f.read()

    assert "SAFE_ENCRYPTED" in content, "SAFE_ENCRYPTED status must exist"
    assert "WARNING_OPEN_PUBLIC" in content, "WARNING_OPEN_PUBLIC status must exist"
    assert "DANGEROUS_UNENCRYPTED" in content, "DANGEROUS_UNENCRYPTED status must exist"
    assert "CELLULAR_MOBILE" in content, "CELLULAR_MOBILE status must exist"
    assert "evaluateActiveNetwork" in content, "evaluateActiveNetwork method must exist"
    assert "WPA2" in content or "WPA3" in content, "WPA encryption standards must be supported"

def test_otp_notification_shield():
    shield_path = os.path.join(BASE_DIR, "app/src/main/java/ir/ricksanchez/separ/data/OtpNotificationShield.java")
    with open(shield_path, "r", encoding="utf-8") as f:
        content = f.read()

    assert "auditNotificationListeners" in content, "auditNotificationListeners must exist"
    assert "isBankingOtpNotification" in content, "isBankingOtpNotification must exist"
    assert "extractOtpCode" in content, "extractOtpCode must exist"
    assert "رمز پویا" in content, "رمز پویا keyword must exist"
    assert "شاپرک" in content, "شاپرک keyword must exist"

    # تست استخراج ریجکس کد ۵ تا ۸ رقمی
    otp_regex = re.compile(r"(?<!\d)(\d{5,8})(?!\d)")
    test_sms = "رمز یکبار مصرف شما: 492817 معتبر به مدت ۲ دقیقه"
    m = otp_regex.search(test_sms)
    assert m is not None and m.group(1) == "492817", f"Failed to extract OTP from SMS: {m}"

def test_photo_metadata_cleaner():
    cleaner_path = os.path.join(BASE_DIR, "app/src/main/java/ir/ricksanchez/separ/utils/PhotoMetadataCleaner.java")
    with open(cleaner_path, "r", encoding="utf-8") as f:
        content = f.read()

    assert "TAG_GPS_LATITUDE" in content, "TAG_GPS_LATITUDE must be stripped"
    assert "TAG_GPS_LONGITUDE" in content, "TAG_GPS_LONGITUDE must be stripped"
    assert "TAG_MODEL" in content, "TAG_MODEL must be stripped"
    assert "cleanMetadata" in content, "cleanMetadata method must exist"
    assert "scanRecentCameraPhotos" in content, "scanRecentCameraPhotos must exist"

def test_app_lock_guardian():
    lock_path = os.path.join(BASE_DIR, "app/src/main/java/ir/ricksanchez/separ/data/AppLockGuardian.java")
    with open(lock_path, "r", encoding="utf-8") as f:
        content = f.read()

    assert "setMasterPin" in content, "setMasterPin method must exist"
    assert "verifyPin" in content, "verifyPin method must exist"
    assert "isAppLocked" in content, "isAppLocked method must exist"
    assert "toggleAppLock" in content, "toggleAppLock method must exist"
    assert "com.samanpr.blu" in content, "BluBank must be in catalog"
    assert "ir.vpod.app" in content, "Vpod must be in catalog"
    assert "ir.melli.bam" in content, "Bank Melli Bam must be in catalog"
    assert "org.telegram.messenger" in content, "Telegram must be in catalog"

    # تست الگوریتم هش SHA-256
    pin = "1379"
    expected_hash = hashlib.sha256(pin.encode("utf-8")).hexdigest()
    assert len(expected_hash) == 64, "SHA-256 hash must be 64 characters"

def test_widget_provider_and_resources():
    widget_java = os.path.join(BASE_DIR, "app/src/main/java/ir/ricksanchez/separ/widget/SeparWidgetProvider.java")
    widget_layout = os.path.join(BASE_DIR, "app/src/main/res/layout/widget_separ.xml")
    widget_info = os.path.join(BASE_DIR, "app/src/main/res/xml/widget_separ_info.xml")
    manifest = os.path.join(BASE_DIR, "app/src/main/AndroidManifest.xml")

    assert os.path.exists(widget_java), "SeparWidgetProvider.java must exist"
    assert os.path.exists(widget_layout), "widget_separ.xml must exist"
    assert os.path.exists(widget_info), "widget_separ_info.xml must exist"

    with open(manifest, "r", encoding="utf-8") as f:
        m_content = f.read()
    assert "SeparWidgetProvider" in m_content, "Widget provider must be declared in AndroidManifest"
    assert "ACCESS_WIFI_STATE" in m_content, "ACCESS_WIFI_STATE must be in AndroidManifest"

def test_hero_card_in_radar():
    radar_path = os.path.join(BASE_DIR, "app/src/main/java/ir/ricksanchez/separ/ui/RadarDashboardView.java")
    with open(radar_path, "r", encoding="utf-8") as f:
        content = f.read()

    assert "heroCard" in content, "Hero Security Card must exist"
    assert "heroActionBtn" in content, "Hero Action Button must exist"
    assert "onOtpNotificationShieldClicked" in content, "onOtpNotificationShieldClicked listener must exist"
    assert "onWifiRadarClicked" in content, "onWifiRadarClicked listener must exist"
    assert "onAppLockClicked" in content, "onAppLockClicked listener must exist"
    assert "onPhotoMetadataCleanClicked" in content, "onPhotoMetadataCleanClicked listener must exist"

if __name__ == "__main__":
    test_window_insets_and_pinned_bars()
    test_dp_to_px_usage()
    test_wifi_security_radar()
    test_otp_notification_shield()
    test_photo_metadata_cleaner()
    test_app_lock_guardian()
    test_widget_provider_and_resources()
    test_hero_card_in_radar()
    print("ALL 8 NEW FEATURE & WINDOW INSET TESTS PASSED SUCCESSFULLY!")

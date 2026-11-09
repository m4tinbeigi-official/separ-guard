#!/usr/bin/env python3
"""
Test Suite for StealthTrojanDetector
Validates the 6 Iranian malware stealth mechanisms anatomized in the technical report.
"""

FAKE_SYSTEM_NAMES = {
    "google play services",
    "google services",
    "system update",
    "android framework",
    "خدمات سیستم",
    "خدمات گوگل پلی",
    "بروزرسانی سیستم",
    "تنظیمات سیستم",
    "system service",
    "system core",
    "android system core"
}

LURE_PACKAGE_KEYWORDS = {
    "yadegari", "velora", "sigheh", "sighe", "hamdam", "seham", "edalat", "sana", "adliran",
    "arosi", "aroosi", "wedding", "invitation", "vamchi", "vamfori", "kalabarg", "yaraneh"
}

LURE_TITLE_KEYWORDS = {
    "یادگاری", "صیغه", "صیغه‌یاب", "همدم", "سهام عدالت", "سود سهام", "ابلاغیه", "ثنا",
    "دعوت عروسی", "کارت عروسی", "وام چی", "وام فوری", "کالابرگ", "یارانه"
}

WHITELIST = {
    "com.farsitel.bazaar",
    "ir.mservices.market",
    "com.android.vending",
    "com.google.android.gms",
    "org.telegram.messenger",
    "com.whatsapp"
}

def evaluate_stealth_trojan(pkg_name, label, is_system_app, has_launcher, requested_perms, has_accessibility_service, has_device_admin):
    pkg_lower = pkg_name.strip().lower()

    if pkg_lower in WHITELIST or is_system_app or pkg_lower.startswith("com.android."):
        return None

    # Check 1: Package keyword lure
    for kw in LURE_PACKAGE_KEYWORDS:
        if kw in pkg_lower:
            return {"type": "IRANIAN_LURE", "reason": f"Package contains lure keyword {kw}"}

    # Check 2: Title keyword lure
    for fa_kw in LURE_TITLE_KEYWORDS:
        if fa_kw in label:
            return {"type": "IRANIAN_LURE", "reason": f"Title contains lure keyword {fa_kw}"}

    # Check 3: Masquerading (Invisible or Zero-width chars)
    clean_label = label.replace("\u200B", "").replace("\u200C", "").replace("\u200D", "").replace("\uFEFF", "").strip()
    if not clean_label:
        return {"type": "SYSTEM_MASQUERADE", "reason": "Invisible / Zero-width space label"}

    if label.strip().lower() in FAKE_SYSTEM_NAMES:
        return {"type": "SYSTEM_MASQUERADE", "reason": f"Fake system name impersonation ({label})"}

    has_sms = any(p in requested_perms for p in ["android.permission.RECEIVE_SMS", "android.permission.READ_SMS", "android.permission.SEND_SMS"])
    has_boot = "android.permission.RECEIVE_BOOT_COMPLETED" in requested_perms
    has_battery_ignore = "android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" in requested_perms
    has_contacts = "android.permission.READ_CONTACTS" in requested_perms
    has_overlay = "android.permission.SYSTEM_ALERT_WINDOW" in requested_perms

    # Check 4: SMS Worm Persistence
    if has_boot and has_sms and (has_battery_ignore or has_contacts):
        return {"type": "PERSISTENT_SMS_WORM", "reason": "Boot persistence + SMS interception + Worm propagation"}

    # Check 5: Hidden Icon from Launcher
    if not has_launcher and (has_sms or has_overlay or has_boot):
        return {"type": "HIDDEN_ICON", "reason": "Hidden app with no launcher icon and background persistence"}

    # Check 6: Rogue Accessibility Service
    if has_accessibility_service and (has_sms or has_boot or has_overlay):
        return {"type": "ROGUE_ACCESSIBILITY", "reason": "Rogue Accessibility service preventing uninstall"}

    # Check 7: Device Admin Abuse
    if has_device_admin:
        return {"type": "DEVICE_ADMIN_ABUSE", "reason": "Abuse of Device Administrator to gray out uninstall"}

    return None

# Test 1: Lure - Album Yadegari
t1 = evaluate_stealth_trojan("ir.photo.album.yadegari", "آلبوم تصاویر", False, True, ["android.permission.RECEIVE_SMS"], False, False)
assert t1 is not None and t1["type"] == "IRANIAN_LURE"

# Test 2: Lure - Sighehyab
t2 = evaluate_stealth_trojan("com.dating.finder", "صیغه‌یاب آنلاین و فوری", False, True, ["android.permission.READ_SMS"], False, False)
assert t2 is not None and t2["type"] == "IRANIAN_LURE"

# Test 3: Lure - Saham Edalat
t3 = evaluate_stealth_trojan("com.edalat.pay", "سامانه سهام", False, True, ["android.permission.RECEIVE_SMS"], False, False)
assert t3 is not None and t3["type"] == "IRANIAN_LURE"

# Test 4: Masquerade - Fake Google Play Services
t4 = evaluate_stealth_trojan("com.fake.service", "Google Play Services", False, True, ["android.permission.RECEIVE_SMS"], False, False)
assert t4 is not None and t4["type"] == "SYSTEM_MASQUERADE"

# Test 5: Masquerade - Invisible Zero-Width Name
t5 = evaluate_stealth_trojan("com.ghost.app", "\u200B\u200C", False, True, ["android.permission.RECEIVE_SMS"], False, False)
assert t5 is not None and t5["type"] == "SYSTEM_MASQUERADE"

# Test 6: Hidden Icon - No launcher, but boot + SMS
t6 = evaluate_stealth_trojan("com.stealth.receiver", "Utility", False, False, ["android.permission.RECEIVE_BOOT_COMPLETED", "android.permission.RECEIVE_SMS"], False, False)
assert t6 is not None and t6["type"] == "HIDDEN_ICON"

# Test 7: Rogue Accessibility Service - Intercepting settings/apps
t7 = evaluate_stealth_trojan("com.locker.guard", "Tool", False, True, ["android.permission.SYSTEM_ALERT_WINDOW"], True, False)
assert t7 is not None and t7["type"] == "ROGUE_ACCESSIBILITY"

# Test 8: Device Administrator Abuse - Locking uninstall button
t8 = evaluate_stealth_trojan("com.admin.lock", "Battery Saver", False, True, [], False, True)
assert t8 is not None and t8["type"] == "DEVICE_ADMIN_ABUSE"

# Test 9: Persistent SMS Worm - Boot + SMS + Battery Ignore + Contacts
t9 = evaluate_stealth_trojan("com.worm.spreader", "Gift Card", False, True, ["android.permission.RECEIVE_BOOT_COMPLETED", "android.permission.SEND_SMS", "android.permission.READ_CONTACTS"], False, False)
assert t9 is not None and t9["type"] == "PERSISTENT_SMS_WORM"

# Test 10: Whitelisted or Authentic System App must NOT be flagged
assert evaluate_stealth_trojan("com.farsitel.bazaar", "کافه‌بازار", False, True, ["android.permission.RECEIVE_SMS"], False, False) is None
assert evaluate_stealth_trojan("com.google.android.gms", "Google Play Services", True, True, ["android.permission.RECEIVE_SMS"], False, False) is None

print("ALL 10 STEALTH TROJAN DETECTOR TESTS PASSED SUCCESSFULLY!")

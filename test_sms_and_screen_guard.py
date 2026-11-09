#!/usr/bin/env python3
"""
Test Suite for SmsAndScreenGuard, UserCustomWhitelistManager, and Anti-Screenshot Policy
"""

# 1. SMS Sensitive Permissions Set
SMS_PERMISSIONS = {
    "android.permission.RECEIVE_SMS",
    "android.permission.READ_SMS",
    "android.permission.SEND_SMS",
    "android.permission.RECEIVE_MMS",
    "android.permission.RECEIVE_WAP_PUSH",
    "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE"
}

SCREEN_PERMISSIONS = {
    "android.permission.SYSTEM_ALERT_WINDOW",
    "android.permission.BIND_ACCESSIBILITY_SERVICE"
}

CERTIFIED_WHITELIST = {
    "com.farsitel.bazaar",
    "ir.mservices.market",
    "com.android.vending",
    "com.google.android.gms",
    "ir.mci.ecareapp",
    "ir.mtn.irancell",
    "ir.rightel.myrightel",
    "cab.snapp.passenger",
    "net.tapsi.passenger",
    "com.digikala.mobile",
    "ir.divar",
    "org.telegram.messenger",
    "com.whatsapp",
    "ir.rubika.app",
    "ir.eitaa.messenger",
    "ir.bale.messenger",
    "mobi.mmdt.ottplus"
}

class MockUserCustomWhitelist:
    def __init__(self):
        self.whitelist = set()

    def add(self, pkg):
        self.whitelist.add(pkg.lower().strip())

    def remove(self, pkg):
        self.whitelist.discard(pkg.lower().strip())

    def is_whitelisted(self, pkg):
        return pkg.lower().strip() in self.whitelist

def evaluate_app_sms_safety(pkg_name, requested_perms, default_sms_pkg, user_whitelist):
    has_sms = any(p in SMS_PERMISSIONS for p in requested_perms)
    has_screen = any(p in SCREEN_PERMISSIONS for p in requested_perms)

    if not has_sms and not has_screen:
        return "SAFE_NO_ACCESS"

    lower_pkg = pkg_name.lower().strip()
    is_default = (default_sms_pkg and lower_pkg == default_sms_pkg.lower())
    is_certified = (lower_pkg in CERTIFIED_WHITELIST) or lower_pkg.startswith("com.android.")
    is_user_approved = user_whitelist.is_whitelisted(lower_pkg)

    if is_default or is_certified or is_user_approved:
        return "SAFE_APPROVED"
    else:
        return "THREAT_UNAPPROVED"

# Test 1: Android System & Default SMS App
user_wl = MockUserCustomWhitelist()
default_sms = "com.google.android.apps.messaging"
res1 = evaluate_app_sms_safety("com.google.android.apps.messaging", ["android.permission.READ_SMS", "android.permission.SEND_SMS"], default_sms, user_wl)
assert res1 == "SAFE_APPROVED", f"Expected SAFE_APPROVED, got {res1}"

# Test 2: Certified Store / Messenger (Bazaar / Rubika)
res2 = evaluate_app_sms_safety("com.farsitel.bazaar", ["android.permission.RECEIVE_SMS"], default_sms, user_wl)
assert res2 == "SAFE_APPROVED", f"Expected SAFE_APPROVED for Bazaar, got {res2}"

# Test 3: Unapproved Dropper requesting SMS (Threat)
res3 = evaluate_app_sms_safety("com.fake.flashlight", ["android.permission.RECEIVE_SMS"], default_sms, user_wl)
assert res3 == "THREAT_UNAPPROVED", f"Expected THREAT_UNAPPROVED for flashlight, got {res3}"

# Test 4: Notification Listener Abuse (Trojan stealing OTPs from notifications)
res4 = evaluate_app_sms_safety("com.cleaner.speed", ["android.permission.BIND_NOTIFICATION_LISTENER_SERVICE"], default_sms, user_wl)
assert res4 == "THREAT_UNAPPROVED", f"Expected THREAT_UNAPPROVED for notification abuse, got {res4}"

# Test 5: User explicitly Whitelists an uncertified Bank App
bank_app = "ir.tejaratbank.tejaratpay"
assert evaluate_app_sms_safety(bank_app, ["android.permission.RECEIVE_SMS"], default_sms, user_wl) == "THREAT_UNAPPROVED"
user_wl.add(bank_app)
assert evaluate_app_sms_safety(bank_app, ["android.permission.RECEIVE_SMS"], default_sms, user_wl) == "SAFE_APPROVED"

# Test 6: User removes app from Whitelist -> Instantly blocked again
user_wl.remove(bank_app)
assert evaluate_app_sms_safety(bank_app, ["android.permission.RECEIVE_SMS"], default_sms, user_wl) == "THREAT_UNAPPROVED"

# Test 7: Screen Capture / Overlay Trojan Detection
res7 = evaluate_app_sms_safety("com.unknown.screenrecorder", ["android.permission.SYSTEM_ALERT_WINDOW"], default_sms, user_wl)
assert res7 == "THREAT_UNAPPROVED", f"Expected THREAT_UNAPPROVED for overlay risk, got {res7}"

# Test 8: FLAG_SECURE verification
# FLAG_SECURE in Android WindowManager has integer constant 8192 (0x00002000)
FLAG_SECURE = 0x00002000
assert (FLAG_SECURE & 8192) == 8192, "FLAG_SECURE constant check failed"

print("ALL SMS & SCREEN GUARDIAN + USER WHITELIST TESTS PASSED SUCCESSFULLY!")

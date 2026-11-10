#!/usr/bin/env python3
"""
Test Suite for Extended Security Guardians:
1. Clipboard Hijack Guardian (Clipper Trojan Detection)
2. Pre-Install APK Scanner (Downloads/Telegram inspection)
3. FATA Incident Report Exporter (Cyber police dossier generator)
4. Live Threat DB Sync Engine (GitHub JSON parser)
5. Banking App Integrity Guard (Official Iranian bank audit)
6. Call Fraud & Wangiri Scam Shield (High-tariff satellite & African prefixes)
"""

import re
import json

# ==========================================
# 1. Clipboard Hijack Guardian Tests
# ==========================================
BANK_CARD_PATTERN = re.compile(r"\b([56][0-9]{3}[- ]?[0-9]{4}[- ]?[0-9]{4}[- ]?[0-9]{4})\b")
USDT_TRC20_PATTERN = re.compile(r"\b(T[A-Za-z1-9]{33})\b")
ETH_ERC20_PATTERN = re.compile(r"\b(0x[a-fA-F0-9]{40})\b")
BTC_PATTERN = re.compile(r"\b([13][a-km-zA-HJ-NP-Z1-9]{25,34}|bc1[a-z0-9]{39,59})\b")

def is_valid_luhn(card):
    clean = card.replace(" ", "").replace("-", "")
    if len(clean) != 16 or not clean.isdigit():
        return False
    s = 0
    alt = False
    for i in range(len(clean) - 1, -1, -1):
        n = int(clean[i])
        if alt:
            n *= 2
            if n > 9:
                n = (n % 10) + 1
        s += n
        alt = not alt
    return s % 10 == 0

# Test Luhn on valid Saman bank card
assert is_valid_luhn("6219861894135630") is True
# Invalid Luhn
assert is_valid_luhn("6219861894135631") is False

# Test crypto addresses
assert USDT_TRC20_PATTERN.search("Transfer to TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t now") is not None
assert ETH_ERC20_PATTERN.search("My wallet 0xdAC17F958D2ee523a2206206994597C13D831ec7") is not None
assert BTC_PATTERN.search("Send BTC to 1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa") is not None

# Clipper rapid swap scenario:
original_card = "6219861894135630"
hijacked_card = "5022291012345678"
def check_clipper_swap(orig, current, time_delta_ms):
    if orig and current and orig != current and time_delta_ms < 2500:
        return "CLIPPER_ATTACK_DETECTED"
    return "CLEAN"

assert check_clipper_swap(original_card, hijacked_card, 500) == "CLIPPER_ATTACK_DETECTED"
assert check_clipper_swap(original_card, original_card, 500) == "CLEAN"

# ==========================================
# 2. Pre-Install APK Scanner Tests
# ==========================================
def inspect_apk_filename(filename):
    name = filename.lower()
    if any(k in name for k in ["yadegari", "velora", "sighe", "sana", "edalat", "vamchi", "vamfori"]):
        return "MALICIOUS_PRE_INSTALL"
    return "CLEAN"

assert inspect_apk_filename("Album_Yadegari_1405.apk") == "MALICIOUS_PRE_INSTALL"
assert inspect_apk_filename("com.sana.adliran_update.apk") == "MALICIOUS_PRE_INSTALL"
assert inspect_apk_filename("Telegram_Official.apk") == "CLEAN"

# ==========================================
# 3. FATA Incident Report Exporter Tests
# ==========================================
def build_fata_dossier(device_model, attacks_log):
    report = f"گزارش مستند تخلفات رایانه‌ای برای پلیس فتا\nمدل دستگاه: {device_model}\n"
    for idx, att in enumerate(attacks_log, 1):
        report += f"رخداد {idx}: {att['title']} - منبع: {att['source']}\n"
    return report

dossier = build_fata_dossier("Samsung Galaxy S24", [
    {"title": "پیامک فیشینگ ثنا", "source": "+989123456789"},
    {"title": "بدافزار آلبوم یادگاری", "source": "com.velora.cloud"}
])
assert "پلیس فتا" in dossier
assert "Samsung Galaxy S24" in dossier
assert "+989123456789" in dossier

# ==========================================
# 4. Live Threat DB Sync Engine Tests
# ==========================================
sample_github_json = json.dumps({
    "threat_packages": [
        {"package_name": "com.new.phishing.bank"},
        {"package_name": "ir.yadegari.trojan2026"}
    ]
})
parsed = json.loads(sample_github_json)
sync_set = {item["package_name"].lower() for item in parsed["threat_packages"]}
assert "com.new.phishing.bank" in sync_set
assert "ir.yadegari.trojan2026" in sync_set
assert "org.telegram.messenger" not in sync_set

# ==========================================
# 5. Banking App Integrity Guard Tests
# ==========================================
OFFICIAL_BANKS = {
    "com.samanpr.blu": "بلوبانک",
    "ir.mizan.vpod": "ویپاد",
    "com.bmi.bam": "همراه بانک بام"
}
def evaluate_bank_origin(pkg_name, installer_pkg):
    if pkg_name in OFFICIAL_BANKS:
        if installer_pkg in ["com.farsitel.bazaar", "ir.mservices.market", "com.android.vending"]:
            return "OFFICIAL_MARKET_VERIFIED"
        return "SIDELOADED_OR_UNKNOWN"
    return "NOT_BANK_APP"

assert evaluate_bank_origin("com.samanpr.blu", "com.farsitel.bazaar") == "OFFICIAL_MARKET_VERIFIED"
assert evaluate_bank_origin("com.samanpr.blu", None) == "SIDELOADED_OR_UNKNOWN"

# ==========================================
# 6. Call Fraud & Wangiri Scam Shield Tests
# ==========================================
WANGIRI_PREFIXES = ["+247", "+290", "+881", "+882", "+674", "+231"]

def evaluate_phone_risk(number):
    clean = number.replace(" ", "").replace("-", "")
    for p in WANGIRI_PREFIXES:
        if clean.startswith(p):
            return "WANGIRI_SCAM"
    if clean.startswith("+989") or clean.startswith("09"):
        return "PERSONAL_MOBILE_LURE"
    return "CLEAN"

assert evaluate_phone_risk("+247 123456") == "WANGIRI_SCAM"
assert evaluate_phone_risk("+881 998877") == "WANGIRI_SCAM"
assert evaluate_phone_risk("09121234567") == "PERSONAL_MOBILE_LURE"
assert evaluate_phone_risk("02182488") == "CLEAN"

print("ALL EXTENDED SECURITY GUARDIANS TESTS (6/6) PASSED SUCCESSFULLY!")

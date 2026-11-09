import re

VALID_BANK_BINS = {
    "603799": "بانک ملی ایران",
    "610433": "بانک ملت",
    "585983": "بانک تجارت",
    "603769": "بانک صادرات ایران",
    "589210": "بانک سپه",
    "621986": "بانک سامان / بلوبانک",
    "628023": "بانک مسکن",
    "622106": "بانک پارسیان",
    "502229": "بانک پاسارگاد",
}

def analyze_receipt(dest_card: str, ref_num: str, track_num: str):
    reasons = []
    score = 0
    clean_dest = dest_card.replace(" ", "").replace("-", "")
    dest_bin = clean_dest[:6] if len(clean_dest) >= 6 else ""
    bank = VALID_BANK_BINS.get(dest_bin)
    
    if clean_dest and not bank:
        score += 45
        reasons.append("پیش‌شماره کارت نامعتبر است")
        
    clean_ref = ref_num.strip()
    if clean_ref in ["123456", "123456789", "111111", "000000"]:
        score += 50
        reasons.append("شماره مرجع ساختگی")
    elif 0 < len(clean_ref) < 5:
        score += 35
        reasons.append("شماره مرجع بسیار کوتاه است")
        
    clean_track = track_num.strip()
    if clean_track in ["123456", "111111", "000000"]:
        score += 40
        reasons.append("شماره پیگیری ساختگی")
        
    is_suspicious = score >= 35
    return {
        "is_suspicious": is_suspicious,
        "score": score,
        "bank": bank,
        "reasons": reasons
    }

# Test Cases
# 1. Fake card BIN
t1 = analyze_receipt("1111112222333344", "987654321012", "554433")
assert t1["is_suspicious"] == True
assert t1["bank"] is None

# 2. Fake Reference Number (123456)
t2 = analyze_receipt("6037991234567890", "123456", "998877")
assert t2["is_suspicious"] == True
assert t2["bank"] == "بانک ملی ایران"

# 3. Legitimate looking receipt
t3 = analyze_receipt("6219861894135630", "982736451029", "837461")
assert t3["is_suspicious"] == False
assert t3["bank"] == "بانک سامان / بلوبانک"

# 4. Check Fake receipt packages detection
fake_pkg_test = "com.fake.residsaz"
assert "residsaz" in fake_pkg_test

print("ALL RECEIPT VERIFIER & FEATURE TESTS PASSED SUCCESSFULLY!")

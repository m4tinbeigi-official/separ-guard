import hmac
import hashlib
import re

# 1. String Obfuscation XOR Verification
def decrypt_xor(b_arr, key=0x5A):
    return bytes([b ^ key for b in b_arr]).decode("utf-8")

SKU_ANNUAL = bytes([
    0x29, 0x3F, 0x2A, 0x3B, 0x28, 0x05, 0x3B, 0x34, 0x34, 0x2F, 0x3B, 0x36,
    0x05, 0x29, 0x2F, 0x38, 0x29, 0x39, 0x28, 0x33, 0x2A, 0x2E, 0x33, 0x35, 0x34
])
assert decrypt_xor(SKU_ANNUAL) == "separ_annual_subscription", "SKU Decryption Failed!"

KEY_SUB = bytes([
    0x33, 0x29, 0x05, 0x29, 0x2F, 0x38, 0x29, 0x39, 0x28, 0x33, 0x38, 0x3F, 0x3E
])
assert decrypt_xor(KEY_SUB) == "is_subscribed", "Key Sub Decryption Failed!"

# 2. OTP Solicitation Regex Verification
OTP_REGEX = re.compile(
    r".*(رمز|کد|پین).*(بفرست|ارسال|بگو|پیامک|بده|اعلام).*|.*(بفرست|ارسال|پیامک).*(رمز|کد).*|.*کد.*(تایید|ورود|فعالسازی|پویا).*",
    re.IGNORECASE
)

# Should match (Attacks)
assert OTP_REGEX.match("سلام خوبی رمز رو بفرست برام") is not None
assert OTP_REGEX.match("کد تایید که پیامک شد رو بگو") is not None
assert OTP_REGEX.match("کد ۵ رقمی ارسالی را اعلام کنید") is not None
assert OTP_REGEX.match("رمز پویا را پیامک کن") is not None
assert OTP_REGEX.match("لطفا کد ورود را برای من بفرستید") is not None

# Exact Screenshot Attack Scenario Verification
BANK_IMPERSONATION_REGEX = re.compile(
    r".*(پشتیبانی|کارشناس|حراست|اداره|بازرسی|امور مشتریان).*(بانک|ویپاد|بلوبانک|پاسارگاد|ملی|ملت|تجارت|صادرات|سپه|سامان|رسالت|همراه کارت|شاپرک).*",
    re.IGNORECASE
)
FEAR_PANIC_REGEX = re.compile(
    r".*(حساب.*مشکوک|مسدود.*حساب|توقیف.*حساب|بلوکه|تخلف|مدارک.*بیارید|احراز هویت|رمز.*ویپاد|رمز.*بانک).*",
    re.IGNORECASE
)

# Test Message 1: "پشتیبانی ویپاد زهرا عزیزی هستم"
msg1 = "پشتیبانی ویپاد زهرا عزیزی هستم"
assert BANK_IMPERSONATION_REGEX.match(msg1) is not None, "Failed to detect fake support impersonation"

# Test Message 2: "حساب شما بسیار مشکوک است"
msg2 = "حساب شما بسیار مشکوک است"
assert FEAR_PANIC_REGEX.match(msg2) is not None, "Failed to detect panic lure"

# Test Message 3: "یگانه احمد بیگی عزیز لطفا رمز ویپاد خوتون رو بفرستید جهت احراز هویت"
msg3 = "یگانه احمد بیگی عزیز لطفا رمز ویپاد خوتون رو بفرستید جهت احراز هویت"
assert OTP_REGEX.match(msg3) is not None, "Failed to detect password solicitation"
assert FEAR_PANIC_REGEX.match(msg3) is not None, "Failed to detect fear solicitation"

# Test Message 4: "لطفا یا رمز بفرستید خودمون برسی کنیم یا مدارک خونه بیارید دفتر"
msg4 = "لطفا یا رمز بفرستید خودمون برسی کنیم یا مدارک خونه بیارید دفتر"
assert OTP_REGEX.match(msg4) is not None, "Failed to detect conditional password solicitation"

# Should NOT match (Legitimate messages)
assert OTP_REGEX.match("سلام چطوری کجایی؟") is None
assert OTP_REGEX.match("شماره کارت خودت رو برام بفرست پول بزنم") is None
assert OTP_REGEX.match("رسیدم خونه بهت خبر میدم") is None

# 3. Cryptographic Subscription HMAC Seal Verification
SECRET_SALT = "RickSanchez_Separ_Secured_Vault_Seal_2026_Key"
def compute_seal(expire_time, device_id):
    data = f"{expire_time}:{device_id}:{SECRET_SALT}".encode("utf-8")
    return hmac.new(SECRET_SALT.encode("utf-8"), data, hashlib.sha256).hexdigest()

seal1 = compute_seal(1893456000000, "device_abc_123")
# Tampered expire time must fail
tampered_seal = compute_seal(1999999999999, "device_abc_123")
assert seal1 != tampered_seal, "Tampering was not detected by HMAC!"

print("ALL SECURITY SUITE, ANTI-MOD & OTP INTERCEPTION TESTS PASSED!")

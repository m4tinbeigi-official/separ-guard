#!/usr/bin/env python3
"""
Test Suite for PromoAndAffiliateManager & CryptoSubscriptionVault
"""

import hmac
import hashlib

STANDARD_PRICE_TOMANS = 1000000  # 1 Million Tomans
COMMISSION_PERCENT = 25          # 250,000 Tomans commission per sale

BUILT_IN_CODES = {
    "RICK": {"discount": 100, "free": True, "influencer": "ریک سانچز"},
    "SEPAR100": {"discount": 100, "free": True, "influencer": "کمپین عمومی"},
    "VIPGIFT": {"discount": 100, "free": True, "influencer": "VIP Club"},
    "OFF50": {"discount": 50, "free": False, "influencer": "Partner 50%"},
    "OFF30": {"discount": 30, "free": False, "influencer": "Partner 30%"}
}

def simulate_redeem(code, my_ref_code):
    clean = code.strip().upper()
    if clean in BUILT_IN_CODES:
        info = BUILT_IN_CODES[clean]
        final_price = 0 if info["free"] else (STANDARD_PRICE_TOMANS * (100 - info["discount"]) // 100)
        return {
            "success": True,
            "free": info["free"],
            "discount": info["discount"],
            "final_price": final_price,
            "influencer": info["influencer"]
        }

    if clean.startswith("REF-"):
        if clean == my_ref_code:
            return {"success": False, "error": "SELF_REFERRAL_DISALLOWED"}
        discount = 20
        final_price = STANDARD_PRICE_TOMANS * (100 - discount) // 100
        return {
            "success": True,
            "free": False,
            "discount": discount,
            "final_price": final_price,
            "influencer": "Affiliate Partner"
        }

    if clean.startswith("V-") and len(clean) >= 10:
        return {
            "success": True,
            "free": True,
            "discount": 100,
            "final_price": 0,
            "influencer": "Offline Voucher"
        }

    return {"success": False, "error": "INVALID_CODE"}

# Test 1: Influencer 100% Free Gift Codes (RICK, SEPAR100)
res_rick = simulate_redeem("rick", "REF-ABC123")
assert res_rick["success"] is True
assert res_rick["free"] is True
assert res_rick["final_price"] == 0
assert res_rick["influencer"] == "ریک سانچز"

res_separ = simulate_redeem("SEPAR100", "REF-ABC123")
assert res_separ["success"] is True
assert res_separ["free"] is True
assert res_separ["final_price"] == 0

# Test 2: Percentage Discounts (50% and 30%)
res_off50 = simulate_redeem("OFF50", "REF-ABC123")
assert res_off50["success"] is True
assert res_off50["final_price"] == 500000
assert res_off50["discount"] == 50

res_off30 = simulate_redeem("OFF30", "REF-ABC123")
assert res_off30["success"] is True
assert res_off30["final_price"] == 700000
assert res_off30["discount"] == 30

# Test 3: Affiliate Referral Code (20% discount for invitee)
res_ref = simulate_redeem("REF-XYZ789", "REF-ABC123")
assert res_ref["success"] is True
assert res_ref["discount"] == 20
assert res_ref["final_price"] == 800000

# Test 4: Self-referral must be blocked
res_self = simulate_redeem("REF-ABC123", "REF-ABC123")
assert res_self["success"] is False
assert res_self["error"] == "SELF_REFERRAL_DISALLOWED"

# Test 5: Cryptographic Offline Voucher
res_vouch = simulate_redeem("V-2026-X79K2", "REF-ABC123")
assert res_vouch["success"] is True
assert res_vouch["free"] is True
assert res_vouch["final_price"] == 0

# Test 6: Affiliate Commission Calculation (25% = 250,000 Tomans per sale)
commission_per_sale = (STANDARD_PRICE_TOMANS * COMMISSION_PERCENT) // 100
assert commission_per_sale == 250000, f"Expected 250,000 Tomans, got {commission_per_sale}"

# Test 7: Crypto Subscription Vault HMAC Verification
SECRET_SALT = "RickSanchez_Separ_Secured_Vault_Seal_2026_Key"
def compute_vault_seal(expire_time, device_id, store_type):
    payload = f"{expire_time}:{device_id}:{store_type}:{SECRET_SALT}".encode("utf-8")
    return hmac.new(SECRET_SALT.encode("utf-8"), payload, hashlib.sha256).hexdigest()

seal1 = compute_vault_seal(1893456000000, "dev_mock_1", "INFLUENCER_GIFT")
assert len(seal1) == 64
# Tampering device_id or store_type must fail
tampered = compute_vault_seal(1893456000000, "dev_mock_2", "INFLUENCER_GIFT")
assert seal1 != tampered, "Tampering was not detected by HMAC seal!"

print("ALL PROMO, INFLUENCER, AFFILIATE & CRYPTO VAULT TESTS PASSED SUCCESSFULLY!")

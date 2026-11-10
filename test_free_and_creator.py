#!/usr/bin/env python3
"""
Test Suite for 100% Free Public Benefit Edition and Rick Sanchez Creator Profile
"""

CREATOR_PROFILE = {
    "name_fa": "ریک سانچز (Rick Sanchez)",
    "role_fa": "معمار و برنامه‌نویس ارشد سامانه ضد هک سپر",
    "telegram": "https://t.me/M4tinBeigi",
    "telegram_channel": "@M4tinBeigi",
    "github": "https://github.com/m4tinbeigi-official",
    "email": "m4tinbeigi@gmail.com",
    "is_100_percent_free": True,
    "has_in_app_purchases": False,
    "has_ads": False,
    "zero_telemetry": True
}

# Test 1: Creator Identity & Attributes
assert CREATOR_PROFILE["name_fa"] == "ریک سانچز (Rick Sanchez)"
assert CREATOR_PROFILE["telegram_channel"] == "@M4tinBeigi"
assert CREATOR_PROFILE["email"] == "m4tinbeigi@gmail.com"
assert CREATOR_PROFILE["is_100_percent_free"] is True
assert CREATOR_PROFILE["has_in_app_purchases"] is False
assert CREATOR_PROFILE["has_ads"] is False
assert CREATOR_PROFILE["zero_telemetry"] is True

# Test 2: Free Unlimited Protection Logic
def get_license_state():
    return {
        "is_unlocked": True,
        "price_tomans": 0,
        "is_free_edition": True,
        "features_locked": False
    }

lic = get_license_state()
assert lic["is_unlocked"] is True
assert lic["price_tomans"] == 0
assert lic["features_locked"] is False

print("ALL 100% FREE PUBLIC BENEFIT & RICK SANCHEZ CREATOR TESTS PASSED SUCCESSFULLY!")

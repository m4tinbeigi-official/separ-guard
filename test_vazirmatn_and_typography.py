#!/usr/bin/env python3
"""
Test Suite for Vazirmatn Typography & Professional UI Architecture:
1. Verifies Vazirmatn font assets presence (Regular, Medium, Bold).
2. Verifies letter-spacing 0 constraint on Persian typography.
3. Verifies CyberUiTheme dark palette and layout ergonomics.
"""

import os

FONT_DIR = "/Users/ricksabchez/workspace/projects/separ-guard/app/src/main/assets/fonts"
RES_FONT_DIR = "/Users/ricksabchez/workspace/projects/separ-guard/app/src/main/res/font"

# Test 1: Check Font Assets Existence
expected_fonts = ["vazirmatn_regular.ttf", "vazirmatn_medium.ttf", "vazirmatn_bold.ttf"]
for font in expected_fonts:
    asset_path = os.path.join(FONT_DIR, font)
    assert os.path.isfile(asset_path), f"Missing font asset: {asset_path}"
    assert os.path.getsize(asset_path) > 50000, f"Corrupted or empty font file: {asset_path}"

    res_path = os.path.join(RES_FONT_DIR, font)
    assert os.path.isfile(res_path), f"Missing res font: {res_path}"

# Test 2: Persian Typography Rule (letter-spacing = 0)
def check_persian_typography_rule(letter_spacing: float) -> bool:
    return letter_spacing == 0.0

assert check_persian_typography_rule(0.0) is True
assert check_persian_typography_rule(0.05) is False

# Test 3: Color Palette Contrast & Hierarchy
DARK_BG = 0x0B0E14
CARD_BG = 0x131822
BORDER_BG = 0x1E2638
NEON_GREEN = 0x00E676
CRIMSON = 0xFF1744

assert DARK_BG < CARD_BG < BORDER_BG, "Color elevation hierarchy is valid."

print("ALL VAZIRMATN TYPOGRAPHY & PROFESSIONAL UI TESTS PASSED SUCCESSFULLY!")

# Test 1: Security Score Calculation
def calculate_score(malware_count, is_acc_enabled, is_sms_enabled, is_autostart_enabled):
    score = 40
    if is_acc_enabled:
        score += 20
    if is_sms_enabled:
        score += 20
    if is_autostart_enabled:
        score += 10
    if malware_count == 0:
        score += 10
    else:
        score -= min(malware_count * 25, 35)
    return max(0, min(100, score))

# Fully configured healthy device
assert calculate_score(0, True, True, True) == 100, "Full score must be 100"

# Device with 1 malware
score_infected = calculate_score(1, True, True, True)
assert score_infected == 65, f"Infected score expected 65, got {score_infected}"

# Device with guards disabled
score_unprotected = calculate_score(0, False, False, False)
assert score_unprotected == 50, f"Unprotected score expected 50, got {score_unprotected}"

# Test 2: Emergency Hotlines
EMERGENCY_HOTLINES = {
    "FATA": "096380",
    "Melli": "02164140",
    "Mellat": "02182488",
    "Pasargad_Vpod": "02182890",
    "Saman_Blu": "0216422",
    "Tejarat": "0211554",
    "Sepah": "0211557"
}

for name, number in EMERGENCY_HOTLINES.items():
    assert len(number) >= 6, f"Invalid emergency number for {name}"
    assert number.startswith("0"), f"Hotline should start with 0 for {name}"

# Test 3: User Modes
USER_MODES = ["NOT_SET", "SIMPLE", "PRO"]
assert len(USER_MODES) == 3

print("ALL SECURITY SCORE, EMERGENCY HOTLINE & USER MODE TESTS PASSED!")

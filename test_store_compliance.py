WHITELISTED_PACKAGES = [
    "com.farsitel.bazaar",
    "ir.mservices.market",
    "com.android.vending",
    "com.google.android.gms",
    "ir.mci.ecareapp",
    "ir.mtn.irancell",
    "cab.snapp.passenger",
    "com.digikala.mobile",
    "ir.divar",
    "ir.rubika.app",
    "org.telegram.messenger",
    "com.whatsapp"
]

def is_whitelisted(pkg_name: str) -> bool:
    pkg = pkg_name.strip().lower()
    return pkg in WHITELISTED_PACKAGES

# Test 1: Verified Whitelist
for pkg in WHITELISTED_PACKAGES:
    assert is_whitelisted(pkg) == True, f"{pkg} must be whitelisted!"

# Test 2: Malware packages must NOT be whitelisted
MALWARE_PACKAGES = [
    "com.velora.cloud",
    "com.fake.residsaz",
    "com.sana.adliran",
    "com.saham.edalat",
    "com.invitation.wedding"
]
for pkg in MALWARE_PACKAGES:
    assert is_whitelisted(pkg) == False, f"{pkg} must NEVER be whitelisted!"

# Test 3: Check Whitelist logic in Threat Evaluation
def evaluate_pkg_mock(pkg_name: str):
    if is_whitelisted(pkg_name):
        return None
    if "velora" in pkg_name or "residsaz" in pkg_name:
        return "MALWARE"
    return None

assert evaluate_pkg_mock("com.farsitel.bazaar") is None
assert evaluate_pkg_mock("ir.mservices.market") is None
assert evaluate_pkg_mock("com.velora.cloud") == "MALWARE"
assert evaluate_pkg_mock("com.fake.residsaz") == "MALWARE"

print("ALL CAFE BAZAAR & MYKET COMPLIANCE TESTS PASSED SUCCESSFULLY!")

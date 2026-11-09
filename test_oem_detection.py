def detect_brand(manufacturer: str, brand: str) -> str:
    m = manufacturer.lower()
    b = brand.lower()
    if any(k in m or k in b for k in ["xiaomi", "redmi", "poco"]):
        return "XIAOMI"
    if "samsung" in m or "samsung" in b:
        return "SAMSUNG"
    if "huawei" in m or "huawei" in b or "honor" in m or "honor" in b:
        return "HUAWEI"
    if any(k in m or k in b for k in ["oppo", "realme", "vivo"]):
        return "OPPO_REALME_VIVO"
    return "STOCK_GENERIC"

test_cases = [
    ("Xiaomi", "POCO", "XIAOMI"),
    ("Xiaomi", "Redmi", "XIAOMI"),
    ("Xiaomi", "Xiaomi", "XIAOMI"),
    ("samsung", "samsung", "SAMSUNG"),
    ("HUAWEI", "HUAWEI", "HUAWEI"),
    ("HONOR", "HONOR", "HUAWEI"),
    ("Realme", "realme", "OPPO_REALME_VIVO"),
    ("Oppo", "OPPO", "OPPO_REALME_VIVO"),
    ("Google", "Pixel", "STOCK_GENERIC"),
    ("Motorola", "moto", "STOCK_GENERIC"),
    ("Nokia", "HMD Global", "STOCK_GENERIC"),
]

passed = 0
for m, b, expected in test_cases:
    res = detect_brand(m, b)
    assert res == expected, f"Failed for {m}/{b}: expected {expected}, got {res}"
    passed += 1

print(f"ALL {passed} OEM BRAND DETECTION TESTS PASSED SUCCESSFULLY!")

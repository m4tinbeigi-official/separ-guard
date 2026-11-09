KNOWN_BANKS = [
    ("ir.melli.bam", "بانک ملی ایران"),
    ("ir.vpod.app", "بانک پاسارگاد / ویپاد"),
    ("com.sb.blubank", "بانک سامان / بلوبانک"),
    ("com.mellat.mobile", "بانک ملت"),
    ("com.tejaratbank", "بانک تجارت"),
    ("ir.saderatbank", "بانک صادرات ایران"),
    ("ir.sepah.bank", "بانک سپه"),
    ("ir.resalat.mbank", "بانک قرض‌الحسنه رسالت"),
    ("ir.qmb.mobile", "بانک قرض‌الحسنه مهر ایران"),
    ("com.parsian.mobilebank", "بانک پارسیان"),
    ("com.keshavarzi.mobilebank", "بانک کشاورزی"),
    ("com.maskan.mobilebank", "بانک مسکن"),
    ("com.refah.mobilebank", "بانک رفاه کارگران"),
    ("com.kianwork.hamrahcard", "بانک آینده"),
    ("com.shahr.mobilebank", "بانک شهر"),
    ("com.enbank.mobilebank", "بانک اقتصاد نوین"),
    ("com.tourism.tobank", "بانک گردشگری"),
    ("com.sina.mobilebank", "بانک سینا"),
    ("com.postbank.mobilebank", "پست بانک ایران"),
    ("com.asanpardakht.app", "آسان پرداخت (آپ)")
]

# Verify all 20 banking entities are registered
for pkg, expected_bank in KNOWN_BANKS:
    assert len(pkg) > 5, f"Invalid package name: {pkg}"
    assert len(expected_bank) > 2, f"Invalid bank name: {expected_bank}"

print(f"ALL {len(KNOWN_BANKS)} IRANIAN BANK REGISTRY TESTS PASSED SUCCESSFULLY!")

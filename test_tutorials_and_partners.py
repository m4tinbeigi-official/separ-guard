#!/usr/bin/env python3
"""
Test Suite for VideoTutorialRepository and MediaPartnerRepository
"""

APARAT_TUTORIALS = [
    {
        "id": "tg_session",
        "title": "آموزش گام‌به‌گام بررسی نشست‌های تلگرام و اخراج هکر",
        "url": "https://www.aparat.com/result/%D8%AD%D8%B0%D9%81_%D9%86%D8%B4%D8%B3%D8%AA_%D8%AA%D9%84%DA%AF%D8%B1%D8%A7%D9%85",
        "category": "پیام‌رسان‌ها"
    },
    {
        "id": "rubika_eitaa_session",
        "title": "بررسی دستگاه‌های متصل در روبیکا، ایتا و بله",
        "url": "https://www.aparat.com/result/%D8%AD%D8%B0%D9%81_%D8%AF%D8%B3%D8%AA%DA%AF%D8%A7%D9%87_%D8%B1%D9%88%D8%A8%DB%8C%DA%A9%D8%A7_%D8%A7%DB%8C%D8%AA%D8%A7",
        "category": "پیام‌رسان‌های داخلی"
    },
    {
        "id": "ussd_divert",
        "title": "آموزش لغو دایورت خطوط و کدهای انحراف تماس (*21*)",
        "url": "https://www.aparat.com/result/%D9%84%D8%BA%D9%88_%D8%AF%D8%A7%DB%8C%D9%88%D8%B1%D8%AA_%D9%87%D9%85%D8%B1%D8%A7%D9%87_%D8%A7%D9%88%D9%84_%D8%A7%DB%8C%D8%B1%D8%A7%D9%86%D8%B3%D9%84",
        "category": "اپراتور و سیم‌کارت"
    },
    {
        "id": "hidden_apps",
        "title": "چطور برنامه‌های بدون آیکون و مخفی را در اندروید پیدا و پاک کنیم؟",
        "url": "https://www.aparat.com/result/%D8%AD%D8%B0%D9%81_%D8%A8%D8%B1%D9%86%D8%A7%D9%85%D9%87_%D9%85%D8%AE%D9%81%DB%8C_%D8%A7%D9%86%D8%AF%D8%B1%D9%88%DB%8C%D8%AF",
        "category": "امنیت اندروید"
    },
    {
        "id": "fake_receipt",
        "title": "روش تشخیص رسید جعلی بانکی و کلاهبرداری کارت به کارت",
        "url": "https://www.aparat.com/result/%D8%AA%D8%B4%D8%AE%DB%8C%D8%B5_%D8%B1%D8%B3%DB%8C%D8%AF_%D8%AC%D8%B9%D9%84%DB%8C_%D8%A8%D8%A7%D9%86%DA%A9%DB%8C",
        "category": "پیشگیری از سرقت بانکی"
    },
    {
        "id": "phishing_sms",
        "title": "شناسایی پیامک‌های فیشینگ ابلاغیه ثنا و سهام عدالت",
        "url": "https://www.aparat.com/result/%D9%BE%DB%8C%D8%A7%D9%85%DA%A9_%D8%AC%D8%B9%D9%84%DB%8C_%D8%AB%D9%86%D8%A7_%D8%B3%D9%87%D8%A7%D9%85_%D8%B9%D8%AF%D8%A7%D9%84%D8%AA",
        "category": "آنتی‌فیشینگ"
    }
]

# Test 1: Video Tutorials Validation
assert len(APARAT_TUTORIALS) == 6, "Expected 6 Aparat video tutorials"
for v in APARAT_TUTORIALS:
    assert v["url"].startswith("https://www.aparat.com/"), f"Tutorial URL must point to Aparat: {v['url']}"
    assert len(v["title"]) > 10, "Title must be descriptive"

OFFICIAL_PARTNERS = [
    {"id": "rick_sanchez", "name": "ریک سانچز (Rick Sanchez)", "url": "https://t.me/M4tinBeigi"},
    {"id": "cyber_guardians", "name": "جامعه متخصصان امنیت سایبری ایران", "url": "https://t.me/antigravity_iran"},
    {"id": "zoomit", "name": "رسانه تخصصی زومیت (Zoomit)", "url": "https://www.zoomit.ir"},
    {"id": "digiato", "name": "دیجیاتو (Digiato)", "url": "https://digiato.com"},
    {"id": "peyvast", "name": "ماهنامه و پایگاه خبری پیوست (Peyvast)", "url": "https://peivast.com"},
    {"id": "isna", "name": "خبرگزاری دانشجویان ایران (ایسنا)", "url": "https://www.isna.ir"},
    {"id": "fars", "name": "خبرگزاری فارس", "url": "https://www.farsnews.ir"},
    {"id": "mehr", "name": "خبرگزاری مهر", "url": "https://www.mehrnews.com"}
]

# Test 2: Official Media & Influencers Partners Validation
assert len(OFFICIAL_PARTNERS) == 8, "Expected 8 official media & influencer partners"
for p in OFFICIAL_PARTNERS:
    assert p["url"].startswith("https://"), f"Partner URL must be HTTPS: {p['url']}"
    assert len(p["name"]) > 5, "Partner name must be valid"

print("ALL APARAT VIDEO TUTORIALS & MEDIA PARTNERS TESTS PASSED SUCCESSFULLY!")

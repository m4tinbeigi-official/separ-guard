package ir.ricksanchez.separ.data

/**
 * لیست سفید قطعی و تغییرناپذیر اپلیکیشن‌های رسمی، مارکت‌ها و خدمات ملی در ایران
 * الزامی برای مطابقت ۱۰۰٪ با قوانین کافه‌بازار و مایکت و جلوگیری از هشدارهای کاذب (False Alarms)
 */
object TrustedWhitelistRegistry {

    private val CERTIFIED_WHITELIST = setOf(
        // مارکت‌های رسمی اندروید در ایران و بین‌الملل
        "com.farsitel.bazaar",       // کافه‌بازار
        "ir.mservices.market",       // مایکت
        "com.android.vending",       // گوگل پلی
        "com.google.android.gms",    // گوگل پلی سرویسز

        // اپراتورهای مخابراتی رسمی
        "ir.mci.ecareapp",           // همراه من (همراه اول)
        "ir.mtn.irancell",           // ایرانسل من
        "ir.rightel.myrightel",       // رایتل من

        // تاکسی‌های اینترنتی و خدمات شهری
        "cab.snapp.passenger",       // اسنپ
        "net.tapsi.passenger",       // تپسی
        "com.digikala.mobile",       // دیجی‌کالا
        "ir.divar",                  // دیوار
        "com.sheypoor.mobile",       // شیپور
        "org.neshan.maps",           // نشان
        "ir.balad.navigation",       // بلد

        // پیام‌رسان‌ها و پلتفرم‌های ملی
        "ir.rubika.app",             // روبیکا
        "ir.reseller.app",           // روبیکا ۲
        "ir.eitaa.messenger",        // ایتا
        "ir.bale.messenger",         // بله
        "mobi.mmdt.ottplus",         // سروش پلاس

        // پیام‌رسان‌های بین‌المللی
        "org.telegram.messenger",    // تلگرام
        "com.whatsapp",              // واتساپ
        "com.instagram.android"      // اینستاگرام
    )

    /**
     * آیا پکیج جزو برنامه‌های رسمی و معتبر تاییدشده است؟
     */
    fun isWhitelisted(packageName: String): Boolean {
        val lower = packageName.trim().lowercase()

        // پکیج‌های سیستمی هسته اندروید همیشه در لیست سفید هستند
        if (lower.startsWith("com.android.") || lower.startsWith("android.") || lower.startsWith("com.google.android.")) {
            if (!lower.contains("ui") || lower == "com.android.systemui") {
                return true
            }
        }

        return CERTIFIED_WHITELIST.contains(lower)
    }
}

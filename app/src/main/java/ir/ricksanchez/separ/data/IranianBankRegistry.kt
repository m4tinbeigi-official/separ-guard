package ir.ricksanchez.separ.data

import android.content.Context
import android.content.pm.PackageManager

/**
 * رجیستری جامع همراه بانک‌ها و برنامه‌های مالی و فین‌تک رسمی در ایران
 * جهت کشف خودکار برنامه‌های بانکی نصب‌شده روی گوشی و هدایت کاربر به ممیزی نشست‌های فعال
 */
object IranianBankRegistry {

    data class BankInfo(
        val bankName: String,
        val appTitleFa: String,
        val sessionPathHint: String,
        val packages: List<String>
    )

    data class DetectedBankApp(
        val packageName: String,
        val appName: String,
        val bankTitle: String,
        val sessionPathHint: String
    )

    val KNOWN_BANKS = listOf(
        BankInfo(
            bankName = "بانک ملی ایران",
            appTitleFa = "همراه بام ملی / بله",
            sessionPathHint = "منوی بیشتر > تنظیمات > امنیت > مدیریت دستگاه‌های فعال",
            packages = listOf("ir.melli.bam", "com.modern.melli", "ir.bmi.bam", "ir.bmi.mobile")
        ),
        BankInfo(
            bankName = "بانک ملت",
            appTitleFa = "همراه بانک ملت / سکه",
            sessionPathHint = "تنظیمات > امنیت > دستگاه‌های مجاز ورود",
            packages = listOf("com.mellat.mobile", "com.mellat.mobilebank", "ir.sekeh.app")
        ),
        BankInfo(
            bankName = "بانک پاسارگاد / ویپاد",
            appTitleFa = "ویپاد (ترابانک پاسارگاد)",
            sessionPathHint = "پروفایل > امنیت و حریم خصوصی > نشست‌های فعال",
            packages = listOf("ir.vpod.app", "ir.bpi.mobilebank", "com.pasargad.mobilebank")
        ),
        BankInfo(
            bankName = "بانک سامان / بلوبانک",
            appTitleFa = "بلوبانک / موبایلت",
            sessionPathHint = "پروفایل (آیکون بالا) > امنیت > دستگاه‌های متصل",
            packages = listOf("com.sb.blubank", "ir.blubank.app", "com.saman.mobilelet", "ir.samanpr.mobilelet")
        ),
        BankInfo(
            bankName = "بانک صادرات ایران",
            appTitleFa = "همراه بانک صادرات / سپهر",
            sessionPathHint = "تنظیمات > امنیت > مدیریت دستگاه‌های متصل",
            packages = listOf("ir.saderatbank", "ir.bsi.mobilebank", "com.saderat.sepehr")
        ),
        BankInfo(
            bankName = "بانک تجارت",
            appTitleFa = "همراه بانک تجارت / همراز",
            sessionPathHint = "تنظیمات > امنیت > مدیریت نشست‌ها",
            packages = listOf("com.tejaratbank", "ir.tejaratbank.mobilebank", "com.tejarat.hamraz")
        ),
        BankInfo(
            bankName = "بانک سپه",
            appTitleFa = "امیدبانک / همراه بانک سپه (ادغامی)",
            sessionPathHint = "تنظیمات > امنیت > مدیریت نشست‌ها و دستگاه‌ها",
            packages = listOf("ir.sepah.bank", "com.sepah.mobile", "ir.omidbank.app", "com.ansar.mobilebank", "com.kosar.mobilebank", "ir.ghavamin.mobile")
        ),
        BankInfo(
            bankName = "بانک قرض‌الحسنه رسالت",
            appTitleFa = "ام‌بانک رسالت",
            sessionPathHint = "تنظیمات > امنیت > دستگاه‌های مجاز",
            packages = listOf("ir.resalat.mbank", "com.resalat.mobilebank")
        ),
        BankInfo(
            bankName = "بانک قرض‌الحسنه مهر ایران",
            appTitleFa = "کیوبانک / همراه بانک مهر ایران",
            sessionPathHint = "پروفایل > امنیت > مدیریت نشست‌های فعال",
            packages = listOf("ir.qmb.mobile", "com.qmb.mobilebank")
        ),
        BankInfo(
            bankName = "بانک پارسیان",
            appTitleFa = "همراه بانک پارسیان / تاپ",
            sessionPathHint = "تنظیمات > امنیت و ورود > دستگاه‌های فعال",
            packages = listOf("com.parsian.mobilebank", "ir.parsianbank.mobile", "ir.pec.top")
        ),
        BankInfo(
            bankName = "بانک کشاورزی",
            appTitleFa = "باران / همراه بانک کشاورزی",
            sessionPathHint = "تنظیمات > امنیت > دستگاه‌های متصل",
            packages = listOf("com.keshavarzi.mobilebank", "ir.bki.mobilebank")
        ),
        BankInfo(
            bankName = "بانک مسکن",
            appTitleFa = "همراه بانک مسکن",
            sessionPathHint = "تنظیمات > امنیت > مدیریت نشست‌ها",
            packages = listOf("com.maskan.mobilebank", "ir.bank-maskan.mobile")
        ),
        BankInfo(
            bankName = "بانک رفاه کارگران",
            appTitleFa = "همراه بانک رفاه",
            sessionPathHint = "تنظیمات > امنیت > مدیریت دستگاه‌ها",
            packages = listOf("com.refah.mobilebank", "ir.bankrefah.mobile")
        ),
        BankInfo(
            bankName = "بانک آینده",
            appTitleFa = "آبانک / همراه کارت",
            sessionPathHint = "تنظیمات > امنیت > دستگاه‌های متصل",
            packages = listOf("com.ayandeh.abank", "com.kianwork.hamrahcard", "ir.ayandeh.mobile")
        ),
        BankInfo(
            bankName = "بانک شهر",
            appTitleFa = "همراه شهر پلاس",
            sessionPathHint = "تنظیمات > امنیت > مدیریت نشست‌های فعال",
            packages = listOf("com.shahr.mobilebank", "ir.shahr.plus")
        ),
        BankInfo(
            bankName = "بانک اقتصاد نوین",
            appTitleFa = "فراز / همراه بانک اقتصاد نوین",
            sessionPathHint = "تنظیمات > امنیت > نشست‌های فعال",
            packages = listOf("com.enbank.mobilebank", "ir.enbank.faraz")
        ),
        BankInfo(
            bankName = "بانک گردشگری",
            appTitleFa = "توبانک / همراه بانک گردشگری",
            sessionPathHint = "پروفایل > امنیت > مدیریت نشست‌ها",
            packages = listOf("com.tourism.tobank", "ir.tourismbank.mobile")
        ),
        BankInfo(
            bankName = "بانک سینا",
            appTitleFa = "همراه بانک سینا",
            sessionPathHint = "تنظیمات > امنیت > مدیریت دستگاه‌ها",
            packages = listOf("com.sina.mobilebank", "ir.sinabank.mobile")
        ),
        BankInfo(
            bankName = "پست بانک ایران",
            appTitleFa = "همراه بانک پست بانک",
            sessionPathHint = "تنظیمات > امنیت > دستگاه‌های مجاز",
            packages = listOf("com.postbank.mobilebank", "ir.postbank.mobile")
        ),
        BankInfo(
            bankName = "آسان پرداخت (آپ)",
            appTitleFa = "اپلیکیشن آپ",
            sessionPathHint = "پروفایل > امنیت و حریم خصوصی > دستگاه‌های متصل",
            packages = listOf("com.asanpardakht.app")
        )
    )

    private val PACKAGE_TO_BANK_MAP: Map<String, BankInfo> by lazy {
        val map = mutableMapOf<String, BankInfo>()
        for (bank in KNOWN_BANKS) {
            for (pkg in bank.packages) {
                map[pkg.lowercase()] = bank
            }
        }
        map
    }

    /**
     * اسکن تمام برنامه‌های نصب‌شده روی گوشی و استخراج همراه بانک‌های موجود به همراه نام و آیکون
     */
    fun detectInstalledBanks(context: Context): List<DetectedBankApp> {
        val pm = context.packageManager
        val detected = mutableListOf<DetectedBankApp>()

        try {
            val installedPackages = pm.getInstalledPackages(PackageManager.GET_META_DATA)
            for (pkg in installedPackages) {
                val pkgName = pkg.packageName.lowercase()
                val bankInfo = PACKAGE_TO_BANK_MAP[pkgName]
                if (bankInfo != null) {
                    val appLabel = try {
                        pkg.applicationInfo?.loadLabel(pm)?.toString() ?: bankInfo.appTitleFa
                    } catch (_: Exception) {
                        bankInfo.appTitleFa
                    }

                    detected.add(
                        DetectedBankApp(
                            packageName = pkg.packageName,
                            appName = appLabel,
                            bankTitle = bankInfo.bankName,
                            sessionPathHint = bankInfo.sessionPathHint
                        )
                    )
                }
            }
        } catch (_: Exception) {}

        return detected
    }
}

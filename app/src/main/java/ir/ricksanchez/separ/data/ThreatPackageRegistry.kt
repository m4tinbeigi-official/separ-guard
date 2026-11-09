package ir.ricksanchez.separ.data

/**
 * رجیستری مرکزی شناسایی بدافزارها، تروجان‌های بانکی، کرم‌های پیامکی و ابزارهای ریموت
 */
object ThreatPackageRegistry {

    enum class ThreatType {
        VELORA_TROJAN,
        YADEGARI_WORM,
        BANKING_PHISHING,
        FAKE_GOV_PHISHING,
        SMS_WORM,
        REMOTE_ACCESS_RISK,
        SPYWARE,
        RANSOMWARE,
        DEBUG_LURE_MALWARE,
        FAKE_RECEIPT_GENERATOR,
        SPYLOAN_MALWARE,
        STALKERWARE_SPY
    }

    enum class Severity {
        CRITICAL,
        HIGH,
        MEDIUM
    }

    data class ThreatResult(
        val isMalicious: Boolean,
        val isCritical: Boolean,
        val shouldForceUninstall: Boolean,
        val threatName: String,
        val threatFamily: String,
        val threatType: ThreatType,
        val description: String,
        val packageName: String
    )

    private val EXACT_MALICIOUS_PACKAGES = mapOf(
        "com.velora.cloud" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "بدافزار آلبوم یادگاری (ولورا)",
            threatFamily = "Velora",
            threatType = ThreatType.VELORA_TROJAN,
            description = "این بدافزار پیامک‌های بانکی شما را شنود کرده و به مخاطبانتان پیام فریبنده ارسال می‌کند.",
            packageName = "com.velora.cloud"
        ),
        "com.sana.adliran" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "سامانه جعلی ابلاغیه ثنا",
            threatFamily = "FakeSana",
            threatType = ThreatType.FAKE_GOV_PHISHING,
            description = "برنامه جعلی برای سرقت اطلاعات هویتی و خالی کردن حساب بانکی به بهانه ابلاغیه دادگاه.",
            packageName = "com.sana.adliran"
        ),
        "ir.sana.adliran" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "جعلی ابلاغیه قضایی ثنا",
            threatFamily = "FakeSana",
            threatType = ThreatType.FAKE_GOV_PHISHING,
            description = "برنامه فیشینگ جعلی دادگستری.",
            packageName = "ir.sana.adliran"
        ),
        "com.eblagh.adliran" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "جعلی مشاهده ابلاغ الکترونیک",
            threatFamily = "FakeSana",
            threatType = ThreatType.FAKE_GOV_PHISHING,
            description = "برنامه فیشینگ اطلاعات کارت بانکی.",
            packageName = "com.eblagh.adliran"
        ),
        "ir.adliran.sana" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "جعلی ابلاغیه عدل‌ایران",
            threatFamily = "FakeSana",
            threatType = ThreatType.FAKE_GOV_PHISHING,
            description = "برنامه فیشینگ سامانه ثنا.",
            packageName = "ir.adliran.sana"
        ),
        "com.adliran.eblagh" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "جعلی ابلاغیه عدل‌ایران",
            threatFamily = "FakeSana",
            threatType = ThreatType.FAKE_GOV_PHISHING,
            description = "برنامه جعلی قوه قضائیه.",
            packageName = "com.adliran.eblagh"
        ),
        "com.sana.app" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "برنامه جعلی ثنا",
            threatFamily = "FakeSana",
            threatType = ThreatType.FAKE_GOV_PHISHING,
            description = "بدافزار سرقت پیامک.",
            packageName = "com.sana.app"
        ),
        "com.saham.edalat" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "سامانه جعلی سود سهام عدالت",
            threatFamily = "FakeSahamEdalat",
            threatType = ThreatType.BANKING_PHISHING,
            description = "برنامه فریبنده سرقت رمز دوم و خالی کردن حساب بانکی به بهانه واریز سود سهام.",
            packageName = "com.saham.edalat"
        ),
        "ir.sahamedalat.app" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "جعلی سهام عدالت",
            threatFamily = "FakeSahamEdalat",
            threatType = ThreatType.BANKING_PHISHING,
            description = "سرقت اطلاعات کارت بانکی به نام سهام عدالت.",
            packageName = "ir.sahamedalat.app"
        ),
        "com.sahamedalat.sejam" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "جعلی سهام عدالت و سجام",
            threatFamily = "FakeSahamEdalat",
            threatType = ThreatType.BANKING_PHISHING,
            description = "فیشینگ و کلاهبرداری مالی.",
            packageName = "com.sahamedalat.sejam"
        ),
        "com.sejam.saham" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "جعلی سهام عدالت",
            threatFamily = "FakeSahamEdalat",
            threatType = ThreatType.BANKING_PHISHING,
            description = "سرقت اطلاعات حساب بانکی.",
            packageName = "com.sejam.saham"
        ),
        "com.sayad.pichak" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "سامانه جعلی ثبت چک صیادی",
            threatFamily = "FakeSayad",
            threatType = ThreatType.BANKING_PHISHING,
            description = "برنامه جعلی برای سرقت رمز دوم کارت بانکی و امضای دیجیتال.",
            packageName = "com.sayad.pichak"
        ),
        "com.sayad.cheque" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "جعلی چک صیاد",
            threatFamily = "FakeSayad",
            threatType = ThreatType.BANKING_PHISHING,
            description = "فیشینگ بانکی صیاد.",
            packageName = "com.sayad.cheque"
        ),
        "ir.sayad.cheque" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "چک صیادی جعلی",
            threatFamily = "FakeSayad",
            threatType = ThreatType.BANKING_PHISHING,
            description = "سرقت کارت بانکی.",
            packageName = "ir.sayad.cheque"
        ),
        "com.rahvar.khalafi" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "جعلی استعلام خلافی راهور",
            threatFamily = "FakePolice",
            threatType = ThreatType.BANKING_PHISHING,
            description = "هدایت به درگاه پرداخت فیک به بهانه پرداخت خلافی خودرو.",
            packageName = "com.rahvar.khalafi"
        ),
        "ir.rahvar120.khalafi" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "جعلی راهور ۱۲۰",
            threatFamily = "FakePolice",
            threatType = ThreatType.BANKING_PHISHING,
            description = "سرقت رمز دوم به بهانه خلافی.",
            packageName = "ir.rahvar120.khalafi"
        ),
        "com.yaraneh.kalabarg" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "جعلی کالابرگ و یارانه",
            threatFamily = "FakeYaraneh",
            threatType = ThreatType.BANKING_PHISHING,
            description = "کلاهبرداری مالی به بهانه ثبت نام یارانه و کالابرگ فجر.",
            packageName = "com.yaraneh.kalabarg"
        ),
        "ir.kalabarg.fajr" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "جعلی کالابرگ الکترونیک",
            threatFamily = "FakeYaraneh",
            threatType = ThreatType.BANKING_PHISHING,
            description = "سرقت اطلاعات کارت بانکی.",
            packageName = "ir.kalabarg.fajr"
        ),
        "com.free.internet" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "بدافزار اینترنت رایگان",
            threatFamily = "FakeTelecom",
            threatType = ThreatType.SMS_WORM,
            description = "کرم پیامکی که از سیم‌کارت شما برای همه مخاطبین پیامک ارسال می‌کند.",
            packageName = "com.free.internet"
        ),
        "com.mci.freeinternet" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "جعلی بسته رایگان همراه اول",
            threatFamily = "FakeTelecom",
            threatType = ThreatType.SMS_WORM,
            description = "کرم پیامکی سوءاستفاده از سیم‌کارت.",
            packageName = "com.mci.freeinternet"
        ),
        "com.irancell.freeinternet" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "جعلی بسته رایگان ایرانسل",
            threatFamily = "FakeTelecom",
            threatType = ThreatType.SMS_WORM,
            description = "کرم پیامکی مخرب.",
            packageName = "com.irancell.freeinternet"
        ),
        "com.invitation.wedding" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "بدافزار کارت دعوت عروسی",
            threatFamily = "WeddingLure",
            threatType = ThreatType.SMS_WORM,
            description = "بدافزار مخرب گزارش‌شده توسط پلیس فتا که پیامک‌های بانکی را سرقت می‌کند.",
            packageName = "com.invitation.wedding"
        ),
        "com.wedding.card.invitation" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "کارت دعوت عروسی جعلی",
            threatFamily = "WeddingLure",
            threatType = ThreatType.SMS_WORM,
            description = "سرقت پیامک‌های رمز دوم.",
            packageName = "com.wedding.card.invitation"
        ),
        "com.arosi.kart" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "بدافزار کارت عروسی",
            threatFamily = "WeddingLure",
            threatType = ThreatType.SMS_WORM,
            description = "کرم پیامکی و جاسوس پیامک.",
            packageName = "com.arosi.kart"
        ),
        "com.lololo" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "باج‌افزار اندرویدی زئوس",
            threatFamily = "ZeusAndroid",
            threatType = ThreatType.RANSOMWARE,
            description = "باج‌افزار قفل‌کننده حافظه (گزارش مرکز ماهر).",
            packageName = "com.lololo"
        ),
        "android.com.ui" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "بدافزار جاسوسی فیلترشکن جعلی",
            threatFamily = "AmadVpn",
            threatType = ThreatType.SPYWARE,
            description = "سرقت اطلاعات دستگاه و شنود نشست‌ها (گزارش مرکز ماهر).",
            packageName = "android.com.ui"
        ),
        "com.shexa.permissionmanager" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "ابزار مخرب سرقت دسترسی‌ها",
            threatFamily = "PermissionStealer",
            threatType = ThreatType.SPYWARE,
            description = "برنامه دستکاری مجوزها و شنود رمز دوم.",
            packageName = "com.shexa.permissionmanager"
        ),
        "ahmyth.mine.king.ahmyth" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "تروجان دسترسی ریموت AhMyth",
            threatFamily = "AhMyth",
            threatType = ThreatType.REMOTE_ACCESS_RISK,
            description = "ابزار ریموت و نفوذ به میکروفون و دوربین و پیامک‌ها.",
            packageName = "ahmyth.mine.king.ahmyth"
        ),
        "com.vamchi.app" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "بدافزار ثبت نام وام فوری (وام‌چی)",
            threatFamily = "SpyLoan",
            threatType = ThreatType.SPYLOAN_MALWARE,
            description = "بدافزار فریبنده وام بدون ضامن برای سرقت اطلاعات هویتی و رمزهای یکبارمصرف بانکی (گزارش پلیس فتا).",
            packageName = "com.vamchi.app"
        ),
        "com.vam.fori" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "بدافزار وام فوری",
            threatFamily = "SpyLoan",
            threatType = ThreatType.SPYLOAN_MALWARE,
            description = "سرقت رمز پویا به بهانه وام بدون ضامن.",
            packageName = "com.vam.fori"
        ),
        "com.radyab.spy" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "جاسوس‌افزار ردیاب و شنود نامحسوس",
            threatFamily = "Stalkerware",
            threatType = ThreatType.STALKERWARE_SPY,
            description = "برنامه غیرقانونی برای شنود میکروفون، خواندن پیامک‌ها و سرقت اطلاعات شخصی.",
            packageName = "com.radyab.spy"
        ),
        "com.hamyar.gps" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "استالکرور همیار جی‌پی‌اس",
            threatFamily = "Stalkerware",
            threatType = ThreatType.STALKERWARE_SPY,
            description = "جاسوس‌افزار سرقت موقعیت مکانی و پیامک‌ها.",
            packageName = "com.hamyar.gps"
        )
    )

    private val REMOTE_ACCESS_RATS = mapOf(
        "com.anydesk.anydeskandroid" to ThreatResult(
            isMalicious = true,
            isCritical = false,
            shouldForceUninstall = true,
            threatName = "برنامه ریموت انی‌دسک (AnyDesk)",
            threatFamily = "RemoteAccessTool",
            threatType = ThreatType.REMOTE_ACCESS_RISK,
            description = "کلاهبرداران تلفنی از طریق این برنامه گوشی شما را از راه دور کنترل کرده و حساب شما را خالی می‌کنند.",
            packageName = "com.anydesk.anydeskandroid"
        ),
        "com.anydesk.adcontrol.ad1" to ThreatResult(
            isMalicious = true,
            isCritical = true,
            shouldForceUninstall = true,
            threatName = "پلاگین کنترل کامل انی‌دسک",
            threatFamily = "RemoteAccessTool",
            threatType = ThreatType.REMOTE_ACCESS_RISK,
            description = "پلاگین خطرناک برای کنترل صفحه و کلیک خودکار توسط افراد ناشناس.",
            packageName = "com.anydesk.adcontrol.ad1"
        ),
        "com.carriez.flutter_rustdesk" to ThreatResult(
            isMalicious = true,
            isCritical = false,
            shouldForceUninstall = true,
            threatName = "برنامه ریموت راست‌دسک (RustDesk)",
            threatFamily = "RemoteAccessTool",
            threatType = ThreatType.REMOTE_ACCESS_RISK,
            description = "برنامه کنترل از راه دور صفحه گوشی مورد سوءاستفاده کلاهبرداران.",
            packageName = "com.carriez.flutter_rustdesk"
        ),
        "com.teamviewer.host.market" to ThreatResult(
            isMalicious = true,
            isCritical = false,
            shouldForceUninstall = true,
            threatName = "تیم‌ویوور هاست (TeamViewer Host)",
            threatFamily = "RemoteAccessTool",
            threatType = ThreatType.REMOTE_ACCESS_RISK,
            description = "برنامه دسترسی بدون اجازه به صفحه نمایش.",
            packageName = "com.teamviewer.host.market"
        ),
        "com.teamviewer.quicksupport.market" to ThreatResult(
            isMalicious = true,
            isCritical = false,
            shouldForceUninstall = true,
            threatName = "تیم‌ویوور کوئیک‌ساپورت",
            threatFamily = "RemoteAccessTool",
            threatType = ThreatType.REMOTE_ACCESS_RISK,
            description = "برنامه اشتراک‌گذاری صفحه و کنترل ریموت.",
            packageName = "com.teamviewer.quicksupport.market"
        ),
        "com.sand.airdroid" to ThreatResult(
            isMalicious = true,
            isCritical = false,
            shouldForceUninstall = true,
            threatName = "ایر‌دروید (AirDroid)",
            threatFamily = "RemoteAccessTool",
            threatType = ThreatType.REMOTE_ACCESS_RISK,
            description = "برنامه کنترل و مشاهده گوشی از کامپیوتر ناشناس.",
            packageName = "com.sand.airdroid"
        ),
        "com.supremo.remote" to ThreatResult(
            isMalicious = true,
            isCritical = false,
            shouldForceUninstall = true,
            threatName = "سوپریمو ریموت (Supremo)",
            threatFamily = "RemoteAccessTool",
            threatType = ThreatType.REMOTE_ACCESS_RISK,
            description = "نرم‌افزار کنترل از راه دور گوشی.",
            packageName = "com.supremo.remote"
        ),
        "com.splashtop.remote" to ThreatResult(
            isMalicious = true,
            isCritical = false,
            shouldForceUninstall = true,
            threatName = "اسپلش‌تاپ (Splashtop)",
            threatFamily = "RemoteAccessTool",
            threatType = ThreatType.REMOTE_ACCESS_RISK,
            description = "برنامه ریموت دسکتاپ مورد سوءاستفاده کلاهبرداران.",
            packageName = "com.splashtop.remote"
        ),
        "com.zoho.assist" to ThreatResult(
            isMalicious = true,
            isCritical = false,
            shouldForceUninstall = true,
            threatName = "زوهو اسیست (Zoho Assist)",
            threatFamily = "RemoteAccessTool",
            threatType = ThreatType.REMOTE_ACCESS_RISK,
            description = "ابزار ریموت و مانیتورینگ صفحه.",
            packageName = "com.zoho.assist"
        )
    )

    private val DEBUG_LURE_REGEX = Regex(
        "^com\\.example\\.(saham|eblagh|adliran|sana|yaraneh|khalafi).*",
        RegexOption.IGNORE_CASE
    )

    /**
     * ارزیابی جامع پکیج‌نیم بر اساس قوانین قطعی و الگوهای رفتاری
     */
    fun evaluatePackage(packageName: String): ThreatResult? {
        val normalized = packageName.trim().lowercase()

        // ۰. بررسی لیست سفید رسمی (مارکت‌های بازار، مایکت و برنامه‌های رسمی هرگز فلگ نمی‌شوند)
        if (TrustedWhitelistRegistry.isWhitelisted(normalized)) {
            return null
        }

        // ۱. قانون الزام قطعی: هر پکیجی که کلمه velora در آن باشد بلافاصله پاک شود
        if (normalized.contains("velora")) {
            return ThreatResult(
                isMalicious = true,
                isCritical = true,
                shouldForceUninstall = true,
                threatName = "بدافزار ولورا (سرقت پیامک و رمز پویا)",
                threatFamily = "Velora",
                threatType = ThreatType.VELORA_TROJAN,
                description = "این بدافزار پیامک‌های رمز پویای بانکی شما را شنود کرده و به سرور هکر ارسال می‌کند.",
                packageName = packageName
            )
        }

        // ۲. قانون الزام قطعی: هر پکیجی که کلمه yadegari در آن باشد
        if (normalized.contains("yadegari")) {
            return ThreatResult(
                isMalicious = true,
                isCritical = true,
                shouldForceUninstall = true,
                threatName = "کرم پیامکی پوشه یادگاری",
                threatFamily = "Yadegari",
                threatType = ThreatType.YADEGARI_WORM,
                description = "این برنامه مخرب از طرف خط شما برای تمام مخاطبانتان پیامک جعلی ارسال می‌کند.",
                packageName = packageName
            )
        }

        // ۳. بررسی برنامه‌های رسیدساز جعلی بانکی
        if (normalized.contains("residsaz") || normalized.contains("fake.receipt") || normalized.contains("fake.residsaz")) {
            return ThreatResult(
                isMalicious = true,
                isCritical = true,
                shouldForceUninstall = true,
                threatName = "برنامه رسیدساز جعلی بانکی",
                threatFamily = "FakeReceiptGenerator",
                threatType = ThreatType.FAKE_RECEIPT_GENERATOR,
                description = "برنامه غیرقانونی برای ساخت رسیدهای انتقال وجه دروغین و کلاهبرداری در معاملات.",
                packageName = packageName
            )
        }

        // ۴. بررسی بدافزارهای وام فوری و وام‌چی (SpyLoan)
        if (normalized.contains("vamchi") || normalized.contains("vamfori") || (normalized.contains("vam") && normalized.contains("fori"))) {
            return ThreatResult(
                isMalicious = true,
                isCritical = true,
                shouldForceUninstall = true,
                threatName = "بدافزار ثبت‌نام وام فوری (وام‌چی)",
                threatFamily = "SpyLoan",
                threatType = ThreatType.SPYLOAN_MALWARE,
                description = "بدافزار کلاهبرداری مالی و سرقت رمزهای پیامکی به بهانه وام فوری بدون ضامن.",
                packageName = packageName
            )
        }

        // ۳. بررسی جدول پکیج‌های آلوده دقیق
        EXACT_MALICIOUS_PACKAGES[normalized]?.let {
            return it.copy(packageName = packageName)
        }

        // ۴. بررسی ابزارهای ریموت و RAT مورد سوءاستفاده
        REMOTE_ACCESS_RATS[normalized]?.let {
            return it.copy(packageName = packageName)
        }

        // ۵. بررسی الگوهای بدافزارهای دیباگ تست و فریبنده
        if (DEBUG_LURE_REGEX.matches(normalized)) {
            return ThreatResult(
                isMalicious = true,
                isCritical = true,
                shouldForceUninstall = true,
                threatName = "بدافزار آزمایشی با عنوان بانکی یا دولتی",
                threatFamily = "DebugLure",
                threatType = ThreatType.DEBUG_LURE_MALWARE,
                description = "یک برنامه ناشناس با عنوان نهادهای دولتی یا بانکی که با فرمت تستی ساخته شده است.",
                packageName = packageName
            )
        }

        return null
    }

    /**
     * آیا پکیج جزو برنامه‌های ریموت دسکتاپ است؟
     */
    fun isRemoteAccessTool(packageName: String): Boolean {
        return REMOTE_ACCESS_RATS.containsKey(packageName.trim().lowercase())
    }
}

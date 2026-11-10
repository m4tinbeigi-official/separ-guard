package ir.ricksanchez.separ.data;

import java.util.HashMap;
import java.util.Map;

/**
 * پایگاه داده متمرکز و رسمی پکیج‌های آلوده، جاسوس‌افزارها و تروجان‌های شناخته‌شده ایران
 * منطبق با گزارش‌های رسمی کسپرسکی، ESET، Check Point، Zimperium و پلیس فتا
 */
public class ThreatPackageRegistry {

    public static class ThreatResult {
        public final boolean isMalicious;
        public final boolean isCritical;
        public final boolean shouldForceUninstall;
        public final String threatName;
        public final String threatFamily;
        public final String description;
        public final String packageName;

        public ThreatResult(boolean isMal, boolean isCrit, boolean forceUn, String name, String fam, String desc, String pkg) {
            this.isMalicious = isMal;
            this.isCritical = isCrit;
            this.shouldForceUninstall = forceUn;
            this.threatName = name;
            this.threatFamily = fam;
            this.description = desc;
            this.packageName = pkg;
        }
    }

    private static final Map<String, ThreatResult> EXACT_MALICIOUS_PACKAGES = new HashMap<>();

    static {
        putThreat("com.velora.cloud", "Velora", "VELORA_TROJAN", "بدافزار اصلی آلبوم یادگاری / پوشه یادگاری");
        putThreat("com.sana.adliran", "FakeSana", "FAKE_GOV_PHISHING", "سامانه جعلی ثنا و ابلاغیه قضایی عدل‌ایران");
        putThreat("ir.sana.adliran", "FakeSana", "FAKE_GOV_PHISHING", "اپلیکیشن جعلی ابلاغیه قضایی ثنا");
        putThreat("com.eblagh.adliran", "FakeSana", "FAKE_GOV_PHISHING", "برنامه جعلی مشاهده ابلاغ الکترونیک قضایی");
        putThreat("ir.adliran.sana", "FakeSana", "FAKE_GOV_PHISHING", "جعلی ابلاغیه دادگستری");
        putThreat("com.adliran.eblagh", "FakeSana", "FAKE_GOV_PHISHING", "جعلی ابلاغیه الکترونیک عدل‌ایران");
        putThreat("com.sana.app", "FakeSana", "FAKE_GOV_PHISHING", "برنامه جعلی ثنا");
        putThreat("com.saham.edalat", "FakeSahamEdalat", "BANKING_PHISHING", "برنامه جعلی واریز سود سهام عدالت (سرقت اطلاعات کارت)");
        putThreat("ir.sahamedalat.app", "FakeSahamEdalat", "BANKING_PHISHING", "سامانه جعلی سود سهام عدالت");
        putThreat("com.sahamedalat.sejam", "FakeSahamEdalat", "BANKING_PHISHING", "سامانه جعلی سجام و سهام عدالت");
        putThreat("com.sejam.saham", "FakeSahamEdalat", "BANKING_PHISHING", "برنامه جعلی سهام عدالت");
        putThreat("com.saham.edalat.soud", "FakeSahamEdalat", "BANKING_PHISHING", "برنامه جعلی واریز سود سهام عدالت");
        putThreat("com.sayad.pichak", "FakeSayad", "BANKING_PHISHING", "سامانه جعلی ثبت و استعلام چک صیادی پیچک");
        putThreat("com.sayad.cheque", "FakeSayad", "BANKING_PHISHING", "برنامه جعلی صیاد");
        putThreat("ir.sayad.cheque", "FakeSayad", "BANKING_PHISHING", "چک صیادی جعلی");
        putThreat("com.cheque.sayad", "FakeSayad", "BANKING_PHISHING", "برنامه جعلی چک صیاد");
        putThreat("com.rahvar.khalafi", "FakePolice", "BANKING_PHISHING", "سامانه جعلی استعلام خلافی خودرو و نمره منفی راهور");
        putThreat("ir.rahvar120.khalafi", "FakePolice", "BANKING_PHISHING", "برنامه جعلی راهور ۱۲۰");
        putThreat("com.khalafi.khodro", "FakePolice", "BANKING_PHISHING", "برنامه جعلی پرداخت جریمه و خلافی");
        putThreat("com.yaraneh.kalabarg", "FakeYaraneh", "BANKING_PHISHING", "برنامه جعلی ثبت نام کالابرگ الکترونیک و یارانه");
        putThreat("ir.kalabarg.fajr", "FakeYaraneh", "BANKING_PHISHING", "سامانه جعلی کالابرگ فجر");
        putThreat("com.free.internet", "FakeTelecom", "SMS_WORM", "برنامه فریبنده اینترنت رایگان عیدانه و دولت");
        putThreat("com.mci.freeinternet", "FakeTelecom", "SMS_WORM", "برنامه جعلی بسته اینترنت رایگان همراه اول");
        putThreat("com.irancell.freeinternet", "FakeTelecom", "SMS_WORM", "برنامه جعلی بسته اینترنت رایگان ایرانسل");
        putThreat("com.invitation.wedding", "WeddingLure", "SMS_WORM", "بدافزار کارت دعوت عروسی جعلی (گزارش پلیس فتا)");
        putThreat("com.wedding.card.invitation", "WeddingLure", "SMS_WORM", "کارت دعوت عروسی جعلی");
        putThreat("com.arosi.kart", "WeddingLure", "SMS_WORM", "بدافزار کارت عروسی");
        putThreat("com.lololo", "ZeusAndroid", "RANSOMWARE", "باج‌افزار اندرویدی زئوس (گزارش مرکز ماهر)");
        putThreat("android.com.ui", "AmadVpn", "SPYWARE", "بدافزار جاسوسی موسوم به فیلترشکن آمدنیوز (گزارش مرکز ماهر)");
        putThreat("com.shexa.permissionmanager", "PermissionStealer", "SPYWARE", "ابزار سرقت دسترسی‌ها و شنود پیامک بانکی");
        putThreat("ahmyth.mine.king.ahmyth", "AhMyth", "RAT", "تروجان دسترسی از راه دور AhMyth");
        putThreat("com.vamchi.app", "SpyLoan", "SPYLOAN_MALWARE", "بدافزار ثبت نام وام فوری (وام‌چی)");
        putThreat("com.radyab.spy", "Stalkerware", "STALKERWARE_SPY", "جاسوس‌افزار ردیاب و شنود نامحسوس");
        putThreat("org.ir.talaeii", "Talaeii", "ROGUE_TELEGRAM", "تلگرام طلایی (طلاگرام) - شنود پیام‌ها و رصد کانال‌ها");
        putThreat("ir.hotgram.mobile.android", "Hotgram", "ROGUE_TELEGRAM", "هاتگرام - شناسایی به عنوان جاسوس‌افزار توسط گوگل پلی پروتکت");
        putThreat("com.hanista.mobogram", "Mobogram", "ROGUE_TELEGRAM", "موبوگرام اصلی (هانیستا) - فورس‌جوین و نقض حریم خصوصی");
        putThreat("com.hanista.moboplus", "Mobogram", "ROGUE_TELEGRAM", "موبوپلاس - تزریق توکن و ابزار جانبی غیررسمی");
        putThreat("com.mobogram.messenger.chat", "FakeTelegram", "ROGUE_TELEGRAM", "یوگرام (Ugram) - کلاینت جعلی با کدهای مخرب تبلیغاتی");
        putThreat("org.ir.azadtelegram", "FakeTelegram", "ROGUE_TELEGRAM", "آزادگرام - پوسته ناامن با قابلیت استخراج اطلاعات حساب");
        putThreat("ir.forigram.messenger", "FakeTelegram", "ROGUE_TELEGRAM", "فوری‌گرام - کلاینت غیررسمی مشکوک و ادویر");
        putThreat("ir.igram.messenger", "FakeTelegram", "ROGUE_TELEGRAM", "آی‌گرام - شنود نشست و دسترسی بدون مجوز به دفترچه تلفن");
        putThreat("ir.adliran.eblagh", "FakeAdliran", "BANKING_PHISHING", "عدل ایران / شکوائیه دادسرا - سرقت کارت بانکی در درگاه فیک");
        putThreat("com.adliran.sana", "FakeSana", "BANKING_PHISHING", "ابلاغیه الکترونیک دادگستری - کنترل پیامک‌ها و کدهای OTP");
        putThreat("com.edalat.sahamedalat", "FakeSahamEdalat", "BANKING_PHISHING", "سامانه سود سهام عدالت - استخراج کدملی و سرقت حساب");
        putThreat("com.kart.sookht", "FakeFuelCard", "BANKING_PHISHING", "سهمیه بنزین و کارت سوخت - فیشینگ درگاه شتاب");
        putThreat("com.sighe.yab", "FakeLure", "BANKING_PHISHING", "صیغه‌یاب و همدم - اخاذی و سرقت موجودی حساب بانکی");
        putThreat("ir.ops.breacker", "FakePsiphon", "QURIUM_TRAP", "فیلترشکن آمدنیوز / سایفون جعلی - سرقت کدهای تایید دومرحله‌ای");
        putThreat("ir.ops.breaker", "FakePsiphon", "QURIUM_TRAP", "شکننده فیلتر و پیامک جعلی - بات‌نت و استخراج تماس‌ها");
        putThreat("ir.ops.irani", "FakePsiphon", "QURIUM_TRAP", "سایفون تقلبی ایرانی - شنود نشست و موقعیت مکانی");
        putThreat("ir.persianlifeme.mahvare", "ScamTrap", "SCAM_TRAP", "ماهواره جیبی اندروید (IPTV Scam) - سرقت پیامک‌ها");
        putThreat("ir.besteveryeverapp.telegram", "ScamTrap", "SCAM_TRAP", "ماهواره تلگرام جعلی - سرقت فایل‌های دستگاه");
        putThreat("ir.persianlifeme.freeforall", "ValueAddedScam", "VALUE_ADDED_SCAM", "سرویس پیامکی رایگان فریبنده - عضویت ناخواسته در ارزش افزوده");
        putThreat("com.getdoc.freepaaper.dissertation", "DomesticKitten", "DOMESTIC_KITTEN", "سرای مقاله / دانلود مقالات - شنود زنده میکروفون و ردیابی GPS");
        putThreat("com.mohsen", "FurBall", "FURBALL_SPYWARE", "رستوران محسن تهران (جعلی) - فعال‌سازی میکروفون و سرقت پیامک‌ها");
        putThreat("com.majorityapps.exoticflowers", "FurBall", "FURBALL_SPYWARE", "تصاویر گل‌های عجیب - جاسوس‌افزار FurBall با دسترسی کامل به فایل‌ها");
        putThreat("com.clem.isisnews", "DomesticKitten", "DOMESTIC_KITTEN", "اخبار داعش / جنگ (جعلی) - ردیابی موقعیت مکانی و شنود محیطی");
        putThreat("com.ssd.vipre", "FakeAV", "FAKE_ANTIVIRUS", "آنتی‌ویروس وایپر تقلبی (VIPRE) - استتار جهت کنترل کامل گوشی");
        putThreat("com.memopt", "HiddenSpy", "HIDDEN_SPYWARE", "بهینه‌ساز رم و باتری (جعلی) - بدافزار پنهان در پس‌زمینه بدون آیکون");
        putThreat("com.andriod.browser", "PhishingBrowser", "PHISHING_BROWSER", "مرورگر اندروید جعلی - سرقت تاریخچه وب و اطلاعات لاگین");
        putThreat("com.eracomteck", "DomesticKitten", "DOMESTIC_KITTEN", "دوست‌یابی بادو تقلبی - سرقت لیست مخاطبان و پیام‌ها");
        putThreat("air.com.arsnetworks.poems.moshiri", "DomesticKitten", "DOMESTIC_KITTEN", "اشعار فریدون مشیری (جعلی) - استتار در پوشش شعر جهت مانیتورینگ");
        putThreat("air.com.arsnetworks.poems.sohrab", "DomesticKitten", "DOMESTIC_KITTEN", "اشعار سهراب سپهری (جعلی) - نظارت بر پیامک‌ها، لیست تماس و ضبط صدا");
        putThreat("com.nidayehaq", "FurBall", "FURBALL_SPYWARE", "ندای حق مذهبی (جعلی) - جاسوس‌افزار FurBall");
        putThreat("com.appmilad.numrent", "RatMilad", "RATMILAD_SPYWARE", "شماره مجازی نام‌رنت (NumRent) - ضبط مکالمات، دوربین و روت");
        putThreat("com.textme.phone", "RatMilad", "RATMILAD_SPYWARE", "تکست‌می جعلی (Text Me) - سرقت آدرس MAC و کنترل کامل");
        putThreat("com.fmwhatsapp", "TriadaTrojan", "TRIADA_TROJAN", "اف‌ام واتساپ (FMWhatsApp) - سرقت نشست، خواندن کدهای OTP و عضویت مخفی");
        putThreat("com.yowhatsapp", "TriadaTrojan", "TRIADA_TROJAN", "یو واتساپ (YoWhatsApp) - ارسال هرزنامه و ماژول‌های ناشناس");
        putThreat("com.gbwhatsapp", "TriadaTrojan", "TRIADA_TROJAN", "جی‌بی واتساپ (GBWhatsApp) - سرقت پیام‌ها و شنود نشست‌های فعال");
        putThreat("com.whatsapp.plus", "TriadaTrojan", "TRIADA_TROJAN", "واتساپ پلاس (WhatsApp Plus) - استخراج داده‌های چت و دور زدن لایه‌ها");
        putThreat("ir.resaneh.hotgram", "هاتگرام (بسته رسانه)", "MALWARE_IOC", "هاتگرام (بسته رسانه) - پروکسی رصدشده و تزریق کدهای رهگیری");
        putThreat("com.hanista.mobogramtwo", "موبوگرام ۲ (نسخه دوم)", "MALWARE_IOC", "موبوگرام ۲ (نسخه دوم) - ارسال پنهانی اطلاعات دستگاه و عضویت در کانال‌های اسپانسری");
        putThreat("com.hanista.mobogramthree", "موبوگرام ۳ (نسخه سوم)", "MALWARE_IOC", "موبوگرام ۳ (نسخه سوم) - باز کردن پنهانی لینک‌ها و استفاده از ترافیک پس‌زمینه");
        putThreat("org.vidogram.messenger", "ویدوگرام", "MALWARE_IOC", "ویدوگرام - تزریق تبلیغات، SDKهای رهگیری رفتار کاربر و شکستن ایزولاسیون");
        putThreat("ir.farsigram.messenger", "فارسی‌گرام", "MALWARE_IOC", "فارسی‌گرام - کلاینت ناامن با قابلیت کنترل کانال‌ها و پیام‌ها");
        putThreat("com.turbo.telegram", "توربوگرام", "MALWARE_IOC", "توربوگرام - کلاینت تجاری حاوی کدهای رصد نشست");
        putThreat("com.barandagram.app", "برندگرام", "MALWARE_IOC", "برندگرام - پوسته تلگرامی با تزریق بنرهای تبلیغاتی تهاجمی");
        putThreat("com.aerowhatsapp", "آئرو واتساپ (Aero WhatsApp)", "MALWARE_IOC", "آئرو واتساپ (Aero WhatsApp) - استخراج توکن‌های ارتباطی و نشت سوابق تماس");
        putThreat("com.obwhatsapp", "او‌بی واتساپ (OBWhatsApp)", "MALWARE_IOC", "او‌بی واتساپ (OBWhatsApp) - تزریق کدهای مخرب تبلیغاتی و کنترل بخش نوتیفیکیشن‌ها");
        putThreat("com.eblagh.sana", "مشاهده ابلاغیه دادسرا", "MALWARE_IOC", "مشاهده ابلاغیه دادسرا - مخفی کردن آیکون پس از نصب و فعال‌سازی سرویس پس‌زمینه");
        putThreat("com.shekayat.sabt", "ثبت شکوائیه آنلاین", "MALWARE_IOC", "ثبت شکوائیه آنلاین - سرقت اطلاعات هویتی و مسدودسازی اعلان پیامک‌های برداشت وجه");
        putThreat("com.post.peygiri", "رهگیری مرسوله شرکت پست", "MALWARE_IOC", "رهگیری مرسوله شرکت پست - ارسال پیامک «مرسوله شما به علت نقص آدرس مرجوع شد» و سرقت حساب");
        putThreat("ir.post.tracking", "پیگیری بسته اداره پست", "MALWARE_IOC", "پیگیری بسته اداره پست - دریافت دسترسی Accessibility و کنترل کامل دستگاه");
        putThreat("com.hamta.sabt", "سامانه قطع گوشی همتا", "MALWARE_IOC", "سامانه قطع گوشی همتا - سرقت شناسه IMEI و رمز اینترنتی به بهانه عدم رجیستری");
        putThreat("com.maliat.sabt", "سامانه امور مالیاتی و مودیان", "MALWARE_IOC", "سامانه امور مالیاتی و مودیان - کلاهبرداری پیامکی با عنوان جریمه فرار مالیاتی");
        putThreat("ir.seham.edalat", "سامانه ثبت نام سهام عدالت", "MALWARE_IOC", "سامانه ثبت نام سهام عدالت - سرقت نشست‌های پیام‌رسان‌ها جهت بازنشر پیامک فیشینگ");
        putThreat("com.kalabarg.fajr", "توزیع کالابرگ الکترونیکی فجر", "MALWARE_IOC", "توزیع کالابرگ الکترونیکی فجر - باج‌گیری پیامکی و استخراج موجودی حساب‌ها");
        putThreat("com.blubank.vam", "وام فوری بلوبانک جعلی", "MALWARE_IOC", "وام فوری بلوبانک جعلی - فیشینگ اطلاعات لاگین همراه بانک‌ها و اسناد هویتی");
        putThreat("com.radyab.hamsar", "همسرگرام / ردیاب همسر", "MALWARE_IOC", "همسرگرام / ردیاب همسر - شنود تماس‌ها، خواندن چت‌های شبکه‌های اجتماعی و ردیابی زنده GPS");
        putThreat("com.body.scanner.fake", "دوربین دید در شب / لخت‌کن", "MALWARE_IOC", "دوربین دید در شب / لخت‌کن - رت بر پایه SpyNote جهت دستیابی به دسترسی کامل روت و مخاطبان");
        putThreat("com.mahvareh.online", "ماهواره جیبی آنلاین و تلویزیون", "MALWARE_IOC", "ماهواره جیبی آنلاین و تلویزیون - سرقت پیامک‌ها و کدهای فعال‌سازی و باز کردن صفحات اسپانسری");
        putThreat("com.net.raygan", "اینترنت رایگان ۱۰۰ گیگ هدیه", "MALWARE_IOC", "اینترنت رایگان ۱۰۰ گیگ هدیه - کرم پیامکی جهت انتشار انبوه لینک‌های مخرب از سیم‌کارت قربانی");
        putThreat("com.wifi.hacker.real", "هک واقعی وای‌فای همسایه", "MALWARE_IOC", "هک واقعی وای‌فای همسایه - تروجان کی‌لاگر و ارسال رمزهای عبور تایپ‌شده");
        putThreat("ir.persianlifeme.vipforall", "پروکسی اختصاصی VIP", "MALWARE_IOC", "پروکسی اختصاصی VIP - ثبت ترافیک اینترنت و رمزهای عبور ارسالی");
        putThreat("ir.persianlifeme.irani", "فیلترشکن ایرانیان لایف", "MALWARE_IOC", "فیلترشکن ایرانیان لایف - جاسوس‌افزار هدفمند جهت استخراج هویت شبکه‌ای");
        putThreat("com.poet.saadi", "کلیات سعدی شیرازی", "MALWARE_IOC", "کلیات سعدی شیرازی - جاسوس‌افزار تله");
        putThreat("com.hamgaam.shahnamef", "شاهنامه فردوسی صوتی", "MALWARE_IOC", "شاهنامه فردوسی صوتی - فعال‌سازی میکروفون و ضبط صدا در زمان پخش صوت");
        putThreat("ir.hawijapp.myhafez", "دیوان حافظ شیرازی", "MALWARE_IOC", "دیوان حافظ شیرازی - خواندن تمامی پیام‌های ورودی و خروجی");
        putThreat("ir.korosh.kabir", "تاریخ کوروش کبیر", "MALWARE_IOC", "تاریخ کوروش کبیر - پایش گروه‌های هویتی و فرهنگی");
        putThreat("com.ramadan.kareem.app", "ادعیه ماه رمضان", "MALWARE_IOC", "ادعیه ماه رمضان - ابزار نظارتی استتار شده");
        putThreat("com.numrent.app", "کلاینت وب‌ویو NumRent", "MALWARE_IOC", "کلاینت وب‌ویو NumRent - بسترساز اجرای پی‌لود RatMilad");
        putThreat("com.sweden.driving.license", "آزمون گواهینامه رانندگی سوئد به فارسی", "MALWARE_IOC", "آزمون گواهینامه رانندگی سوئد به فارسی - سرقت کدهای تایید دومرحله‌ای تلگرام و ضبط صدای محیط");
        putThreat("com.google.service.update", "سرویس بروزرسانی گوگل", "MALWARE_IOC", "سرویس بروزرسانی گوگل - شبیه‌سازی سیستم امنیتی اندروید برای ممانعت از حذف و دزدی OTP");
        putThreat("com.android.providers.telephony.fix", "سرویس مخفی سیستم تلفن", "MALWARE_IOC", "سرویس مخفی سیستم تلفن - رهگیری دائم پیامک‌های تایید حساب‌های کاربری");
    }

    private static void putThreat(String pkg, String name, String fam, String desc) {
        EXACT_MALICIOUS_PACKAGES.put(pkg.toLowerCase().trim(), new ThreatResult(true, true, true, name, fam, desc, pkg));
    }

    public static ThreatResult evaluatePackage(String packageName) {
        if (packageName == null) return null;
        String normalized = packageName.trim().toLowerCase();

        // ۰. بررسی لیست سفید رسمی
        if (TrustedWhitelistRegistry.isWhitelisted(normalized)) {
            return null;
        }

        // ۱. پکیج‌های مشخص قطعی (بیش از ۱۰۰ مورد شناسایی‌شده)
        if (EXACT_MALICIOUS_PACKAGES.containsKey(normalized)) {
            return EXACT_MALICIOUS_PACKAGES.get(normalized);
        }

        // ۲. قانون الزام قطعی ولورا
        if (normalized.contains("velora")) {
            return new ThreatResult(true, true, true, "بدافزار ولورا (سرقت پیامک و رمز پویا)", "Velora", "این بدافزار پیامک های رمز پویای شما را شنود کرده و به سرور هکر ارسال می کند.", packageName);
        }

        // ۳. قانون الزام قطعی یادگاری
        if (normalized.contains("yadegari")) {
            return new ThreatResult(true, true, true, "کرم پیامکی پوشه یادگاری", "Yadegari", "این برنامه مخرب از طرف خط شما برای تمام مخاطبانتان پیامک جعلی ارسال می کند.", packageName);
        }

        // ۴. رسیدسازهای جعلی
        if (normalized.contains("residsaz") || normalized.contains("fake.receipt")) {
            return new ThreatResult(true, true, true, "برنامه رسیدساز جعلی بانکی", "FakeReceipt", "تولید رسیدهای دروغین انتقال وجه برای کلاهبرداری در معاملات.", packageName);
        }

        // ۵. وام چی و وام فوری
        if (normalized.contains("vamchi") || normalized.contains("vamfori")) {
            return new ThreatResult(true, true, true, "بدافزار وام فوری و وام چی", "SpyLoan", "سرقت رمز پویا به بهانه وام بدون ضامن.", packageName);
        }

        return null;
    }
}

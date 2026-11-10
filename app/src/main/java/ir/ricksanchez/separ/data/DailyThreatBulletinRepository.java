package ir.ricksanchez.separ.data;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;

/**
 * سامانه بولتن خبری و هشدارهای روزانه امنیت سایبری (Daily Threat Flash Bulletin)
 * نمایش خودکار جدیدترین شگردها، هک‌ها و اخبار امنیتی در هر بار باز شدن اپلیکیشن جهت آگاهی مستمر کاربر
 */
public class DailyThreatBulletinRepository {

    private static final String PREF_NAME = "separ_daily_news_prefs";
    private static final String KEY_LAST_NEWS_INDEX = "last_news_index";

    public static class ThreatNewsItem {
        public final String id;
        public final String headline;
        public final String shortSummary;
        public final String fullContent;
        public final String categoryTag;
        public final String colorHex;

        public ThreatNewsItem(String id, String headline, String shortSummary, String fullContent, String tag, String color) {
            this.id = id;
            this.headline = headline;
            this.shortSummary = shortSummary;
            this.fullContent = fullContent;
            this.categoryTag = tag;
            this.colorHex = color;
        }
    }

    private static final List<ThreatNewsItem> NEWS_ITEMS = new ArrayList<>();

    static {
        NEWS_ITEMS.add(new ThreatNewsItem(
            "news_saham_edalat_surge",
            "کشف موج جدید پیامک‌های فیشینگ سود سهام عدالت",
            "کلاهبرداران با سرشماره‌های شخصی ۰۹... مدعی واریز مرحله جدید سود سهام شده‌اند.",
            "گزارش ویژه امنیت روز:\n\n" +
            "در ۲۴ ساعت گذشته، پلیس فتا از کشف موج جدیدی از پیامک‌های جعلی با عنوان «واریز سود سهام عدالت به حساب شما؛ تایید در سامانه» خبر داده است.\n\n" +
            "کلاهبرداران با سرشماره‌های عادی موبایل پیامکی ارسال می‌کنند که حاوی یک لینک با پسوند دامنه‌های خارجی (.com یا .top) است. با باز کردن لینک، برنامه‌ای آلوده روی گوشی دانلود شده و با درخواست رمز پویا، حساب بانکی قربانی را تخلیه می‌کند.\n\n" +
            "دستورالعمل حیاتی سپر:\n" +
            "سود سهام عدالت به صورت خودکار به شماره شبای بانکی شما واریز می‌شود و هیچ نیازی به ورود به هیچ سایتی ندارد. به هیچ پیامکی با سرشماره شخصی اعتماد نکنید.",
            "هشدار فوری",
            "#FF1744"
        ));

        NEWS_ITEMS.add(new ThreatNewsItem(
            "news_post_scam_warning",
            "هشدار شرکت ملی پست: پیامک مامور پست جعلی است",
            "هیچ مامور پستی پیامکی حاوی لینک نصب اپلیکیشن برای تحویل بسته ارسال نمی‌کند.",
            "خبر امنیتی موثق:\n\n" +
            "شرکت ملی پست جمهوری اسلامی ایران طی اطلاعیه‌ای رسمی اعلام کرد پیامک‌هایی با متن «سلام مامور پست هستم؛ بسته شما معطل مانده است، جهت دریافت به لینک زیر مراجعه کنید» کاملاً کلاهبرداری است.\n\n" +
            "این لینک‌ها حاوی بدافزاری است که کنترل پیامک‌های بانکی را به دست می‌گیرد.\n\n" +
            "راهکار سپر:\n" +
            "پیگیری مرسولات منحصراً از طریق سامانه tracking.post.ir امکان‌پذیر است و ماموران پست هرگز پیامک لینک‌دار ارسال نمی‌کنند.",
            "اطلاعیه رسمی",
            "#FF9100"
        ));

        NEWS_ITEMS.add(new ThreatNewsItem(
            "news_bank_call_spoofing",
            "هشدار درباره تماس‌های تلفنی با جعل عنوان پشتیبان بانک",
            "هکرها پس از درخواست کد ورود، با قربانی تماس گرفته و ادعای مسدودی حساب می‌کنند.",
            "شگرد نفوذ جدید:\n\n" +
            "روش جدید مهاجمان به این صورت است که ابتدا چندین پیامک کد ورود (بمباران OTP) به شماره شما ارسال می‌کنند. سپس بلافاصله با شما تماس گرفته و می‌گویند: «از پشتیبانی بانک یا فتا تماس می‌گیریم، حسابتان در حال هک است؛ کدی که الان برایتان آمد را بخوانید تا مسدود کنیم!»\n\n" +
            "دستورالعمل حیاتی سپر:\n" +
            "هیچ کارمند بانکی یا مامور پلیسی حق پرسیدن کدهای پیامکی شما را ندارد. در صورت مواجهه با چنین تماسی، بلافاصله تلفن را قطع کنید و کدی را نخوانید.",
            "شگرد مهندسی اجتماعی",
            "#FFD600"
        ));

        NEWS_ITEMS.add(new ThreatNewsItem(
            "news_yadegari_telegram_rat",
            "شناسایی نسخه جهش‌یافته بدافزار آلبوم یادگاری در تلگرام",
            "بدافزار در قالب فایل فشرده عکس‌های قدیمی در گروه‌ها و پی‌وی‌ها فوروارد می‌شود.",
            "گزارش شکار بدافزار:\n\n" +
            "آزمایشگاه سپر موفق به شکار نسخه جدیدی از بدافزار آلبوم یادگاری (پکیج com.velora.cloud) شد که با استفاده از پکر محافظ Jiagu کدهای خود را استتار کرده است.\n\n" +
            "این برنامه به محض دانلود، تلاش می‌کند اجازه نصب برنامه ناشناخته را در تلگرام و روبیکا فعال کند.\n\n" +
            "راهکار سپر:\n" +
            "در تب رادار سپر، گزینه «قفل اجازه نصب برنامه از پیام‌رسان‌ها» را بزنید و اجازه نصب مستقیم را خاموش نگه دارید تا هیچ بدافزاری نتواند نصب شود.",
            "شکار تروجان",
            "#00E5FF"
        ));

        NEWS_ITEMS.add(new ThreatNewsItem(
            "news_wangiri_missed_calls",
            "موج تماس‌های تک‌زنگ مشکوک خارجی (وانگیری)",
            "تماس با پیش‌شماره‌های کشورهای آفریقایی و اروپای شرقی با هدف سرقت اعتبار سیم‌کارت.",
            "تحلیل کلاهبرداری تلفنی:\n\n" +
            "طی روزهای اخیر کاربران زیادی گزارش داده‌اند که تماس‌های بسیار کوتاه و تک‌زنگ از شماره‌های ناشناس خارجی (مثل +222، +247، +235) دریافت می‌کنند.\n\n" +
            "هدف مهاجم این است که شما از روی کنجکاوی مجدداً با آن شماره تماس بگیرید. این خطوط در رده خطوط ویژه گران‌قیمت (Premium Rate) قرار دارند و به محض برقراری تماس، مبالغ هنگفتی روی قبض خط شما محاسبه می‌شود.\n\n" +
            "دستورالعمل سپر:\n" +
            "به هیچ تماس ناشناس بین‌المللی که با آن کاری ندارید پاسخ ندهید و هرگز با آن‌ها تماس نگیرید.",
            "هشدار اپراتوری",
            "#00E676"
        ));
    }

    /**
     * واکشی خبر روز و چرخش خودکار به خبر بعدی در هر بار باز کردن برنامه
     */
    public static ThreatNewsItem getNextRotatingNews(Context context) {
        if (context == null || NEWS_ITEMS.isEmpty()) {
            return NEWS_ITEMS.get(0);
        }

        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        int currentIndex = sp.getInt(KEY_LAST_NEWS_INDEX, -1);
        int nextIndex = (currentIndex + 1) % NEWS_ITEMS.size();

        // ذخیره اندیس جدید برای اجرای بعدی
        sp.edit().putInt(KEY_LAST_NEWS_INDEX, nextIndex).apply();

        return NEWS_ITEMS.get(nextIndex);
    }

    /**
     * خواندن تمام اخبار ثبت‌شده
     */
    public static List<ThreatNewsItem> getAllNews() {
        return new ArrayList<>(NEWS_ITEMS);
    }
}

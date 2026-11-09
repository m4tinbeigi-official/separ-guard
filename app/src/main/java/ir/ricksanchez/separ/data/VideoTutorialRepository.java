package ir.ricksanchez.separ.data;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import java.util.ArrayList;
import java.util.List;

/**
 * مخزن ویدیوهای آموزشی امنیتی بارگذاری‌شده در آپارات (اینترنت نیم‌بها و بدون فیلتر)
 */
public class VideoTutorialRepository {

    public static class VideoTutorial {
        public final String id;
        public final String titleFa;
        public final String descriptionFa;
        public final String aparatUrl;
        public final String durationFa;
        public final String categoryFa;
        public final String icon;

        public VideoTutorial(String id, String titleFa, String descriptionFa, String aparatUrl,
                             String durationFa, String categoryFa, String icon) {
            this.id = id;
            this.titleFa = titleFa;
            this.descriptionFa = descriptionFa;
            this.aparatUrl = aparatUrl;
            this.durationFa = durationFa;
            this.categoryFa = categoryFa;
            this.icon = icon;
        }
    }

    public static List<VideoTutorial> getAllTutorials() {
        List<VideoTutorial> list = new ArrayList<>();

        list.add(new VideoTutorial(
            "tg_session",
            "آموزش گام‌به‌گام بررسی نشست‌های تلگرام و اخراج هکر",
            "چگونه ببینیم چه دستگاه‌هایی وارد تلگرام ما شده‌اند و چطور با یک کلیک همه را بیرون بیندازیم.",
            "https://www.aparat.com/result/%D8%AD%D8%B0%D9%81_%D9%86%D8%B4%D8%B3%D8%AA_%D8%AA%D9%84%DA%AF%D8%B1%D8%A7%D9%85",
            "۲ دقیقه",
            "پیام‌رسان‌ها",
            "✈️"
        ));

        list.add(new VideoTutorial(
            "rubika_eitaa_session",
            "بررسی دستگاه‌های متصل در روبیکا، ایتا و بله",
            "مسیر دقیق تنظیمات امنیتی در پیام‌رسان‌های ایرانی برای قطع دسترسی نفوذگران و سارقان حساب.",
            "https://www.aparat.com/result/%D8%AD%D8%B0%D9%81_%D8%AF%D8%B3%D8%AA%DA%AF%D8%A7%D9%87_%D8%B1%D9%88%D8%A8%DB%8C%DA%A9%D8%A7_%D8%A7%DB%8C%D8%AA%D8%A7",
            "۳ دقیقه",
            "پیام‌رسان‌های داخلی",
            "🇮🇷"
        ));

        list.add(new VideoTutorial(
            "ussd_divert",
            "آموزش لغو دایورت خطوط و کدهای انحراف تماس (*21*)",
            "ترفند کلاهبرداران در تماس‌های تلفنی و نحوه غیرفعال‌سازی فوری دایورت با کد دستوری ##002#.",
            "https://www.aparat.com/result/%D9%84%D8%BA%D9%88_%D8%AF%D8%A7%DB%8C%D9%88%D8%B1%D8%AA_%D9%87%D9%85%D8%B1%D8%A7%D9%87_%D8%A7%D9%88%D9%84_%D8%A7%DB%8C%D8%B1%D8%A7%D9%86%D8%B3%D9%84",
            "۱ دقیقه",
            "اپراتور و سیم‌کارت",
            "📵"
        ));

        list.add(new VideoTutorial(
            "hidden_apps",
            "چطور برنامه‌های بدون آیکون و مخفی را در اندروید پیدا و پاک کنیم؟",
            "نحوه مشاهده لیست کامل برنامه‌ها در تنظیمات گوشی و حذف بدافزارهای استتاریافته بدون آیکون.",
            "https://www.aparat.com/result/%D8%AD%D8%B0%D9%81_%D8%A8%D8%B1%D9%86%D8%A7%D9%85%D9%87_%D9%85%D8%AE%D9%81%DB%8C_%D8%A7%D9%86%D8%AF%D8%B1%D9%88%DB%8C%D8%AF",
            "۲.۵ دقیقه",
            "امنیت اندروید",
            "🔍"
        ));

        list.add(new VideoTutorial(
            "fake_receipt",
            "روش تشخیص رسید جعلی بانکی و کلاهبرداری کارت به کارت",
            "چرا به اسکرین‌شات فیش انتقال نباید اعتماد کرد و چطور درجا موجودی واقعی را چک کنیم.",
            "https://www.aparat.com/result/%D8%AA%D8%B4%D8%AE%DB%8C%D8%B5_%D8%B1%D8%B3%DB%8C%D8%AF_%D8%AC%D8%B9%D9%84%DB%8C_%D8%A8%D8%A7%D9%86%DA%A9%DB%8C",
            "۳ دقیقه",
            "پیشگیری از سرقت بانکی",
            "💳"
        ));

        list.add(new VideoTutorial(
            "phishing_sms",
            "شناسایی پیامک‌های فیشینگ ابلاغیه ثنا و سهام عدالت",
            "تکنیک‌های بررسی لینک‌های جعلی و جلوگیری از دانلود ناخواسته بدافزارهای وام‌چی و یادگاری.",
            "https://www.aparat.com/result/%D9%BE%DB%8C%D8%A7%D9%85%DA%A9_%D8%AC%D8%B9%D9%84%DB%8C_%D8%AB%D9%86%D8%A7_%D8%B3%D9%87%D8%A7%D9%85_%D8%B9%D8%AF%D8%A7%D9%84%D8%AA",
            "۲ دقیقه",
            "آنتی‌فیشینگ",
            "🛡️"
        ));

        return list;
    }

    public static void openAparatVideo(Context context, String aparatUrl) {
        if (context == null || aparatUrl == null) return;
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(aparatUrl));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception ignored) {}
    }
}

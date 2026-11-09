package ir.ricksanchez.separ.data;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import java.util.ArrayList;
import java.util.List;

/**
 * مخزن خبرگزاری‌ها، رسانه‌های فناوری و اینفلوئنسرهای همکار در اطلاع‌رسانی و آموزش امنیت
 */
public class MediaPartnerRepository {

    public static class PartnerInfo {
        public final String id;
        public final String nameFa;
        public final String typeFa;
        public final String roleDescriptionFa;
        public final String url;
        public final String badgeColorHex;

        public PartnerInfo(String id, String name, String type, String desc, String url, String color) {
            this.id = id;
            this.nameFa = name;
            this.typeFa = type;
            this.roleDescriptionFa = desc;
            this.url = url;
            this.badgeColorHex = color;
        }
    }

    public static List<PartnerInfo> getOfficialPartners() {
        List<PartnerInfo> list = new ArrayList<>();

        // ۱. اینفلوئنسرها و رهبران آگاه‌سازی
        list.add(new PartnerInfo(
            "rick_sanchez",
            "ریک سانچز (Rick Sanchez)",
            "معمار سامانه و تحلیلگر ارشد امنیت",
            "طراحی الگوریتم‌های کشف بدافزارهای استتاریافته و اطلاع‌رسانی مستمر به جامعه کاربری.",
            "https://t.me/M4tinBeigi",
            "#00E676"
        ));

        list.add(new PartnerInfo(
            "cyber_guardians",
            "جامعه متخصصان امنیت سایبری ایران",
            "شبکه سفیران امنیت و آموزش همگانی",
            "اشتراک‌گذاری شاخص‌های آلودگی (IoC) بدافزارهای بانکی و تولید محتوای آموزشی برای عموم مردم.",
            "https://t.me/antigravity_iran",
            "#00E5FF"
        ));

        // ۲. رسانه‌های تخصصی فناوری و اقتصاد دیجیتال
        list.add(new PartnerInfo(
            "zoomit",
            "رسانه تخصصی زومیت (Zoomit)",
            "رسانه فناوری و امنیت دیجیتال",
            "انتشار هشدارهای تحلیلی درباره نحوه کارکرد بدافزارهای آلبوم یادگاری و راهکارهای پیشگیری.",
            "https://www.zoomit.ir",
            "#FF9100"
        ));

        list.add(new PartnerInfo(
            "digiato",
            "دیجیاتو (Digiato)",
            "رسانه اخبار فناوری و استارتاپی",
            "پوشش گسترده هشدارهای سرقت رمز دوم و پیامک‌های جعلی سود سهام عدالت.",
            "https://digiato.com",
            "#2979FF"
        ));

        list.add(new PartnerInfo(
            "peyvast",
            "ماهنامه و پایگاه خبری پیوست (Peyvast)",
            "رسانه اقتصاد دیجیتال و فین‌تک",
            "بررسی ابعاد حقوقی و فنی فیشینگ‌های درگاه‌های بانکی و جعل شاپرک.",
            "https://peivast.com",
            "#7C4DFF"
        ));

        // ۳. خبرگزاری‌های رسمی و مرجع
        list.add(new PartnerInfo(
            "isna",
            "خبرگزاری دانشجویان ایران (ایسنا)",
            "خبرگزاری رسمی",
            "اطلاع‌رسانی فوری بیانیه‌های پلیس فتا در خصوص شگردهای جدید کلاهبرداری پیامکی.",
            "https://www.isna.ir",
            "#00B0FF"
        ));

        list.add(new PartnerInfo(
            "fars",
            "خبرگزاری فارس",
            "خبرگزاری رسمی",
            "انتشار گزارش‌های میدانی از قربانیان فیشینگ و راهکارهای ایمن‌سازی گوشی‌های هوشمند.",
            "https://www.farsnews.ir",
            "#FF5252"
        ));

        list.add(new PartnerInfo(
            "mehr",
            "خبرگزاری مهر",
            "خبرگزاری رسمی",
            "بازتاب هشدارهای ممیزی نشست‌های فعال در پیام‌رسان‌ها و جلوگیری از نفوذ به حساب‌ها.",
            "https://www.mehrnews.com",
            "#E040FB"
        ));

        return list;
    }

    public static void openPartnerUrl(Context context, String url) {
        if (context == null || url == null) return;
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception ignored) {}
    }
}

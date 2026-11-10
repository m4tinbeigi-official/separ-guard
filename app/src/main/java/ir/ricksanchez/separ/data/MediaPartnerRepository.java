package ir.ricksanchez.separ.data;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import ir.ricksanchez.separ.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * مخزن خبرگزاری‌ها، رسانه‌های فناوری و اینفلوئنسرهای همکار در اطلاع‌رسانی و آموزش امنیت
 */
public class MediaPartnerRepository {

    public static class SocialLink {
        public final String titleFa;
        public final String url;
        public final String colorHex;

        public SocialLink(String titleFa, String url, String colorHex) {
            this.titleFa = titleFa;
            this.url = url;
            this.colorHex = colorHex;
        }
    }

    public static class PartnerInfo {
        public final String id;
        public final String nameFa;
        public final String typeFa;
        public final String roleDescriptionFa;
        public final String url;
        public final String badgeColorHex;
        public final int imageResId;
        public final List<SocialLink> socialLinks;

        public PartnerInfo(String id, String name, String type, String desc, String url, String color, int imageResId, List<SocialLink> socialLinks) {
            this.id = id;
            this.nameFa = name;
            this.typeFa = type;
            this.roleDescriptionFa = desc;
            this.url = url;
            this.badgeColorHex = color;
            this.imageResId = imageResId;
            this.socialLinks = (socialLinks != null) ? socialLinks : new ArrayList<>();
        }
    }

    public static List<PartnerInfo> getOfficialPartners() {
        List<PartnerInfo> list = new ArrayList<>();

        // ۱. عادل خان (adelkhaan) - حامی رسمی رسانه‌ای
        list.add(new PartnerInfo(
            "adelkhaan",
            "عادل خان (adelkhaan)",
            "حامی رسانه‌ای و متخصص آگاه‌سازی تکنولوژی",
            "تولیدکننده تخصصی محتوای دنیای فناوری، هشداردهنده شگردهای سرقت رمز دوم و سفیر اطلاع‌رسانی سامانه سپر.",
            "https://instagram.com/adelkhaan",
            "#00E5FF",
            R.drawable.adelkhaan,
            Arrays.asList(
                new SocialLink("اینستاگرام", "https://instagram.com/adelkhaan", "#E1306C"),
                new SocialLink("تلگرام", "https://t.me/adelkhaan", "#0088CC"),
                new SocialLink("یوتیوب", "https://youtube.com/@adelkhaan", "#FF0000"),
                new SocialLink("وب‌سایت", "https://adelkhaan.com", "#00E5FF"),
                new SocialLink("آپارات", "https://aparat.com/adelkhaan", "#EA1E63")
            )
        ));

        // ۲. مسعود میرپناهی • عایوتک (iotechoi) - حامی رسمی رسانه‌ای
        list.add(new PartnerInfo(
            "iotechoi",
            "مسعود میرپناهی • عایوتک (iotechoi)",
            "حامی رسانه‌ای و طراح ارشد محصول",
            "بنیان‌گذار رسانه تحلیلی عایوتک، تحلیلگر تجربه کاربری و پیشگام ارتقای امنیت نرم‌افزارهای موبایل و فین‌تک در ایران.",
            "https://instagram.com/iotechoi",
            "#00E676",
            R.drawable.iotechoi,
            Arrays.asList(
                new SocialLink("اینستاگرام", "https://instagram.com/iotechoi", "#E1306C"),
                new SocialLink("تلگرام", "https://t.me/iotechoi", "#0088CC")
            )
        ));

        // ۳. ریک سانچز (Rick Sanchez) - سازنده و معمار سامانه
        list.add(new PartnerInfo(
            "rick_sanchez",
            "ریک سانچز (Rick Sanchez)",
            "معمار سامانه و تحلیلگر ارشد امنیت",
            "طراحی الگوریتم‌های کشف بدافزارهای استتاریافته، توسعه سپر و آموزش جامعه توسعه‌دهندگان.",
            "https://t.me/M4tinBeigi",
            "#00E676",
            R.drawable.rick_sanchez,
            Arrays.asList(
                new SocialLink("کانال تلگرام", "https://t.me/M4tinBeigi", "#0088CC"),
                new SocialLink("گیت‌هاب", "https://github.com/m4tinbeigi-official", "#94A3B8")
            )
        ));

        // ۴. جامعه متخصصان امنیت سایبری ایران
        list.add(new PartnerInfo(
            "cyber_guardians",
            "جامعه متخصصان امنیت سایبری ایران",
            "شبکه سفیران امنیت و آموزش همگانی",
            "اشتراک‌گذاری شاخص‌های آلودگی (IoC) بدافزارهای بانکی و تولید محتوای آموزشی برای عموم مردم.",
            "https://t.me/antigravity_iran",
            "#00E5FF",
            0,
            Arrays.asList(
                new SocialLink("کانال اطلاع‌رسانی", "https://t.me/antigravity_iran", "#0088CC")
            )
        ));

        // ۵. خبرگزاری‌ها و رسانه‌های تخصصی
        list.add(new PartnerInfo(
            "zoomit",
            "رسانه تخصصی زومیت (Zoomit)",
            "رسانه فناوری و امنیت دیجیتال",
            "انتشار هشدارهای تحلیلی درباره نحوه کارکرد بدافزارهای آلبوم یادگاری و راهکارهای پیشگیری.",
            "https://www.zoomit.ir",
            "#FF9100",
            0,
            Arrays.asList(new SocialLink("وب‌سایت زومیت", "https://www.zoomit.ir", "#FF9100"))
        ));

        list.add(new PartnerInfo(
            "digiato",
            "دیجیاتو (Digiato)",
            "رسانه اخبار فناوری و استارتاپی",
            "پوشش گسترده هشدارهای سرقت رمز دوم و پیامک‌های جعلی سود سهام عدالت.",
            "https://digiato.com",
            "#2979FF",
            0,
            Arrays.asList(new SocialLink("وب‌سایت دیجیاتو", "https://digiato.com", "#2979FF"))
        ));

        list.add(new PartnerInfo(
            "peyvast",
            "ماهنامه و پایگاه خبری پیوست (Peyvast)",
            "رسانه اقتصاد دیجیتال و فین‌تک",
            "بررسی ابعاد حقوقی و فنی فیشینگ‌های درگاه‌های بانکی و جعل شاپرک.",
            "https://peivast.com",
            "#7C4DFF",
            0,
            Arrays.asList(new SocialLink("پایگاه خبری پیوست", "https://peivast.com", "#7C4DFF"))
        ));

        list.add(new PartnerInfo(
            "isna",
            "خبرگزاری دانشجویان ایران (ایسنا)",
            "خبرگزاری رسمی",
            "اطلاع‌رسانی فوری بیانیه‌های پلیس فتا در خصوص شگردهای جدید کلاهبرداری پیامکی.",
            "https://www.isna.ir",
            "#00B0FF",
            0,
            Arrays.asList(new SocialLink("خبرگزاری ایسنا", "https://www.isna.ir", "#00B0FF"))
        ));

        list.add(new PartnerInfo(
            "fars",
            "خبرگزاری فارس",
            "خبرگزاری رسمی",
            "انتشار گزارش‌های میدانی از قربانیان فیشینگ و راهکارهای ایمن‌سازی گوشی‌های هوشمند.",
            "https://www.farsnews.ir",
            "#FF5252",
            0,
            Arrays.asList(new SocialLink("خبرگزاری فارس", "https://www.farsnews.ir", "#FF5252"))
        ));

        list.add(new PartnerInfo(
            "mehr",
            "خبرگزاری مهر",
            "خبرگزاری رسمی",
            "بازتاب هشدارهای ممیزی نشست‌های فعال در پیام‌رسان‌ها و جلوگیری از نفوذ به حساب‌ها.",
            "https://www.mehrnews.com",
            "#E040FB",
            0,
            Arrays.asList(new SocialLink("خبرگزاری مهر", "https://www.mehrnews.com", "#E040FB"))
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

package ir.ricksanchez.separ.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import java.util.ArrayList;
import java.util.List;

/**
 * راهنمای گام‌به‌گام و هدایت مستقیم (Deep Link) برای اخراج نشست‌های غیرمجاز و هکرها از پیام‌رسان‌ها و حساب‌های اپراتوری
 */
public class SessionRemediationHelper {

    public static class AppSessionGuide {
        public final String appName;
        public final String packageName;
        public final String pathDescriptionFa;
        public final String deepLinkUri;

        public AppSessionGuide(String name, String pkg, String path, String link) {
            this.appName = name;
            this.packageName = pkg;
            this.pathDescriptionFa = path;
            this.deepLinkUri = link;
        }
    }

    public static List<AppSessionGuide> getSupportedPlatforms() {
        List<AppSessionGuide> list = new ArrayList<>();

        // ۱. پیام‌رسان‌های بین‌المللی
        list.add(new AppSessionGuide(
            "تلگرام (Telegram)",
            "org.telegram.messenger",
            "تنظیمات > دستگاه‌ها (Devices) > لمس دکمه قرمز «خروج از سایر نشست‌ها» (Terminate all other sessions)",
            "tg://settings/devices"
        ));

        list.add(new AppSessionGuide(
            "واتساپ (WhatsApp)",
            "com.whatsapp",
            "منوی سه‌نقطه > دستگاه‌های متصل (Linked devices) > لمس دستگاه ناشناس > خروج (Log out)",
            null
        ));

        list.add(new AppSessionGuide(
            "اینستاگرام (Instagram)",
            "com.instagram.android",
            "پروفایل > سه خط بالا > مرکز حساب‌ها > رمز عبور و امنیت > ورودهای به حساب > خروج از دستگاه‌های ناشناس",
            null
        ));

        // ۲. پیام‌رسان‌های بومی ایران
        list.add(new AppSessionGuide(
            "روبیکا (Rubika)",
            "ir.rubika.app",
            "تنظیمات > حریم خصوصی و امنیت > دستگاه‌های فعال > پایان دادن به سایر نشست‌ها",
            null
        ));

        list.add(new AppSessionGuide(
            "ایتا (Eitaa)",
            "ir.eitaa.messenger",
            "تنظیمات > حریم خصوصی و امنیت > دستگاه‌های متصل > خروج از سایر دستگاه‌ها",
            null
        ));

        list.add(new AppSessionGuide(
            "بله (Bale)",
            "ir.bale.messenger",
            "تنظیمات > امنیت و حریم خصوصی > دستگاه‌های متصل > بستن تمامی نشست‌های دیگر",
            null
        ));

        list.add(new AppSessionGuide(
            "سروش پلاس (Soroush+)",
            "mobi.mmdt.ottplus",
            "تنظیمات > امنیت > دستگاه‌های متصل > لغو دسترسی نشست‌های ناشناس",
            null
        ));

        return list;
    }

    /**
     * باز کردن صفحه برنامه یا تنظیمات آن برای خروج سریع
     */
    public static void openAppOrSettings(Context context, AppSessionGuide guide) {
        if (context == null || guide == null) return;

        // تلاش برای باز کردن دیپ‌لینک اختصاصی
        if (guide.deepLinkUri != null) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(guide.deepLinkUri));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return;
            } catch (Exception ignored) {}
        }

        // باز کردن خود برنامه
        try {
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(guide.packageName);
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(launchIntent);
                return;
            }
        } catch (Exception ignored) {}

        // در صورت عدم امکان، باز کردن صفحه اطلاعات برنامه در تنظیمات اندروید
        try {
            Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            settingsIntent.setData(Uri.parse("package:" + guide.packageName));
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(settingsIntent);
        } catch (Exception ignored) {}
    }
}

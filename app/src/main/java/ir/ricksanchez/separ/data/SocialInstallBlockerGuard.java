package ir.ricksanchez.separ.data;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * سامانه پایش و قفل نصب برنامه از شبکه‌های اجتماعی (Social Sideloading Blocker)
 * رصد مجوز «نصب برنامه‌های ناشناخته» (Install Unknown Apps) در تلگرام، واتساپ، روبیکا، ایتا و مرورگرها
 * کانال اصلی ورود ۹۹٪ بدافزارها و تروجان‌های بانکی به گوشی، اجازه نصب از داخل پیام‌رسان‌هاست.
 */
public class SocialInstallBlockerGuard {

    public static class RiskySocialApp {
        public final String packageName;
        public final String appNameFa;
        public final boolean isInstalled;
        public final boolean canInstallUnknownApps;

        public RiskySocialApp(String pkg, String name, boolean installed, boolean canInstall) {
            this.packageName = pkg;
            this.appNameFa = name;
            this.isInstalled = installed;
            this.canInstallUnknownApps = canInstall;
        }
    }

    private static final List<RiskySocialApp> MONITORED_SIDELOAD_VECTORS = Arrays.asList(
        new RiskySocialApp("org.telegram.messenger", "تلگرام اصلی (Telegram)", false, false),
        new RiskySocialApp("com.whatsapp", "واتساپ (WhatsApp)", false, false),
        new RiskySocialApp("ir.rubika.app", "روبیکا (Rubika)", false, false),
        new RiskySocialApp("ir.eitaa.messenger", "ایتا (Eitaa)", false, false),
        new RiskySocialApp("ir.bale.messenger", "بله (Bale)", false, false),
        new RiskySocialApp("com.android.chrome", "گوگل کروم (Google Chrome)", false, false),
        new RiskySocialApp("com.sec.android.app.sbrowser", "مرورگر سامسونگ (Samsung Browser)", false, false),
        new RiskySocialApp("org.mozilla.firefox", "فایرفاکس (Firefox)", false, false)
    );

    /**
     * بررسی اینکه آیا برنامه‌ای اجازه نصب برنامه‌های ناشناخته (REQUEST_INSTALL_PACKAGES) را دارد
     */
    public static boolean canAppInstallUnknownPackages(Context context, String packageName) {
        if (context == null || packageName == null) return false;
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            if (ai == null) return false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                if (appOps != null) {
                    int mode = appOps.checkOpNoThrow(
                        "android:request_install_packages",
                        ai.uid,
                        packageName
                    );
                    return mode == AppOpsManager.MODE_ALLOWED;
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    /**
     * واکاوی تمام پیام‌رسان‌ها و مرورگرها و استخراج مواردی که اجازه نصب فعال دارند
     */
    public static List<RiskySocialApp> auditSocialInstallPermissions(Context context) {
        List<RiskySocialApp> results = new ArrayList<>();
        if (context == null) return results;

        PackageManager pm = context.getPackageManager();
        for (RiskySocialApp vector : MONITORED_SIDELOAD_VECTORS) {
            boolean installed = false;
            try {
                pm.getApplicationInfo(vector.packageName, 0);
                installed = true;
            } catch (Exception ignored) {}

            if (installed) {
                boolean canInstall = canAppInstallUnknownPackages(context, vector.packageName);
                results.add(new RiskySocialApp(vector.packageName, vector.appNameFa, true, canInstall));
            }
        }

        return results;
    }

    /**
     * هدایت مستقیم کاربر به صفحه قطع مجوز نصب برنامه ناشناخته در تنظیمات اندروید
     */
    public static void launchManageUnknownSources(Context context, String packageName) {
        if (context == null) return;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && packageName != null) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                intent.setData(Uri.parse("package:" + packageName));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                return;
            }
        } catch (Exception ignored) {}

        try {
            Intent fallback = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
            fallback.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(fallback);
        } catch (Exception ignored) {}
    }
}

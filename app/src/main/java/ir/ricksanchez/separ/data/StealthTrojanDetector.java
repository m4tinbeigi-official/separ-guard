package ir.ricksanchez.separ.data;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * موتور تشخیص هوشمند و تخصصی بدافزارهای ایرانی با ترفندهای پنهان‌سازی پیشرفته:
 * ۱. حذف آیکون از لانچر (Hidden / Invisible App)
 * ۲. استتار هویتی (Fake System Names / Zero-width characters)
 * ۳. گروگان‌گیری صفحه با سرویس دسترسی‌پذیری (Accessibility Service Abuse)
 * ۴. قفل دکمه حذف با دسترسی مدیر دستگاه (Device Administrator Abuse)
 * ۵. کرم‌های پیامکی خودکار و پایداری در پس‌زمینه (SMS Worm Persistence)
 * ۶. کلیدواژه‌های شناخته‌شده تله (آلبوم یادگاری، صیغه‌یاب، سهام عدالت و ...)
 */
public class StealthTrojanDetector {

    public static class StealthEvaluationResult {
        public final boolean isMalicious;
        public final String threatType;
        public final String threatTitle;
        public final String detailedReasonFa;
        public final boolean isDeviceAdmin;

        public StealthEvaluationResult(boolean isMal, String type, String title, String reason, boolean isDevAdmin) {
            this.isMalicious = isMal;
            this.threatType = type;
            this.threatTitle = title;
            this.detailedReasonFa = reason;
            this.isDeviceAdmin = isDevAdmin;
        }
    }

    private static final Set<String> FAKE_SYSTEM_NAMES = new HashSet<>(Arrays.asList(
        "google play services",
        "google services",
        "system update",
        "android framework",
        "خدمات سیستم",
        "خدمات گوگل پلی",
        "بروزرسانی سیستم",
        "تنظیمات سیستم",
        "system service",
        "system core",
        "android system core"
    ));

    private static final Set<String> LURE_PACKAGE_KEYWORDS = new HashSet<>(Arrays.asList(
        "yadegari", "velora", "sigheh", "sighe", "hamdam", "seham", "edalat", "sana", "adliran",
        "arosi", "aroosi", "wedding", "invitation", "vamchi", "vamfori", "kalabarg", "yaraneh"
    ));

    private static final Set<String> LURE_TITLE_KEYWORDS = new HashSet<>(Arrays.asList(
        "یادگاری", "صیغه", "صیغه‌یاب", "همدم", "سهام عدالت", "سود سهام", "ابلاغیه", "ثنا",
        "دعوت عروسی", "کارت عروسی", "وام چی", "وام فوری", "کالابرگ", "یارانه"
    ));

    public static StealthEvaluationResult evaluatePackage(Context context, PackageInfo pkgInfo) {
        if (pkgInfo == null || pkgInfo.packageName == null) return null;
        String pkg = pkgInfo.packageName.toLowerCase().trim();

        // ۰. معافیت خود نرم‌افزار سپر
        if (context != null && pkg.equalsIgnoreCase(context.getPackageName())) {
            return null;
        }

        // ۰. معافیت برنامه‌های لیست سفید معتبر
        if (TrustedWhitelistRegistry.isWhitelisted(pkg)) {
            return null;
        }

        // بررسی برنامه‌های سیستمی واقعی در پارتیشن رام دستگاه
        boolean isSystemApp = false;
        if (pkgInfo.applicationInfo != null) {
            isSystemApp = (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0 ||
                          (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0;
        }
        if (isSystemApp) return null;

        PackageManager pm = context.getPackageManager();
        CharSequence labelChars = null;
        try {
            labelChars = pm.getApplicationLabel(pkgInfo.applicationInfo);
        } catch (Exception ignored) {}
        String label = (labelChars != null) ? labelChars.toString().trim() : "";
        String labelLower = label.toLowerCase();

        // بررسی ۱: کلیدواژه‌های تله و فیشینگ ایرانی در نام بسته
        for (String kw : LURE_PACKAGE_KEYWORDS) {
            if (pkg.contains(kw)) {
                return new StealthEvaluationResult(
                    true,
                    "IRANIAN_LURE",
                    "بدافزار فیشینگ و کرم پیامکی (" + kw + ")",
                    "این برنامه به عنوان بدافزار فعال در کشور (آلبوم یادگاری، صیغه‌یاب یا سهام عدالت) شناسایی شد که برای سرقت پیامک‌ها و رمزهای بانکی ساخته شده است.",
                    false
                );
            }
        }

        // بررسی ۲: کلیدواژه‌های تله در عنوان برنامه (Title Lure)
        for (String faKw : LURE_TITLE_KEYWORDS) {
            if (label.contains(faKw)) {
                return new StealthEvaluationResult(
                    true,
                    "IRANIAN_LURE",
                    "بدافزار فیشینگ با عنوان فریبنده «" + faKw + "»",
                    "این برنامه با جعل عنوان «" + faKw + "» قصد ترغیب کاربر و سرقت رمزهای پویای بانکی را دارد.",
                    false
                );
            }
        }

        // بررسی ۳: استتار هویتی (نام خالی، کاراکترهای نامرئی یا جعل نام سیستم)
        boolean isBlankOrInvisible = label.isEmpty() ||
            label.replace("\u200B", "").replace("\u200C", "").replace("\u200D", "").replace("\uFEFF", "").trim().isEmpty();

        if (isBlankOrInvisible) {
            return new StealthEvaluationResult(
                true,
                "SYSTEM_MASQUERADE",
                "بدافزار استتاریافته بدون نام (کاراکتر نامرئی)",
                "این برنامه برای فریب شما، از نام خالی یا کاراکترهای نامرئی یونیکد استفاده کرده تا در لیست برنامه‌ها پنهان بماند.",
                false
            );
        }

        if (FAKE_SYSTEM_NAMES.contains(labelLower)) {
            return new StealthEvaluationResult(
                true,
                "SYSTEM_MASQUERADE",
                "بدافزار جعل هویت سیستم («" + label + "»)",
                "این برنامه غیرسیستمی است اما نام «" + label + "» را جعل کرده تا کاربر آن را با سرویس‌های رسمی گوگل یا اندروید اشتباه بگیرد.",
                false
            );
        }

        // بررسی اینکه آیا برنامه از استورهای رسمی نصب شده است
        String installer = null;
        try {
            installer = pm.getInstallerPackageName(pkg);
        } catch (Exception ignored) {}
        boolean isStoreInstalled = (installer != null && (
            installer.contains("vending") || installer.contains("bazaar") || installer.contains("market")
        ));
        if (isStoreInstalled) {
            // برنامه‌های نصب‌شده از مارکت‌های رسمی معاف از حدس‌های فریبنده هستند
            return null;
        }

        // بررسی دسترسی‌های درخواستی
        Set<String> requestedPerms = new HashSet<>();
        if (pkgInfo.requestedPermissions != null) {
            requestedPerms.addAll(Arrays.asList(pkgInfo.requestedPermissions));
        }

        boolean hasGrantedSms = SmsAndScreenGuard.hasActiveSmsPermission(pm, pkgInfo);
        boolean hasBoot = requestedPerms.contains("android.permission.RECEIVE_BOOT_COMPLETED");
        boolean hasBatteryIgnore = requestedPerms.contains("android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS");
        boolean hasOverlay = requestedPerms.contains("android.permission.SYSTEM_ALERT_WINDOW");

        // بررسی ۴: پایداری در پس‌زمینه با گیرنده‌های سیستمی (SMS Worm Persistence)
        if (hasBoot && hasGrantedSms && (hasBatteryIgnore || requestedPerms.contains("android.permission.READ_CONTACTS"))) {
            return new StealthEvaluationResult(
                true,
                "PERSISTENT_SMS_WORM",
                "کرم پیامکی خودکار (Worm Persistence)",
                "این برنامه پس از روشن شدن گوشی خودکار بارگذاری شده، پیامک‌های بانکی را دزدیده و به تمام مخاطبانتان پیامک مخرب ارسال می‌کند.",
                false
            );
        }

        // بررسی ۵: حذف آیکون از لانچر (Hidden Launcher Component)
        Intent launchIntent = pm.getLaunchIntentForPackage(pkg);
        boolean hasNoLauncher = (launchIntent == null);
        if (hasNoLauncher && hasGrantedSms && hasBoot) {
            return new StealthEvaluationResult(
                true,
                "HIDDEN_ICON",
                "برنامه مخفی فاقد آیکون (Hidden App)",
                "این برنامه آیکون میانبر خود را پنهان کرده تا کاربر متوجه حضور و فعالیت مخرب آن در پس‌زمینه نشود.",
                false
            );
        }

        // بررسی ۶: گروگان‌گیری صفحه با سرویس دسترسی‌پذیری (Accessibility Service Abuse)
        if (pkgInfo.services != null) {
            for (ServiceInfo serviceInfo : pkgInfo.services) {
                if ("android.permission.BIND_ACCESSIBILITY_SERVICE".equals(serviceInfo.permission)) {
                    if (hasGrantedSms && (hasBoot || hasOverlay)) {
                        return new StealthEvaluationResult(
                            true,
                            "ROGUE_ACCESSIBILITY",
                            "بدافزار گروگان‌گیر صفحه (سوءاستفاده از دسترسی‌پذیری)",
                            "این برنامه با سوءاستفاده از سرویس دسترسی‌پذیری، به هنگام ورود شما به تنظیمات مانع از لمس دکمه لغو نصب می‌شود.",
                            false
                        );
                    }
                }
            }
        }

        // بررسی ۷: سوءاستفاده از دسترسی مدیر دستگاه (Device Admin Abuse)
        if (pkgInfo.receivers != null) {
            for (ActivityInfo receiverInfo : pkgInfo.receivers) {
                if ("android.permission.BIND_DEVICE_ADMIN".equals(receiverInfo.permission)) {
                    return new StealthEvaluationResult(
                        true,
                        "DEVICE_ADMIN_ABUSE",
                        "بدافزار قفل‌کننده دکمه حذف (Device Administrator)",
                        "این برنامه از دسترسی مدیر دستگاه استفاده می‌کند تا دکمه «حذف نصب» در تنظیمات گوشی خاکستری و غیرفعال شود.",
                        true
                    );
                }
            }
        }

        return null;
    }
}

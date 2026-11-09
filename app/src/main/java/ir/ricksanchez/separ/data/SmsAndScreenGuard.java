package ir.ricksanchez.separ.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.provider.Telephony;
import android.view.WindowManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * سامانه مانیتورینگ و ممیزی دسترسی به پیامک‌ها و ضبط صفحه (اسکرین‌شات)
 * تمامی برنامه‌های دارای دسترسی به پیامک یا ضبط صفحه را رصد کرده و در صورت نبودن در لیست سفید، بلافاصله اخطار و اکشن لغو را فعال می‌کند.
 */
public class SmsAndScreenGuard {

    private static final Set<String> SMS_PERMISSIONS = new HashSet<>(Arrays.asList(
        "android.permission.RECEIVE_SMS",
        "android.permission.READ_SMS",
        "android.permission.SEND_SMS",
        "android.permission.RECEIVE_MMS",
        "android.permission.RECEIVE_WAP_PUSH",
        "android.permission.BIND_NOTIFICATION_LISTENER_SERVICE"
    ));

    private static final Set<String> SCREEN_PERMISSIONS = new HashSet<>(Arrays.asList(
        "android.permission.SYSTEM_ALERT_WINDOW",
        "android.permission.BIND_ACCESSIBILITY_SERVICE"
    ));

    public static class AppAuditResult {
        public final String packageName;
        public final String appName;
        public final boolean hasSmsAccess;
        public final boolean hasScreenRisk;
        public final boolean isCertifiedWhitelisted;
        public final boolean isUserWhitelisted;
        public final boolean isDefaultSmsApp;
        public final boolean isKnownMalware;
        public final String description;

        public AppAuditResult(String packageName, String appName, boolean hasSmsAccess,
                              boolean hasScreenRisk, boolean isCertifiedWhitelisted,
                              boolean isUserWhitelisted, boolean isDefaultSmsApp,
                              boolean isKnownMalware, String description) {
            this.packageName = packageName;
            this.appName = appName;
            this.hasSmsAccess = hasSmsAccess;
            this.hasScreenRisk = hasScreenRisk;
            this.isCertifiedWhitelisted = isCertifiedWhitelisted;
            this.isUserWhitelisted = isUserWhitelisted;
            this.isDefaultSmsApp = isDefaultSmsApp;
            this.isKnownMalware = isKnownMalware;
            this.description = description;
        }

        public boolean isSafe() {
            return isCertifiedWhitelisted || isUserWhitelisted || isDefaultSmsApp;
        }
    }

    /**
     * اعمال سیاست ضد اسکرین‌شات و ضبط صفحه در سطح سیستم‌عامل
     * از تصویربرداری و ضبط صفحه توسط تروجان‌ها و ابزارهای ریموت جلوگیری می‌کند.
     */
    public static void enforceScreenSecurity(Activity activity) {
        if (activity != null && activity.getWindow() != null) {
            activity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            );
        }
    }

    public static boolean hasSmsPermissions(PackageInfo pkgInfo) {
        if (pkgInfo == null || pkgInfo.requestedPermissions == null) return false;
        for (String perm : pkgInfo.requestedPermissions) {
            if (SMS_PERMISSIONS.contains(perm)) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasScreenCaptureRisk(PackageInfo pkgInfo) {
        if (pkgInfo == null || pkgInfo.requestedPermissions == null) return false;
        for (String perm : pkgInfo.requestedPermissions) {
            if (SCREEN_PERMISSIONS.contains(perm)) {
                return true;
            }
        }
        return false;
    }

    public static List<AppAuditResult> auditInstalledPackages(Context context, UserCustomWhitelistManager userWhitelist) {
        List<AppAuditResult> results = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        String defaultSms = null;
        try {
            defaultSms = Telephony.Sms.getDefaultSmsPackage(context);
        } catch (Exception ignored) {}

        List<PackageInfo> installedPackages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        for (PackageInfo pkg : installedPackages) {
            if (pkg.packageName.equals(context.getPackageName())) continue;

            boolean hasSms = hasSmsPermissions(pkg);
            boolean hasScreen = hasScreenCaptureRisk(pkg);

            // فقط برنامه‌هایی که حداقل یکی از دسترسی‌های حساس پیامک یا ضبط صفحه را دارند بررسی می‌شوند
            if (!hasSms && !hasScreen) continue;

            String appName;
            try {
                appName = pm.getApplicationLabel(pkg.applicationInfo).toString();
            } catch (Exception e) {
                appName = pkg.packageName;
            }

            boolean isDefault = (defaultSms != null && defaultSms.equalsIgnoreCase(pkg.packageName));
            boolean isCertified = TrustedWhitelistRegistry.isWhitelisted(pkg.packageName);
            boolean isUserWhitelisted = userWhitelist != null && userWhitelist.isWhitelisted(pkg.packageName);

            ThreatPackageRegistry.ThreatResult threat = ThreatPackageRegistry.evaluatePackage(pkg.packageName);
            boolean isMalware = (threat != null);

            String desc;
            if (isMalware) {
                desc = "بدافزار قطعی شناسایی‌شده! این برنامه پیامک‌های شما را شنود می‌کند.";
            } else if (isDefault) {
                desc = "برنامه پیش‌فرض پیامک‌های سیستم‌عامل (مجاز و امن)";
            } else if (isCertified) {
                desc = "برنامه سیستمی یا پلتفرم معتبر تأییدشده";
            } else if (isUserWhitelisted) {
                desc = "تأییدشده توسط خود شما در لیست سفید";
            } else {
                desc = "برنامه ناشناس با دسترسی حساس به پیامک/صفحه! نیازمند حذف یا سلب دسترسی فوری.";
            }

            results.add(new AppAuditResult(
                pkg.packageName,
                appName,
                hasSms,
                hasScreen,
                isCertified,
                isUserWhitelisted,
                isDefault,
                isMalware,
                desc
            ));
        }

        return results;
    }

    /**
     * حذف سریع یا هدایت فوری به بخش مجوزها برای سلب دسترسی
     */
    public static void launchRevokeOrUninstall(Context context, String packageName, boolean forceUninstall) {
        if (packageName == null || context == null) return;

        if (forceUninstall) {
            try {
                Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                uninstallIntent.setData(Uri.parse("package:" + packageName));
                uninstallIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
                uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(uninstallIntent);
                return;
            } catch (Exception ignored) {}
        }

        // هدایت به صفحه مدیریت مجوزها در تنظیمات برای غیرفعال‌سازی دسترسی پیامک
        try {
            Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            settingsIntent.setData(Uri.parse("package:" + packageName));
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(settingsIntent);
        } catch (Exception ignored) {}
    }
}

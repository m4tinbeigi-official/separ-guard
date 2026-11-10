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
 * تنها برنامه‌هایی که در حال حاضر دسترسی فعال (Granted Runtime Permission) دارند فهرست می‌شوند
 * و برنامه‌هایی که صرفاً درخواست داده‌اند ولی کاربر به آن‌ها دسترسی نداده، حذف می‌گردند.
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

    /**
     * بررسی استاتیک درخواست مجوز در مانیفست
     */
    public static boolean hasSmsPermissions(PackageInfo pkgInfo) {
        if (pkgInfo == null || pkgInfo.requestedPermissions == null) return false;
        for (String perm : pkgInfo.requestedPermissions) {
            if (SMS_PERMISSIONS.contains(perm)) {
                return true;
            }
        }
        return false;
    }

    /**
     * بررسی استاتیک درخواست مجوز ضبط صفحه در مانیفست
     */
    public static boolean hasScreenCaptureRisk(PackageInfo pkgInfo) {
        if (pkgInfo == null || pkgInfo.requestedPermissions == null) return false;
        for (String perm : pkgInfo.requestedPermissions) {
            if (SCREEN_PERMISSIONS.contains(perm)) {
                return true;
            }
        }
        return false;
    }

    /**
     * بررسی دقیق اینکه آیا مجوز به صورت واقعی و ران‌تایم توسط کاربر به برنامه اعطا شده است یا خیر
     */
    public static boolean isPermissionActuallyGranted(PackageManager pm, PackageInfo pkgInfo, Set<String> targetPermissions) {
        if (pkgInfo == null || targetPermissions == null) return false;

        // ۱. بررسی رسمی از طریق PackageManager.checkPermission (سازگار با تمامی نسخه‌ها)
        if (pm != null && pkgInfo.packageName != null) {
            for (String perm : targetPermissions) {
                try {
                    if (pm.checkPermission(perm, pkgInfo.packageName) == PackageManager.PERMISSION_GRANTED) {
                        return true;
                    }
                } catch (Exception ignored) {}
            }
        }

        // ۲. بررسی تکمیلی از طریق فلگ‌های PackageInfo.requestedPermissionsFlags
        if (pkgInfo.requestedPermissions != null && pkgInfo.requestedPermissionsFlags != null) {
            for (int i = 0; i < pkgInfo.requestedPermissions.length; i++) {
                String perm = pkgInfo.requestedPermissions[i];
                if (targetPermissions.contains(perm)) {
                    if (i < pkgInfo.requestedPermissionsFlags.length) {
                        if ((pkgInfo.requestedPermissionsFlags[i] & PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * آیا برنامه هم‌اکنون دسترسی فعال و تاییدشده پیامک دارد؟
     */
    public static boolean hasActiveSmsPermission(PackageManager pm, PackageInfo pkgInfo) {
        if (pkgInfo == null || pkgInfo.requestedPermissions == null) return false;
        if (pm != null || pkgInfo.requestedPermissionsFlags != null) {
            return isPermissionActuallyGranted(pm, pkgInfo, SMS_PERMISSIONS);
        }
        return hasSmsPermissions(pkgInfo);
    }

    /**
     * آیا برنامه هم‌اکنون دسترسی فعال و تاییدشده ضبط صفحه/همپوشانی دارد؟
     */
    public static boolean hasActiveScreenRisk(PackageManager pm, PackageInfo pkgInfo) {
        if (pkgInfo == null || pkgInfo.requestedPermissions == null) return false;
        if (pm != null || pkgInfo.requestedPermissionsFlags != null) {
            return isPermissionActuallyGranted(pm, pkgInfo, SCREEN_PERMISSIONS);
        }
        return hasScreenCaptureRisk(pkgInfo);
    }

    public static List<AppAuditResult> auditInstalledPackages(Context context, UserCustomWhitelistManager userWhitelist) {
        List<AppAuditResult> results = new ArrayList<>();
        if (context == null) return results;

        PackageManager pm = context.getPackageManager();
        String defaultSms = null;
        try {
            defaultSms = Telephony.Sms.getDefaultSmsPackage(context);
        } catch (Exception ignored) {}

        List<PackageInfo> installedPackages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        for (PackageInfo pkg : installedPackages) {
            if (pkg.packageName.equals(context.getPackageName())) continue;

            // تشخیص بدافزارهای قطعی (حتی اگر هنوز دسترسی نگرفته باشند)
            ThreatPackageRegistry.ThreatResult threat = ThreatPackageRegistry.evaluatePackage(pkg.packageName);
            boolean isMalware = (threat != null);

            // فقط و فقط برنامه‌هایی که در حال حاضر مجوز پیامک یا صفحه به آن‌ها اعطا شده است
            boolean hasActiveSms = hasActiveSmsPermission(pm, pkg);
            boolean hasActiveScreen = hasActiveScreenRisk(pm, pkg);

            // اگر دسترسی به آن اعطا نشده باشد و بدافزار قطعی هم نباشد، کاملاً نادیده گرفته می‌شود
            if (!hasActiveSms && !hasActiveScreen && !isMalware) {
                continue;
            }

            String appName;
            try {
                appName = pm.getApplicationLabel(pkg.applicationInfo).toString();
            } catch (Exception e) {
                appName = pkg.packageName;
            }

            boolean isDefault = (defaultSms != null && defaultSms.equalsIgnoreCase(pkg.packageName));
            boolean isCertified = TrustedWhitelistRegistry.isWhitelisted(pkg.packageName);
            boolean isUserWhitelisted = userWhitelist != null && userWhitelist.isWhitelisted(pkg.packageName);

            String desc;
            if (isMalware) {
                desc = "بدافزار قطعی شناسایی‌شده! این برنامه برای سرقت پیامک‌ها ساخته شده و باید فوراً حذف شود.";
            } else if (isDefault) {
                desc = "برنامه رسمی و پیش‌فرض پیامک‌های گوشی شما (مجاز و امن)";
            } else if (isCertified) {
                desc = "برنامه سیستمی یا پلتفرم رسمی تاییدشده کشور";
            } else if (isUserWhitelisted) {
                desc = "تاییدشده توسط خود شما در لیست سفید";
            } else {
                desc = "این برنامه هم‌اکنون مجوز فعال خواندن پیامک/صفحه را دارد! در صورت عدم نیاز، دسترسی آن را سلب کنید.";
            }

            results.add(new AppAuditResult(
                pkg.packageName,
                appName,
                hasActiveSms,
                hasActiveScreen,
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

        // هدایت مستقیم به صفحه مدیریت مجوزها در تنظیمات برای غیرفعال‌سازی دسترسی پیامک
        try {
            Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            settingsIntent.setData(Uri.parse("package:" + packageName));
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(settingsIntent);
        } catch (Exception ignored) {}
    }
}

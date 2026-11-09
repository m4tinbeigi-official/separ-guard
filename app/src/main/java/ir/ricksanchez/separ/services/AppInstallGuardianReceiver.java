package ir.ricksanchez.separ.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;
import ir.ricksanchez.separ.SeparApp;
import ir.ricksanchez.separ.data.SmsAndScreenGuard;
import ir.ricksanchez.separ.data.StealthTrojanDetector;
import ir.ricksanchez.separ.data.ThreatPackageRegistry;
import ir.ricksanchez.separ.data.TrustedWhitelistRegistry;
import ir.ricksanchez.separ.data.UserCustomWhitelistManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * سپر لحظه‌ای نصب فایل‌ها و برنامه‌ها
 * در ثانیه اول نصب برنامه جدید، ۶ الگوی بدافزاری کالبدشکافی‌شده و دسترسی‌های حساس پیامک را رصد و مسدود می‌کند:
 * ۱. حذف آیکون از لانچر
 * ۲. استتار هویتی و جعل نام سرویس‌های سیستم
 * ۳. سوءاستفاده از سرویس دسترسی‌پذیری جهت جلوگیری از حذف
 * ۴. سوءاستفاده از دسترسی مدیر دستگاه (Device Admin)
 * ۵. کرم‌های پیامکی با گیرنده روشن شدن دستگاه (BOOT_COMPLETED)
 * ۶. عناوین و بسته‌های تله فیشینگ (آلبوم یادگاری، صیغه‌یاب، سهام عدالت)
 */
public class AppInstallGuardianReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getData() == null) return;
        String packageName = intent.getData().getSchemeSpecificPart();
        if (packageName == null || packageName.trim().isEmpty()) return;

        // برنامه خود سپر نادیده گرفته می‌شود
        if (packageName.equalsIgnoreCase(context.getPackageName())) return;

        PackageManager pm = context.getPackageManager();
        PackageInfo pkgInfo = null;
        try {
            pkgInfo = pm.getPackageInfo(
                packageName,
                PackageManager.GET_PERMISSIONS | PackageManager.GET_SERVICES | PackageManager.GET_RECEIVERS
            );
        } catch (Exception ignored) {}

        // ۱. بررسی عمیق با موتور تشخیص ترفندهای پنهان‌سازی (StealthTrojanDetector)
        if (pkgInfo != null) {
            StealthTrojanDetector.StealthEvaluationResult stealthResult =
                StealthTrojanDetector.evaluatePackage(context, pkgInfo);

            if (stealthResult != null && stealthResult.isMalicious) {
                String appName;
                try {
                    appName = pm.getApplicationLabel(pkgInfo.applicationInfo).toString();
                } catch (Exception e) {
                    appName = packageName;
                }

                recordRescue(context, packageName, stealthResult.threatTitle, stealthResult.detailedReasonFa, true);

                // اجرای پوسته قرمز مسدودساز تمام‌صفحه و آلارم
                try {
                    Intent overlayIntent = new Intent(context, AlertOverlayService.class);
                    overlayIntent.putExtra("threat_package", packageName);
                    overlayIntent.putExtra("app_name", appName);
                    overlayIntent.putExtra("alert_title", "🚨 " + stealthResult.threatTitle);
                    overlayIntent.putExtra("alert_message", stealthResult.detailedReasonFa +
                        "\n\n⛔ سپر امنیتی اجازه نصب و ماندگاری این برنامه را نمی‌دهد! فوراً آن را حذف کنید.");
                    overlayIntent.putExtra("is_device_admin", stealthResult.isDeviceAdmin);
                    overlayIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startService(overlayIntent);
                } catch (Exception ignored) {}

                Toast.makeText(context, "⛔ بدافزار خطرناک شناسایی شد: " + stealthResult.threatTitle, Toast.LENGTH_LONG).show();

                // فراخوانی اکشن حذف فوری
                launchUninstallPrompt(context, packageName);
                return;
            }
        }

        // ۲. بررسی قطعی در رجیستری بدافزارهای بانکی (ولورا، یادگاری، فیشینگ‌ها)
        ThreatPackageRegistry.ThreatResult threat = ThreatPackageRegistry.evaluatePackage(packageName);
        if (threat != null) {
            recordRescue(context, packageName, threat.threatName, threat.description, true);

            try {
                Intent overlayIntent = new Intent(context, AlertOverlayService.class);
                overlayIntent.putExtra("threat_package", packageName);
                overlayIntent.putExtra("alert_title", "🚨 " + threat.threatName);
                overlayIntent.putExtra("alert_message", threat.description + "\n\nاین بدافزار باید فوراً از گوشی پاک شود.");
                overlayIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startService(overlayIntent);
            } catch (Exception ignored) {}

            launchUninstallPrompt(context, packageName);
            return;
        }

        // ۳. بررسی دسترسی به پیامک‌ها و ضبط صفحه در بسته تازه نصب‌شده
        if (pkgInfo != null) {
            boolean hasSms = SmsAndScreenGuard.hasSmsPermissions(pkgInfo);
            boolean hasScreen = SmsAndScreenGuard.hasScreenCaptureRisk(pkgInfo);

            if (hasSms || hasScreen) {
                UserCustomWhitelistManager userWhitelist = SeparApp.getInstance().getUserWhitelist();
                boolean isWhitelisted = TrustedWhitelistRegistry.isWhitelisted(packageName) ||
                                       (userWhitelist != null && userWhitelist.isWhitelisted(packageName));

                if (!isWhitelisted) {
                    String appName;
                    try {
                        appName = pm.getApplicationLabel(pkgInfo.applicationInfo).toString();
                    } catch (Exception e) {
                        appName = packageName;
                    }

                    String threatTitle = hasSms ? "خطر شنود پیامک بانکی" : "خطر ضبط صفحه و اسکرین‌شات";
                    String threatDesc = "برنامه تازه نصب‌شده «" + appName + "» بدون اجازه شما به " +
                            (hasSms ? "پیامک‌ها و رمزهای پویا" : "صفحه نمایش") + " دسترسی یافته است.";

                    recordRescue(context, packageName, threatTitle, threatDesc, true);

                    // اجرای پوسته مسدودساز و هشدار صوتی
                    try {
                        Intent overlayIntent = new Intent(context, AlertOverlayService.class);
                        overlayIntent.putExtra("threat_package", packageName);
                        overlayIntent.putExtra("app_name", appName);
                        overlayIntent.putExtra("alert_title", "🚨 " + threatTitle);
                        overlayIntent.putExtra("alert_message", threatDesc + "\nاین برنامه در لیست سفید نیست و باید فوراً حذف شود.");
                        overlayIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startService(overlayIntent);
                    } catch (Exception ignored) {}

                    Toast.makeText(context, "⚠️ هشدار سپر: " + threatDesc, Toast.LENGTH_LONG).show();

                    // باز کردن درخواست حذف فوری
                    launchUninstallPrompt(context, packageName);
                }
            }
        }
    }

    private void recordRescue(Context context, String pkg, String title, String summary, boolean isFinancial) {
        try {
            String dateStr = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
            SeparApp.getInstance().getRescueLedger().recordRescue(
                System.currentTimeMillis(),
                dateStr,
                1,
                title,
                summary,
                isFinancial,
                pkg
            );
        } catch (Exception ignored) {}
    }

    private void launchUninstallPrompt(Context context, String packageName) {
        try {
            Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
            uninstallIntent.setData(Uri.parse("package:" + packageName));
            uninstallIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
            uninstallIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(uninstallIntent);
        } catch (Exception ignored) {}
    }
}

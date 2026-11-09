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
import ir.ricksanchez.separ.data.ThreatPackageRegistry;
import ir.ricksanchez.separ.data.TrustedWhitelistRegistry;
import ir.ricksanchez.separ.data.UserCustomWhitelistManager;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * سپر لحظه‌ای نصب فایل‌ها و برنامه‌ها
 * در ثانیه اول نصب برنامه جدید، دسترسی‌های حساس پیامک و ضبط صفحه و بدافزار بودن آن را بررسی می‌کند.
 */
public class AppInstallGuardianReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getData() == null) return;
        String packageName = intent.getData().getSchemeSpecificPart();
        if (packageName == null || packageName.trim().isEmpty()) return;

        // برنامه خود سپر نادیده گرفته می‌شود
        if (packageName.equalsIgnoreCase(context.getPackageName())) return;

        // ۱. بررسی قطعی در رجیستری بدافزارهای بانکی (ولورا، یادگاری، فیشینگ‌ها)
        ThreatPackageRegistry.ThreatResult threat = ThreatPackageRegistry.evaluatePackage(packageName);
        if (threat != null) {
            recordRescue(context, packageName, threat.threatName, threat.description, true);
            launchUninstallPrompt(context, packageName);
            return;
        }

        // ۲. بررسی دسترسی به پیامک‌ها و ضبط صفحه در بسته تازه نصب‌شده
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pkgInfo = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
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
        } catch (Exception ignored) {}
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

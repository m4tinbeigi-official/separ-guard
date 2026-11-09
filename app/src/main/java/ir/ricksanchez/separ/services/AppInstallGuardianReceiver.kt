package ir.ricksanchez.separ.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import ir.ricksanchez.separ.SeparApp
import ir.ricksanchez.separ.data.ThreatPackageRegistry
import ir.ricksanchez.separ.data.models.RescuedAttack
import ir.ricksanchez.separ.data.models.RescuedAttackType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * سپر لحظه‌ای نصب فایل‌ها و برنامه‌ها برای متوقف‌سازی فوری بدافزارها در ثانیه اول نصب
 */
class AppInstallGuardianReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val packageName = intent.data?.schemeSpecificPart ?: return

        // بررسی در رجیستری تهدیدها
        val threat = ThreatPackageRegistry.evaluatePackage(packageName) ?: return

        // ثبت در کارنامه نجات
        val app = context.applicationContext as? SeparApp
        val dateStr = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())

        app?.rescueLedger?.recordRescue(
            RescuedAttack(
                timestamp = System.currentTimeMillis(),
                jalaliDate = dateStr,
                type = RescuedAttackType.MALICIOUS_APP_BLOCKED,
                title = threat.threatName,
                summaryFa = "بدافزار خطرناک تازه نصب‌شده ($packageName) شناسایی شد و درخواست حذف فوری صادر گردید.",
                financialProtection = threat.isCritical,
                sourcePackageOrDomain = packageName
            )
        )

        Toast.makeText(
            context,
            "🔴 بدافزار خطرناک شناسایی شد: ${threat.threatName}\nلطفاً بلافاصله آن را حذف کنید.",
            Toast.LENGTH_LONG
        ).show()

        // فراخوانی پنجره حذف سیستمی
        try {
            val uninstallIntent = Intent(Intent.ACTION_UNINSTALL_PACKAGE).apply {
                data = Uri.parse("package:$packageName")
                putExtra(Intent.EXTRA_RETURN_RESULT, true)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(uninstallIntent)
        } catch (_: Exception) {}
    }
}

package ir.ricksanchez.separ.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

/**
 * هدایت ۱ کلیکی کاربر به بخش نشست‌ها و دستگاه‌های متصل در پیام‌رسان‌های داخلی و خارجی
 */
object IntentHelper {

    fun openTelegramSessions(context: Context) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("tg://settings/devices"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (_: Exception) {
            openAppDetails(context, "org.telegram.messenger")
        }
    }

    fun openWhatsAppSessions(context: Context) {
        openAppDetails(context, "com.whatsapp")
    }

    fun openInstagramSessions(context: Context) {
        openAppDetails(context, "com.instagram.android")
    }

    fun openRubikaSessions(context: Context) {
        // ابتدا پکیج اصلی روبیکا، سپس نسخه دوم
        if (!openAppDetails(context, "ir.reseller.app")) {
            openAppDetails(context, "ir.rubika.app")
        }
    }

    fun openEitaaSessions(context: Context) {
        openAppDetails(context, "ir.eitaa.messenger")
    }

    fun openBaleSessions(context: Context) {
        openAppDetails(context, "ir.bale.messenger")
    }

    fun openSoroushSessions(context: Context) {
        openAppDetails(context, "mobi.mmdt.ottplus")
    }

    private fun openAppDetails(context: Context, packageName: String): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:$packageName")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            true
        } catch (_: Exception) {
            try {
                // اگر اپ روی گوشی نبود، صفحه کل برنامه‌ها را باز کن
                val intent = Intent(Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                false
            } catch (_: Exception) {
                false
            }
        }
    }
}

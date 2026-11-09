package ir.ricksanchez.separ.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

/**
 * تشخیص برنامه‌های مخرب پنهان‌شده که آیکون خود را از صفحه گوشی حذف کرده‌اند
 */
object HiddenAppDetector {

    data class HiddenCheckResult(
        val isHidden: Boolean,
        val reason: String?
    )

    fun checkPackage(context: Context, packageName: String): HiddenCheckResult {
        val pm = context.packageManager

        try {
            // برنامه‌های سیستمی را نادیده بگیر
            if (pm.checkSignatures(packageName, "android") == PackageManager.SIGNATURE_MATCH) {
                return HiddenCheckResult(false, null)
            }

            val appEnabled = pm.getApplicationEnabledSetting(packageName)
            if (appEnabled == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                appEnabled == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
            ) {
                return HiddenCheckResult(false, null)
            }

            val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
                setPackage(packageName)
            }

            @Suppress("DEPRECATION")
            val launchers = pm.queryIntentActivities(launcherIntent, PackageManager.GET_DISABLED_COMPONENTS)

            var hasEnabledLauncher = false
            var hasBlankedDisabledLauncher = false

            for (resolveInfo in launchers) {
                val activity = resolveInfo.activityInfo ?: continue
                val component = ComponentName(activity.packageName, activity.name)
                val enabled = when (pm.getComponentEnabledSetting(component)) {
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> true
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED -> false
                    else -> activity.enabled
                }
                if (enabled) {
                    hasEnabledLauncher = true
                    break
                }
                val hasOwnLabel = activity.labelRes != 0 || !activity.nonLocalizedLabel.isNullOrBlank()
                if (!hasOwnLabel) hasBlankedDisabledLauncher = true
            }

            if (!hasEnabledLauncher && hasBlankedDisabledLauncher) {
                return HiddenCheckResult(true, "آیکون برنامه در صفحه گوشی پنهان شده است.")
            }

            return HiddenCheckResult(false, null)
        } catch (_: Exception) {
            return HiddenCheckResult(false, null)
        }
    }
}

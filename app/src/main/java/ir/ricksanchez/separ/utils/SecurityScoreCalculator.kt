package ir.ricksanchez.separ.utils

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.view.accessibility.AccessibilityManager
import ir.ricksanchez.separ.services.UrlGuardAccessibilityService

/**
 * محاسبه‌گر هوشمند شاخص نمره امنیت دستگاه (از ۰ تا ۱۰۰)
 */
object SecurityScoreCalculator {

    data class ScoreBreakdown(
        val totalScore: Int,
        val hasMalware: Boolean,
        val isWebGuardActive: Boolean,
        val isSmsGuardActive: Boolean,
        val isAutostartConfigured: Boolean,
        val statusTitleFa: String
    )

    fun calculate(
        context: Context,
        malwareCount: Int,
        smsGuardEnabled: Boolean,
        autostartEnabled: Boolean
    ): ScoreBreakdown {
        var score = 40 // امتیاز پایه سیستم سالم

        // وضعیت فعال بودن سرویس دسترسی‌پذیری محافظت شاپرک
        val isAccessibilityActive = isAccessibilityServiceEnabled(context)
        if (isAccessibilityActive) {
            score += 20
        }

        if (smsGuardEnabled) {
            score += 20
        }

        if (autostartEnabled) {
            score += 10
        }

        if (malwareCount == 0) {
            score += 10
        } else {
            // در صورت وجود بدافزار فعال، نمره افت شدید پیدا می‌کند
            score -= (malwareCount * 25).coerceAtMost(35)
        }

        val finalScore = score.coerceIn(0, 100)

        val title = when {
            finalScore >= 90 -> "بسیار عالی و کاملاً ایمن"
            finalScore >= 70 -> "امنیت خوب (نیازمند فعال‌سازی سپرهای تکمیلی)"
            finalScore >= 50 -> "امنیت متوسط (آسیب‌پذیر در برابر فیشینگ)"
            else -> "وضعیت بحرانی (خطر فوری سرقت اطلاعات)"
        }

        return ScoreBreakdown(
            totalScore = finalScore,
            hasMalware = malwareCount > 0,
            isWebGuardActive = isAccessibilityActive,
            isSmsGuardActive = smsGuardEnabled,
            isAutostartConfigured = autostartEnabled,
            statusTitleFa = title
        )
    }

    fun isAccessibilityServiceEnabled(context: Context): Boolean {
        val am = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager ?: return false
        val enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        val expectedServiceName = "${context.packageName}/${UrlGuardAccessibilityService::class.java.canonicalName}"

        for (service in enabledServices) {
            if (service.id.equals(expectedServiceName, ignoreCase = true) ||
                service.id.contains("UrlGuardAccessibilityService", ignoreCase = true)
            ) {
                return true
            }
        }
        return false
    }
}

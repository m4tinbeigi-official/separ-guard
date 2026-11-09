package ir.ricksanchez.separ.services

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import ir.ricksanchez.separ.SeparApp
import ir.ricksanchez.separ.data.models.RescuedAttack
import ir.ricksanchez.separ.data.models.RescuedAttackType
import java.net.URI
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * سرویس دسترسی‌پذیری برای مانیتورینگ نوار آدرس مرورگرها و محافظت از کارت بانکی در برابر درگاه‌های جعلی شاپرک
 */
class UrlGuardAccessibilityService : AccessibilityService() {

    private val BROWSER_PACKAGES = setOf(
        "com.android.chrome",
        "com.sec.android.app.sbrowser",
        "org.mozilla.firefox",
        "com.opera.browser",
        "com.microsoft.emmx",
        "com.mi.globalbrowser"
    )

    private val BANK_GATEWAY_KEYWORDS = listOf("shaparak", "sep.ir", "bpm.shaparak", "sadad", "asancard", "parsian")

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val packageName = event.packageName?.toString() ?: return

        if (!BROWSER_PACKAGES.contains(packageName)) return

        val rootNode = rootInActiveWindow ?: return
        val addressText = findAddressBarText(rootNode) ?: return

        evaluateUrl(addressText)
    }

    private fun findAddressBarText(node: AccessibilityNodeInfo): String? {
        val viewId = node.viewIdResourceName?.lowercase() ?: ""
        if (viewId.contains("url") || viewId.contains("address") || viewId.contains("location")) {
            val text = node.text?.toString()
            if (!text.isNullOrBlank() && (text.contains(".") || text.startsWith("http"))) {
                return text
            }
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val found = findAddressBarText(child)
            if (found != null) return found
        }
        return null
    }

    private fun evaluateUrl(rawUrl: String) {
        val lowerUrl = rawUrl.trim().lowercase()

        // استخراج دامنه
        val domain = try {
            val formatted = if (!lowerUrl.startsWith("http")) "https://$lowerUrl" else lowerUrl
            URI(formatted).host ?: lowerUrl
        } catch (_: Exception) {
            lowerUrl
        }

        // آیا کلمه شاپرک یا نام درگاه‌ها در دامنه است اما دامنه اصلی شاپرک نیست؟
        val containsShaparakLure = BANK_GATEWAY_KEYWORDS.any { domain.contains(it) }
        val isAuthenticShaparak = domain.endsWith(".shaparak.ir") || domain == "shaparak.ir"

        if (containsShaparakLure && !isAuthenticShaparak) {
            // درگاه جعلی شاپرک کشف شد!
            val app = applicationContext as? SeparApp
            val dateStr = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())

            app?.rescueLedger?.recordRescue(
                RescuedAttack(
                    timestamp = System.currentTimeMillis(),
                    jalaliDate = dateStr,
                    type = RescuedAttackType.FAKE_SHAPARAK_GATEWAY,
                    title = "درگاه جعلی پرداخت شاپرک",
                    summaryFa = "سپر جلوی ورود شما به درگاه سرقت اطلاعات کارت بانکی ($domain) را گرفت.",
                    financialProtection = true,
                    sourcePackageOrDomain = domain
                )
            )

            // فراخوانی پنجره مسدودکننده قرمز
            val overlayIntent = Intent(this, AlertOverlayService::class.java).apply {
                putExtra("threat_domain", domain)
            }
            startService(overlayIntent)
        }
    }

    override fun onInterrupt() {}
}

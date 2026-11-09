package ir.ricksanchez.separ.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

/**
 * پایشگر کدهای دستوری دایورت و انتقال تماس و پیامک (ترفند سرقت پیامک از طریق USSD)
 */
object CallForwardingAuditor {

    const val CANCEL_FORWARDING_CODE = "#21#"
    const val CANCEL_ALL_FORWARDING_CODE = "##002#"

    /**
     * باز کردن مستقیم شماره‌گیر گوشی با کد لغو دایورت
     */
    fun dialCancelForwarding(context: Context) {
        try {
            val encodedHash = Uri.encode("#")
            val ussdUri = Uri.parse("tel:$encodedHash$encodedHash" + "002" + encodedHash)
            val intent = Intent(Intent.ACTION_DIAL, ussdUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    fun getVoiceExplanation(): String {
        return "اگر تماس‌گیرنده ناشناسی از شما خواسته کدی شبیه ستاره بیست و یک شماره‌گیری کنید، خط شما دایورت شده و پیامک‌های بانکی برای کلاهبردار فرستاده می‌شود. این دکمه را لمس کنید تا دایورت خط فوراً لغو شود."
    }
}

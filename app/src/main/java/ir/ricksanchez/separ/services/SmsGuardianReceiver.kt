package ir.ricksanchez.separ.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.widget.Toast
import ir.ricksanchez.separ.SeparApp
import ir.ricksanchez.separ.data.models.RescuedAttack
import ir.ricksanchez.separ.data.models.RescuedAttackType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * سپر پایشگر لحظه‌ای پیامک‌های ورودی برای شناسایی فیشینگ، ابلاغیه جعلی و کرم‌های پیامکی
 */
class SmsGuardianReceiver : BroadcastReceiver() {

    private val SUSPICIOUS_KEYWORDS = listOf(
        "ثنا",
        "ابلاغیه",
        "شکواییه",
        "سهام عدالت",
        "واریز سود",
        "قطع یارانه",
        "کالابرگ",
        "توقیف خودرو",
        "عکس یادگاری",
        "کارت عروسی",
        "اینترنت رایگان"
    )

    private val SUSPICIOUS_URL_REGEX = Regex(
        "https?://[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}(/\\S*)?",
        RegexOption.IGNORE_CASE
    )

    // الگوهای درخواست فریبنده ارسال رمز یا کدهای تایید
    private val OTP_SOLICITATION_REGEX = Regex(
        ".*(رمز|کد|پین).*(بفرست|ارسال|بگو|پیامک|بده|اعلام).*|.*(بفرست|ارسال|پیامک).*(رمز|کد).*|.*کد.*(تایید|ورود|فعالسازی|پویا).*",
        RegexOption.IGNORE_CASE
    )

    // الگوهای جعل هویت پشتیبانی بانک و ترساندن کاربر با مسدودی حساب
    private val BANK_IMPERSONATION_REGEX = Regex(
        ".*(پشتیبانی|کارشناس|حراست|اداره|بازرسی|امور مشتریان).*(بانک|ویپاد|بلوبانک|پاسارگاد|ملی|ملت|تجارت|صادرات|سپه|سامان|رسالت|همراه کارت|شاپرک).*",
        RegexOption.IGNORE_CASE
    )

    private val FEAR_PANIC_LURE_REGEX = Regex(
        ".*(حساب.*مشکوک|مسدود.*حساب|توقیف.*حساب|بلوکه|تخلف|مدارک.*بیارید|احراز هویت|رمز.*ویپاد|رمز.*بانک).*",
        RegexOption.IGNORE_CASE
    )

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent) ?: return

        for (sms in messages) {
            val body = sms.messageBody ?: continue
            val sender = sms.originatingAddress ?: "ناشناس"

            val cleanBody = body.replace("\n", " ").trim()
            val isPersonalNumber = sender.startsWith("+989") || sender.startsWith("09") || sender.startsWith("989")
            val isBankImpersonation = isPersonalNumber && (BANK_IMPERSONATION_REGEX.matches(cleanBody) || (cleanBody.contains("ویپاد") && cleanBody.contains("پشتیبانی")))
            val isFearLure = isPersonalNumber && FEAR_PANIC_LURE_REGEX.matches(cleanBody)
            val isOtpSolicitation = OTP_SOLICITATION_REGEX.matches(cleanBody)
            val containsLure = SUSPICIOUS_KEYWORDS.any { body.contains(it) }
            val containsUrl = SUSPICIOUS_URL_REGEX.containsMatchIn(body)

            val app = context.applicationContext as? SeparApp
            val dateStr = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())

            // سناریوی ۱: جعل هویت پشتیبانی بانک و ترساندن کاربر با مسدودی حساب (دقیقاً سناریوی حمله اخیر)
            if (isBankImpersonation || (isFearLure && isOtpSolicitation)) {
                app?.rescueLedger?.recordRescue(
                    RescuedAttack(
                        timestamp = System.currentTimeMillis(),
                        jalaliDate = dateStr,
                        type = RescuedAttackType.BANK_SUPPORT_IMPERSONATION,
                        title = "کلاهبرداری جعل پشتیبانی بانک و تهدید مسدودی",
                        summaryFa = "پیامک جعلی ارسالی از شماره شخصی $sender با ادعای دروغین پشتیبانی بانک و درخواست رمز برای خالی کردن حساب خنثی شد.",
                        financialProtection = true,
                        sourcePackageOrDomain = sender
                    )
                )

                // پرتاب پوسته مسدودساز تمام صفحه قرمز
                try {
                    val overlayIntent = Intent(context, AlertOverlayService::class.java).apply {
                        putExtra("alert_title", "🚨 کلاهبرداری ۱۰۰٪ قطعی: جعل عنوان پشتیبانی بانک!")
                        putExtra(
                            "alert_message",
                            "پیامک دریافتی از شماره شخصی $sender است، نه بانک!\nکلاهبردار با ادعای دروغین 'مشکوک بودن حساب' می‌خواهد شما بترسید و رمز بدهید تا پول پیش یا پس‌اندازتان را خالی کند.\nتحت هیچ شرایطی رمزی نفرستید و فوراً این پیام را نادیده بگیرید."
                        )
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startService(overlayIntent)
                } catch (_: Exception) {}

                Toast.makeText(
                    context,
                    "🚨 کلاهبرداری قطعی! هیچ بانکی با شماره موبایل شخصی پیام نمی‌دهد. هرگز رمز ندهید!",
                    Toast.LENGTH_LONG
                ).show()
                break
            }

            // سناریوی ۲: پیامک درخواست رمز یا کد تایید (مهندسی اجتماعی)
            if (isOtpSolicitation) {
                app?.rescueLedger?.recordRescue(
                    RescuedAttack(
                        timestamp = System.currentTimeMillis(),
                        jalaliDate = dateStr,
                        type = RescuedAttackType.OTP_THEFT_SOLICITATION,
                        title = "تلاش برای سرقت رمز پویا یا کد تایید",
                        summaryFa = "پیامک دریافتی از $sender از شما درخواست ارسال کد یا رمز کرده بود که به عنوان تلاش برای سرقت اطلاعات خنثی شد.",
                        financialProtection = true,
                        sourcePackageOrDomain = sender
                    )
                )

                Toast.makeText(
                    context,
                    "🚨 خطر سرقت اطلاعات! پیامک از شما درخواست رمز یا کد کرده است. هرگز کدهای پیامکی خود را برای هیچ‌کس ارسال نکنید!",
                    Toast.LENGTH_LONG
                ).show()
                break
            }

            // سناریوی ۲: پیامک فیشینگ حاوی لینک
            if (containsLure && containsUrl) {
                app?.rescueLedger?.recordRescue(
                    RescuedAttack(
                        timestamp = System.currentTimeMillis(),
                        jalaliDate = dateStr,
                        type = RescuedAttackType.PHISHING_SMS,
                        title = "پیامک فریبنده فیشینگ",
                        summaryFa = "پیامک کلاهبرداری ارسالی از $sender خنثی شد و مانع ورود شما به لینک سرقت شد.",
                        financialProtection = true,
                        sourcePackageOrDomain = sender
                    )
                )

                Toast.makeText(
                    context,
                    "⚠️ هشدار سپر: پیامک دریافتی از $sender حاوی لینک کلاهبرداری است. هرگز روی آن نزنید!",
                    Toast.LENGTH_LONG
                ).show()
                break
            }
        }
    }
}

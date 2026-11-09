package ir.ricksanchez.separ.data

import android.graphics.Bitmap

/**
 * تحلیلگر هوشمند اصالت رسیدهای بانکی و کشف رسیدهای جعلی کلاهبرداری
 */
object FakeReceiptAnalyzer {

    // پیش‌شماره‌های ۶ رقمی معتبر شبکه بانکی شتاب ایران (BIN)
    private val VALID_BANK_BINS = mapOf(
        "603799" to "بانک ملی ایران",
        "610433" to "بانک ملت",
        "585983" to "بانک تجارت",
        "627353" to "بانک تجارت",
        "603769" to "بانک صادرات ایران",
        "589210" to "بانک سپه",
        "603770" to "بانک کشاورزی",
        "639217" to "بانک کشاورزی",
        "628023" to "بانک مسکن",
        "621986" to "بانک سامان / بلوبانک",
        "622106" to "بانک پارسیان",
        "639194" to "بانک پارسیان",
        "502229" to "بانک پاسارگاد",
        "639347" to "بانک پاسارگاد",
        "639346" to "بانک سینا",
        "502806" to "بانک شهر",
        "504706" to "بانک شهر",
        "636214" to "بانک آینده",
        "589463" to "بانک رفاه کارگران",
        "627760" to "پست بانک ایران",
        "504172" to "بانک قرض‌الحسنه رسالت",
        "606373" to "بانک قرض‌الحسنه مهر ایران",
        "627412" to "بانک اقتصاد نوین",
        "505416" to "بانک گردشگری",
        "636949" to "بانک حکمت ایرانیان / سپه",
        "627961" to "بانک صنعت و معدن",
        "639599" to "بانک قوامین / سپه",
        "505785" to "بانک ایران زمین",
        "505801" to "موسسه کوثر / سپه"
    )

    data class VerificationResult(
        val isSuspicious: Boolean,
        val riskScore: Int, // 0 (کم‌خطر) تا 100 (جعلی قطعی)
        val titleFa: String,
        val reasons: List<String>,
        val recognizedBank: String?,
        val voiceWarning: String
    )

    /**
     * بررسی ارکان رسید بر اساس اطلاعات استخراج‌شده یا وارد شده از اسکرین‌شات
     */
    fun analyzeReceipt(
        destinationCard: String,
        sourceCard: String? = null,
        referenceNumber: String,
        trackingNumber: String,
        amountTomans: Long,
        bitmap: Bitmap? = null
    ): VerificationResult {
        val reasons = mutableListOf<String>()
        var score = 0

        // ۱. اعتبارسنجی کارت مقصد
        val cleanDest = destinationCard.replace(" ", "").replace("-", "")
        val destBin = if (cleanDest.length >= 6) cleanDest.substring(0, 6) else ""
        val bankName = VALID_BANK_BINS[destBin]

        if (cleanDest.isNotEmpty() && bankName == null) {
            score += 45
            reasons.add("پیش‌شماره کارت مقصد ($destBin) متعلق به هیچ بانک رسمی در ایران نیست.")
        }

        // ۲. اعتبارسنجی کارت مبدا در صورت وجود
        if (!sourceCard.isNullOrBlank()) {
            val cleanSrc = sourceCard.replace(" ", "").replace("-", "")
            val srcBin = if (cleanSrc.length >= 6) cleanSrc.substring(0, 6) else ""
            if (cleanSrc.length >= 6 && !VALID_BANK_BINS.containsKey(srcBin)) {
                score += 30
                reasons.add("پیش‌شماره کارت مبدا نامعتبر است.")
            }
        }

        // ۳. بررسی شماره مرجع و پیگیری
        val cleanRef = referenceNumber.trim()
        if (cleanRef.length in 1..4) {
            score += 35
            reasons.add("شماره مرجع کمتر از حد استاندارد تراکنش‌های شتاب است (معمولاً ۶ یا ۱۲ رقم).")
        }

        if (cleanRef == "123456" || cleanRef == "123456789" || cleanRef == "111111" || cleanRef == "000000") {
            score += 50
            reasons.add("شماره مرجع دارای الگوی ساختگی و تستی است.")
        }

        val cleanTrack = trackingNumber.trim()
        if (cleanTrack == "123456" || cleanTrack == "111111" || cleanTrack == "000000") {
            score += 40
            reasons.add("شماره پیگیری دارای ارقام پشت‌سرهم ساختگی است.")
        }

        // ۴. تحلیل تصویر در صورت وجود اسکرین‌شات
        if (bitmap != null) {
            val width = bitmap.width
            val height = bitmap.height

            // بررسی نسبت ابعاد غیرعادی (رسیدهای موبایل معمولا عمودی هستند)
            if (width > height) {
                score += 20
                reasons.add("تصویر ارسالی افقی است و شبیه اسکرین‌شات استاندارد موبایل بانک نیست.")
            }

            // بررسی رزولوشن خیلی پایین ناشی از خروجی وب‌سایت‌های رسیدساز
            if (width < 300 || height < 400) {
                score += 25
                reasons.add("کیفیت تصویر بسیار پایین است و نشان‌دهنده تولید با رسیدسازهای اینترنتی است.")
            }
        }

        // ۵. هشدار قطعی پلیس فتا
        reasons.add("توصیه طلایی پلیس فتا: حتی رسیدهای بدون نقص ظاهری ممکن است با نرم‌افزارهای رسیدساز جعلی ساخته شده باشند. تا دریافت پیامک واریز از بانک یا چک کردن مانده حساب در همراه بانک، کالا را تحویل ندهید.")

        val isSuspicious = score >= 35

        val title = if (isSuspicious) {
            "🔴 رسید به شدت مشکوک یا جعلی است!"
        } else {
            "🟡 ارکان رسید بررسی شد، اما حتماً حساب را چک کنید"
        }

        val voice = if (isSuspicious) {
            "هشدار مهم! این رسید ممکن است جعلی باشد. تا زمانی که پیامک واریز از بانک خودتان نیامده یا حساب را چک نکرده‌اید، کالا یا پول را تحویل ندهید."
        } else {
            "ارکان شماره کارت بررسی شد، اما به رسید تصویر اعتماد نکنید و فقط پیامک رسمی بانک خود را ملاک واریز قرار دهید."
        }

        return VerificationResult(
            isSuspicious = isSuspicious,
            riskScore = score.coerceAtMost(100),
            titleFa = title,
            reasons = reasons,
            recognizedBank = bankName,
            voiceWarning = voice
        )
    }
}

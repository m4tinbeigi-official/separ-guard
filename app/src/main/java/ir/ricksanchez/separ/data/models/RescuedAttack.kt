package ir.ricksanchez.separ.data.models

/**
 * مدل داده رویدادهای دفاعی و نجات کاربر از حملات سایبری و بانکی
 */
enum class RescuedAttackType {
    FAKE_SHAPARAK_GATEWAY,  // درگاه جعلی پرداخت شاپرک
    PHISHING_SMS,           // پیامک فریبنده فیشینگ (ثنا، سهام، یارانه)
    MALICIOUS_APP_BLOCKED,  // نصب بدافزار تروجان یا کرم پیامکی
    ABUSED_RAT_NEUTRALIZED, // برنامه ریموت دسکتاپ ناشناس
    OTP_THEFT_SOLICITATION, // درخواست پیامکی فریبنده برای ارسال رمز و کد تایید
    BANK_SUPPORT_IMPERSONATION // جعل هویت پشتیبانی بانک و تهدید به مسدودی حساب
}

data class RescuedAttack(
    val id: Long = 0,
    val timestamp: Long,
    val jalaliDate: String,
    val type: RescuedAttackType,
    val title: String,
    val summaryFa: String,
    val financialProtection: Boolean,
    val sourcePackageOrDomain: String
)

package ir.ricksanchez.separ.services;

import android.content.Context;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * پایشگر تماس‌های مشکوک، کلاهبرداری صوتی (Vishing) و تله‌های تک‌زنگ بین‌المللی (Wangiri Scam)
 * شماره‌های با هزینه گزاف و تماس‌های فریبنده بانکی را شناسایی و هشدار می‌دهد.
 */
public class CallFraudGuardian {

    public static class CallRiskAssessment {
        public final boolean isHighRisk;
        public final String riskType; // "WANGIRI_SCAM", "FAKE_BANK_SUPPORT", "CLEAN"
        public final String riskTitleFa;
        public final String recommendationFa;

        public CallRiskAssessment(boolean isHighRisk, String type, String title, String rec) {
            this.isHighRisk = isHighRisk;
            this.riskType = type;
            this.riskTitleFa = title;
            this.recommendationFa = rec;
        }
    }

    // پیش‌شماره‌های بین‌المللی پرخطر در حملات تک‌زنگ (Wangiri) به خطوط همراه اول و ایرانسل
    private static final Set<String> WANGIRI_COUNTRY_PREFIXES = new HashSet<>(Arrays.asList(
        "+247", "+290", "+297", "+298", "+299", // جزایر اسنشن، سنت هلن، گرینلند
        "+231", "+232", "+233", "+234", "+235", // لیبریا، سیرالئون، غنا، نیجریه
        "+242", "+243", "+244", "+245",         // کنگو، آنگولا
        "+881", "+882", "+883",                 // شبکه‌های ماهواره‌ای اینمارست با تعرفه فوق‌العاده سنگین
        "+674", "+675", "+676", "+677", "+678", // نائورو، پاپوآ گینه نو، تونگا، وانواتو
        "+685", "+686", "+687", "+688"          // ساموآ، تووالو، کیریباتی
    ));

    public static CallRiskAssessment evaluateIncomingNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return new CallRiskAssessment(false, "CLEAN", "تماس عادی", "هیچ خطری شناسایی نشد.");
        }

        String clean = phoneNumber.trim().replace(" ", "").replace("-", "");

        // ۱. بررسی تله تک‌زنگ بین‌المللی (Wangiri)
        for (String prefix : WANGIRI_COUNTRY_PREFIXES) {
            if (clean.startsWith(prefix) || clean.startsWith("00" + prefix.substring(1))) {
                return new CallRiskAssessment(
                    true,
                    "WANGIRI_SCAM",
                    "⚠️ هشدار تله تک‌زنگ بین‌المللی (Wangiri Scam)",
                    "این تماس از پیش‌شماره بین‌المللی با هزینه فوق‌العاده سنگین است. هرگز با این شماره تماس مجدد نگیرید!"
                );
            }
        }

        // ۲. شماره‌های تلفن همراه عادی ایران که ادعای پشتیبانی دارند
        // اگر فردی با شماره ۰۹... زنگ بزند و ادعای پشتیبانی بانک کند
        if (clean.startsWith("09") || clean.startsWith("+989") || clean.startsWith("00989")) {
            return new CallRiskAssessment(
                false,
                "IRANIAN_MOBILE",
                "شماره موبایل شخصی",
                "توجه: هیچ بانکی با شماره موبایل شخصی تماس نمی‌گیرد و رمز دوم یا کد اس‌ام‌اس درخواست نمی‌کند."
            );
        }

        return new CallRiskAssessment(false, "CLEAN", "شماره عادی", "تماس عادی ارزیابی شد.");
    }
}

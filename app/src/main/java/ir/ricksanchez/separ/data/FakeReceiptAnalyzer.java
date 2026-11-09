package ir.ricksanchez.separ.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FakeReceiptAnalyzer {

    private static final Map<String, String> VALID_BANK_BINS = new HashMap<>();

    static {
        VALID_BANK_BINS.put("603799", "بانک ملی ایران");
        VALID_BANK_BINS.put("610433", "بانک ملت");
        VALID_BANK_BINS.put("585983", "بانک تجارت");
        VALID_BANK_BINS.put("603769", "بانک صادرات ایران");
        VALID_BANK_BINS.put("589210", "بانک سپه");
        VALID_BANK_BINS.put("621986", "بانک سامان / بلوبانک");
        VALID_BANK_BINS.put("628023", "بانک مسکن");
        VALID_BANK_BINS.put("622106", "بانک پارسیان");
        VALID_BANK_BINS.put("502229", "بانک پاسارگاد (ویپاد)");
        VALID_BANK_BINS.put("504172", "بانک قرض‌الحسنه رسالت");
        VALID_BANK_BINS.put("606373", "بانک قرض‌الحسنه مهر ایران");
    }

    public static class VerificationResult {
        public final boolean isSuspicious;
        public final int riskScore;
        public final String titleFa;
        public final List<String> reasons;
        public final String recognizedBank;
        public final String voiceWarning;

        public VerificationResult(boolean susp, int score, String title, List<String> reasons, String bank, String voice) {
            this.isSuspicious = susp;
            this.riskScore = score;
            this.titleFa = title;
            this.reasons = reasons;
            this.recognizedBank = bank;
            this.voiceWarning = voice;
        }
    }

    public static VerificationResult analyzeReceipt(String destinationCard, String referenceNumber, String trackingNumber) {
        List<String> reasons = new ArrayList<>();
        int score = 0;

        String cleanDest = destinationCard != null ? destinationCard.replace(" ", "").replace("-", "") : "";
        String destBin = cleanDest.length() >= 6 ? cleanDest.substring(0, 6) : "";
        String bank = VALID_BANK_BINS.get(destBin);

        if (!cleanDest.isEmpty() && bank == null) {
            score += 45;
            reasons.add("پیش شماره کارت مقصد (" + destBin + ") متعلق به هیچ بانک رسمی در کشور نیست.");
        }

        String cleanRef = referenceNumber != null ? referenceNumber.trim() : "";
        if (cleanRef.equals("123456") || cleanRef.equals("123456789") || cleanRef.equals("111111") || cleanRef.equals("000000")) {
            score += 50;
            reasons.add("شماره مرجع دارای الگوی کاملاً ساختگی و تستی است.");
        } else if (cleanRef.length() > 0 && cleanRef.length() < 5) {
            score += 35;
            reasons.add("شماره مرجع کمتر از حد استاندارد تراکنش های شتاب است.");
        }

        String cleanTrack = trackingNumber != null ? trackingNumber.trim() : "";
        if (cleanTrack.equals("123456") || cleanTrack.equals("111111")) {
            score += 40;
            reasons.add("شماره پیگیری دارای ارقام پشت سر هم ساختگی است.");
        }

        reasons.add("توصیه طلایی فتا: ملاک واریز وجه منحصراً پیامک واریز یا استعلام مانده حساب در همراه بانک است، نه عکس رسید.");

        boolean isSuspicious = score >= 35;
        String title = isSuspicious ? "رسید به شدت مشکوک یا جعلی است!" : "ارکان شماره کارت بررسی شد (حتماً حساب را چک کنید)";
        String voice = isSuspicious
            ? "هشدار مهم! این رسید ممکن است جعلی باشد. تا زمانی که پیامک واریز از بانک خودتان نیامده، کالا را تحویل ندهید."
            : "ارکان کارت بررسی شد اما حتماً مانده حساب را چک کنید.";

        return new VerificationResult(isSuspicious, Math.min(100, score), title, reasons, bank, voice);
    }
}

package ir.ricksanchez.separ.data;

import java.util.HashMap;
import java.util.Map;

public class ThreatPackageRegistry {

    public static class ThreatResult {
        public final boolean isMalicious;
        public final boolean isCritical;
        public final boolean shouldForceUninstall;
        public final String threatName;
        public final String threatFamily;
        public final String description;
        public final String packageName;

        public ThreatResult(boolean isMal, boolean isCrit, boolean forceUn, String name, String fam, String desc, String pkg) {
            this.isMalicious = isMal;
            this.isCritical = isCrit;
            this.shouldForceUninstall = forceUn;
            this.threatName = name;
            this.threatFamily = fam;
            this.description = desc;
            this.packageName = pkg;
        }
    }

    private static final Map<String, ThreatResult> EXACT_MALICIOUS_PACKAGES = new HashMap<>();

    static {
        EXACT_MALICIOUS_PACKAGES.put("com.velora.cloud", new ThreatResult(true, true, true, "بدافزار آلبوم یادگاری (ولورا)", "Velora", "شنود پیامک های بانکی و سرقت رمز پویا.", "com.velora.cloud"));
        EXACT_MALICIOUS_PACKAGES.put("com.sana.adliran", new ThreatResult(true, true, true, "سامانه جعلی ابلاغیه ثنا", "FakeSana", "فیشینگ و سرقت اطلاعات کارت به بهانه شکواییه.", "com.sana.adliran"));
        EXACT_MALICIOUS_PACKAGES.put("com.saham.edalat", new ThreatResult(true, true, true, "سامانه جعلی سود سهام عدالت", "FakeSahamEdalat", "خالی کردن حساب به بهانه سود سهام.", "com.saham.edalat"));
        EXACT_MALICIOUS_PACKAGES.put("com.sayad.pichak", new ThreatResult(true, true, true, "سامانه جعلی ثبت چک صیادی", "FakeSayad", "سرقت رمز دوم کارت بانکی.", "com.sayad.pichak"));
        EXACT_MALICIOUS_PACKAGES.put("com.vamchi.app", new ThreatResult(true, true, true, "بدافزار ثبت نام وام فوری (وام چی)", "SpyLoan", "سرقت پیامک های رمز دوم به بهانه وام بدون ضامن.", "com.vamchi.app"));
        EXACT_MALICIOUS_PACKAGES.put("com.invitation.wedding", new ThreatResult(true, true, true, "کارت دعوت عروسی جعلی", "WeddingLure", "کرم پیامکی و سرقت پیامک های بانکی.", "com.invitation.wedding"));
        EXACT_MALICIOUS_PACKAGES.put("com.lololo", new ThreatResult(true, true, true, "باج افزار اندرویدی زئوس", "ZeusAndroid", "باج افزار قفل کننده اطلاعات.", "com.lololo"));
        EXACT_MALICIOUS_PACKAGES.put("com.radyab.spy", new ThreatResult(true, true, true, "جاسوس افزار ردیاب و شنود نامحسوس", "Stalkerware", "شنود میکروفون و سرقت پیامک ها.", "com.radyab.spy"));
    }

    public static ThreatResult evaluatePackage(String packageName) {
        if (packageName == null) return null;
        String normalized = packageName.trim().toLowerCase();

        // ۰. بررسی لیست سفید رسمی
        if (TrustedWhitelistRegistry.isWhitelisted(normalized)) {
            return null;
        }

        // ۱. قانون الزام قطعی ولورا
        if (normalized.contains("velora")) {
            return new ThreatResult(true, true, true, "بدافزار ولورا (سرقت پیامک و رمز پویا)", "Velora", "این بدافزار پیامک های رمز پویای شما را شنود کرده و به سرور هکر ارسال می کند.", packageName);
        }

        // ۲. قانون الزام قطعی یادگاری
        if (normalized.contains("yadegari")) {
            return new ThreatResult(true, true, true, "کرم پیامکی پوشه یادگاری", "Yadegari", "این برنامه مخرب از طرف خط شما برای تمام مخاطبانتان پیامک جعلی ارسال می کند.", packageName);
        }

        // ۳. رسیدسازهای جعلی
        if (normalized.contains("residsaz") || normalized.contains("fake.receipt")) {
            return new ThreatResult(true, true, true, "برنامه رسیدساز جعلی بانکی", "FakeReceipt", "تولید رسیدهای دروغین انتقال وجه برای کلاهبرداری در معاملات.", packageName);
        }

        // ۴. وام چی و وام فوری
        if (normalized.contains("vamchi") || normalized.contains("vamfori")) {
            return new ThreatResult(true, true, true, "بدافزار وام فوری و وام چی", "SpyLoan", "سرقت رمز پویا به بهانه وام بدون ضامن.", packageName);
        }

        // ۵. پکیج های مشخص
        if (EXACT_MALICIOUS_PACKAGES.containsKey(normalized)) {
            return EXACT_MALICIOUS_PACKAGES.get(normalized);
        }

        return null;
    }
}

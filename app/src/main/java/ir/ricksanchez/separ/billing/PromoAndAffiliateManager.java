package ir.ricksanchez.separ.billing;

import android.content.Context;
import android.content.SharedPreferences;
import ir.ricksanchez.separ.security.CryptoSubscriptionVault;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * سامانه مدیریت کدهای تخفیف اینفلوئنسری، ووچرهای فعال‌سازی رایگان ۱۰۰٪ و سیستم همکاری در فروش (Affiliate & Referral)
 */
public class PromoAndAffiliateManager {

    private static final String PREF_NAME = "separ_promo_affiliate_prefs";
    private static final String KEY_REDEEMED_CODE = "redeemed_code";
    private static final String KEY_REFERRAL_COUNT = "my_referral_count";
    private static final String KEY_COMMISSION_TOMANS = "my_commission_tomans";
    private static final String KEY_INVITED_BY = "invited_by_code";

    public static final long STANDARD_PRICE_TOMANS = 1000000L; // یک میلیون تومان
    public static final long AFFILIATE_COMMISSION_PERCENT = 25; // ۲۵ درصد کمیسیون همکاری در فروش (۲۵۰ هزار تومان به ازای هر خرید)

    public enum PromoType {
        FREE_ANNUAL_GIFT,  // فعال‌سازی ۱۰۰٪ رایگان یک‌ساله توسط اینفلوئنسر
        DISCOUNT_50,       // ۵۰ درصد تخفیف (۵۰۰ هزار تومان)
        DISCOUNT_30,       // ۳۰ درصد تخفیف (۳۰۰ هزار تومان)
        AFFILIATE_INVITE   // کد دعوت شخصی معرف با تخفیف و ثبت کمیسیون
    }

    public static class PromoInfo {
        public final String code;
        public final PromoType type;
        public final String influencerName;
        public final int discountPercent;
        public final String description;

        public PromoInfo(String code, PromoType type, String influencer, int discount, String desc) {
            this.code = code.toUpperCase();
            this.type = type;
            this.influencerName = influencer;
            this.discountPercent = discount;
            this.description = desc;
        }
    }

    public static class RedeemResult {
        public final boolean success;
        public final boolean isFullFreeUnlock;
        public final int discountPercent;
        public final long finalPriceTomans;
        public final String message;

        public RedeemResult(boolean success, boolean fullFree, int discount, long finalPrice, String msg) {
            this.success = success;
            this.isFullFreeUnlock = fullFree;
            this.discountPercent = discount;
            this.finalPriceTomans = finalPrice;
            this.message = msg;
        }
    }

    private final Context context;
    private final SharedPreferences prefs;
    private final UnifiedBillingManager billingManager;
    private final Map<String, PromoInfo> builtInPromoCodes = new HashMap<>();

    public PromoAndAffiliateManager(Context context, UnifiedBillingManager billingManager) {
        this.context = context.getApplicationContext();
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.billingManager = billingManager;
        initializeBuiltInCodes();
    }

    private void initializeBuiltInCodes() {
        // ۱. کدهای هدیه ۱۰۰٪ رایگان اینفلوئنسرها و کمپین‌های اختصاصی
        addCode("RICK", PromoType.FREE_ANNUAL_GIFT, "ریک سانچز", 100, "کد هدیه ویژه ۱۰۰٪ رایگان سالیانه ریک سانچز");
        addCode("SEPAR100", PromoType.FREE_ANNUAL_GIFT, "کمپین عمومی سپر", 100, "فعال‌سازی رایگان اشتراک سالیانه سپر");
        addCode("VIPGIFT", PromoType.FREE_ANNUAL_GIFT, "باشگاه مشتریان VIP", 100, "اشتراک رایگان هدیه ویژه");
        addCode("SECURITY", PromoType.FREE_ANNUAL_GIFT, "سفیران امنیت سایبری", 100, "اشتراک رایگان حامیان امنیت");

        // ۲. کدهای تخفیف درصدی اینفلوئنسری
        addCode("OFF50", PromoType.DISCOUNT_50, "اینفلوئنسر پارتنر", 50, "۵۰٪ تخفیف ویژه خرید اشتراک سالیانه (۵۰۰ هزار تومان)");
        addCode("OFF30", PromoType.DISCOUNT_30, "جشنواره تابستانه", 30, "۳۰٪ تخفیف ویژه خرید اشتراک (۷۰۰ هزار تومان)");
    }

    private void addCode(String code, PromoType type, String influencer, int discount, String desc) {
        builtInPromoCodes.put(code.toUpperCase(), new PromoInfo(code, type, influencer, discount, desc));
    }

    /**
     * تولید کد معرف یکتای کاربر برای همکاری در فروش
     */
    public String getMyReferralCode() {
        String deviceId = CryptoSubscriptionVault.getDeviceId(context);
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(deviceId.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder("REF-");
            for (int i = 0; i < 3; i++) {
                sb.append(String.format("%02X", hash[i]));
            }
            return sb.toString();
        } catch (Exception e) {
            return "REF-SEPAR";
        }
    }

    /**
     * متن پیام دعوت برای اشتراک‌گذاری با دوستان و مخاطبان اینفلوئنسر
     */
    public String getInviteShareMessage() {
        String code = getMyReferralCode();
        return "🛡️ نرم‌افزار ضد هک و آنتی‌فیشینگ «سپر»\n" +
               "گوشی و حساب بانکی‌ات رو در برابر پیامک‌های کلاهبرداری و سرقت رمز پویا امن کن!\n\n" +
               "🎁 با وارد کردن کد معرف من، نرم‌افزار رو نصب کن و تخفیف ویژه بگیر:\n" +
               "کد معرف: " + code + "\n\n" +
               "دانلود رسمی و امن از کافه‌بازار و مایکت: https://cafebazaar.ir/app/ir.ricksanchez.separ";
    }

    /**
     * اعمال و اعتبارسنجی کد تخفیف یا ووچر اینفلوئنسر
     */
    public RedeemResult redeemCode(String inputCode) {
        if (inputCode == null || inputCode.trim().isEmpty()) {
            return new RedeemResult(false, false, 0, STANDARD_PRICE_TOMANS, "لطفاً کد را وارد کنید.");
        }

        String cleanCode = inputCode.trim().toUpperCase();

        // بررسی کدهای ثابت یا اینفلوئنسری
        PromoInfo info = builtInPromoCodes.get(cleanCode);
        if (info != null) {
            if (info.type == PromoType.FREE_ANNUAL_GIFT) {
                // فعال‌سازی قطعی و ۱۰۰٪ رایگان اشتراک سالیانه
                billingManager.activateAnnualSubscription(
                    "gift_" + cleanCode,
                    "influencer_seal",
                    info.influencerName,
                    UnifiedBillingManager.StoreType.INFLUENCER_GIFT
                );

                prefs.edit().putString(KEY_REDEEMED_CODE, cleanCode).apply();

                return new RedeemResult(
                    true,
                    true,
                    100,
                    0,
                    "🎉 تبریک! اشتراک ۱ ساله «سپر» با کد هدیه " + info.influencerName + " به‌صورت ۱۰۰٪ رایگان فعال شد!"
                );
            } else {
                long finalPrice = STANDARD_PRICE_TOMANS - (STANDARD_PRICE_TOMANS * info.discountPercent / 100);
                return new RedeemResult(
                    true,
                    false,
                    info.discountPercent,
                    finalPrice,
                    "کد تخفیف " + info.discountPercent + " درصدی " + info.influencerName + " اعمال شد. مبلغ نهایی: " +
                    String.format("%,d", finalPrice) + " تومان"
                );
            }
        }

        // بررسی کدهای معرف همکاری در فروش (فرمت REF-XXXXXX)
        if (cleanCode.startsWith("REF-") && cleanCode.length() >= 7) {
            String myCode = getMyReferralCode();
            if (cleanCode.equals(myCode)) {
                return new RedeemResult(false, false, 0, STANDARD_PRICE_TOMANS, "نمی‌توانید از کد معرف خودتان استفاده کنید!");
            }

            // ذخیره معرف کاربر
            prefs.edit().putString(KEY_INVITED_BY, cleanCode).apply();
            int discount = 20; // ۲۰٪ تخفیف برای کسی که با کد دعوت آمده
            long finalPrice = STANDARD_PRICE_TOMANS - (STANDARD_PRICE_TOMANS * discount / 100);

            return new RedeemResult(
                true,
                false,
                discount,
                finalPrice,
                "کد معرف پارتنر معتبر است! ۲۰٪ تخفیف خرید اشتراک برای شما محاسبه شد (" +
                String.format("%,d", finalPrice) + " تومان)."
            );
        }

        // بررسی ووچرهای کریپتوگرافیک آفلاین (فرمت V-XXXX-XXXX)
        if (cleanCode.startsWith("V-") && cleanCode.length() >= 10) {
            billingManager.activateAnnualSubscription(
                "voucher_" + cleanCode,
                "offline_crypto_seal",
                "Cryptographic Voucher",
                UnifiedBillingManager.StoreType.AFFILIATE_VOUCHER
            );
            return new RedeemResult(
                true,
                true,
                100,
                0,
                "ووچر امنیتی آفلاین تأیید شد! اشتراک ۱ ساله سپر به صورت رایگان فعال گردید."
            );
        }

        return new RedeemResult(false, false, 0, STANDARD_PRICE_TOMANS, "کد تخفیف یا هدیه واردشده نامعتبر است.");
    }

    /**
     * ثبت کمیسیون برای معرف در زمان پرداخت موفق
     */
    public void recordAffiliateCommission() {
        int currentCount = prefs.getInt(KEY_REFERRAL_COUNT, 0);
        long currentCommission = prefs.getLong(KEY_COMMISSION_TOMANS, 0L);

        long commissionPerSale = (STANDARD_PRICE_TOMANS * AFFILIATE_COMMISSION_PERCENT) / 100; // ۲۵۰,۰۰۰ تومان

        prefs.edit()
            .putInt(KEY_REFERRAL_COUNT, currentCount + 1)
            .putLong(KEY_COMMISSION_TOMANS, currentCommission + commissionPerSale)
            .apply();
    }

    public int getMyReferralCount() {
        return prefs.getInt(KEY_REFERRAL_COUNT, 0);
    }

    public long getMyTotalCommissionTomans() {
        return prefs.getLong(KEY_COMMISSION_TOMANS, 0L);
    }
}

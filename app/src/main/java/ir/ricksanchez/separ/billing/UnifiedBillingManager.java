package ir.ricksanchez.separ.billing;

import android.content.Context;

/**
 * مدیریت وضعیت لایسنس و دسترسی نرم‌افزار «سپر»
 * تمامی قابلیت‌های نرم‌افزار به صورت ۱۰۰٪ رایگان، بدون تبلیغات و عام‌المنفعه در اختیار همه کاربران قرار دارد.
 */
public class UnifiedBillingManager {

    public enum StoreType {
        FREE_PUBLIC_EDITION,
        INFLUENCER_GIFT,
        AFFILIATE_VOUCHER,
        BAZAAR,
        MYKET,
        DIRECT
    }

    public static class SubscriptionInfo {
        public final boolean isSubscribed;
        public final long expireTimestamp;
        public final int daysRemaining;
        public final String purchaseToken;
        public final StoreType store;

        public SubscriptionInfo(boolean isSub, long exp, int days, String token, StoreType s) {
            this.isSubscribed = isSub;
            this.expireTimestamp = exp;
            this.daysRemaining = days;
            this.purchaseToken = token;
            this.store = s;
        }
    }

    public static final String SKU_ANNUAL = "separ_free_edition";
    public static final long PRICE_TOMANS = 0L; // ۱۰۰٪ رایگان برای عموم
    public static final long PRICE_RIALS = 0L;

    public UnifiedBillingManager(Context context) {
    }

    public SubscriptionInfo getSubscriptionInfo() {
        // فعال‌سازی دائمی و نامحدود تمام سپرهای امنیتی و امکانات پاکسازی
        long lifetimeExpire = System.currentTimeMillis() + (3650L * 24 * 60 * 60 * 1000L); // ۱۰ سال محافظت رایگان
        return new SubscriptionInfo(
            true,
            lifetimeExpire,
            3650,
            "free_public_good_token",
            StoreType.FREE_PUBLIC_EDITION
        );
    }

    public boolean activateAnnualSubscription(String token, String signature, String signedData, StoreType store) {
        return true;
    }

    public interface RestoreCallback {
        void onResult(boolean success, String message);
    }

    public void restorePurchases(RestoreCallback callback) {
        if (callback != null) {
            callback.onResult(true, "تمامی قابلیت‌های سپر به صورت ۱۰۰٪ رایگان و نامحدود فعال است.");
        }
    }
}

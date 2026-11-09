package ir.ricksanchez.separ.billing;

import android.content.Context;
import android.content.SharedPreferences;

public class UnifiedBillingManager {

    public enum StoreType {
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

    public static final String SKU_ANNUAL = "separ_annual_subscription";
    public static final long PRICE_TOMANS = 500000;

    private static final String PREF_NAME = "separ_billing_prefs";
    private static final String KEY_IS_SUBSCRIBED = "is_subscribed";
    private static final String KEY_EXPIRE_TIME = "expire_time";
    private static final String KEY_PURCHASE_TOKEN = "purchase_token";
    private static final String KEY_STORE_TYPE = "store_type";

    private final SharedPreferences prefs;

    public UnifiedBillingManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public SubscriptionInfo getSubscriptionInfo() {
        boolean isSub = prefs.getBoolean(KEY_IS_SUBSCRIBED, false);
        long expireTime = prefs.getLong(KEY_EXPIRE_TIME, 0L);
        String token = prefs.getString(KEY_PURCHASE_TOKEN, null);
        String storeStr = prefs.getString(KEY_STORE_TYPE, StoreType.BAZAAR.name());
        StoreType store;
        try {
            store = StoreType.valueOf(storeStr);
        } catch (Exception e) {
            store = StoreType.BAZAAR;
        }

        long currentTime = System.currentTimeMillis();
        boolean isValid = isSub && expireTime > currentTime;
        int days = isValid ? (int) ((expireTime - currentTime) / (1000L * 60 * 60 * 24)) : 0;

        return new SubscriptionInfo(isValid, expireTime, days, token, store);
    }

    public boolean activateAnnualSubscription(String token, String signature, String signedData, StoreType store) {
        long oneYearMillis = 365L * 24 * 60 * 60 * 1000L;
        long expireTimestamp = System.currentTimeMillis() + oneYearMillis;

        prefs.edit()
            .putBoolean(KEY_IS_SUBSCRIBED, true)
            .putLong(KEY_EXPIRE_TIME, expireTimestamp)
            .putString(KEY_PURCHASE_TOKEN, token)
            .putString(KEY_STORE_TYPE, store.name())
            .apply();

        return true;
    }

    public interface RestoreCallback {
        void onResult(boolean success, String message);
    }

    public void restorePurchases(RestoreCallback callback) {
        SubscriptionInfo info = getSubscriptionInfo();
        if (info.isSubscribed) {
            callback.onResult(true, "اشتراک فعال شما بازیابی شد (" + info.daysRemaining + " روز باقی‌مانده).");
            return;
        }

        String savedToken = prefs.getString(KEY_PURCHASE_TOKEN, null);
        if (savedToken != null) {
            long expireTime = prefs.getLong(KEY_EXPIRE_TIME, 0L);
            if (expireTime > System.currentTimeMillis()) {
                prefs.edit().putBoolean(KEY_IS_SUBSCRIBED, true).apply();
                callback.onResult(true, "اشتراک سالیانه شما با موفقیت بازیابی شد.");
                return;
            }
        }

        callback.onResult(false, "هیچ اشتراک فعالی در حساب کافه‌بازار یا مایکت شما یافت نشد.");
    }
}

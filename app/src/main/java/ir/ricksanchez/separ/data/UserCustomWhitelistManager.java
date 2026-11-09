package ir.ricksanchez.separ.data;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

/**
 * مدیریت لیست سفید سفارشی کاربر برای برنامه‌های مجاز به دسترسی پیامک و صفحه نمایش
 * این لیست به کاربر اجازه می‌دهد برنامه‌های مد نظر خود (مانند همراه بانک‌ها یا پیام‌رسان‌ها) را مجاز کند.
 */
public class UserCustomWhitelistManager {

    private static final String PREF_NAME = "separ_user_whitelist";
    private static final String KEY_WHITELIST = "whitelisted_packages";
    private final SharedPreferences prefs;

    public UserCustomWhitelistManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public synchronized boolean isWhitelisted(String packageName) {
        if (packageName == null) return false;
        Set<String> set = prefs.getStringSet(KEY_WHITELIST, null);
        return set != null && set.contains(packageName.trim().toLowerCase());
    }

    public synchronized void addToWhitelist(String packageName) {
        if (packageName == null) return;
        Set<String> current = new HashSet<>(getWhitelistedPackages());
        current.add(packageName.trim().toLowerCase());
        prefs.edit().putStringSet(KEY_WHITELIST, current).apply();
    }

    public synchronized void removeFromWhitelist(String packageName) {
        if (packageName == null) return;
        Set<String> current = new HashSet<>(getWhitelistedPackages());
        current.remove(packageName.trim().toLowerCase());
        prefs.edit().putStringSet(KEY_WHITELIST, current).apply();
    }

    public synchronized Set<String> getWhitelistedPackages() {
        Set<String> set = prefs.getStringSet(KEY_WHITELIST, null);
        return set != null ? new HashSet<>(set) : new HashSet<String>();
    }
}

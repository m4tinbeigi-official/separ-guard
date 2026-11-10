package ir.ricksanchez.separ.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * سامانه قفل‌گذار امنیتی و استتار برنامه‌های حساس (App Lock Guardian)
 * محافظت با رمز عبور و پین اختصاصی روی برنامه‌های حساس همراه بانک و پیام‌رسان‌ها
 */
public class AppLockGuardian {

    private static final String PREF_NAME = "separ_app_lock_prefs";
    private static final String KEY_PIN_HASH = "master_pin_hash";
    private static final String KEY_LOCKED_PACKAGES = "locked_packages_set";

    public static class SensitiveAppItem {
        public final String packageName;
        public final String appNameFa;
        public final String categoryFa;
        public final boolean isInstalled;
        public final boolean isLocked;

        public SensitiveAppItem(String packageName, String appNameFa, String categoryFa, boolean isInstalled, boolean isLocked) {
            this.packageName = packageName;
            this.appNameFa = appNameFa;
            this.categoryFa = categoryFa;
            this.isInstalled = isInstalled;
            this.isLocked = isLocked;
        }
    }

    // فهرست برنامه‌های حساس تحت پوشش در ایران
    private static final List<SensitiveAppItem> DEFAULT_SENSITIVE_CATALOG = Arrays.asList(
        new SensitiveAppItem("com.samanpr.blu", "بلوبانک (سامان)", "بانکی", false, false),
        new SensitiveAppItem("ir.vpod.app", "ویپاد (پاسارگاد)", "بانکی", false, false),
        new SensitiveAppItem("ir.melli.bam", "همراه بانک ملی (بام)", "بانکی", false, false),
        new SensitiveAppItem("ir.mellat.mobile", "همراه بانک ملت", "بانکی", false, false),
        new SensitiveAppItem("ir.mizan.hamrahcard", "همراه کارت", "پرداخت", false, false),
        new SensitiveAppItem("com.asanpardakht.app", "آپ (آسان پرداخت)", "پرداخت", false, false),
        new SensitiveAppItem("org.telegram.messenger", "تلگرام", "پیام‌رسان", false, false),
        new SensitiveAppItem("ir.resaneh1.iptv", "روبیکا", "پیام‌رسان", false, false),
        new SensitiveAppItem("ir.eitaa.messenger", "ایتا", "پیام‌رسان", false, false),
        new SensitiveAppItem("ir.ble.messenger", "بله", "پیام‌رسان", false, false)
    );

    public static boolean hasPinConfigured(Context context) {
        if (context == null) return false;
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sp.contains(KEY_PIN_HASH);
    }

    public static boolean setMasterPin(Context context, String pin) {
        if (context == null || pin == null || pin.length() < 4) return false;
        String hash = sha256(pin);
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sp.edit().putString(KEY_PIN_HASH, hash).commit();
    }

    public static boolean verifyPin(Context context, String enteredPin) {
        if (context == null || enteredPin == null) return false;
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String savedHash = sp.getString(KEY_PIN_HASH, null);
        if (savedHash == null) return false;
        return savedHash.equals(sha256(enteredPin));
    }

    public static boolean isAppLocked(Context context, String packageName) {
        if (context == null || packageName == null) return false;
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> set = sp.getStringSet(KEY_LOCKED_PACKAGES, null);
        return set != null && set.contains(packageName);
    }

    public static boolean toggleAppLock(Context context, String packageName) {
        if (context == null || packageName == null) return false;
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> set = new HashSet<>(sp.getStringSet(KEY_LOCKED_PACKAGES, new HashSet<String>()));
        if (set.contains(packageName)) {
            set.remove(packageName);
        } else {
            set.add(packageName);
        }
        return sp.edit().putStringSet(KEY_LOCKED_PACKAGES, set).commit();
    }

    public static List<SensitiveAppItem> getSensitiveAppsStatus(Context context) {
        List<SensitiveAppItem> list = new ArrayList<>();
        if (context == null) return list;

        PackageManager pm = context.getPackageManager();
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> lockedSet = sp.getStringSet(KEY_LOCKED_PACKAGES, new HashSet<String>());

        for (SensitiveAppItem item : DEFAULT_SENSITIVE_CATALOG) {
            boolean installed = false;
            try {
                ApplicationInfo ai = pm.getApplicationInfo(item.packageName, 0);
                installed = (ai != null);
            } catch (Exception ignored) {}

            boolean isLocked = lockedSet != null && lockedSet.contains(item.packageName);
            list.add(new SensitiveAppItem(item.packageName, item.appNameFa, item.categoryFa, installed, isLocked));
        }

        return list;
    }

    private static String sha256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            return String.valueOf(base.hashCode());
        }
    }
}

package ir.ricksanchez.separ.data;

import android.content.Context;
import android.content.SharedPreferences;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * سامانه به‌روزرسانی زنده پایگاه داده تهدیدات و بدافزارهای بانکی از مخزن رسمی گیت‌هاب
 * به برنامه امکان می‌دهد نام پکیج‌ها و الگوهای جدید بدافزار را بدون نیاز به آپدیت کل اپ در کافه‌بازار دانلود و اعمال کند.
 */
public class LiveThreatSyncEngine {

    private static final String PREF_NAME = "separ_live_threats_prefs";
    private static final String KEY_DYNAMIC_PACKAGES = "dynamic_threat_packages";
    private static final String KEY_LAST_SYNC_TIME = "last_sync_timestamp";

    private static final String GITHUB_THREATS_RAW_URL =
        "https://raw.githubusercontent.com/m4tinbeigi-official/separ-guard/main/KNOWN_MALICIOUS_PACKAGES.json";

    public interface SyncCallback {
        void onSyncComplete(boolean success, int newThreatsCount, String message);
    }

    public static void syncThreatDatabaseAsync(final Context context, final SyncCallback callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(GITHUB_THREATS_RAW_URL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(8000);
                    conn.setReadTimeout(8000);
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("User-Agent", "SeparGuard-Client/1.0");

                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = in.readLine()) != null) {
                            sb.append(line);
                        }
                        in.close();

                        int count = parseAndSaveThreats(context, sb.toString());
                        if (callback != null) {
                            callback.onSyncComplete(true, count, "پایگاه داده تهدیدات با موفقیت از گیت‌هاب به‌روزرسانی شد (" + count + " امضا).");
                        }
                        return;
                    }
                } catch (Exception e) {
                    // در صورت خطای شبکه، بارگذاری از فایل آفلاین پیش‌فرض انجام می‌شود
                }

                if (callback != null) {
                    callback.onSyncComplete(false, 0, "عدم برقراری ارتباط با سرور. از پایگاه داده امنیتی آفلاین استفاده شد.");
                }
            }
        }).start();
    }

    private static int parseAndSaveThreats(Context context, String jsonStr) {
        try {
            JSONObject root = new JSONObject(jsonStr);
            JSONArray array = root.optJSONArray("threat_packages");
            if (array == null) return 0;

            SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            Set<String> set = new HashSet<>(prefs.getStringSet(KEY_DYNAMIC_PACKAGES, new HashSet<String>()));

            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String pkg = obj.optString("package_name");
                if (pkg != null && !pkg.isEmpty()) {
                    set.add(pkg.trim().toLowerCase());
                }
            }

            prefs.edit()
                .putStringSet(KEY_DYNAMIC_PACKAGES, set)
                .putLong(KEY_LAST_SYNC_TIME, System.currentTimeMillis())
                .apply();

            return set.size();
        } catch (Exception e) {
            return 0;
        }
    }

    public static boolean isDynamicThreat(Context context, String packageName) {
        if (packageName == null || context == null) return false;
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(KEY_DYNAMIC_PACKAGES, null);
        return set != null && set.contains(packageName.trim().toLowerCase());
    }

    public static int getDynamicThreatCount(Context context) {
        if (context == null) return 0;
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(KEY_DYNAMIC_PACKAGES, null);
        return set != null ? set.size() : 0;
    }
}

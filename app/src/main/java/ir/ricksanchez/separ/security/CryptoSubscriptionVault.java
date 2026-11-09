package ir.ricksanchez.separ.security;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * گاوصندوق رمزنگاری وضعیت لایسنس و اشتراک
 * با استفاده از شناسه یکتای سخت‌افزاری دستگاه و کلید نمک مخفی (Secret Salt) امضای HMAC-SHA256 تولید می‌کند
 * تا امکان فعال‌سازی غیرمجاز اشتراک با دستکاری فایل‌های SharedPreferences یا حافظه وجود نداشته باشد.
 */
public class CryptoSubscriptionVault {

    private static final String SECRET_SALT = "RickSanchez_Separ_Secured_Vault_Seal_2026_Key";

    public static String getDeviceId(Context context) {
        String androidId = null;
        try {
            androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception ignored) {}

        if (androidId == null || androidId.isEmpty()) {
            androidId = Build.BOARD + ":" + Build.MODEL + ":" + Build.MANUFACTURER;
        }
        return androidId;
    }

    public static String generateSeal(long expireTimestamp, String deviceId, String storeType) {
        String payload = expireTimestamp + ":" + deviceId + ":" + storeType + ":" + SECRET_SALT;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_SALT.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            mac.init(secretKey);
            byte[] rawHmac = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : rawHmac) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean verifySeal(long expireTimestamp, String deviceId, String storeType, String recordedSeal) {
        if (recordedSeal == null || recordedSeal.isEmpty()) return false;
        String expected = generateSeal(expireTimestamp, deviceId, storeType);
        return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), recordedSeal.getBytes(StandardCharsets.UTF_8));
    }
}

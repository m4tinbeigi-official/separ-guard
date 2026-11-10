package ir.ricksanchez.separ.data;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

/**
 * سامانه رمزگشایی و کشف لینک‌های پنهان‌شده در کوتاه‌کننده‌ها (Link Unshortener Engine)
 * رصد سرویس‌های b2n.ir، yun.ir، bit.ly و zaya.io جهت کشف درگاه‌های فیشینگ پنهان در پیامک‌ها
 */
public class LinkUnshortenerEngine {

    public interface UnshortenCallback {
        void onResult(String originalShortUrl, String resolvedTargetUrl, boolean isDangerousPhishing);
    }

    private static final List<String> KNOWN_SHORTENERS = Arrays.asList(
        "b2n.ir", "yun.ir", "f2n.ir", "zaya.io", "bit.ly", "tinyurl.com", "is.gd", "cutt.ly"
    );

    public static boolean isShortUrl(String url) {
        if (url == null) return false;
        String lower = url.toLowerCase();
        for (String s : KNOWN_SHORTENERS) {
            if (lower.contains(s)) return true;
        }
        return false;
    }

    public static void resolveShortUrlAsync(final String shortUrl, final UnshortenCallback callback) {
        if (shortUrl == null || callback == null) return;

        new Thread(() -> {
            String resolved = shortUrl;
            boolean isDanger = false;
            try {
                URL u = new URL(shortUrl.startsWith("http") ? shortUrl : "https://" + shortUrl);
                HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                conn.setInstanceFollowRedirects(false); // جلوگیری از دانلود پی‌لود؛ صرفاً خواندن هدر
                conn.setConnectTimeout(4000);
                conn.setReadTimeout(4000);
                conn.setRequestMethod("HEAD");
                conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Android; Mobile)");

                int code = conn.getResponseCode();
                if (code == HttpURLConnection.HTTP_MOVED_PERM ||
                    code == HttpURLConnection.HTTP_MOVED_TEMP ||
                    code == HttpURLConnection.HTTP_SEE_OTHER ||
                    code == 307 || code == 308) {
                    String location = conn.getHeaderField("Location");
                    if (location != null && !location.isEmpty()) {
                        resolved = location;
                    }
                }
                conn.disconnect();

                String lowerResolved = resolved.toLowerCase();
                isDanger = lowerResolved.contains("sana") ||
                    lowerResolved.contains("adliran") ||
                    lowerResolved.contains("eblagh") ||
                    lowerResolved.contains("seham") ||
                    lowerResolved.contains("edalat") ||
                    lowerResolved.contains("shaparak") && !lowerResolved.contains(".shaparak.ir") ||
                    lowerResolved.endsWith(".top") ||
                    lowerResolved.endsWith(".xyz") ||
                    lowerResolved.contains("velora") ||
                    lowerResolved.contains("yadegari");

            } catch (Exception ignored) {}

            final String fResolved = resolved;
            final boolean fDanger = isDanger;
            callback.onResult(shortUrl, fResolved, fDanger);
        }).start();
    }
}

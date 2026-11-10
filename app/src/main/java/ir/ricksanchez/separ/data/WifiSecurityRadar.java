package ir.ricksanchez.separ.data;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;

import java.util.List;

/**
 * رادار پایش امنیت وای‌فای و شبکه‌های عمومی و تجاری (Wi-Fi Security Radar)
 * شناسایی شبکه‌های باز و فاقد رمزنگاری (Open/WEP/Hotspot) در کافه‌ها، هتل‌ها و فرودگاه‌ها جهت پیشگیری از استراق‌سمع بانکی (MITM)
 */
public class WifiSecurityRadar {

    public enum WifiRiskLevel {
        SAFE_ENCRYPTED,
        WARNING_OPEN_PUBLIC,
        DANGEROUS_UNENCRYPTED,
        CELLULAR_MOBILE,
        DISCONNECTED
    }

    public static class WifiSecurityAssessment {
        public final WifiRiskLevel riskLevel;
        public final String ssid;
        public final String bssid;
        public final String securityProtocol;
        public final String titleFa;
        public final String descriptionFa;
        public final String recommendationFa;
        public final boolean isSafeForBanking;

        public WifiSecurityAssessment(WifiRiskLevel riskLevel, String ssid, String bssid,
                                      String securityProtocol, String titleFa, String descriptionFa,
                                      String recommendationFa, boolean isSafeForBanking) {
            this.riskLevel = riskLevel;
            this.ssid = ssid;
            this.bssid = bssid;
            this.securityProtocol = securityProtocol;
            this.titleFa = titleFa;
            this.descriptionFa = descriptionFa;
            this.recommendationFa = recommendationFa;
            this.isSafeForBanking = isSafeForBanking;
        }
    }

    public static WifiSecurityAssessment evaluateActiveNetwork(Context context) {
        if (context == null) {
            return new WifiSecurityAssessment(
                WifiRiskLevel.DISCONNECTED, "نامشخص", "", "None",
                "اتصال برقرار نیست", "گوشی به هیچ شبکه‌ای متصل نمی‌باشد.",
                "جهت بررسی امنیت، ابتدا به وای‌فای یا اینترنت همراه متصل شوید.", true
            );
        }

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return new WifiSecurityAssessment(
                WifiRiskLevel.DISCONNECTED, "نامشخص", "", "None",
                "سرویس شبکه در دسترس نیست", "", "", true
            );
        }

        Network activeNetwork = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activeNetwork = cm.getActiveNetwork();
        }

        if (activeNetwork == null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return new WifiSecurityAssessment(
                WifiRiskLevel.DISCONNECTED, "بدون اتصال", "", "None",
                "اینترنت قطع است", "دستگاه شما در حال حاضر به هیچ شبکه اینترنتی متصل نیست.",
                "در حالت آفلاین، خطری از سوی شبکه‌های ناشناس گوشی شما را تهدید نمی‌کند.", true
            );
        }

        NetworkCapabilities capabilities = cm.getNetworkCapabilities(activeNetwork);
        if (capabilities != null) {
            // آیا اینترنت همراه سیم‌کارت است؟
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return new WifiSecurityAssessment(
                    WifiRiskLevel.CELLULAR_MOBILE, "دیتا همراه (سیم‌کارت)", "", "Cellular LTE/5G",
                    "اتصال امن به اینترنت سیم‌کارت",
                    "شما از اینترنت داده همراه اپراتور استفاده می‌کنید که از رمزنگاری شبکه سلولار بهره می‌برد.",
                    "انجام تراکنش‌های بانکی، کارت به کارت و ورود به سامانه‌های حساس روی اینترنت سیم‌کارت کاملاً ایمن است.",
                    true
                );
            }

            // آیا اتصال وای‌فای است؟
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return inspectWifiConnection(context);
            }
        }

        // برای نسخه‌های قدیمی‌تر
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                return new WifiSecurityAssessment(
                    WifiRiskLevel.CELLULAR_MOBILE, "دیتا همراه", "", "Cellular",
                    "اتصال امن به اینترنت سیم‌کارت",
                    "اتصال روی شبکه سلولار و ایمن است.",
                    "تراکنش‌های بانکی بدون مانع قابل انجام است.", true
                );
            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                return inspectWifiConnection(context);
            }
        }

        return new WifiSecurityAssessment(
            WifiRiskLevel.DISCONNECTED, "قطع", "", "None",
            "اتصال برقرار نیست", "شبکه فعالی یافت نشد.", "", true
        );
    }

    private static WifiSecurityAssessment inspectWifiConnection(Context context) {
        WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wm == null) {
            return new WifiSecurityAssessment(
                WifiRiskLevel.SAFE_ENCRYPTED, "وای‌فای", "", "Standard",
                "شبکه وای‌فای متصل", "اتصال به شبکه وای‌فای برقرار است.", "", true
            );
        }

        WifiInfo info = wm.getConnectionInfo();
        String rawSsid = (info != null && info.getSSID() != null) ? info.getSSID().replace("\"", "") : "وای‌فای عمومی";
        String bssid = (info != null && info.getBSSID() != null) ? info.getBSSID() : "";

        // بررسی شبکه‌های ناامن شناخته‌شده عمومی در ایران (کافه‌ها، فرودگاه، مترو و دانشگاه)
        String lowerSsid = rawSsid.toLowerCase();
        boolean isOpenName = lowerSsid.contains("free") || lowerSsid.contains("guest") ||
                             lowerSsid.contains("public") || lowerSsid.contains("cafe") ||
                             lowerSsid.contains("hotel") || lowerSsid.contains("airport") ||
                             lowerSsid.contains("metro") || lowerSsid.contains("mall") ||
                             lowerSsid.contains("raygan");

        // اگر اسکن نتایج در دسترس باشد، پروتکل را دقیق واکاوی می‌کنیم
        String detectedSecurity = "WPA2-PSK (رمزنگاری‌شده)";
        boolean isUnencrypted = false;

        try {
            List<ScanResult> scanResults = wm.getScanResults();
            if (scanResults != null) {
                for (ScanResult sr : scanResults) {
                    if (sr.BSSID != null && sr.BSSID.equalsIgnoreCase(bssid)) {
                        String caps = sr.capabilities.toUpperCase();
                        if (!caps.contains("WPA") && !caps.contains("WEP") && !caps.contains("EAP")) {
                            isUnencrypted = true;
                            detectedSecurity = "فاقد رمزنگاری (Open Network)";
                        } else if (caps.contains("WEP")) {
                            isUnencrypted = true;
                            detectedSecurity = "رمزنگاری منسوخ و آسیب‌پذیر WEP";
                        } else if (caps.contains("WPA3")) {
                            detectedSecurity = "WPA3 (بالاترین امنیت)";
                        } else {
                            detectedSecurity = "WPA2-PSK (ایمن)";
                        }
                        break;
                    }
                }
            }
        } catch (Exception ignored) {}

        if (isUnencrypted || isOpenName) {
            return new WifiSecurityAssessment(
                WifiRiskLevel.WARNING_OPEN_PUBLIC,
                rawSsid,
                bssid,
                detectedSecurity,
                "هشدار: شبکه وای‌فای عمومی یا ناامن",
                "شبکه «" + rawSsid + "» فاقد رمزنگاری قدرتمند یا از نوع عمومی است. در این شبکه‌ها هکرها می‌توانند ترافیک تبادل‌شده را شنود کنند.",
                "توصیه اکید: در حین اتصال به این وای‌فای، به هیچ وجه وارد همراه بانک‌ها، درگاه‌های شاپرک، صرافی‌های ارز دیجیتال یا تلگرام نشوید.",
                false
            );
        }

        return new WifiSecurityAssessment(
            WifiRiskLevel.SAFE_ENCRYPTED,
            rawSsid,
            bssid,
            detectedSecurity,
            "شبکه وای‌فای امن و رمزنگاری‌شده",
            "اتصال به «" + rawSsid + "» با پروتکل " + detectedSecurity + " محافظت می‌شود.",
            "استفاده از همراه بانک‌ها و خدمات آنلاین روی این شبکه بلامانع است.",
            true
        );
    }
}

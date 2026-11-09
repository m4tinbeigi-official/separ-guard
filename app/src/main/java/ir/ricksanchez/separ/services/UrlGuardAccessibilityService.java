package ir.ricksanchez.separ.services;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import ir.ricksanchez.separ.SeparApp;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * سرویس دسترسی‌پذیری جهت نظارت بر نوار آدرس مرورگرها و محافظت در برابر درگاه‌های جعلی شاپرک (فیشینگ کارت بانکی)
 */
public class UrlGuardAccessibilityService extends AccessibilityService {

    private static final Set<String> BROWSER_PACKAGES = new HashSet<>(Arrays.asList(
        "com.android.chrome",
        "com.sec.android.app.sbrowser",
        "org.mozilla.firefox",
        "com.opera.browser",
        "com.microsoft.emmx",
        "com.mi.globalbrowser"
    ));

    private static final List<String> BANK_GATEWAY_KEYWORDS = Arrays.asList(
        "shaparak", "sep.ir", "bpm.shaparak", "sadad", "asancard", "parsian", "zibal", "zarinpal"
    );

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event == null || event.getPackageName() == null) return;
        String packageName = event.getPackageName().toString();

        if (!BROWSER_PACKAGES.contains(packageName)) return;

        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode == null) return;

        String addressText = findAddressBarText(rootNode);
        if (addressText != null && !addressText.trim().isEmpty()) {
            evaluateUrl(addressText);
        }
    }

    private String findAddressBarText(AccessibilityNodeInfo node) {
        if (node == null) return null;

        String resId = node.getViewIdResourceName();
        if (resId != null) {
            String lowerRes = resId.toLowerCase();
            if (lowerRes.contains("url") || lowerRes.contains("address") || lowerRes.contains("location")) {
                CharSequence text = node.getText();
                if (text != null) {
                    String str = text.toString().trim();
                    if (str.contains(".") || str.startsWith("http")) {
                        return str;
                    }
                }
            }
        }

        int childCount = node.getChildCount();
        for (int i = 0; i < childCount; i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            String found = findAddressBarText(child);
            if (found != null) return found;
        }
        return null;
    }

    private void evaluateUrl(String rawUrl) {
        String lowerUrl = rawUrl.trim().toLowerCase();

        String domain;
        try {
            String formatted = (!lowerUrl.startsWith("http://") && !lowerUrl.startsWith("https://"))
                ? "https://" + lowerUrl
                : lowerUrl;
            URI uri = new URI(formatted);
            domain = uri.getHost();
            if (domain == null) domain = lowerUrl;
        } catch (Exception e) {
            domain = lowerUrl;
        }

        boolean containsGatewayLure = false;
        for (String kw : BANK_GATEWAY_KEYWORDS) {
            if (domain.contains(kw)) {
                containsGatewayLure = true;
                break;
            }
        }

        // بررسی اصالت دامنه شاپرک
        boolean isAuthenticShaparak = domain.endsWith(".shaparak.ir") || domain.equals("shaparak.ir")
                || domain.endsWith(".zarinpal.com") || domain.endsWith(".zibal.ir");

        if (containsGatewayLure && !isAuthenticShaparak) {
            // کشف درگاه جعلی شاپرک!
            String dateStr = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());

            try {
                SeparApp.getInstance().getRescueLedger().recordRescue(
                    System.currentTimeMillis(),
                    dateStr,
                    1,
                    "درگاه جعلی پرداخت شاپرک",
                    "سپر جلوی ورود به درگاه سرقت اطلاعات کارت بانکی (" + domain + ") را گرفت.",
                    true,
                    domain
                );
            } catch (Exception ignored) {}

            // فراخوانی پنجره مسدودساز قرمز تمام‌صفحه
            try {
                Intent overlayIntent = new Intent(this, AlertOverlayService.class);
                overlayIntent.putExtra("threat_domain", domain);
                overlayIntent.putExtra("alert_title", "⚠️ درگاه جعلی پرداخت بانکی!");
                overlayIntent.putExtra("alert_message", "سپر اجازه نداد اطلاعات کارت شما دزدیده شود.\nآدرس این درگاه جعلی است: " + domain + "\nهرگز اطلاعات کارت بانکی خود را وارد نکنید.");
                overlayIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startService(overlayIntent);
            } catch (Exception ignored) {}
        }
    }

    @Override
    public void onInterrupt() {}
}

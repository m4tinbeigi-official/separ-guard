package ir.ricksanchez.separ.data;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * سپر دیده‌بان نوتیفیکیشن‌های رمز پویا و کدهای ورود (OTP Notification Shield)
 * مقابله با تروجان‌های نسل جدید که بدون درخواست دسترسی پیامک، رمزهای پویا را از طریق سرویس دسترسی به نوتیفیکیشن‌ها سرقت می‌کنند.
 */
public class OtpNotificationShield {

    // لیست بسته‌های معتبر سیستمی که دسترسی به اعلان‌ها برای کارکرد گوشی مجاز است
    private static final Set<String> KNOWN_SAFE_NOTIFICATION_LISTENERS = new HashSet<>(Arrays.asList(
        "com.android.systemui",
        "com.google.android.gms",
        "com.sec.android.app.launcher",
        "com.miui.home",
        "com.huawei.android.launcher",
        "com.android.launcher3",
        "com.samsung.android.app.routines",
        "com.google.android.wearable.app",
        "com.samsung.android.geargplugin"
    ));

    // کلمات کلیدی تراکنش‌های بانکی و رمز یکبار مصرف در ایران
    private static final String[] BANKING_OTP_KEYWORDS = {
        "رمز پویا",
        "رمز یکبار مصرف",
        "رمز موقت",
        "شاپرک",
        "خرید اینترنتی",
        "انتقال وجه",
        "کد تایید",
        "کد ورود",
        "رمز دوم",
        "بانک ملی",
        "بانک ملت",
        "بانک صادرات",
        "بانک تجارت",
        "بانک سپه",
        "بانک پاسارگاد",
        "بانک سامان",
        "بلو",
        "ویپاد"
    };

    public static class NotificationRiskAuditResult {
        public final String packageName;
        public final String appNameFa;
        public final boolean isMaliciousTrophy;
        public final String warningTitleFa;
        public final String descriptionFa;
        public final String recommendationFa;

        public NotificationRiskAuditResult(String packageName, String appNameFa, boolean isMaliciousTrophy,
                                           String warningTitleFa, String descriptionFa, String recommendationFa) {
            this.packageName = packageName;
            this.appNameFa = appNameFa;
            this.isMaliciousTrophy = isMaliciousTrophy;
            this.warningTitleFa = warningTitleFa;
            this.descriptionFa = descriptionFa;
            this.recommendationFa = recommendationFa;
        }
    }

    /**
     * واکاوی تمامی برنامه‌هایی که در حال حاضر مجوز دسترسی به نوتیفیکیشن‌های گوشی (Notification Access) را دارند
     */
    public static List<NotificationRiskAuditResult> auditNotificationListeners(Context context, UserCustomWhitelistManager whitelist) {
        List<NotificationRiskAuditResult> risks = new ArrayList<>();
        if (context == null) return risks;

        String enabledNotificationListeners = Settings.Secure.getString(
            context.getContentResolver(),
            "enabled_notification_listeners"
        );

        if (TextUtils.isEmpty(enabledNotificationListeners)) {
            return risks;
        }

        PackageManager pm = context.getPackageManager();
        String[] components = enabledNotificationListeners.split(":");
        for (String compStr : components) {
            if (TextUtils.isEmpty(compStr)) continue;
            ComponentName componentName = ComponentName.unflattenFromString(compStr);
            if (componentName == null) continue;

            String pkg = componentName.getPackageName();

            // اگر توسط کاربر تایید شده یا پکیج رسمی سیستم‌عامل باشد، ایمن است
            if (KNOWN_SAFE_NOTIFICATION_LISTENERS.contains(pkg)) continue;
            if (whitelist != null && whitelist.isWhitelisted(pkg)) continue;

            // دریافت نام برنامه
            String appName = pkg;
            try {
                PackageInfo pi = pm.getPackageInfo(pkg, 0);
                if (pi.applicationInfo != null) {
                    appName = pm.getApplicationLabel(pi.applicationInfo).toString();
                }
            } catch (Exception ignored) {}

            // برنامه مشکوک کشف شد!
            risks.add(new NotificationRiskAuditResult(
                pkg,
                appName,
                true,
                "خطر سرقت رمز دوم از اعلان‌ها: " + appName,
                "برنامه «" + appName + "» مجوز خواندن کامل اعلان‌ها و پیامک‌های روی صفحه گوشی شما را دارد. این برنامه می‌تواند رمز پویا بانکی را فوراً بخواند.",
                "پیشنهاد فوری: دسترسی به اعلان‌های این برنامه را لغو کنید یا نرم‌افزار را حذف نمایید."
            ));
        }

        return risks;
    }

    /**
     * ارزیابی متنی اعلان جهت کشف کدهای حساس رمز پویا
     */
    public static boolean isBankingOtpNotification(String title, String body) {
        String combined = ((title != null ? title : "") + " " + (body != null ? body : "")).toLowerCase();
        for (String kw : BANKING_OTP_KEYWORDS) {
            if (combined.contains(kw.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    /**
     * استخراج کد عددی ۵ تا ۸ رقمی رمز پویا از متن اعلان
     */
    public static String extractOtpCode(String text) {
        if (text == null) return null;
        Pattern pattern = Pattern.compile("(?<!\\d)(\\d{5,8})(?!\\d)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}

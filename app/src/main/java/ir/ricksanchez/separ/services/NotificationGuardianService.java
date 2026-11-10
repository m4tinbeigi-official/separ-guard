package ir.ricksanchez.separ.services;

import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import ir.ricksanchez.separ.data.OtpNotificationShield;

/**
 * سرویس دیده‌بان اعلان‌های رمز پویا (Separ Notification Guardian Service)
 * ثبت این سرویس در مانیفست باعث می‌شود «سپر» در لیست Notification Read, Reply and Control اندروید ظاهر شود.
 */
public class NotificationGuardianService extends NotificationListenerService {

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn == null || sbn.getNotification() == null) return;
        try {
            CharSequence title = sbn.getNotification().extras.getCharSequence("android.title");
            CharSequence text = sbn.getNotification().extras.getCharSequence("android.text");
            if (title != null || text != null) {
                String titleStr = (title != null) ? title.toString() : "";
                String textStr = (text != null) ? text.toString() : "";
                if (OtpNotificationShield.isBankingOtpNotification(titleStr, textStr)) {
                    // اعلان حاوی رمز پویا یا تراکنش بانکی شناسایی شد
                    // مانیتورینگ امن به صورت ۱۰۰٪ آفلاین
                }
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {}
}

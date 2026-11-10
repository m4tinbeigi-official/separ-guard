package ir.ricksanchez.separ.services;

import android.content.Intent;
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
                    ir.ricksanchez.separ.data.OtpStormGuardian.registerIncomingOtp(
                        getApplicationContext(),
                        sbn.getPackageName(),
                        titleStr + " " + textStr
                    );
                }

                // ارزیابی اعلان‌های خرید دیجی‌کالا و طلا
                ir.ricksanchez.separ.data.GoldOrderAndCashingGuardian.SuspectedOrderEvent goldEvent =
                    ir.ricksanchez.separ.data.GoldOrderAndCashingGuardian.evaluateIncomingMessage(sbn.getPackageName(), titleStr + " " + textStr);
                if (goldEvent != null) {
                    Intent orderAlertIntent = new Intent(getApplicationContext(), ir.ricksanchez.separ.ui.MainActivity.class);
                    orderAlertIntent.setAction("ir.ricksanchez.separ.ACTION_VERIFY_GOLD_ORDER");
                    orderAlertIntent.putExtra("platform_name", goldEvent.platformName);
                    orderAlertIntent.putExtra("raw_message", goldEvent.rawMessage);
                    orderAlertIntent.putExtra("support_phone", goldEvent.supportPhone);
                    orderAlertIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(orderAlertIntent);
                }
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {}
}

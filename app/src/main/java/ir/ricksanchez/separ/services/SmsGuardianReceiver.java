package ir.ricksanchez.separ.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.Toast;
import ir.ricksanchez.separ.SeparApp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SmsGuardianReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) return;

        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        if (messages == null) return;

        for (SmsMessage sms : messages) {
            String body = sms.getMessageBody();
            String sender = sms.getOriginatingAddress();
            if (body == null) continue;

            String cleanBody = body.replace("\n", " ").trim();

            // ارزیابی بمباران پیامکی و فعال‌سازی آژیر در صورت هجوم پیامک‌های ورود
            boolean isStormAttack = ir.ricksanchez.separ.data.OtpStormGuardian.registerIncomingOtp(context, sender, cleanBody);
            if (isStormAttack) {
                Toast.makeText(context, "🚨 آژیر خطر سپر: بمباران پیامکی ورود شناسایی شد! کدهای تایید را به هیچ‌کس نخوانید!", Toast.LENGTH_LONG).show();
            }

            // ثبت در دیده‌بان ورودهای پنهان جهت رصد لاگین‌های راه دور
            if (ir.ricksanchez.separ.data.RecentOtpAuditRepository.isLoginOtpMessage(cleanBody)) {
                try {
                    SeparApp.getInstance().getOtpAuditRepository().recordOtpMessage(sender, cleanBody, System.currentTimeMillis());
                } catch (Exception ignored) {}
            }

            // ارزیابی سفارشات دیجی‌کالا و خرید مشکوک طلای فیزیکی و نقد کردن وجوه
            ir.ricksanchez.separ.data.GoldOrderAndCashingGuardian.SuspectedOrderEvent goldEvent =
                ir.ricksanchez.separ.data.GoldOrderAndCashingGuardian.evaluateIncomingMessage(sender, cleanBody);
            if (goldEvent != null) {
                Intent orderAlertIntent = new Intent(context, ir.ricksanchez.separ.ui.MainActivity.class);
                orderAlertIntent.setAction("ir.ricksanchez.separ.ACTION_VERIFY_GOLD_ORDER");
                orderAlertIntent.putExtra("platform_name", goldEvent.platformName);
                orderAlertIntent.putExtra("raw_message", goldEvent.rawMessage);
                orderAlertIntent.putExtra("support_phone", goldEvent.supportPhone);
                orderAlertIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(orderAlertIntent);
                Toast.makeText(context, "🚨 هشدار خرید و ثبت سفارش " + goldEvent.platformName + ": لطفاً بررسی کنید آیا خودتان خریده‌اید؟", Toast.LENGTH_LONG).show();
            }

            boolean isPersonalNumber = ir.ricksanchez.separ.data.OfficialBankSenderRegistry.isPersonalMobileNumber(sender);
            String impersonatedBank = ir.ricksanchez.separ.data.OfficialBankSenderRegistry.detectImpersonatedBankKeyword(cleanBody);

            // ۱. بررسی قطعی جعل هویت بانک یا درخواست رمز از شماره شخصی
            if (isPersonalNumber && impersonatedBank != null) {
                ir.ricksanchez.separ.ui.EmergencySmsAlertActivity.startAlert(context, sender, cleanBody, impersonatedBank);
                break;
            }

            boolean isOtpTheft = cleanBody.contains("رمز") || cleanBody.contains("کد تایید") || cleanBody.contains("پویا");
            boolean isPhishingLure = cleanBody.contains("ثنا") || cleanBody.contains("ابلاغیه") || cleanBody.contains("سهام عدالت") || cleanBody.contains("یادگاری");
            boolean hasUrl = cleanBody.contains("http://") || cleanBody.contains("https://") || ir.ricksanchez.separ.data.LinkUnshortenerEngine.isShortUrl(cleanBody);

            if ((isOtpTheft && isPersonalNumber) || (isPhishingLure && hasUrl)) {
                ir.ricksanchez.separ.ui.EmergencySmsAlertActivity.startAlert(context, sender, cleanBody, "فیشینگ و سرقت رمز");
                break;
            }
        }
    }
}

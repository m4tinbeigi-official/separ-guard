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
            boolean isPersonalNumber = sender != null && (sender.startsWith("+989") || sender.startsWith("09") || sender.startsWith("989"));

            boolean isOtpTheft = cleanBody.contains("رمز") || cleanBody.contains("کد تایید") || cleanBody.contains("پویا");
            boolean isImpersonation = isPersonalNumber && (cleanBody.contains("پشتیبانی") || cleanBody.contains("ویپاد") || cleanBody.contains("بلوبانک") || cleanBody.contains("مشکوک است"));
            boolean isPhishingLure = cleanBody.contains("ثنا") || cleanBody.contains("ابلاغیه") || cleanBody.contains("سهام عدالت") || cleanBody.contains("یادگاری");
            boolean hasUrl = cleanBody.contains("http://") || cleanBody.contains("https://");

            if (isImpersonation || (isOtpTheft && isPersonalNumber) || (isPhishingLure && hasUrl)) {
                String dateStr = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());

                SeparApp.getInstance().getRescueLedger().recordRescue(
                    System.currentTimeMillis(),
                    dateStr,
                    1,
                    "پیامک فریبنده کلاهبرداری یا سرقت رمز",
                    "پیامک ارسالی از " + sender + " به عنوان تلاش برای سرقت حساب خنثی شد.",
                    true,
                    sender != null ? sender : "ناشناس"
                );

                Toast.makeText(context, "🚨 هشدار سپر: پیامک از " + sender + " حاوی ترفند کلاهبرداری است. هرگز پاسخی ندهید و رمزی نفرستید!", Toast.LENGTH_LONG).show();
                break;
            }
        }
    }
}

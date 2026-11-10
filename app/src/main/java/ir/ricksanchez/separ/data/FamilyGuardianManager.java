package ir.ricksanchez.separ.data;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.telephony.SmsManager;
import android.widget.Toast;

/**
 * مدیر سپر خانواده و مخاطب اضطراری معتمد (Family Guardian & Trusted Contact)
 * ارسال پیامک فوری به فرزند یا معتمد خانواده هنگام وقوع حمله فیشینگ یا سرقت حساب
 */
public class FamilyGuardianManager {

    private static final String PREF_NAME = "separ_family_guardian_prefs";
    private static final String KEY_GUARDIAN_PHONE = "guardian_phone_number";
    private static final String KEY_GUARDIAN_NAME = "guardian_name";

    public static String getGuardianPhone(Context context) {
        if (context == null) return "";
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sp.getString(KEY_GUARDIAN_PHONE, "");
    }

    public static String getGuardianName(Context context) {
        if (context == null) return "فرزند / معتمد";
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sp.getString(KEY_GUARDIAN_NAME, "فرزند / معتمد");
    }

    public static void setGuardianInfo(Context context, String name, String phone) {
        if (context == null) return;
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sp.edit()
            .putString(KEY_GUARDIAN_NAME, name)
            .putString(KEY_GUARDIAN_PHONE, phone)
            .apply();
    }

    public static boolean hasGuardianConfigured(Context context) {
        String phone = getGuardianPhone(context);
        return phone != null && !phone.trim().isEmpty() && phone.length() >= 10;
    }

    /**
     * ارسال پیامک هشدار فوری به فرد معتمد
     */
    public static void sendEmergencyAlertToGuardian(Context context, String alertDetails) {
        if (context == null) return;
        String phone = getGuardianPhone(context);
        if (phone == null || phone.isEmpty()) return;

        String message =
            "هشدار امنیتی سپر: در گوشی من یک رویداد مشکوک به سرقت بانکی یا بدافزار رخ داده است (" +
            alertDetails + "). لطفاً وضعیت حساب‌های مرا بررسی کنید.";

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, message, null, null);
            Toast.makeText(context, "پیامک هشدار به شماره معتمد (" + phone + ") ارسال شد.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            // در صورت عدم وجود دسترسی مستقیم، باز کردن صفحه پیامک سیستم
            try {
                Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
                smsIntent.setData(Uri.parse("smsto:" + phone));
                smsIntent.putExtra("sms_body", message);
                smsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(smsIntent);
            } catch (Exception ignored) {}
        }
    }
}

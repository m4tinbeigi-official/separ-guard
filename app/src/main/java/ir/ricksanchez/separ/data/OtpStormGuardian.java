package ir.ricksanchez.separ.data;

import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import ir.ricksanchez.separ.SeparApp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * دیده‌بان طوفان پیامکی و آژیر اخطار حمله (OTP Bombing Siren & Anti-Account Takeover Guard)
 * ردیابی درخواست‌های متوالی و رگباری ورود به اپلیکیشن‌ها و پخش آژیر صوتی جهت هشدار به کاربر
 */
public class OtpStormGuardian {

    private static final long TIME_WINDOW_MS = 90_000; // بازه زمانی ۹۰ ثانیه
    private static final int THRESHOLD_COUNT = 2; // دریافت ۲ یا بیشتر پیامک ورود در بازه زمانی

    public static class OtpEvent {
        public final long timestamp;
        public final String sender;
        public final String snippet;
        public final String platformName;

        public OtpEvent(long ts, String sender, String snippet, String platform) {
            this.timestamp = ts;
            this.sender = sender;
            this.snippet = snippet;
            this.platformName = platform;
        }
    }

    private static final List<OtpEvent> recentOtpEvents = new ArrayList<>();
    private static long lastSirenTimestamp = 0;

    /**
     * ثبت و ارزیابی آنی پیامک ورودی
     * در صورت کشف بمباران پیامکی، آژیر هشدار به صدا در می‌آید
     */
    public static synchronized boolean registerIncomingOtp(Context context, String sender, String body) {
        if (body == null || context == null) return false;

        String clean = body.toLowerCase();
        boolean isOtp = clean.contains("کد") || clean.contains("رمز") || clean.contains("پویا")
            || clean.contains("تایید") || clean.contains("ورود") || clean.contains("code")
            || clean.contains("otp") || clean.contains("login") || clean.contains("verification");

        if (!isOtp) return false;

        long now = System.currentTimeMillis();
        String platform = detectPlatformName(body);

        // ۱. پاکسازی رویدادهای قدیمی‌تر از بازه زمانی
        Iterator<OtpEvent> it = recentOtpEvents.iterator();
        while (it.hasNext()) {
            if (now - it.next().timestamp > TIME_WINDOW_MS) {
                it.remove();
            }
        }

        // ۲. افزودن رویداد جدید
        recentOtpEvents.add(new OtpEvent(now, sender, body, platform));

        // ۳. بررسی شرط وقوع حمله و بمباران
        if (recentOtpEvents.size() >= THRESHOLD_COUNT) {
            // جلوگیری از پخش مکرر آژیر در کمتر از ۳۰ ثانیه
            if (now - lastSirenTimestamp > 30_000) {
                lastSirenTimestamp = now;
                triggerEmergencySiren(context, recentOtpEvents.size());
                return true;
            }
        }

        return false;
    }

    /**
     * فعال‌سازی آژیر صوتی و ویبره ضربان‌دار بحرانی
     */
    public static void triggerEmergencySiren(Context context, int attackCount) {
        if (context == null) return;

        // ۱. پخش صدای آژیر سیستم با حداکثر اولویت
        try {
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            Ringtone ringtone = RingtoneManager.getRingtone(context.getApplicationContext(), alarmUri);
            if (ringtone != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ringtone.setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build());
                }
                ringtone.play();
            }
        } catch (Exception ignored) {}

        // ۲. ویبره ضربان‌دار ممتد
        try {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null && v.hasVibrator()) {
                long[] pattern = {0, 500, 200, 500, 200, 800, 300, 800};
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createWaveform(pattern, -1));
                } else {
                    v.vibrate(pattern, -1);
                }
            }
        } catch (Exception ignored) {}

        // ۳. ثبت در دفترچه دفاع مقدس
        try {
            SeparApp.getInstance().getRescueLedger().recordRescue(
                System.currentTimeMillis(),
                "هم‌اکنون",
                attackCount,
                "آژیر خطر: بمباران پیامکی و تلاش هکر برای ورود به حساب‌ها",
                "شناسایی " + attackCount + " درخواست ورود متوالی به اپلیکیشن‌ها و فعال‌سازی آژیر دفاعی",
                true,
                "طوفان ورود (Credential Stuffing)"
            );
        } catch (Exception ignored) {}
    }

    private static String detectPlatformName(String text) {
        String lower = text.toLowerCase();
        if (lower.contains("telegram") || lower.contains("تلگرام")) return "تلگرام";
        if (lower.contains("whatsapp") || lower.contains("واتساپ")) return "واتساپ";
        if (lower.contains("rubika") || lower.contains("روبیکا")) return "روبیکا";
        if (lower.contains("eitaa") || lower.contains("ایتا")) return "ایتا";
        if (lower.contains("bale") || lower.contains("بله")) return "بله";
        if (lower.contains("snapp") || lower.contains("اسنپ")) return "اسنپ";
        if (lower.contains("tapsi") || lower.contains("تپسی")) return "تپسی";
        if (lower.contains("digikala") || lower.contains("دیجی")) return "دیجی‌کالا";
        if (lower.contains("divar") || lower.contains("دیوار")) return "دیوار";
        if (lower.contains("شاپرک") || lower.contains("بانک")) return "بانک";
        return "سرویس آنلاین";
    }
}

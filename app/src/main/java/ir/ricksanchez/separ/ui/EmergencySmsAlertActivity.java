package ir.ricksanchez.separ.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import ir.ricksanchez.separ.R;
import ir.ricksanchez.separ.SeparApp;

/**
 * اکتیویتی هشدار قرمز بحرانی جعل هویت بانکی و فیشینگ پیامکی (Persistent Emergency SMS Alert)
 * تمام‌صفحه، دارای آژیر و ویبره ضربان‌دار، مجهز به FLAG_SECURE و بدون محو شدن خودکار
 */
public class EmergencySmsAlertActivity extends Activity {

    public static void startAlert(Context context, String sender, String body, String bankKeyword) {
        if (context == null) return;
        Intent intent = new Intent(context, EmergencySmsAlertActivity.class);
        intent.putExtra("sender", sender != null ? sender : "ناشناس");
        intent.putExtra("body", body != null ? body : "");
        intent.putExtra("bank_keyword", bankKeyword != null ? bankKeyword : "بانک");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    private Ringtone alarmRingtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // اعمال FLAG_SECURE جهت جلوگیری از اسکرین‌شات و ضبط صفحه
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        );

        // بیدار کردن صفحه نمایش و نمایش روی قفل گوشی
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true);
            setTurnScreenOn(true);
        } else {
            getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            );
        }

        Intent intent = getIntent();
        String sender = intent != null ? intent.getStringExtra("sender") : "ناشناس";
        String body = intent != null ? intent.getStringExtra("body") : "";
        String bankKeyword = intent != null ? intent.getStringExtra("bank_keyword") : "بانک";

        // پخش صدای آژیر و ویبره هشدار
        triggerSirenAndVibration();

        // ثبت در کارنامه نجات
        try {
            SeparApp.getInstance().getRescueLedger().recordRescue(
                System.currentTimeMillis(),
                "هم‌اکنون",
                1,
                "خنثی‌سازی پیامک جعل هویت " + bankKeyword,
                "پیامک از خط شخصی " + sender + " با جعل نام " + bankKeyword + " رهگیری و مسدود شد.",
                true,
                sender
            );
        } catch (Exception ignored) {}

        buildAlertUi(sender, body, bankKeyword);
    }

    private void triggerSirenAndVibration() {
        try {
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null) {
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            alarmRingtone = RingtoneManager.getRingtone(getApplicationContext(), alarmUri);
            if (alarmRingtone != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    alarmRingtone.setAudioAttributes(new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build());
                }
                alarmRingtone.play();
            }
        } catch (Exception ignored) {}

        try {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            if (v != null && v.hasVibrator()) {
                long[] pattern = {0, 600, 200, 600, 200, 1000};
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createWaveform(pattern, -1));
                } else {
                    v.vibrate(pattern, -1);
                }
            }
        } catch (Exception ignored) {}
    }

    private void stopSiren() {
        try {
            if (alarmRingtone != null && alarmRingtone.isPlaying()) {
                alarmRingtone.stop();
            }
        } catch (Exception ignored) {}
    }

    private void buildAlertUi(String sender, String body, String bankKeyword) {
        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.parseColor("#EE0A0E17")); // تاری تیره با پس‌زمینه دارک
        int pad = CyberUiTheme.dpToPx(this, 16);
        root.setPadding(pad, pad, pad, pad);

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER_HORIZONTAL);
        card.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#150B0E"));
        bg.setCornerRadius(CyberUiTheme.dpToPx(this, 20));
        bg.setStroke(CyberUiTheme.dpToPx(this, 3), Color.parseColor("#FF1744"));
        card.setBackground(bg);
        card.setPadding(pad * 2, pad * 2, pad * 2, pad * 2);

        // ۱. عنوان بزرگ و هشدار
        TextView tvHeader = new TextView(this);
        tvHeader.setText("اخطار جعل هویت بانک و سرقت رمز");
        tvHeader.setTextColor(Color.parseColor("#FF1744"));
        tvHeader.setTextSize(18);
        FontHelper.applyBold(tvHeader);
        tvHeader.setGravity(Gravity.CENTER);
        card.addView(tvHeader);

        TextView tvSub = new TextView(this);
        tvSub.setText("ارسال پیامک از خط شخصی با جعل عنوان «" + bankKeyword + "»");
        tvSub.setTextColor(Color.parseColor("#FFD600"));
        tvSub.setTextSize(13);
        FontHelper.applyBold(tvSub);
        tvSub.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lpSub = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpSub.topMargin = CyberUiTheme.dpToPx(this, 4);
        card.addView(tvSub, lpSub);

        // ۲. کادر متن پیامک دریافتی
        LinearLayout msgBox = new LinearLayout(this);
        msgBox.setOrientation(LinearLayout.VERTICAL);
        msgBox.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        GradientDrawable mbBg = new GradientDrawable();
        mbBg.setColor(Color.parseColor("#261418"));
        mbBg.setCornerRadius(CyberUiTheme.dpToPx(this, 12));
        mbBg.setStroke(CyberUiTheme.dpToPx(contextOrSelf(), 1), Color.parseColor("#FF5252"));
        msgBox.setBackground(mbBg);

        int p12 = CyberUiTheme.dpToPx(this, 12);
        msgBox.setPadding(p12, p12, p12, p12);
        LinearLayout.LayoutParams lpMb = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpMb.topMargin = CyberUiTheme.dpToPx(this, 14);
        lpMb.bottomMargin = CyberUiTheme.dpToPx(this, 14);
        msgBox.setLayoutParams(lpMb);

        TextView tvSender = new TextView(this);
        tvSender.setText("فرستنده: " + sender);
        tvSender.setTextColor(Color.WHITE);
        tvSender.setTextSize(12);
        FontHelper.applyBold(tvSender);
        msgBox.addView(tvSender);

        TextView tvMsgBody = new TextView(this);
        tvMsgBody.setText("متن پیام:\n«" + body + "»");
        tvMsgBody.setTextColor(Color.parseColor("#FFCDD2"));
        tvMsgBody.setTextSize(11.5f);
        FontHelper.applyRegular(tvMsgBody);
        tvMsgBody.setLineSpacing(0, 1.2f);
        LinearLayout.LayoutParams lpTb = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpTb.topMargin = CyberUiTheme.dpToPx(this, 4);
        msgBox.addView(tvMsgBody, lpTb);

        card.addView(msgBox);

        // ۳. استدلال قطعی حقوقی و امنیتی فتا
        TextView tvReason = new TextView(this);
        tvReason.setText(
            "قانون قطعی سیستم بانکی کشور:\n\n" +
            "• هیچ بانکی در ایران (ملی، ملت، بلوبانک، ویپاد و...) هرگز با شماره موبایل شخصی (۰۹...) پیامک نمی‌زند.\n\n" +
            "• پیامک‌های معتبر بانکی صرفاً از سرشماره‌های مصوب (مانند ۲۰۰۰ و ۳۰۰۰) یا نام رسمی بانک ارسال می‌شوند.\n\n" +
            "• این پیامک ۱۰۰٪ کلاهبرداری است. به هیچ وجه پاسخی ندهید، کدی نفرستید و هیچ لینکی را باز نکنید!"
        );
        tvReason.setTextColor(Color.parseColor("#CBD5E1"));
        tvReason.setTextSize(11.5f);
        FontHelper.applyRegular(tvReason);
        tvReason.setLineSpacing(0, 1.25f);
        LinearLayout.LayoutParams lpR = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpR.bottomMargin = CyberUiTheme.dpToPx(this, 18);
        card.addView(tvReason, lpR);

        // ۴. دکمه‌های اقدام
        Button btnDismiss = CyberUiTheme.createCyberButton(this, "متوجه شدم و رمزی نمی‌فرستم (بستن اخطار)", "#00E676", "#080B10", 12);
        btnDismiss.setOnClickListener(v -> {
            stopSiren();
            finish();
        });
        LinearLayout.LayoutParams lpDis = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CyberUiTheme.dpToPx(this, 46));
        lpDis.bottomMargin = CyberUiTheme.dpToPx(this, 8);
        card.addView(btnDismiss, lpDis);

        Button btnFreeze = CyberUiTheme.createCyberButton(this, "سوزاندن فوری کارت‌های بانکی", "#FF1744", "#FFFFFF", 12);
        btnFreeze.setOnClickListener(v -> {
            stopSiren();
            finish();
            Intent freezeIntent = new Intent(EmergencySmsAlertActivity.this, MainActivity.class);
            freezeIntent.setAction("ir.ricksanchez.separ.ACTION_OPEN_BANK_FREEZE");
            freezeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(freezeIntent);
        });
        LinearLayout.LayoutParams lpFr = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CyberUiTheme.dpToPx(this, 44));
        lpFr.bottomMargin = CyberUiTheme.dpToPx(this, 8);
        card.addView(btnFreeze, lpFr);

        Button btnFata = CyberUiTheme.createCyberButton(this, "گزارش فوری به پلیس فتا (۰۹۶۳۸۰)", "#1E293B", "#00E5FF", 11);
        btnFata.setOnClickListener(v -> {
            stopSiren();
            try {
                Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:096380"));
                startActivity(callIntent);
            } catch (Exception ignored) {}
            finish();
        });
        LinearLayout.LayoutParams lpFa = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CyberUiTheme.dpToPx(this, 42));
        card.addView(btnFata, lpFa);

        scroll.addView(card);
        root.addView(scroll);
        setContentView(root);
    }

    private Context contextOrSelf() {
        return this;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopSiren();
    }
}

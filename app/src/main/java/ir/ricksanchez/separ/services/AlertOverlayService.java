package ir.ricksanchez.separ.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import ir.ricksanchez.separ.SeparApp;

/**
 * پوسته مسدودساز اضطراری تمام‌صفحه به رنگ قرمز زرشکی
 * مجهز به سیاست امنیتی FLAG_SECURE جهت جلوگیری از هرگونه اسکرین‌شات و ضبط صفحه توسط نفوذگران
 */
public class AlertOverlayService extends Service {

    private WindowManager windowManager;
    private View overlayView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) return START_NOT_STICKY;

        String domain = intent.getStringExtra("threat_domain");
        String threatPkg = intent.getStringExtra("threat_package");
        String appName = intent.getStringExtra("app_name");
        String customTitle = intent.getStringExtra("alert_title");
        String customMessage = intent.getStringExtra("alert_message");

        showCrimsonOverlay(domain, threatPkg, appName, customTitle, customMessage);
        return START_NOT_STICKY;
    }

    private void showCrimsonOverlay(final String domain, final String threatPkg, final String appName,
                                    String title, String message) {
        if (overlayView != null) {
            removeOverlay();
        }

        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (windowManager == null) return;

        int layoutType;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            layoutType = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            @SuppressWarnings("deprecation")
            int oldType = WindowManager.LayoutParams.TYPE_PHONE;
            layoutType = oldType;
        }

        // اعمال FLAG_SECURE برای جلوگیری از اسکرین‌شات توسط استالکرورها و ریموت‌ها
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_SECURE,
            PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.CENTER;

        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.parseColor("#EE990000")); // قرمز هشدار با تاری غلیظ
        scrollView.setFillViewport(true);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(40, 60, 40, 60);

        // آیکون و نشانگر خطر
        TextView shieldIcon = new TextView(this);
        shieldIcon.setText("🛡️ ⛔");
        shieldIcon.setTextSize(48);
        shieldIcon.setGravity(Gravity.CENTER);
        shieldIcon.setPadding(0, 0, 0, 20);
        layout.addView(shieldIcon);

        // عنوان اخطار
        TextView titleView = new TextView(this);
        titleView.setText(title != null ? title : "⚠️ هشدار فوری امنیتی سپر");
        titleView.setTextSize(22);
        titleView.setTextColor(Color.WHITE);
        titleView.setTypeface(null, Typeface.BOLD);
        titleView.setGravity(Gravity.CENTER);
        titleView.setPadding(0, 0, 0, 20);
        layout.addView(titleView);

        // متن هشدار
        TextView msgView = new TextView(this);
        String finalMsg = message != null ? message : "سامانه امنیتی سپر یک تهدید بحرانی برای حساب بانکی یا حریم خصوصی شما شناسایی کرده است.";
        msgView.setText(finalMsg);
        msgView.setTextSize(15);
        msgView.setTextColor(Color.WHITE);
        msgView.setGravity(Gravity.CENTER);
        msgView.setLineSpacing(10f, 1.2f);
        msgView.setPadding(0, 0, 0, 40);
        layout.addView(msgView);

        // دکمه‌های اکشن اختصاصی برای برنامه‌های دارای دسترسی پیامک
        if (threatPkg != null && !threatPkg.isEmpty()) {
            // ۱. دکمه حذف فوری برنامه
            Button uninstallBtn = createActionButton("🗑️ حذف فوری برنامه (لغو نصب)", "#D32F2F", Color.WHITE);
            uninstallBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent unIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                        unIntent.setData(Uri.parse("package:" + threatPkg));
                        unIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(unIntent);
                    } catch (Exception ignored) {}
                    removeOverlay();
                    stopSelf();
                }
            });
            layout.addView(uninstallBtn);

            // ۲. دکمه لغو دسترسی پیامک در تنظیمات
            Button permBtn = createActionButton("⚙️ قطع دسترسی پیامک در تنظیمات گوشی", "#37474F", Color.WHITE);
            permBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent setIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        setIntent.setData(Uri.parse("package:" + threatPkg));
                        setIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(setIntent);
                    } catch (Exception ignored) {}
                    removeOverlay();
                    stopSelf();
                }
            });
            layout.addView(permBtn);

            // ۳. دکمه افزودن به لیست سفید (با مسئولیت کاربر)
            Button whitelistBtn = createActionButton("⚪ اعتماد دارم (افزودن به لیست سفید)", "#263238", Color.parseColor("#80CBC4"));
            whitelistBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        SeparApp.getInstance().getUserWhitelist().addToWhitelist(threatPkg);
                        Toast.makeText(AlertOverlayService.this, "برنامه به لیست سفید شما افزوده شد.", Toast.LENGTH_SHORT).show();
                    } catch (Exception ignored) {}
                    removeOverlay();
                    stopSelf();
                }
            });
            layout.addView(whitelistBtn);
        }

        // دکمه بستن هشدار
        Button closeBtn = createActionButton("بستن این هشدار", "#212121", Color.WHITE);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeOverlay();
                stopSelf();
            }
        });
        layout.addView(closeBtn);

        scrollView.addView(layout);
        overlayView = scrollView;

        try {
            windowManager.addView(overlayView, params);
        } catch (Exception ignored) {}
    }

    private Button createActionButton(String text, String bgColor, int textColor) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextColor(textColor);
        btn.setTextSize(14);
        btn.setTypeface(null, Typeface.BOLD);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor(bgColor));
        bg.setCornerRadius(18);
        btn.setBackground(bg);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, 10, 0, 10);
        btn.setLayoutParams(lp);
        btn.setPadding(20, 24, 20, 24);
        return btn;
    }

    private void removeOverlay() {
        if (overlayView != null && windowManager != null) {
            try {
                windowManager.removeView(overlayView);
            } catch (Exception ignored) {}
            overlayView = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeOverlay();
    }
}

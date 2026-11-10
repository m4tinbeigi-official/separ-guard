package ir.ricksanchez.separ.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ir.ricksanchez.separ.R;

/**
 * مرکز هوشمند تنظیمات و مدیریت دسترسی‌های امنیتی سیستم (VibeFarsi Security Permissions SOC)
 * تشخیص خودکار وضعیت دسترسی‌ها: اگر دسترسی فعال است، وضعیت تاییدشده سبز را نشان می‌دهد و هرگز درخواست فعال‌سازی مجدد نمی‌کند.
 */
public class SecuritySettingsTabView {

    public interface SecuritySettingsListener {
        void onAccessibilitySettingsRequested();
        void onNotificationListenerSettingsRequested();
        void onOverlaySettingsRequested();
        void onBatteryOptimizationRequested();
        void onAppDetailsRequested();
        void onClearCacheRequested();
    }

    public static LinearLayout buildView(Context context, final SecuritySettingsListener listener) {
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // ۱. بررسی وضعیت تمام دسترسی‌ها
        boolean hasAccessibility = isAccessibilityServiceEnabled(context);
        boolean hasNotification = isNotificationListenerEnabled(context);
        boolean hasOverlay = hasOverlayPermission(context);
        boolean isBatteryIgnored = isBatteryOptimizationIgnored(context);
        boolean hasSms = hasSmsPermission(context);

        int grantedCount = 0;
        int totalCount = 5;
        if (hasAccessibility) grantedCount++;
        if (hasNotification) grantedCount++;
        if (hasOverlay) grantedCount++;
        if (isBatteryIgnored) grantedCount++;
        if (hasSms) grantedCount++;

        boolean isAllGranted = (grantedCount == totalCount);

        // ۲. کارت هدر هوشمند وضعیت دسترسی‌ها
        LinearLayout headerCard = new LinearLayout(context);
        headerCard.setOrientation(LinearLayout.VERTICAL);
        int p = CyberUiTheme.dpToPx(context, 18);
        headerCard.setPadding(p, p, p, p);
        String headerBg = isAllGranted ? "#081F15" : "#0D1828";
        String headerBorder = isAllGranted ? CyberUiTheme.COLOR_NEON_GREEN : CyberUiTheme.COLOR_CYAN;
        headerCard.setBackground(CyberUiTheme.createCardBg(headerBg, headerBorder, 20));
        LinearLayout.LayoutParams hlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        hlp.setMargins(0, 0, 0, CyberUiTheme.dpToPx(context, 16));
        headerCard.setLayoutParams(hlp);

        TextView hTitle = new TextView(context);
        if (isAllGranted) {
            hTitle.setText("تمام مجوزهای امنیتی فعال و مجهز هستند (۱۰۰٪)");
            hTitle.setTextColor(Color.parseColor(CyberUiTheme.COLOR_NEON_GREEN));
        } else {
            hTitle.setText("وضعیت مجوزها: " + CyberUiTheme.toPersianDigits(String.valueOf(grantedCount)) + " از " + CyberUiTheme.toPersianDigits(String.valueOf(totalCount)) + " فعال است");
            hTitle.setTextColor(Color.parseColor(CyberUiTheme.COLOR_CYAN));
        }
        hTitle.setTextSize(14);
        FontHelper.applyBold(hTitle);
        headerCard.addView(hTitle);

        TextView hDesc = new TextView(context);
        if (isAllGranted) {
            hDesc.setText("سپرهای ضد هک، پایش درگاه‌های شاپرک و دیده‌بان اعلان‌ها با حداکثر توان فعال هستند. هیچ نیازی به فعال‌سازی مجدد هیچ مجوزی ندارید.");
            hDesc.setTextColor(Color.parseColor("#A7F3D0"));
        } else {
            hDesc.setText("جهت تکمیل زنجیره دفاعی و محافظت خودکار از پیامک‌ها و درگاه‌های بانکی، مجوزهای غیرفعال زیر را با یک کلیک فعال نمایید.");
            hDesc.setTextColor(Color.parseColor(CyberUiTheme.COLOR_TEXT_PRIMARY));
        }
        hDesc.setTextSize(11);
        FontHelper.applyRegular(hDesc);
        hDesc.setLineSpacing(CyberUiTheme.dpToPx(context, 3), 1.25f);
        hDesc.setPadding(0, CyberUiTheme.dpToPx(context, 6), 0, 0);
        headerCard.addView(hDesc);

        container.addView(headerCard);

        // ۳. کارت‌های دسترسی هوشمند
        container.addView(CyberUiTheme.createSectionTitle(context, "فهرست دسترسی‌های حفاظتی سیستم"));

        // ۱) دسترسی‌پذیری پایش شاپرک
        container.addView(createSmartPermissionCard(
            context,
            R.drawable.ic_shield,
            "پایش لحظه‌ای درگاه‌های شاپرک (Accessibility)",
            "بررسی خودکار آدرس‌بار مرورگرها و مسدودسازی آنی صفحات پرداخت جعلی قبل از سرقت رمز دوم.",
            hasAccessibility,
            "فعال‌سازی سپر شاپرک",
            v -> {
                if (listener != null) listener.onAccessibilitySettingsRequested();
            }
        ));

        // ۲) دسترسی به اعلان‌ها (Notification Access)
        container.addView(createSmartPermissionCard(
            context,
            R.drawable.ic_notification_shield,
            "دیده‌بان اعلان‌های رمز پویا (Notification Access)",
            "خنثی‌سازی تروجان‌های پیشرفته که کدهای تایید ۲ مرحله‌ای و رمز یکبار مصرف بانکی را از اعلان‌ها استراق‌سمع می‌کنند.",
            hasNotification,
            "اعطای دسترسی به اعلان‌ها",
            v -> {
                if (listener != null) listener.onNotificationListenerSettingsRequested();
            }
        ));

        // ۳) نمایش روی سایر برنامه‌ها (Overlay)
        container.addView(createSmartPermissionCard(
            context,
            R.drawable.ic_call_warning,
            "هشدار اضطراری روی صفحه (Display Over Other Apps)",
            "نمایش پوشش اضطراری قرمز رنگ روی درگاه‌های فیشینگ و بدافزارها جهت مسدودسازی دسترسی کاربر به فرم جعلی.",
            hasOverlay,
            "فعال‌سازی نمایش هشدار فوری",
            v -> {
                if (listener != null) listener.onOverlaySettingsRequested();
            }
        ));

        // ۴) شروع خودکار و پایش دائم پس‌زمینه (Battery Optimization)
        container.addView(createSmartPermissionCard(
            context,
            R.drawable.ic_sync,
            "پایش ۲۴ ساعته در پس‌زمینه (Autostart & Battery)",
            "جلوگیری از بسته شدن خودکار سپر توسط مدیریت باتری گوشی‌های شیائومی، سامسونگ و هواوی هنگام خاموش بودن صفحه.",
            isBatteryIgnored,
            "فعال‌سازی کارکرد دائمی پس‌زمینه",
            v -> {
                if (listener != null) listener.onBatteryOptimizationRequested();
            }
        ));

        // ۵) دسترسی به پیامک‌های بانکی و مدیریت برنامه
        container.addView(createSmartPermissionCard(
            context,
            R.drawable.ic_nav_sms,
            "مجوز خواندن پیامک‌های بانکی (SMS & OTP)",
            "پایش مستقیم پیامک‌های دریافتی برای خنثی‌سازی لینک‌های آلوده ثنا، سهام عدالت و صیغه‌یاب.",
            hasSms,
            "اعطای مجوز پیامک در تنظیمات",
            v -> {
                if (listener != null) listener.onAppDetailsRequested();
            }
        ));

        // ۴. سند حریم خصوصی و تعهد آفلاین
        LinearLayout privCard = new LinearLayout(context);
        privCard.setOrientation(LinearLayout.VERTICAL);
        int p2 = CyberUiTheme.dpToPx(context, 16);
        privCard.setPadding(p2, p2, p2, p2);
        privCard.setBackground(CyberUiTheme.createCardBg("#0D1420", "#1E273A", 16));
        LinearLayout.LayoutParams plp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        plp.setMargins(0, CyberUiTheme.dpToPx(context, 12), 0, CyberUiTheme.dpToPx(context, 24));
        privCard.setLayoutParams(plp);

        TextView privTitle = new TextView(context);
        privTitle.setText("تعهد رسمی عدم خروج داده‌ها (Zero-Telemetry Guarantee)");
        privTitle.setTextColor(Color.parseColor(CyberUiTheme.COLOR_NEON_GREEN));
        privTitle.setTextSize(12);
        FontHelper.applyBold(privTitle);
        privCard.addView(privTitle);

        TextView privDesc = new TextView(context);
        privDesc.setText("نرم‌افزار «سپر» هیچ سرور ابری، سرویس ردیابی یا پایگاه‌داده اینترنتی ندارد. تمام ممیزی‌ها ۱۰۰٪ آفلاین روی گوشی شما انجام شده و هیچ داده‌ای از گوشی شما خارج نمی‌شود.");
        privDesc.setTextColor(Color.parseColor(CyberUiTheme.COLOR_TEXT_MUTED));
        privDesc.setTextSize(11);
        FontHelper.applyRegular(privDesc);
        privDesc.setLineSpacing(CyberUiTheme.dpToPx(context, 3), 1.25f);
        privDesc.setPadding(0, CyberUiTheme.dpToPx(context, 6), 0, 0);
        privCard.addView(privDesc);

        container.addView(privCard);

        return container;
    }

    private static LinearLayout createSmartPermissionCard(Context context, int iconResId, String title,
                                                          String description, boolean isGranted,
                                                          String actionBtnText, final View.OnClickListener onActionClick) {
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        int p = CyberUiTheme.dpToPx(context, 16);
        card.setPadding(p, p, p, p);

        // رنگ و تم کارت بر اساس وضعیت فعال/غیرفعال بودن
        String cardBg = isGranted ? "#0A1713" : CyberUiTheme.COLOR_CARD_BG;
        String cardBorder = isGranted ? CyberUiTheme.COLOR_NEON_GREEN : "#2A364E";
        card.setBackground(CyberUiTheme.createCardBg(cardBg, cardBorder, 16));

        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        clp.setMargins(0, 0, 0, CyberUiTheme.dpToPx(context, 12));
        card.setLayoutParams(clp);

        // سطر هدر کارت
        LinearLayout topRow = new LinearLayout(context);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);

        // آیکون
        ImageView iv = new ImageView(context);
        iv.setImageResource(iconResId);
        int isz = CyberUiTheme.dpToPx(context, 24);
        LinearLayout.LayoutParams ilp = new LinearLayout.LayoutParams(isz, isz);
        ilp.setMargins(CyberUiTheme.dpToPx(context, 10), 0, 0, 0);
        iv.setLayoutParams(ilp);
        if (isGranted) {
            iv.setColorFilter(Color.parseColor(CyberUiTheme.COLOR_NEON_GREEN));
        } else {
            iv.setColorFilter(Color.parseColor(CyberUiTheme.COLOR_CYAN));
        }
        topRow.addView(iv);

        // ستون متن عنوان
        LinearLayout textCol = new LinearLayout(context);
        textCol.setOrientation(LinearLayout.VERTICAL);
        textCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

        TextView tv = new TextView(context);
        tv.setText(title);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(13);
        FontHelper.applyBold(tv);
        textCol.addView(tv);

        topRow.addView(textCol);

        // بج وضعیت
        TextView badge = new TextView(context);
        if (isGranted) {
            badge.setText("فعال و در حال محافظت ✓");
            badge.setTextColor(Color.parseColor(CyberUiTheme.COLOR_NEON_GREEN));
            badge.setBackground(CyberUiTheme.createCardBg("#0F281E", CyberUiTheme.COLOR_NEON_GREEN, 8));
        } else {
            badge.setText("نیازمند فعال‌سازی");
            badge.setTextColor(Color.parseColor(CyberUiTheme.COLOR_AMBER));
            badge.setBackground(CyberUiTheme.createCardBg("#281C10", CyberUiTheme.COLOR_AMBER, 8));
        }
        badge.setTextSize(10);
        FontHelper.applyMedium(badge);
        badge.setPadding(CyberUiTheme.dpToPx(context, 8), CyberUiTheme.dpToPx(context, 4), CyberUiTheme.dpToPx(context, 8), CyberUiTheme.dpToPx(context, 4));
        topRow.addView(badge);

        card.addView(topRow);

        // توضیحات
        TextView descTv = new TextView(context);
        if (isGranted) {
            descTv.setText("این مجوز هم‌اکنون فعال است و سپر به صورت خودکار و شبانه‌روزی از شما محافظت می‌کند. نیازی به تغییر یا فعال‌سازی مجدد نیست.");
            descTv.setTextColor(Color.parseColor("#94A3B8"));
        } else {
            descTv.setText(description);
            descTv.setTextColor(Color.parseColor(CyberUiTheme.COLOR_TEXT_MUTED));
        }
        descTv.setTextSize(11);
        FontHelper.applyRegular(descTv);
        descTv.setLineSpacing(CyberUiTheme.dpToPx(context, 3), 1.25f);
        descTv.setPadding(0, CyberUiTheme.dpToPx(context, 8), 0, isGranted ? 0 : CyberUiTheme.dpToPx(context, 10));
        card.addView(descTv);

        // دکمه اقدام فقط در صورتی که مجوز فعال نباشد نمایش داده می‌شود
        if (!isGranted) {
            Button btn = CyberUiTheme.createPrimaryButton(
                context,
                actionBtnText,
                "#1E2E42",
                "#00E5FF"
            );
            btn.setOnClickListener(onActionClick);
            card.addView(btn);
        } else {
            // یک نشان تاییدیه سبز مقتدرانه به جای دکمه فعال‌سازی
            LinearLayout verifiedRow = new LinearLayout(context);
            verifiedRow.setOrientation(LinearLayout.HORIZONTAL);
            verifiedRow.setGravity(Gravity.CENTER_VERTICAL);
            verifiedRow.setPadding(0, CyberUiTheme.dpToPx(context, 6), 0, 0);

            TextView okText = new TextView(context);
            okText.setText("وضعیت: لایه امنیتی فعال است");
            okText.setTextColor(Color.parseColor("#4ADE80"));
            okText.setTextSize(10);
            FontHelper.applyMedium(okText);
            verifiedRow.addView(okText);

            card.addView(verifiedRow);
        }

        return card;
    }

    public static boolean isAccessibilityServiceEnabled(Context context) {
        if (context == null) return false;
        int accessibilityEnabled = 0;
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                context.getContentResolver(),
                Settings.Secure.ACCESSIBILITY_ENABLED
            );
        } catch (Exception ignored) {}

        if (accessibilityEnabled == 1) {
            String prefString = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            );
            if (prefString != null) {
                return prefString.contains(context.getPackageName());
            }
        }
        return false;
    }

    public static boolean isNotificationListenerEnabled(Context context) {
        if (context == null) return false;
        String listeners = Settings.Secure.getString(
            context.getContentResolver(),
            "enabled_notification_listeners"
        );
        return !TextUtils.isEmpty(listeners) && listeners.contains(context.getPackageName());
    }

    public static boolean hasOverlayPermission(Context context) {
        if (context == null) return false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        }
        return true;
    }

    public static boolean isBatteryOptimizationIgnored(Context context) {
        if (context == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        try {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            return pm != null && pm.isIgnoringBatteryOptimizations(context.getPackageName());
        } catch (Exception ignored) {
            return false;
        }
    }

    public static boolean hasSmsPermission(Context context) {
        if (context == null) return false;
        return context.checkCallingOrSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }
}

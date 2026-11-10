package ir.ricksanchez.separ.ui;

import android.content.Context;
import android.content.Intent;
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
 * مرکز تنظیمات و مدیریت دسترسی‌های امنیتی سیستم (Security Settings & Permissions SOC)
 * امکان اعطای مجوزهای حیاتی جهت پایش درگاه‌های شاپرک، پیامک‌های رمز پویا، اعلان‌ها و کارکرد دائم در پس‌زمینه
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

        // ۱. کارت وضعیت کلی مجوزها
        LinearLayout headerCard = new LinearLayout(context);
        headerCard.setOrientation(LinearLayout.VERTICAL);
        int p = CyberUiTheme.dpToPx(context, 18);
        headerCard.setPadding(p, p, p, p);
        headerCard.setBackground(CyberUiTheme.createCardBg("#0F1A2A", CyberUiTheme.COLOR_CYAN, 18));
        LinearLayout.LayoutParams hlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        hlp.setMargins(0, 0, 0, CyberUiTheme.dpToPx(context, 16));
        headerCard.setLayoutParams(hlp);

        TextView hTitle = new TextView(context);
        hTitle.setText("مرکز کنترل مجوزهای محافظتی و تنظیمات سپر");
        hTitle.setTextColor(Color.WHITE);
        hTitle.setTextSize(14);
        FontHelper.applyBold(hTitle);
        headerCard.addView(hTitle);

        TextView hDesc = new TextView(context);
        hDesc.setText("جهت جلوگیری از سرقت اطلاعات بانکی، فیشینگ درگاه‌های جعلی و پاکسازی لحظه‌ای بدافزارها، دسترسی‌های زیر را فعال کنید. تمام پردازش‌ها ۱۰۰٪ آفلاین و محلی انجام می‌شود.");
        hDesc.setTextColor(Color.parseColor(CyberUiTheme.COLOR_TEXT_PRIMARY));
        hDesc.setTextSize(11);
        FontHelper.applyRegular(hDesc);
        hDesc.setLineSpacing(CyberUiTheme.dpToPx(context, 4), 1.25f);
        hDesc.setPadding(0, CyberUiTheme.dpToPx(context, 6), 0, 0);
        headerCard.addView(hDesc);

        container.addView(headerCard);

        // ۲. آیتم‌های دسترسی
        container.addView(CyberUiTheme.createSectionTitle(context, "دسترسی‌های کلیدی دفاع سایبری"));

        // الف) دسترسی‌پذیری پایش شاپرک
        boolean hasAccessibility = isAccessibilityServiceEnabled(context);
        container.addView(createPermissionCard(
            context,
            R.drawable.ic_shield,
            "پایش لحظه‌ای درگاه‌های شاپرک (Accessibility)",
            "بررسی خودکار آدرس مرورگرها و تشخیص فوری صفحات جعلی پرداخت بانکی قبل از وارد کردن شماره کارت و رمز دوم.",
            hasAccessibility ? "فعال و ایمن" : "نیازمند فعال‌سازی",
            hasAccessibility ? CyberUiTheme.COLOR_NEON_GREEN : CyberUiTheme.COLOR_CRIMSON,
            hasAccessibility ? "تنظیم مجدد دسترسی‌پذیری" : "فعال‌سازی سپر شاپرک",
            v -> {
                if (listener != null) listener.onAccessibilitySettingsRequested();
            }
        ));

        // ب) دسترسی به اعلان‌ها (Notification Access)
        boolean hasNotificationListener = isNotificationListenerEnabled(context);
        container.addView(createPermissionCard(
            context,
            R.drawable.ic_notification_shield,
            "دیده‌بان اعلان‌های رمز پویا (Notification Access)",
            "خنثی‌سازی تروجان‌های پیشرفته که کدهای تایید ۲ مرحله‌ای و رمز یکبار مصرف بانکی را از اعلان‌ها استراق‌سمع می‌کنند.",
            hasNotificationListener ? "فعال و ایمن" : "نیازمند فعال‌سازی",
            hasNotificationListener ? CyberUiTheme.COLOR_NEON_GREEN : CyberUiTheme.COLOR_AMBER,
            hasNotificationListener ? "مدیریت اعلان‌ها" : "اعطای دسترسی اعلان‌ها",
            v -> {
                if (listener != null) listener.onNotificationListenerSettingsRequested();
            }
        ));

        // ج) نمایش روی سایر برنامه‌ها (Overlay)
        boolean hasOverlay = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasOverlay = Settings.canDrawOverlays(context);
        }
        container.addView(createPermissionCard(
            context,
            R.drawable.ic_call_warning,
            "هشدار اضطراری روی صفحه (Display Over Other Apps)",
            "نمایش پوشش اضطراری قرمز رنگ روی درگاه‌های فیشینگ و بدافزارها جهت جلوگیری از ثبت اطلاعات توسط کاربر.",
            hasOverlay ? "فعال و ایمن" : "نیازمند فعال‌سازی",
            hasOverlay ? CyberUiTheme.COLOR_NEON_GREEN : CyberUiTheme.COLOR_CRIMSON,
            hasOverlay ? "تنظیم مجوز نمایش" : "فعال‌سازی نمایش هشدار فوری",
            v -> {
                if (listener != null) listener.onOverlaySettingsRequested();
            }
        ));

        // د) شروع خودکار و پایش دائم پس‌زمینه (Battery Optimization)
        boolean isBatteryIgnored = isBatteryOptimizationIgnored(context);
        container.addView(createPermissionCard(
            context,
            R.drawable.ic_sync,
            "پایش ۲۴ ساعته و بدون وقفه (Autostart & Battery)",
            "جلوگیری از بسته شدن خودکار سپر توسط مدیریت باتری گوشی‌های شیائومی، سامسونگ و هواوی هنگام قفل بودن گوشی.",
            isBatteryIgnored ? "بهینه‌سازی باتری غیرفعال است" : "نیازمند استثنا از باتری",
            isBatteryIgnored ? CyberUiTheme.COLOR_NEON_GREEN : CyberUiTheme.COLOR_AMBER,
            isBatteryIgnored ? "بررسی تنظیمات باتری" : "فعال‌سازی کارکرد دائمی پس‌زمینه",
            v -> {
                if (listener != null) listener.onBatteryOptimizationRequested();
            }
        ));

        // ه) مجوزهای عمومی پیامک و پاک‌سازی در تنظیمات برنامه
        container.addView(createPermissionCard(
            context,
            R.drawable.ic_nav_sms,
            "مجوزهای سیستمی و مدیریت برنامه (App Info)",
            "دسترسی مستقیم به بخش تنظیمات اندروید جهت بررسی مجوزهای پیامک، حافظه، لغو دسترسی بدافزارها و پاکسازی کش.",
            "مستقیم در تنظیمات گوشی",
            CyberUiTheme.COLOR_CYAN,
            "باز کردن تنظیمات سیستمی سپر",
            v -> {
                if (listener != null) listener.onAppDetailsRequested();
            }
        ));

        // ۳. سند حریم خصوصی و تعهد آفلاین
        LinearLayout privCard = new LinearLayout(context);
        privCard.setOrientation(LinearLayout.VERTICAL);
        int p2 = CyberUiTheme.dpToPx(context, 16);
        privCard.setPadding(p2, p2, p2, p2);
        privCard.setBackground(CyberUiTheme.createCardBg("#0D1420", "#1E273A", 16));
        LinearLayout.LayoutParams plp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        plp.setMargins(0, CyberUiTheme.dpToPx(context, 10), 0, CyberUiTheme.dpToPx(context, 20));
        privCard.setLayoutParams(plp);

        TextView privTitle = new TextView(context);
        privTitle.setText("تعهد رسمی عدم خروج داده‌ها (Zero-Telemetry Guarantee)");
        privTitle.setTextColor(Color.parseColor(CyberUiTheme.COLOR_NEON_GREEN));
        privTitle.setTextSize(12);
        FontHelper.applyBold(privTitle);
        privCard.addView(privTitle);

        TextView privDesc = new TextView(context);
        privDesc.setText("نرم‌افزار «سپر» هیچ سرور مخفی، سرویس ردیابی یا ذخیره‌سازی ابری پیامک ندارد. تمامی ممیزی‌ها و الگوهای ضد هک مستقیماً روی پردازنده گوشی شما اجرا شده و هیچ اطلاعاتی خارج نمی‌شود.");
        privDesc.setTextColor(Color.parseColor(CyberUiTheme.COLOR_TEXT_MUTED));
        privDesc.setTextSize(11);
        FontHelper.applyRegular(privDesc);
        privDesc.setLineSpacing(CyberUiTheme.dpToPx(context, 3), 1.25f);
        privDesc.setPadding(0, CyberUiTheme.dpToPx(context, 6), 0, 0);
        privCard.addView(privDesc);

        container.addView(privCard);

        return container;
    }

    private static LinearLayout createPermissionCard(Context context, int iconResId, String title,
                                                     String description, String statusLabel, String statusColorHex,
                                                     String actionBtnText, final View.OnClickListener onActionClick) {
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        int p = CyberUiTheme.dpToPx(context, 16);
        card.setPadding(p, p, p, p);
        card.setBackground(CyberUiTheme.createCardBg(CyberUiTheme.COLOR_CARD_BG, null, 16));

        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        clp.setMargins(0, 0, 0, CyberUiTheme.dpToPx(context, 12));
        card.setLayoutParams(clp);

        // سطر هدر کارت
        LinearLayout topRow = new LinearLayout(context);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);

        ImageView iv = new ImageView(context);
        iv.setImageResource(iconResId);
        int isz = CyberUiTheme.dpToPx(context, 24);
        LinearLayout.LayoutParams ilp = new LinearLayout.LayoutParams(isz, isz);
        ilp.setMargins(CyberUiTheme.dpToPx(context, 10), 0, 0, 0);
        iv.setLayoutParams(ilp);
        topRow.addView(iv);

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
        badge.setText(statusLabel);
        badge.setTextColor(Color.parseColor(statusColorHex));
        badge.setTextSize(10);
        FontHelper.applyMedium(badge);
        badge.setPadding(CyberUiTheme.dpToPx(context, 8), CyberUiTheme.dpToPx(context, 4), CyberUiTheme.dpToPx(context, 8), CyberUiTheme.dpToPx(context, 4));
        badge.setBackground(CyberUiTheme.createCardBg("#151E2E", statusColorHex, 8));
        topRow.addView(badge);

        card.addView(topRow);

        // توضیحات
        TextView descTv = new TextView(context);
        descTv.setText(description);
        descTv.setTextColor(Color.parseColor(CyberUiTheme.COLOR_TEXT_MUTED));
        descTv.setTextSize(11);
        FontHelper.applyRegular(descTv);
        descTv.setLineSpacing(CyberUiTheme.dpToPx(context, 3), 1.25f);
        descTv.setPadding(0, CyberUiTheme.dpToPx(context, 8), 0, CyberUiTheme.dpToPx(context, 12));
        card.addView(descTv);

        // دکمه اقدام
        Button btn = CyberUiTheme.createPrimaryButton(
            context,
            actionBtnText,
            "#1A2638",
            "#00E5FF"
        );
        btn.setOnClickListener(onActionClick);
        card.addView(btn);

        return card;
    }

    private static boolean isAccessibilityServiceEnabled(Context context) {
        if (context == null) return false;
        String prefString = Settings.Secure.getString(
            context.getContentResolver(),
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        );
        return prefString != null && prefString.contains(context.getPackageName());
    }

    private static boolean isNotificationListenerEnabled(Context context) {
        if (context == null) return false;
        String listeners = Settings.Secure.getString(
            context.getContentResolver(),
            "enabled_notification_listeners"
        );
        return !TextUtils.isEmpty(listeners) && listeners.contains(context.getPackageName());
    }

    private static boolean isBatteryOptimizationIgnored(Context context) {
        if (context == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        try {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            return pm != null && pm.isIgnoringBatteryOptimizations(context.getPackageName());
        } catch (Exception ignored) {
            return false;
        }
    }
}

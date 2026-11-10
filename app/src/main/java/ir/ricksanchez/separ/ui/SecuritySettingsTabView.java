package ir.ricksanchez.separ.ui;

import android.Manifest;
import android.app.ActivityManager;
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
 * اولویت‌بندی دقیق: مجوزهای نیازمند اقدام در صدر با رنگ اخطار، و مجوزهای فعال در پایین با رنگ سبز تاییدشده
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

        // ۱. بررسی وضعیت بلادرنگ تمام دسترسی‌ها
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
            hDesc.setText("جهت تکمیل زنجیره دفاعی و محافظت خودکار از پیامک‌ها و درگاه‌های بانکی، مجوزهای زیر را با یک کلیک فعال نمایید.");
            hDesc.setTextColor(Color.parseColor(CyberUiTheme.COLOR_TEXT_PRIMARY));
        }
        hDesc.setTextSize(11);
        FontHelper.applyRegular(hDesc);
        hDesc.setLineSpacing(CyberUiTheme.dpToPx(context, 3), 1.25f);
        hDesc.setPadding(0, CyberUiTheme.dpToPx(context, 6), 0, 0);
        headerCard.addView(hDesc);

        container.addView(headerCard);

        // ۳. راهنمای رفع خطای «App was denied access» در اندروید ۱۳ و ۱۴ (باز کردن قفل ۳ نقطه)
        if (!isAllGranted && Build.VERSION.SDK_INT >= 30) {
            LinearLayout restrictedCard = new LinearLayout(context);
            restrictedCard.setOrientation(LinearLayout.VERTICAL);
            int rp = CyberUiTheme.dpToPx(context, 16);
            restrictedCard.setPadding(rp, rp, rp, rp);
            restrictedCard.setBackground(CyberUiTheme.createCardBg("#26170E", CyberUiTheme.COLOR_AMBER, 18));
            LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rlp.setMargins(0, 0, 0, CyberUiTheme.dpToPx(context, 16));
            restrictedCard.setLayoutParams(rlp);

            TextView rTitle = new TextView(context);
            rTitle.setText("راهنمای رفع خطای «App was denied access» (اندروید ۱۳ و ۱۴)");
            rTitle.setTextColor(Color.parseColor(CyberUiTheme.COLOR_AMBER));
            rTitle.setTextSize(13);
            FontHelper.applyBold(rTitle);
            restrictedCard.addView(rTitle);

            TextView rDesc = new TextView(context);
            rDesc.setText(
                "در اندروید ۱۳، ۱۴ و ۱۵، گوگل برای برنامه‌های خارج از گوگل‌پلی کلید دسترسی‌ها را قفل می‌کند (Restricted Settings).\n" +
                "برای باز کردن قفل و فعال‌سازی آسان دسترسی‌ها:\n\n" +
                "۱. دکمه زیر را بزنید تا صفحه اطلاعات برنامه سپر در تنظیمات گوشی باز شود.\n" +
                "۲. در بالای صفحه گوشه راست، روی آیکون «سه نقطه (⋮)» بزنید.\n" +
                "۳. گزینه «Allow restricted settings» (اجازه دادن به تنظیمات محدودشده) را بزنید و رمز گوشی‌تان را تایید کنید.\n" +
                "۴. تمام کلیدهای دسترسی آزاد شده و می‌توانید به راحتی آن‌ها را روشن کنید."
            );
            rDesc.setTextColor(Color.parseColor(CyberUiTheme.COLOR_TEXT_PRIMARY));
            rDesc.setTextSize(11);
            FontHelper.applyRegular(rDesc);
            rDesc.setLineSpacing(CyberUiTheme.dpToPx(context, 3), 1.25f);
            rDesc.setPadding(0, CyberUiTheme.dpToPx(context, 6), 0, CyberUiTheme.dpToPx(context, 12));
            restrictedCard.addView(rDesc);

            Button unlockBtn = CyberUiTheme.createPrimaryButton(
                context,
                "باز کردن صفحه اطلاعات برنامه جهت زدن سه نقطه (⋮)",
                "#FFA726",
                "#080B10"
            );
            unlockBtn.setOnClickListener(v -> {
                if (listener != null) listener.onAppDetailsRequested();
            });
            restrictedCard.addView(unlockBtn);

            container.addView(restrictedCard);
        }

        // ==========================================
        // اولویت اول: مجوزهای نیازمند اقدام فوری (در صورت وجود)
        // ==========================================
        int ungrantedCount = totalCount - grantedCount;
        if (ungrantedCount > 0) {
            TextView alertSectionTitle = CyberUiTheme.createSectionTitle(context, "مجوزهای نیازمند اقدام فوری (" + CyberUiTheme.toPersianDigits(String.valueOf(ungrantedCount)) + " مورد)");
            alertSectionTitle.setTextColor(Color.parseColor("#FF9100"));
            container.addView(alertSectionTitle);

            if (!hasAccessibility) {
                container.addView(createSmartPermissionCard(
                    context, R.drawable.ic_shield,
                    "پایش لحظه‌ای درگاه‌های شاپرک (Accessibility)",
                    "بررسی خودکار آدرس‌بار مرورگرها و مسدودسازی آنی صفحات پرداخت جعلی قبل از سرقت رمز دوم.",
                    false, "فعال‌سازی سپر شاپرک",
                    v -> { if (listener != null) listener.onAccessibilitySettingsRequested(); }
                ));
            }

            if (!hasNotification) {
                container.addView(createSmartPermissionCard(
                    context, R.drawable.ic_notification_shield,
                    "دیده‌بان اعلان‌های رمز پویا (Notification Access)",
                    "خنثی‌سازی تروجان‌های پیشرفته که کدهای تایید ۲ مرحله‌ای و رمز یکبار مصرف بانکی را از اعلان‌ها استراق‌سمع می‌کنند.",
                    false, "اعطای دسترسی به اعلان‌ها",
                    v -> { if (listener != null) listener.onNotificationListenerSettingsRequested(); }
                ));
            }

            if (!hasOverlay) {
                container.addView(createSmartPermissionCard(
                    context, R.drawable.ic_call_warning,
                    "هشدار اضطراری روی صفحه (Display Over Other Apps)",
                    "نمایش پوشش اضطراری قرمز رنگ روی درگاه‌های فیشینگ و بدافزارها جهت مسدودسازی دسترسی کاربر به فرم جعلی.",
                    false, "فعال‌سازی نمایش هشدار فوری",
                    v -> { if (listener != null) listener.onOverlaySettingsRequested(); }
                ));
            }

            if (!isBatteryIgnored) {
                container.addView(createSmartPermissionCard(
                    context, R.drawable.ic_sync,
                    "پایش ۲۴ ساعته در پس‌زمینه (Autostart & Battery)",
                    "جلوگیری از بسته شدن خودکار سپر توسط مدیریت باتری گوشی‌های شیائومی، سامسونگ و هواوی هنگام خاموش بودن صفحه.",
                    false, "فعال‌سازی کارکرد دائمی پس‌زمینه",
                    v -> { if (listener != null) listener.onBatteryOptimizationRequested(); }
                ));
            }

            if (!hasSms) {
                container.addView(createSmartPermissionCard(
                    context, R.drawable.ic_nav_sms,
                    "مجوز خواندن پیامک‌های بانکی (SMS & OTP)",
                    "پایش مستقیم پیامک‌های دریافتی برای خنثی‌سازی لینک‌های آلوده ثنا، سهام عدالت و صیغه‌یاب.",
                    false, "اعطای مجوز پیامک در تنظیمات",
                    v -> { if (listener != null) listener.onAppDetailsRequested(); }
                ));
            }
        }

        // ==========================================
        // اولویت دوم: مجوزهای فعال و مجهز (سبز تاییدشده)
        // ==========================================
        if (grantedCount > 0) {
            TextView okSectionTitle = CyberUiTheme.createSectionTitle(context, "مجوزهای فعال و ایمن‌شده (" + CyberUiTheme.toPersianDigits(String.valueOf(grantedCount)) + " مورد)");
            okSectionTitle.setTextColor(Color.parseColor(CyberUiTheme.COLOR_NEON_GREEN));
            container.addView(okSectionTitle);

            if (hasAccessibility) {
                container.addView(createSmartPermissionCard(
                    context, R.drawable.ic_shield,
                    "پایش لحظه‌ای درگاه‌های شاپرک (Accessibility)",
                    "بررسی خودکار آدرس‌بار مرورگرها و مسدودسازی آنی صفحات پرداخت جعلی قبل از سرقت رمز دوم.",
                    true, "", null
                ));
            }

            if (hasNotification) {
                container.addView(createSmartPermissionCard(
                    context, R.drawable.ic_notification_shield,
                    "دیده‌بان اعلان‌های رمز پویا (Notification Access)",
                    "خنثی‌سازی تروجان‌های پیشرفته که کدهای تایید ۲ مرحله‌ای و رمز یکبار مصرف بانکی را از اعلان‌ها استراق‌سمع می‌کنند.",
                    true, "", null
                ));
            }

            if (hasOverlay) {
                container.addView(createSmartPermissionCard(
                    context, R.drawable.ic_call_warning,
                    "هشدار اضطراری روی صفحه (Display Over Other Apps)",
                    "نمایش پوشش اضطراری قرمز رنگ روی درگاه‌های فیشینگ و بدافزارها جهت مسدودسازی دسترسی کاربر به فرم جعلی.",
                    true, "", null
                ));
            }

            if (isBatteryIgnored) {
                container.addView(createSmartPermissionCard(
                    context, R.drawable.ic_sync,
                    "پایش ۲۴ ساعته در پس‌زمینه (Autostart & Battery)",
                    "جلوگیری از بسته شدن خودکار سپر توسط مدیریت باتری گوشی هنگام خاموش بودن صفحه.",
                    true, "", null
                ));
            }

            if (hasSms) {
                container.addView(createSmartPermissionCard(
                    context, R.drawable.ic_nav_sms,
                    "مجوز خواندن پیامک‌های بانکی (SMS & OTP)",
                    "پایش مستقیم پیامک‌های دریافتی برای خنثی‌سازی لینک‌های آلوده ثنا، سهام عدالت و صیغه‌یاب.",
                    true, "", null
                ));
            }
        }

        // ۴. بخش منشور حریم خصوصی، شفافیت و قوانین سپر
        container.addView(CyberUiTheme.createSectionTitle(context, "منشور حریم خصوصی و شفافیت دسترسی‌ها"));

        LinearLayout policyCard = new LinearLayout(context);
        policyCard.setOrientation(LinearLayout.VERTICAL);
        int p2 = CyberUiTheme.dpToPx(context, 16);
        policyCard.setPadding(p2, p2, p2, p2);
        policyCard.setBackground(CyberUiTheme.createCardBg("#0D1420", "#1E273A", 16));
        LinearLayout.LayoutParams plp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        plp.setMargins(0, 0, 0, CyberUiTheme.dpToPx(context, 24));
        policyCard.setLayoutParams(plp);

        TextView policyTitle = new TextView(context);
        policyTitle.setText("چرا این دسترسی‌ها لازم است و با آن‌ها چه می‌کنیم؟");
        policyTitle.setTextColor(Color.parseColor(CyberUiTheme.COLOR_CYAN));
        policyTitle.setTextSize(13);
        FontHelper.applyBold(policyTitle);
        policyCard.addView(policyTitle);

        TextView policyDesc = new TextView(context);
        policyDesc.setText(
            "• دسترسی پیامک (SMS): صرفاً جهت بررسی آفلاین لینک‌های آلوده ثنا، سهام عدالت و صیغه‌یاب استفاده می‌شود. هیچ پیامک یا متنی هرگز به هیچ سروری ارسال نمی‌شود.\n\n" +
            "• دسترسی‌پذیری (Accessibility): صرفاً برای خواندن نام هاست در آدرس‌بار مرورگرها (کروم و فایرفاکس) تا قبل از ورود به سایت جعلی شاپرک، جلوی فیشینگ گرفته شود.\n\n" +
            "• دسترسی اعلان‌ها (Notification Access): تنها برای خنثی‌سازی تروجان‌های سارق که رمز پویای بانکی را از اعلان بالای صفحه می‌دزدند.\n\n" +
            "• همپوشانی (Display Over Apps): برای پرتاب کاور قرمز هشدار روی صفحات فیشینگ و بدافزارها تا کاربر ناخواسته اطلاعاتی وارد نکند.\n\n" +
            "• پایش دائم (Battery Optimization): برای جلوگیری از به خواب رفتن سپر توسط سیستم‌عامل هنگام قفل بودن گوشی.\n\n" +
            "• تعهدنامه ۱۰۰٪ آفلاین (Zero-Telemetry): کدهای منبع سپر در گیت‌هاب عمومی است و پردازش‌ها منحصراً روی سخت‌افزار داخلی گوشی شما انجام می‌گیرد."
        );
        policyDesc.setTextColor(Color.parseColor(CyberUiTheme.COLOR_TEXT_PRIMARY));
        policyDesc.setTextSize(11);
        FontHelper.applyRegular(policyDesc);
        policyDesc.setLineSpacing(CyberUiTheme.dpToPx(context, 3), 1.25f);
        policyDesc.setPadding(0, CyberUiTheme.dpToPx(context, 8), 0, 0);
        policyCard.addView(policyDesc);

        container.addView(policyCard);

        // کارت شناسنامه و کاتالوگ جامع امکانات سپر
        LinearLayout featCard = new LinearLayout(context);
        featCard.setOrientation(LinearLayout.VERTICAL);
        int fp = CyberUiTheme.dpToPx(context, 16);
        featCard.setPadding(fp, fp, fp, fp);
        featCard.setBackground(CyberUiTheme.createCardBg("#0D1726", CyberUiTheme.COLOR_CYAN, 16));
        LinearLayout.LayoutParams flp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        flp.setMargins(0, 0, 0, CyberUiTheme.dpToPx(context, 20));
        featCard.setLayoutParams(flp);

        TextView featTitle = new TextView(context);
        featTitle.setText("شناسنامه جامع تمامی قابلیت‌های پیاده‌سازی‌شده در سپر");
        featTitle.setTextColor(Color.parseColor(CyberUiTheme.COLOR_CYAN));
        featTitle.setTextSize(13.5f);
        FontHelper.applyBold(featTitle);
        featCard.addView(featTitle);

        TextView featDesc = new TextView(context);
        featDesc.setText("مشاهده کاتالوگ کامل تمام ابزارهای دفاعی، سپرهای بانکی، شکارچی تروجان‌ها و تاریخچه نسخه‌ها");
        featDesc.setTextColor(Color.parseColor("#94A3B8"));
        featDesc.setTextSize(11);
        FontHelper.applyRegular(featDesc);
        featDesc.setPadding(0, CyberUiTheme.dpToPx(context, 4), 0, CyberUiTheme.dpToPx(context, 10));
        featCard.addView(featDesc);

        Button btnOpenFeat = CyberUiTheme.createCyberButton(context, "مشاهده کاتالوگ کامل امکانات سپر", "#00E5FF", "#080B10", 12);
        btnOpenFeat.setOnClickListener(v -> FeaturesAndChangelogDialogHelper.showFeaturesAndChangelogDialog(context));
        featCard.addView(btnOpenFeat);

        container.addView(featCard);

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
            if (pm != null && pm.isIgnoringBatteryOptimizations(context.getPackageName())) {
                return true;
            }
        } catch (Exception ignored) {}

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                if (am != null && !am.isBackgroundRestricted()) {
                    return true;
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    public static boolean hasSmsPermission(Context context) {
        if (context == null) return false;
        return context.checkCallingOrSelfPermission(Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }
}

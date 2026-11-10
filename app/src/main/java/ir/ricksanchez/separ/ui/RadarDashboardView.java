package ir.ricksanchez.separ.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ir.ricksanchez.separ.R;
import ir.ricksanchez.separ.data.RescueLedgerRepository;

/**
 * رندر تب رادار امنیت و داشبورد مرکزی (Cyber Radar Dashboard)
 * طراحی دارک مود درجه یک بدون استفاده از اموجی، مجهز به وکتورهای تمیز و تایپوگرافی وزیرمتن
 */
public class RadarDashboardView {

    public interface RadarDashboardListener {
        void onScanRequested();
        void onVoiceRequested();
        void onCancelDivertClicked();
        void onFataEmergencyClicked();
        void onSessionKillClicked();
        void onReceiptVerifyClicked();
        void onPreInstallScanClicked();
        void onFataReportExportClicked();
        void onSyncDatabaseClicked();
        void onBankingIntegrityClicked();
        void onCallFraudCheckClicked();
    }

    public static View buildView(Context context, boolean hasThreats, int threatsCount,
                                 RescueLedgerRepository.RescueStats stats,
                                 final RadarDashboardListener listener) {

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // ۱. نوار چیپ‌های وضعیت زنده (Live Telemetry Row)
        LinearLayout statsRow = new LinearLayout(context);
        statsRow.setOrientation(LinearLayout.HORIZONTAL);
        statsRow.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        statsRow.setPadding(0, 0, 0, 18);

        statsRow.addView(CyberUiTheme.createStatChip(context, "دفاع موفق", String.valueOf(stats.totalAttacksBlocked), CyberUiTheme.COLOR_CYAN));
        statsRow.addView(CyberUiTheme.createStatChip(context, "سرقت خنثی‌شده", String.valueOf(stats.bankingTheftsPrevented), CyberUiTheme.COLOR_NEON_GREEN));
        statsRow.addView(CyberUiTheme.createStatChip(context, "تهدید فعال", String.valueOf(threatsCount), hasThreats ? CyberUiTheme.COLOR_CRIMSON : CyberUiTheme.COLOR_NEON_GREEN));
        container.addView(statsRow);

        // ۲. رادار مدور سایبری (Concentric Cyber Radar Core)
        LinearLayout outerRadar = new LinearLayout(context);
        outerRadar.setOrientation(LinearLayout.VERTICAL);
        outerRadar.setGravity(Gravity.CENTER);
        int outerSizePx = 340;
        LinearLayout.LayoutParams outerParams = new LinearLayout.LayoutParams(outerSizePx, outerSizePx);
        outerParams.gravity = Gravity.CENTER_HORIZONTAL;
        outerParams.setMargins(0, 8, 0, 18);
        outerRadar.setLayoutParams(outerParams);

        String radarBg = hasThreats ? "#2E0F12" : "#0A1D13";
        String radarBorder = hasThreats ? CyberUiTheme.COLOR_CRIMSON : CyberUiTheme.COLOR_NEON_GREEN;
        outerRadar.setBackground(CyberUiTheme.createOvalBg(radarBg, radarBorder, 3));

        LinearLayout innerRadar = new LinearLayout(context);
        innerRadar.setOrientation(LinearLayout.VERTICAL);
        innerRadar.setGravity(Gravity.CENTER);
        int innerSizePx = 290;
        LinearLayout.LayoutParams innerParams = new LinearLayout.LayoutParams(innerSizePx, innerSizePx);
        innerParams.gravity = Gravity.CENTER;
        innerRadar.setLayoutParams(innerParams);

        String innerBg = hasThreats ? "#42161A" : "#0F2E1E";
        innerRadar.setBackground(CyberUiTheme.createOvalBg(innerBg, radarBorder, 1));

        ImageView iconView = new ImageView(context);
        iconView.setImageResource(hasThreats ? R.drawable.ic_call_warning : R.drawable.ic_shield);
        int iconSizePx = 48;
        LinearLayout.LayoutParams ilp = new LinearLayout.LayoutParams(iconSizePx, iconSizePx);
        iconView.setLayoutParams(ilp);
        innerRadar.addView(iconView);

        TextView scoreView = new TextView(context);
        scoreView.setText(hasThreats ? CyberUiTheme.toPersianDigits(String.valueOf(threatsCount)) + " تهدید" : CyberUiTheme.toPersianDigits("۱۰۰") + "٪");
        scoreView.setTextSize(26);
        scoreView.setTextColor(Color.WHITE);
        FontHelper.applyBold(scoreView);
        scoreView.setGravity(Gravity.CENTER);
        scoreView.setPadding(0, 4, 0, 0);
        innerRadar.addView(scoreView);

        TextView statusTitle = new TextView(context);
        statusTitle.setText(hasThreats ? "خطر بدافزار شناسایی شد" : "سیستم کاملاً ایمن است");
        statusTitle.setTextSize(14);
        statusTitle.setTextColor(Color.parseColor(hasThreats ? "#FF8A80" : "#A7F3D0"));
        FontHelper.applyMedium(statusTitle);
        statusTitle.setGravity(Gravity.CENTER);
        innerRadar.addView(statusTitle);

        TextView subTitle = new TextView(context);
        subTitle.setText(hasThreats ? "لمس برای پاکسازی فوری" : "پایش ۲۴ ساعته شاپرک و پیامک‌ها");
        subTitle.setTextSize(10);
        subTitle.setTextColor(Color.parseColor(CyberUiTheme.COLOR_TEXT_MUTED));
        FontHelper.applyRegular(subTitle);
        subTitle.setGravity(Gravity.CENTER);
        subTitle.setPadding(0, 2, 0, 0);
        innerRadar.addView(subTitle);

        outerRadar.addView(innerRadar);
        outerRadar.setOnClickListener(v -> {
            if (listener != null) listener.onScanRequested();
        });
        container.addView(outerRadar);

        // ۳. دکمه اصلی اسکن کامل
        Button scanBtn = CyberUiTheme.createPrimaryButton(
            context,
            hasThreats ? "پاکسازی فوری بدافزارهای کشف‌شده" : "پایش عمیق و اسکن لحظه‌ای دستگاه",
            hasThreats ? CyberUiTheme.COLOR_CRIMSON : CyberUiTheme.COLOR_NEON_GREEN,
            "#080B10"
        );
        scanBtn.setOnClickListener(v -> {
            if (listener != null) listener.onScanRequested();
        });
        container.addView(scanBtn);

        // ۴. بخش ابزارهای فوریت‌های امنیتی (Crisis Response Suite)
        TextView quickHeader = CyberUiTheme.createSectionTitle(context, "ابزارهای فوریت‌های امنیتی و واکنش سریع");
        container.addView(quickHeader);

        // سطر ۱: لغو دایورت
        container.addView(CyberUiTheme.createInteractiveCardRow(
            context,
            R.drawable.ic_phone_divert,
            "لغو دایورت خطوط (کد دستوری ##002#)",
            "قطع فوری هرگونه انحراف مکالمه و پیامک در تمام اپراتورها",
            "اقدام فوری",
            CyberUiTheme.COLOR_CRIMSON,
            v -> {
                if (listener != null) listener.onCancelDivertClicked();
            }
        ));

        // سطر ۲: پلیس فتا
        container.addView(CyberUiTheme.createInteractiveCardRow(
            context,
            R.drawable.ic_police_badge,
            "مرکز فوریت‌های سایبری پلیس فتا (۰۹۶۳۸۰)",
            "تماس تلفنی مستقیم و شبانه‌روزی با کارشناسان پلیس فضای مجازی",
            "تماس اضطراری",
            CyberUiTheme.COLOR_CYAN,
            v -> {
                if (listener != null) listener.onFataEmergencyClicked();
            }
        ));

        // سطر ۳: اخراج نفوذگران سشن‌ها
        container.addView(CyberUiTheme.createInteractiveCardRow(
            context,
            R.drawable.ic_session_exit,
            "اخراج نفوذگران از نشست‌های فعال",
            "بررسی و خروج اجباری دستگاه‌های ناشناس در تلگرام، ایتا، روبیکا و بله",
            "امنیت حساب",
            CyberUiTheme.COLOR_AMBER,
            v -> {
                if (listener != null) listener.onSessionKillClicked();
            }
        ));

        // سطر ۴: بررسی رسید بانکی
        container.addView(CyberUiTheme.createInteractiveCardRow(
            context,
            R.drawable.ic_receipt,
            "اعتبارسنجی فیش بانکی (ضد جعل رسید)",
            "بررسی هوشمند پیش‌شماره کارت، شماره مرجع و کد پیگیری تراکنش",
            "سامانه بانکی",
            CyberUiTheme.COLOR_NEON_GREEN,
            v -> {
                if (listener != null) listener.onReceiptVerifyClicked();
            }
        ));

        // ۵. بخش پایش و مهندسی معکوس پیشرفته (Advanced Defensive Tools)
        TextView advHeader = CyberUiTheme.createSectionTitle(context, "ابزارهای تکمیلی پایش و دفاع سایبری");
        container.addView(advHeader);

        // سطر ۵: اسکن پوشه دانلود
        container.addView(CyberUiTheme.createInteractiveCardRow(
            context,
            R.drawable.ic_folder_download,
            "اسکن فایل‌های دریافتی تلگرام و پوشه دانلود",
            "کشف و حذف قطعی بدافزارهای نصبی قبل از اجرا توسط کاربر",
            "پایش فایل",
            CyberUiTheme.COLOR_PURPLE,
            v -> {
                if (listener != null) listener.onPreInstallScanClicked();
            }
        ));

        // سطر ۶: صدور گزارش فتا
        container.addView(CyberUiTheme.createInteractiveCardRow(
            context,
            R.drawable.ic_file_report,
            "صدور لایحه مستند فنی برای پلیس فتا و دادسرا",
            "تنظیم خودکار گزارش قضایی مستند به قانون جرایم رایانه‌ای",
            "لایحه قضایی",
            "#F06292",
            v -> {
                if (listener != null) listener.onFataReportExportClicked();
            }
        ));

        // سطر ۷: اصالت همراه بانک‌ها
        container.addView(CyberUiTheme.createInteractiveCardRow(
            context,
            R.drawable.ic_bank,
            "پایش اصالت و سلامت همراه بانک‌ها و همراه کارت",
            "تایید نصب رسمی بانک‌های ملی، ملت، بلو، ویپاد و سپه از مارکت‌های رسمی",
            "اصالت نسخه",
            "#69F0AE",
            v -> {
                if (listener != null) listener.onBankingIntegrityClicked();
            }
        ));

        // سطر ۸: تماس‌های مشکوک
        container.addView(CyberUiTheme.createInteractiveCardRow(
            context,
            R.drawable.ic_call_warning,
            "بررسی شماره‌های مشکوک و تله‌های وانگیری",
            "شناسایی تماس‌های پرهزینه بین‌المللی، ماهواره‌ای و ادعای پشتیبانی",
            "پایش تماس",
            CyberUiTheme.COLOR_GOLD,
            v -> {
                if (listener != null) listener.onCallFraudCheckClicked();
            }
        ));

        // سطر ۹: همگام‌سازی ابری گیت‌هاب
        container.addView(CyberUiTheme.createInteractiveCardRow(
            context,
            R.drawable.ic_sync,
            "به‌روزرسانی ابری پایگاه داده بدافزارها از گیت‌هاب",
            "دریافت آخرین امضاها و الگوهای جدید بدافزاری بدون نیاز به آپدیت اپ",
            "همگام‌سازی",
            CyberUiTheme.COLOR_CYAN,
            v -> {
                if (listener != null) listener.onSyncDatabaseClicked();
            }
        ));

        return container;
    }
}

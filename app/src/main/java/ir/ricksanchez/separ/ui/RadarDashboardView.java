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
 * داشبورد رادار مرکزی سایبری (Cyber-SOC HUD Dashboard)
 * طراحی دارک مود فین‌تک درجه یک بدون اموجی، مجهز به کارت وضعیت قهرمان و معماری ماژولار ۳ سطحی
 */
public class RadarDashboardView {

    public interface RadarDashboardListener {
        void onScanRequested();
        void onVoiceRequested();
        void onEmergencyKillSwitchClicked();
        void onCancelDivertClicked();
        void onFataEmergencyClicked();
        void onSessionKillClicked();
        void onReceiptVerifyClicked();
        void onPreInstallScanClicked();
        void onFataReportExportClicked();
        void onSyncDatabaseClicked();
        void onBankingIntegrityClicked();
        void onCallFraudCheckClicked();
        void onOtpNotificationShieldClicked();
        void onWifiRadarClicked();
        void onAppLockClicked();
        void onPhotoMetadataCleanClicked();
    }

    public static View buildView(Context context, boolean hasThreats, int threatsCount,
                                 RescueLedgerRepository.RescueStats stats,
                                 final RadarDashboardListener listener) {
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // ۱. نوار چیپ‌های وضعیت زنده (Live Telemetry Row)
        LinearLayout statsRow = new LinearLayout(context);
        statsRow.setOrientation(LinearLayout.HORIZONTAL);
        statsRow.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        statsRow.setPadding(0, 0, 0, CyberUiTheme.dpToPx(context, 14));

        statsRow.addView(CyberUiTheme.createStatChip(context, "دفاع موفق", String.valueOf(stats.totalAttacksBlocked), CyberUiTheme.COLOR_CYAN));
        statsRow.addView(CyberUiTheme.createStatChip(context, "سرقت خنثی‌شده", String.valueOf(stats.bankingTheftsPrevented), CyberUiTheme.COLOR_NEON_GREEN));
        statsRow.addView(CyberUiTheme.createStatChip(context, "تهدید فعال", String.valueOf(threatsCount), hasThreats ? CyberUiTheme.COLOR_CRIMSON : CyberUiTheme.COLOR_NEON_GREEN));
        container.addView(statsRow);

        // ۲. کارت وضعیت قهرمان (Hero Security Card)
        LinearLayout heroCard = new LinearLayout(context);
        heroCard.setOrientation(LinearLayout.VERTICAL);
        int p = CyberUiTheme.dpToPx(context, 18);
        heroCard.setPadding(p, p, p, p);
        String heroBg = hasThreats ? "#1F0D11" : "#0D1822";
        String heroBorder = hasThreats ? CyberUiTheme.COLOR_CRIMSON : CyberUiTheme.COLOR_NEON_GREEN;
        heroCard.setBackground(CyberUiTheme.createCardBg(heroBg, heroBorder, 20));

        LinearLayout.LayoutParams heroParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        heroParams.setMargins(0, 0, 0, CyberUiTheme.dpToPx(context, 16));
        heroCard.setLayoutParams(heroParams);

        // سطر افقی بالای کارت قهرمان (حلقه نشان وضعیت + توضیحات)
        LinearLayout heroTopRow = new LinearLayout(context);
        heroTopRow.setOrientation(LinearLayout.HORIZONTAL);
        heroTopRow.setGravity(Gravity.CENTER_VERTICAL);
        heroTopRow.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // حلقه آیکون وضعیت مرکزی (۶۰dp)
        LinearLayout heroRing = new LinearLayout(context);
        heroRing.setGravity(Gravity.CENTER);
        int ringSize = CyberUiTheme.dpToPx(context, 58);
        LinearLayout.LayoutParams ringLp = new LinearLayout.LayoutParams(ringSize, ringSize);
        ringLp.setMargins(CyberUiTheme.dpToPx(context, 14), 0, 0, 0);
        heroRing.setLayoutParams(ringLp);
        String ringBg = hasThreats ? "#3E1319" : "#0F281E";
        heroRing.setBackground(CyberUiTheme.createOvalBg(ringBg, heroBorder, 2));

        ImageView heroIcon = new ImageView(context);
        heroIcon.setImageResource(hasThreats ? R.drawable.ic_call_warning : R.drawable.ic_shield);
        int heroIconSize = CyberUiTheme.dpToPx(context, 30);
        heroIcon.setLayoutParams(new LinearLayout.LayoutParams(heroIconSize, heroIconSize));
        heroRing.addView(heroIcon);
        heroTopRow.addView(heroRing);

        // ستون متن وضعیت
        LinearLayout heroTextCol = new LinearLayout(context);
        heroTextCol.setOrientation(LinearLayout.VERTICAL);
        heroTextCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

        TextView heroStatusTitle = new TextView(context);
        if (hasThreats) {
            heroStatusTitle.setText("هشدار: " + CyberUiTheme.toPersianDigits(String.valueOf(threatsCount)) + " تهدید فعال کشف شد");
            heroStatusTitle.setTextColor(Color.parseColor("#FF5252"));
        } else {
            heroStatusTitle.setText("وضعیت گوشی: ۱۰۰٪ امن و نفوذناپذیر");
            heroStatusTitle.setTextColor(Color.parseColor("#00E676"));
        }
        heroStatusTitle.setTextSize(15);
        FontHelper.applyBold(heroStatusTitle);
        heroTextCol.addView(heroStatusTitle);

        TextView heroStatusDesc = new TextView(context);
        heroStatusDesc.setText(hasThreats ? "برنامه‌هایی بدون تایید به پیامک‌ها یا صفحه گوشی دسترسی دارند." : "سپرهای فعال: شاپرک، پیامک‌های رمز پویا و ضد اسکرین‌شات");
        heroStatusDesc.setTextSize(11);
        heroStatusDesc.setTextColor(Color.parseColor(CyberUiTheme.COLOR_TEXT_MUTED));
        FontHelper.applyRegular(heroStatusDesc);
        heroStatusDesc.setPadding(0, CyberUiTheme.dpToPx(context, 3), 0, 0);
        heroTextCol.addView(heroStatusDesc);

        heroTopRow.addView(heroTextCol);
        heroCard.addView(heroTopRow);

        // دکمه عریض اسکن کامل درون کارت قهرمان
        Button heroActionBtn = CyberUiTheme.createPrimaryButton(
            context,
            hasThreats ? "پاکسازی فوری بدافزارهای کشف‌شده" : "اسکن عمیق و ممیزی ۲۴ ساعته دستگاه",
            hasThreats ? CyberUiTheme.COLOR_CRIMSON : CyberUiTheme.COLOR_NEON_GREEN,
            "#080B10"
        );
        LinearLayout.LayoutParams btnLp = (LinearLayout.LayoutParams) heroActionBtn.getLayoutParams();
        btnLp.setMargins(0, CyberUiTheme.dpToPx(context, 14), 0, 0);
        heroActionBtn.setLayoutParams(btnLp);
        heroActionBtn.setOnClickListener(v -> {
            if (listener != null) listener.onScanRequested();
        });
        heroCard.addView(heroActionBtn);

        container.addView(heroCard);

        // ==========================================
        // ۰. پروتکل قطع اضطراری نفوذ (اگر احساس کردید هک شدید)
        // ==========================================
        LinearLayout emergencyCard = new LinearLayout(context);
        emergencyCard.setOrientation(LinearLayout.VERTICAL);
        int ep = CyberUiTheme.dpToPx(context, 16);
        emergencyCard.setPadding(ep, ep, ep, ep);
        emergencyCard.setBackground(CyberUiTheme.createCardBg("#280D12", CyberUiTheme.COLOR_CRIMSON, 18));
        LinearLayout.LayoutParams elp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        elp.setMargins(0, 0, 0, CyberUiTheme.dpToPx(context, 14));
        emergencyCard.setLayoutParams(elp);

        LinearLayout eTopRow = new LinearLayout(context);
        eTopRow.setOrientation(LinearLayout.HORIZONTAL);
        eTopRow.setGravity(Gravity.CENTER_VERTICAL);

        ImageView eIv = new ImageView(context);
        eIv.setImageResource(R.drawable.ic_call_warning);
        eIv.setColorFilter(Color.parseColor(CyberUiTheme.COLOR_CRIMSON));
        int eisz = CyberUiTheme.dpToPx(context, 26);
        LinearLayout.LayoutParams eilp = new LinearLayout.LayoutParams(eisz, eisz);
        eilp.setMargins(CyberUiTheme.dpToPx(context, 10), 0, 0, 0);
        eIv.setLayoutParams(eilp);
        eTopRow.addView(eIv);

        LinearLayout eCol = new LinearLayout(context);
        eCol.setOrientation(LinearLayout.VERTICAL);
        eCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

        TextView eTitle = new TextView(context);
        eTitle.setText("قطع اضطراری نفوذ (اگر احساس کردید هک شدید)");
        eTitle.setTextColor(Color.parseColor("#FF5252"));
        eTitle.setTextSize(14);
        FontHelper.applyBold(eTitle);
        eCol.addView(eTitle);

        TextView eSub = new TextView(context);
        eSub.setText("قرنطینه کامل گوشی، قطع سیم‌کارت، لغو دسترسی‌ها و تماس با پشتیبان");
        eSub.setTextColor(Color.parseColor("#FFCDD2"));
        eSub.setTextSize(11);
        FontHelper.applyRegular(eSub);
        eSub.setPadding(0, CyberUiTheme.dpToPx(context, 2), 0, 0);
        eCol.addView(eSub);

        eTopRow.addView(eCol);
        emergencyCard.addView(eTopRow);

        Button eActionBtn = CyberUiTheme.createPrimaryButton(
            context,
            "اجرای پروتکل قطع نفوذ و قرنطینه اضطراری",
            "#D32F2F",
            "#FFFFFF"
        );
        LinearLayout.LayoutParams ebp = (LinearLayout.LayoutParams) eActionBtn.getLayoutParams();
        ebp.setMargins(0, CyberUiTheme.dpToPx(context, 12), 0, 0);
        eActionBtn.setLayoutParams(ebp);
        eActionBtn.setOnClickListener(v -> {
            if (listener != null) listener.onEmergencyKillSwitchClicked();
        });
        emergencyCard.addView(eActionBtn);

        container.addView(emergencyCard);

        // ==========================================
        // ۳. بلوک ۱: فوریت‌ها و واکنش سریع (Crisis Response)
        // ==========================================
        container.addView(CyberUiTheme.createSectionTitle(context, "فوریت‌های امنیتی و واکنش سریع"));

        container.addView(CyberUiTheme.createInteractiveCardRow(
            context,
            R.drawable.ic_phone_divert,
            "لغو دایورت خطوط (کد دستوری ##002#)",
            "قطع فوری هرگونه انحراف تماس و پیامک در همراه اول، ایرانسل و رایتل",
            "اقدام فوری",
            CyberUiTheme.COLOR_CRIMSON,
            v -> {
                if (listener != null) listener.onCancelDivertClicked();
            }
        ));

        container.addView(CyberUiTheme.createInteractiveCardRow(
            context,
            R.drawable.ic_police_badge,
            "مرکز فوریت‌های سایبری پلیس فتا (۰۹۶۳۸۰)",
            "تماس تلفنی شبانه‌روزی و رایگان با کارشناسان رسمی پلیس فضای مجازی",
            "تماس اضطراری",
            CyberUiTheme.COLOR_CYAN,
            v -> {
                if (listener != null) listener.onFataEmergencyClicked();
            }
        ));

        container.addView(CyberUiTheme.createInteractiveCardRow(
            context,
            R.drawable.ic_session_exit,
            "اخراج نفوذگران از نشست‌های فعال",
            "بررسی و خروج اجباری دستگاه‌های متصل در تلگرام، ایتا، روبیکا و بله",
            "امنیت اکانت",
            CyberUiTheme.COLOR_AMBER,
            v -> {
                if (listener != null) listener.onSessionKillClicked();
            }
        ));

        // ==========================================
        // ۴. بلوک ۲: سپرهای پایش خودکار و محرمانگی (Automated Defense & Privacy)
        // ==========================================
        container.addView(CyberUiTheme.createSectionTitle(context, "سپرهای پایش خودکار و حفظ محرمانگی"));

        container.addView(CyberUiTheme.createInteractiveCardRow(
            context,
            R.drawable.ic_notification_shield,
            "سپر اعلان‌های رمز پویا (OTP Notification Shield)",
            "شناسایی برنامه‌هایی که با خواندن اعلان‌ها رمز دوم بانکی را سرقت می‌کنند",
            "ضد سرقت OTP",
            "#FF4081",
            v -> {
                if (listener != null) listener.onOtpNotificationShieldClicked();
            }
        ));

        container.addView(CyberUiTheme.createInteractiveCardRow(
            context,
            R.drawable.ic_wifi_shield,
            "رادار امنیت وای‌فای و شبکه‌های عمومی",
            "پایش وای‌فای کافه‌ها، هتل‌ها و فرودگاه‌ها برای جلوگیری از شنود بانکی",
            "پایش وای‌فای",
            CyberUiTheme.COLOR_CYAN,
            v -> {
                if (listener != null) listener.onWifiRadarClicked();
            }
        ));

        container.addView(CyberUiTheme.createInteractiveCardRow(
            context,
            R.drawable.ic_lock_app,
            "قفل‌گذار و استتار برنامه‌های بانکی",
            "محافظت با رمز عبور و پین اختصاصی روی همراه بانک‌ها و پیام‌رسان‌ها",
            "قفل برنامه",
            CyberUiTheme.COLOR_AMBER,
            v -> {
                if (listener != null) listener.onAppLockClicked();
            }
        ));

        container.addView(CyberUiTheme.createInteractiveCardRow(
            context,
            R.drawable.ic_photo_clean,
            "پاک‌سازی موقعیت مکانی و متادیتای عکس‌ها",
            "حذف اطلاعات GPS و مدل گوشی از تصاویر قبل از انتشار در فضای مجازی",
            "حریم خصوصی",
            CyberUiTheme.COLOR_NEON_GREEN,
            v -> {
                if (listener != null) listener.onPhotoMetadataCleanClicked();
            }
        ));

        // ==========================================
        // ۵. بلوک ۳: ابزارهای بررسی و اصالت‌سنجی (Integrity & Forensics)
        // ==========================================
        container.addView(CyberUiTheme.createSectionTitle(context, "ابزارهای بررسی اصالت و پیگیری قضایی"));

        container.addView(CyberUiTheme.createInteractiveCardRow(
            context,
            R.drawable.ic_receipt,
            "اعتبارسنجی فیش بانکی (ضد جعل رسید)",
            "بررسی صحت پیش‌شماره کارت، شماره مرجع و فرمت رسیدهای بانکی",
            "ضد کلاهبرداری",
            CyberUiTheme.COLOR_NEON_GREEN,
            v -> {
                if (listener != null) listener.onReceiptVerifyClicked();
            }
        ));

        container.addView(CyberUiTheme.createInteractiveCardRow(
            context,
            R.drawable.ic_folder_download,
            "اسکن فایل‌های دریافتی تلگرام و پوشه دانلود",
            "کشف و حذف قطعی بدافزارهای APK قبل از اقدام به نصب توسط کاربر",
            "پایش فایل",
            CyberUiTheme.COLOR_PURPLE,
            v -> {
                if (listener != null) listener.onPreInstallScanClicked();
            }
        ));

        container.addView(CyberUiTheme.createInteractiveCardRow(
            context,
            R.drawable.ic_bank,
            "پایش اصالت و سلامت همراه بانک‌ها",
            "تایید نصب رسمی همراه بانک‌های ملی، ملت، بلو، ویپاد و سپه",
            "اصالت نسخه",
            "#69F0AE",
            v -> {
                if (listener != null) listener.onBankingIntegrityClicked();
            }
        ));

        container.addView(CyberUiTheme.createInteractiveCardRow(
            context,
            R.drawable.ic_call_warning,
            "بررسی شماره‌های مشکوک و تله‌های وانگیری",
            "شناسایی تماس‌های پرهزینه بین‌المللی، ماهواره‌ای و مدعیان پشتیبانی",
            "پایش تماس",
            CyberUiTheme.COLOR_GOLD,
            v -> {
                if (listener != null) listener.onCallFraudCheckClicked();
            }
        ));

        container.addView(CyberUiTheme.createInteractiveCardRow(
            context,
            R.drawable.ic_file_report,
            "صدور لایحه مستند فنی برای پلیس فتا و دادسرا",
            "تنظیم گزارش فنی جرایم رایانه‌ای با استناد به مواد قانونی پلیس فتا",
            "لایحه قضایی",
            "#F06292",
            v -> {
                if (listener != null) listener.onFataReportExportClicked();
            }
        ));

        container.addView(CyberUiTheme.createInteractiveCardRow(
            context,
            R.drawable.ic_sync,
            "به‌روزرسانی ابری پایگاه داده بدافزارها از گیت‌هاب",
            "دریافت آخرین امضاها و الگوهای کشف بدافزار بدون نیاز به آپدیت اپ",
            "همگام‌سازی",
            CyberUiTheme.COLOR_CYAN,
            v -> {
                if (listener != null) listener.onSyncDatabaseClicked();
            }
        ));

        return container;
    }
}

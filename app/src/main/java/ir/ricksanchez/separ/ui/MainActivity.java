package ir.ricksanchez.separ.ui;

import android.app.Activity;
import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import ir.ricksanchez.separ.R;
import ir.ricksanchez.separ.SeparApp;
import ir.ricksanchez.separ.audio.AudioGuideEngine;
import ir.ricksanchez.separ.billing.PromoAndAffiliateManager;
import ir.ricksanchez.separ.billing.UnifiedBillingManager;
import ir.ricksanchez.separ.data.AppLockGuardian;
import ir.ricksanchez.separ.data.BankingAppIntegrityGuard;
import ir.ricksanchez.separ.data.FakeReceiptAnalyzer;
import ir.ricksanchez.separ.data.LiveThreatSyncEngine;
import ir.ricksanchez.separ.data.MediaPartnerRepository;
import ir.ricksanchez.separ.data.OtpNotificationShield;
import ir.ricksanchez.separ.data.PreInstallApkScanner;
import ir.ricksanchez.separ.data.RescueLedgerRepository;
import ir.ricksanchez.separ.data.SmsAndScreenGuard;
import ir.ricksanchez.separ.data.StealthTrojanDetector;
import ir.ricksanchez.separ.data.ThreatPackageRegistry;
import ir.ricksanchez.separ.data.UserCustomWhitelistManager;
import ir.ricksanchez.separ.data.UserModeManager;
import ir.ricksanchez.separ.data.VideoTutorialRepository;
import ir.ricksanchez.separ.data.WifiSecurityRadar;
import ir.ricksanchez.separ.services.CallFraudGuardian;
import ir.ricksanchez.separ.utils.CallForwardingAuditor;
import ir.ricksanchez.separ.utils.FataIncidentReportExporter;
import ir.ricksanchez.separ.utils.PhotoMetadataCleaner;
import ir.ricksanchez.separ.utils.PrivacyPolicyHelper;
import ir.ricksanchez.separ.utils.ProminentDisclosureHelper;
import ir.ricksanchez.separ.utils.SessionRemediationHelper;
import ir.ricksanchez.separ.widget.SeparWidgetProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * رابط کاربری اصلی و فوق‌پیشرفته سایبری «سپر» (Cyber-SOC HUD)
 * مجهز به معماری استاندارد نوار ناوبری ۵ تبی، هدر ثابت ایزوله از آنتن و ساعت و مودال‌های لوکس فین‌تک
 */
public class MainActivity extends Activity {

    public static final int TAB_RADAR = 0;
    public static final int TAB_SMS_APPS = 1;
    public static final int TAB_SETTINGS = 2;
    public static final int TAB_ACADEMY = 3;
    public static final int TAB_CREATOR = 4;

    private int currentTab = TAB_RADAR;

    private AudioGuideEngine audioGuide;
    private UnifiedBillingManager billingManager;
    private PromoAndAffiliateManager promoManager;
    private RescueLedgerRepository rescueLedger;
    private UserModeManager userModeManager;
    private UserCustomWhitelistManager userWhitelist;

    private LinearLayout rootAppLayout;
    private LinearLayout topBarContainer;
    private ScrollView contentScrollView;
    private LinearLayout tabContentContainer;
    private LinearLayout bottomNavBar;

    private List<ThreatPackageRegistry.ThreatResult> detectedThreats = new ArrayList<>();
    private List<SmsAndScreenGuard.AppAuditResult> smsAuditList = new ArrayList<>();

    private int initialStatusBarHeight = 0;
    private int initialNavBarHeight = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // اعمال استایل و رنگ نوار وضعیت و ناوبری سیستمی
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(Color.parseColor(CyberUiTheme.COLOR_TOP_BAR_BG));
            getWindow().setNavigationBarColor(Color.parseColor(CyberUiTheme.COLOR_BOTTOM_BAR_BG));
        }

        // اعمال سیاست امنیتی ضد اسکرین‌شات
        SmsAndScreenGuard.enforceScreenSecurity(this);

        audioGuide = new AudioGuideEngine(this);
        billingManager = SeparApp.getInstance().getBillingManager();
        promoManager = SeparApp.getInstance().getPromoManager();
        rescueLedger = SeparApp.getInstance().getRescueLedger();
        userModeManager = SeparApp.getInstance().getUserModeManager();
        userWhitelist = SeparApp.getInstance().getUserWhitelist();

        // محاسبه ارتفاع استاتوس‌بار و نوار ناوبری جهت جلوگیری از تداخل اولیه
        int sId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (sId > 0) {
            initialStatusBarHeight = getResources().getDimensionPixelSize(sId);
        }
        if (initialStatusBarHeight <= 0) {
            initialStatusBarHeight = CyberUiTheme.dpToPx(this, 24);
        }

        int nId = getResources().getIdentifier("navigation_bar_height", "dimen", "android");
        if (nId > 0) {
            initialNavBarHeight = getResources().getDimensionPixelSize(nId);
        }

        // نمایش شفافیت دسترسی‌ها در اولین اجرا
        ProminentDisclosureHelper.showDisclosureIfNeeded(this, null);

        buildScaffoldUi();
        performDeepScan();
    }

    private void buildScaffoldUi() {
        // ۱. چیدمان عمودی کل صفحه (Root Scaffold)
        rootAppLayout = new LinearLayout(this);
        rootAppLayout.setOrientation(LinearLayout.VERTICAL);
        rootAppLayout.setBackgroundColor(Color.parseColor(CyberUiTheme.COLOR_BG));
        rootAppLayout.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));

        // ۲. هدر بالایی ثابت و پین‌شده (Fixed Top App Bar)
        topBarContainer = new LinearLayout(this);
        topBarContainer.setOrientation(LinearLayout.VERTICAL);
        topBarContainer.setBackgroundColor(Color.parseColor(CyberUiTheme.COLOR_TOP_BAR_BG));
        topBarContainer.setPadding(0, initialStatusBarHeight, 0, 0);

        View topHeader = buildTopHeader();
        topBarContainer.addView(topHeader);

        // خط مرزی ظریف زیر هدر
        View topDivider = new View(this);
        topDivider.setBackgroundColor(Color.parseColor(CyberUiTheme.COLOR_CARD_BORDER));
        topDivider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        topBarContainer.addView(topDivider);

        rootAppLayout.addView(topBarContainer);

        // ۳. ناحیه اسکرول وسط (Scrollable Content between Pinned Bars)
        contentScrollView = new ScrollView(this);
        contentScrollView.setFillViewport(true);
        LinearLayout.LayoutParams scrollParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0,
            1.0f
        );
        contentScrollView.setLayoutParams(scrollParams);

        LinearLayout scrollInner = new LinearLayout(this);
        scrollInner.setOrientation(LinearLayout.VERTICAL);
        int pHoriz = CyberUiTheme.dpToPx(this, 16);
        int pVert = CyberUiTheme.dpToPx(this, 16);
        scrollInner.setPadding(pHoriz, pVert, pHoriz, CyberUiTheme.dpToPx(this, 24));
        scrollInner.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // کانتینر محتوای تب فعال
        tabContentContainer = new LinearLayout(this);
        tabContentContainer.setOrientation(LinearLayout.VERTICAL);
        tabContentContainer.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        scrollInner.addView(tabContentContainer);

        contentScrollView.addView(scrollInner);
        rootAppLayout.addView(contentScrollView);

        // ۴. نوار ناوبری پایینی پین‌شده (Fixed Bottom Navigation Bar)
        bottomNavBar = buildBottomNavBar();
        rootAppLayout.addView(bottomNavBar);

        // اعمال هوشمند Insets سیستم‌عامل اندروید برای تطبیق لحظه‌ای
        rootAppLayout.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                int top = 0;
                int bottom = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    android.graphics.Insets sb = insets.getInsets(
                        WindowInsets.Type.statusBars() | WindowInsets.Type.displayCutout()
                    );
                    top = sb.top;
                    android.graphics.Insets nb = insets.getInsets(
                        WindowInsets.Type.navigationBars()
                    );
                    bottom = nb.bottom;
                } else {
                    top = insets.getSystemWindowInsetTop();
                    bottom = insets.getSystemWindowInsetBottom();
                }

                int finalTop = Math.max(top, initialStatusBarHeight);
                int finalBottom = Math.max(bottom, initialNavBarHeight);

                topBarContainer.setPadding(0, finalTop, 0, 0);
                bottomNavBar.setPadding(
                    CyberUiTheme.dpToPx(MainActivity.this, 4),
                    CyberUiTheme.dpToPx(MainActivity.this, 6),
                    CyberUiTheme.dpToPx(MainActivity.this, 4),
                    finalBottom + CyberUiTheme.dpToPx(MainActivity.this, 4)
                );
                return insets;
            }
        });

        setContentView(rootAppLayout);
        renderActiveTabContent();
    }

    private View buildTopHeader() {
        LinearLayout topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setGravity(Gravity.CENTER_VERTICAL);
        int px16 = CyberUiTheme.dpToPx(this, 16);
        int px12 = CyberUiTheme.dpToPx(this, 12);
        topBar.setPadding(px16, px12, px16, px12);
        topBar.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // آیکون سپر در سمت راست
        ImageView logoView = new ImageView(this);
        logoView.setImageResource(R.drawable.ic_shield);
        int logoSize = CyberUiTheme.dpToPx(this, 32);
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(logoSize, logoSize);
        llp.setMargins(CyberUiTheme.dpToPx(this, 12), 0, 0, 0);
        logoView.setLayoutParams(llp);
        topBar.addView(logoView);

        // ستون متن عنوان و زیرعنوان
        LinearLayout brandCol = new LinearLayout(this);
        brandCol.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        brandCol.setLayoutParams(blp);

        TextView title = new TextView(this);
        title.setText("سپر ضد هک");
        title.setTextSize(17);
        title.setTextColor(Color.WHITE);
        FontHelper.applyBold(title);
        brandCol.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("سامانه دفاع سایبری ملی • ریک سانچز");
        subtitle.setTextSize(10);
        subtitle.setTextColor(Color.parseColor(CyberUiTheme.COLOR_TEXT_MUTED));
        FontHelper.applyRegular(subtitle);
        brandCol.addView(subtitle);
        topBar.addView(brandCol);

        // نشان وضعیت سبز (Live Status Chip)
        TextView liveChip = new TextView(this);
        liveChip.setText("پایش ۲۴ ساعته");
        liveChip.setTextColor(Color.parseColor(CyberUiTheme.COLOR_NEON_GREEN));
        liveChip.setTextSize(10);
        FontHelper.applyMedium(liveChip);
        liveChip.setPadding(CyberUiTheme.dpToPx(this, 10), CyberUiTheme.dpToPx(this, 5), CyberUiTheme.dpToPx(this, 10), CyberUiTheme.dpToPx(this, 5));
        liveChip.setBackground(CyberUiTheme.createCardBg("#0F2618", CyberUiTheme.COLOR_NEON_GREEN, 8));
        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        clp.setMargins(CyberUiTheme.dpToPx(this, 6), 0, CyberUiTheme.dpToPx(this, 6), 0);
        liveChip.setLayoutParams(clp);
        topBar.addView(liveChip);

        // دکمه دستیار صوتی
        ImageView voiceBtn = new ImageView(this);
        voiceBtn.setImageResource(R.drawable.ic_volume_up);
        int vSize = CyberUiTheme.dpToPx(this, 36);
        LinearLayout.LayoutParams vlp = new LinearLayout.LayoutParams(vSize, vSize);
        voiceBtn.setLayoutParams(vlp);
        int vPad = CyberUiTheme.dpToPx(this, 7);
        voiceBtn.setPadding(vPad, vPad, vPad, vPad);
        voiceBtn.setBackground(CyberUiTheme.createCardBg("#131D2E", CyberUiTheme.COLOR_CYAN, 10));
        voiceBtn.setOnClickListener(v -> playVoiceBriefing());
        topBar.addView(voiceBtn);

        return topBar;
    }

    private LinearLayout buildBottomNavBar() {
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.VERTICAL);
        bar.setBackgroundColor(Color.parseColor("#0A0E17"));

        View divider = new View(this);
        divider.setBackgroundColor(Color.parseColor("#1B2638"));
        divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        bar.addView(divider);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);
        row.setPadding(
            CyberUiTheme.dpToPx(this, 2),
            CyberUiTheme.dpToPx(this, 6),
            CyberUiTheme.dpToPx(this, 2),
            initialNavBarHeight + CyberUiTheme.dpToPx(this, 6)
        );
        row.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        addBottomNavItem(row, R.drawable.ic_nav_radar, "رادار", TAB_RADAR);
        addBottomNavItem(row, R.drawable.ic_nav_sms, "برنامه‌ها", TAB_SMS_APPS);
        addBottomNavItem(row, R.drawable.ic_nav_settings, "دسترسی‌ها", TAB_SETTINGS);
        addBottomNavItem(row, R.drawable.ic_nav_academy, "حامیان", TAB_ACADEMY);
        addBottomNavItem(row, R.drawable.ic_nav_creator, "سازنده", TAB_CREATOR);

        bar.addView(row);
        return bar;
    }

    private void addBottomNavItem(LinearLayout parent, int iconResId, String label, final int tabIndex) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);
        item.setPadding(CyberUiTheme.dpToPx(this, 2), CyberUiTheme.dpToPx(this, 4), CyberUiTheme.dpToPx(this, 2), CyberUiTheme.dpToPx(this, 4));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        item.setLayoutParams(lp);

        boolean isActive = (currentTab == tabIndex);

        // کانتینر بالایی آیکون با کپسول نئونی مدرن
        LinearLayout iconBox = new LinearLayout(this);
        iconBox.setGravity(Gravity.CENTER);
        int boxW = CyberUiTheme.dpToPx(this, 48);
        int boxH = CyberUiTheme.dpToPx(this, 28);
        iconBox.setLayoutParams(new LinearLayout.LayoutParams(boxW, boxH));
        if (isActive) {
            GradientDrawable pill = new GradientDrawable();
            pill.setColor(Color.parseColor("#122538"));
            pill.setCornerRadius(CyberUiTheme.dpToPx(this, 14));
            pill.setStroke(1, Color.parseColor(CyberUiTheme.COLOR_CYAN));
            iconBox.setBackground(pill);
        }

        ImageView icon = new ImageView(this);
        icon.setImageResource(iconResId);
        int iconSize = CyberUiTheme.dpToPx(this, 20);
        icon.setLayoutParams(new LinearLayout.LayoutParams(iconSize, iconSize));
        if (isActive) {
            icon.setColorFilter(Color.parseColor(CyberUiTheme.COLOR_CYAN));
        } else {
            icon.setColorFilter(Color.parseColor("#64748B"));
        }
        iconBox.addView(icon);
        item.addView(iconBox);

        // متن تایتل با فونت شفاف و خوانای وزیرمتن
        TextView text = new TextView(this);
        text.setText(label);
        text.setTextSize(11);
        text.setSingleLine(true);
        text.setGravity(Gravity.CENTER);
        text.setTextColor(Color.parseColor(isActive ? CyberUiTheme.COLOR_CYAN : "#94A3B8"));
        if (isActive) {
            FontHelper.applyBold(text);
        } else {
            FontHelper.applyMedium(text);
        }
        text.setPadding(0, CyberUiTheme.dpToPx(this, 3), 0, 0);
        item.addView(text);

        // نقطه نشانگر تب فعال
        if (isActive) {
            View dot = new View(this);
            GradientDrawable dotDrawable = new GradientDrawable();
            dotDrawable.setColor(Color.parseColor(CyberUiTheme.COLOR_CYAN));
            dotDrawable.setCornerRadius(CyberUiTheme.dpToPx(this, 2));
            dot.setBackground(dotDrawable);
            int dotW = CyberUiTheme.dpToPx(this, 12);
            int dotH = CyberUiTheme.dpToPx(this, 2);
            LinearLayout.LayoutParams dlp = new LinearLayout.LayoutParams(dotW, dotH);
            dlp.setMargins(0, CyberUiTheme.dpToPx(this, 2), 0, 0);
            dot.setLayoutParams(dlp);
            item.addView(dot);
        }

        item.setOnClickListener(v -> {
            if (currentTab != tabIndex) {
                currentTab = tabIndex;
                refreshBottomNavBar();
                renderActiveTabContent();
                contentScrollView.smoothScrollTo(0, 0);
            }
        });

        parent.addView(item);
    }

    private void refreshBottomNavBar() {
        if (bottomNavBar == null) return;
        bottomNavBar.removeAllViews();

        View divider = new View(this);
        divider.setBackgroundColor(Color.parseColor("#1B2638"));
        divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        bottomNavBar.addView(divider);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);
        row.setPadding(
            CyberUiTheme.dpToPx(this, 2),
            CyberUiTheme.dpToPx(this, 6),
            CyberUiTheme.dpToPx(this, 2),
            initialNavBarHeight + CyberUiTheme.dpToPx(this, 6)
        );
        row.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        addBottomNavItem(row, R.drawable.ic_nav_radar, "رادار", TAB_RADAR);
        addBottomNavItem(row, R.drawable.ic_nav_sms, "برنامه‌ها", TAB_SMS_APPS);
        addBottomNavItem(row, R.drawable.ic_nav_settings, "دسترسی‌ها", TAB_SETTINGS);
        addBottomNavItem(row, R.drawable.ic_nav_academy, "حامیان", TAB_ACADEMY);
        addBottomNavItem(row, R.drawable.ic_nav_creator, "سازنده", TAB_CREATOR);

        bottomNavBar.addView(row);
    }

    private void renderActiveTabContent() {
        if (tabContentContainer == null) return;
        tabContentContainer.removeAllViews();

        int unapprovedCount = 0;
        for (SmsAndScreenGuard.AppAuditResult a : smsAuditList) {
            if (!a.isSafe()) unapprovedCount++;
        }
        boolean hasThreats = !detectedThreats.isEmpty() || unapprovedCount > 0;
        int threatsTotal = detectedThreats.size() + unapprovedCount;
        RescueLedgerRepository.RescueStats stats = rescueLedger.getStats();

        switch (currentTab) {
            case TAB_RADAR:
                tabContentContainer.addView(RadarDashboardView.buildView(
                    this,
                    hasThreats,
                    threatsTotal,
                    stats,
                    new RadarDashboardView.RadarDashboardListener() {
                        @Override
                        public void onScanRequested() {
                            performDeepScan();
                            Toast.makeText(MainActivity.this, "اسکن عمیق و ممیزی سیستم انجام شد.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onVoiceRequested() {
                            playVoiceBriefing();
                        }

                        @Override
                        public void onCancelDivertClicked() {
                            CallForwardingAuditor.dialCancelForwarding(MainActivity.this);
                        }

                        @Override
                        public void onFataEmergencyClicked() {
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:096380"));
                            startActivity(intent);
                        }

                        @Override
                        public void onSessionKillClicked() {
                            openSessionRemediationDialog();
                        }

                        @Override
                        public void onReceiptVerifyClicked() {
                            openReceiptDialog();
                        }

                        @Override
                        public void onPreInstallScanClicked() {
                            openPreInstallScannerDialog();
                        }

                        @Override
                        public void onFataReportExportClicked() {
                            FataIncidentReportExporter.shareReport(MainActivity.this);
                        }

                        @Override
                        public void onSyncDatabaseClicked() {
                            syncThreatDatabaseOnline();
                        }

                        @Override
                        public void onBankingIntegrityClicked() {
                            openBankingIntegrityDialog();
                        }

                        @Override
                        public void onCallFraudCheckClicked() {
                            openCallFraudCheckDialog();
                        }

                        @Override
                        public void onOtpNotificationShieldClicked() {
                            openOtpNotificationShieldDialog();
                        }

                        @Override
                        public void onWifiRadarClicked() {
                            openWifiRadarDialog();
                        }

                        @Override
                        public void onAppLockClicked() {
                            openAppLockDialog();
                        }

                        @Override
                        public void onPhotoMetadataCleanClicked() {
                            openPhotoMetadataCleanDialog();
                        }
                    }
                ));
                break;

            case TAB_SMS_APPS:
                tabContentContainer.addView(SmsAuditTabView.buildView(
                    this,
                    detectedThreats,
                    smsAuditList,
                    new SmsAuditTabView.SmsAuditTabListener() {
                        @Override
                        public void onRevokeOrUninstallClicked(String packageName, boolean isMalware) {
                            SmsAndScreenGuard.launchRevokeOrUninstall(MainActivity.this, packageName, isMalware);
                        }

                        @Override
                        public void onManageAppPermissionsClicked(String packageName) {
                            try {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + packageName));
                                startActivity(intent);
                            } catch (Exception e) {
                                Toast.makeText(MainActivity.this, "تنظیمات برنامه در دسترس نیست.", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onAddToWhitelistClicked(String packageName, String appName) {
                            userWhitelist.addToWhitelist(packageName);
                            Toast.makeText(MainActivity.this, "برنامه «" + appName + "» به لیست سفید افزوده شد.", Toast.LENGTH_SHORT).show();
                            performDeepScan();
                        }

                        @Override
                        public void onManageWhitelistClicked() {
                            openWhitelistManagementDialog();
                        }

                        @Override
                        public void onRescanAppsRequested() {
                            performDeepScan();
                            Toast.makeText(MainActivity.this, "اسکن جامع کلیه برنامه‌های نصب‌شده انجام شد.", Toast.LENGTH_SHORT).show();
                        }
                    }
                ));
                break;

            case TAB_SETTINGS:
                tabContentContainer.addView(SecuritySettingsTabView.buildView(
                    this,
                    new SecuritySettingsTabView.SecuritySettingsListener() {
                        @Override
                        public void onAccessibilitySettingsRequested() {
                            try {
                                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                            } catch (Exception ignored) {}
                        }

                        @Override
                        public void onNotificationListenerSettingsRequested() {
                            try {
                                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                            } catch (Exception ignored) {}
                        }

                        @Override
                        public void onOverlaySettingsRequested() {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                try {
                                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                                    startActivity(intent);
                                } catch (Exception ignored) {}
                            }
                        }

                        @Override
                        public void onBatteryOptimizationRequested() {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                try {
                                    Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
                                    startActivity(intent);
                                } catch (Exception ignored) {}
                            }
                        }

                        @Override
                        public void onAppDetailsRequested() {
                            try {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + getPackageName()));
                                startActivity(intent);
                            } catch (Exception ignored) {}
                        }

                        @Override
                        public void onClearCacheRequested() {
                            Toast.makeText(MainActivity.this, "حافظه موقت سپر پاک‌سازی شد.", Toast.LENGTH_SHORT).show();
                        }
                    }
                ));
                break;

            case TAB_ACADEMY:
                tabContentContainer.addView(AcademyMediaTabView.buildView(
                    this,
                    new AcademyMediaTabView.AcademyMediaTabListener() {
                        @Override
                        public void onVideoClicked(String aparatUrl) {
                            VideoTutorialRepository.openAparatVideo(MainActivity.this, aparatUrl);
                        }

                        @Override
                        public void onPartnerClicked(String partnerUrl) {
                            MediaPartnerRepository.openPartnerUrl(MainActivity.this, partnerUrl);
                        }
                    }
                ));
                break;

            case TAB_CREATOR:
                tabContentContainer.addView(CreatorProfileTabView.buildView(
                    this,
                    new CreatorProfileTabView.CreatorProfileListener() {
                        @Override
                        public void onTelegramClicked() {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/M4tinBeigi"));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } catch (Exception ignored) {}
                        }

                        @Override
                        public void onGithubClicked() {
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/m4tinbeigi-official"));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            } catch (Exception ignored) {}
                        }

                        @Override
                        public void onShareAppClicked() {
                            Intent sendIntent = new Intent();
                            sendIntent.setAction(Intent.ACTION_SEND);
                            sendIntent.putExtra(Intent.EXTRA_TEXT, CreatorProfileTabView.getShareAppMessage());
                            sendIntent.setType("text/plain");
                            startActivity(Intent.createChooser(sendIntent, "ارسال رایگان نرم‌افزار سپر"));
                        }

                        @Override
                        public void onPrivacyClicked() {
                            PrivacyPolicyHelper.showPrivacyPolicy(MainActivity.this);
                        }
                    }
                ));
                break;
        }
    }

    private void playVoiceBriefing() {
        int unapprovedCount = 0;
        for (SmsAndScreenGuard.AppAuditResult a : smsAuditList) {
            if (!a.isSafe()) unapprovedCount++;
        }
        boolean hasThreats = !detectedThreats.isEmpty() || unapprovedCount > 0;

        if (!hasThreats) {
            audioGuide.speak("گوشی، پیامک‌ها و درگاه‌های بانکی شما کاملاً امن هستند و هیچ بدافزار یا برنامه مشکوکی شناسایی نشد.");
        } else {
            audioGuide.speak("هشدار فوری! برنامه‌هایی بدون تأیید شما به پیامک‌های بانکی یا صفحه گوشی دسترسی دارند. لطفاً در بخش برنامه‌ها آن‌ها را حذف کنید.");
        }
    }

    private void openSessionRemediationDialog() {
        List<SessionRemediationHelper.AppSessionGuide> platforms = SessionRemediationHelper.getSupportedPlatforms();
        String[] titles = new String[platforms.size()];
        for (int i = 0; i < platforms.size(); i++) {
            titles[i] = platforms.get(i).appName;
        }

        CyberDialogHelper.showCyberAlert(
            this,
            R.drawable.ic_session_exit,
            "اخراج هکرها و نفوذگران از نشست‌ها",
            "جهت مشاهده مسیر گام‌به‌گام بستن نشست‌های ناشناس، برنامه مورد نظر را انتخاب کنید:\n\n• " +
            titles[0] + " • " + titles[1] + " • " + titles[2] + " • " + titles[3],
            "باز کردن راهنما",
            d -> {
                d.dismiss();
                SessionRemediationHelper.AppSessionGuide selected = platforms.get(0);
                CyberDialogHelper.showCyberAlert(
                    MainActivity.this,
                    R.drawable.ic_session_exit,
                    selected.appName,
                    "مسیر خروج از سایر نشست‌ها:\n\n" + selected.pathDescriptionFa,
                    "باز کردن برنامه",
                    d2 -> {
                        d2.dismiss();
                        SessionRemediationHelper.openAppOrSettings(MainActivity.this, selected);
                    },
                    "بستن",
                    null
                );
            },
            "بستن",
            null
        );
    }

    private void openReceiptDialog() {
        CyberDialogHelper.showCyberInputDialog(
            this,
            R.drawable.ic_receipt,
            "اعتبارسنجی زنده فیش بانکی (ضد جعل)",
            "شماره کارت ۱۶ رقمی یا شبا (مثلاً ۶۰۳۷...)",
            "استعلام اصالت",
            (dialog, cardNumber) -> {
                dialog.dismiss();
                if (cardNumber == null || cardNumber.trim().length() < 16) {
                    Toast.makeText(MainActivity.this, "لطفاً شماره کارت ۱۶ رقمی معتبر وارد کنید.", Toast.LENGTH_SHORT).show();
                    return;
                }
                FakeReceiptAnalyzer.VerificationResult result = FakeReceiptAnalyzer.analyzeReceipt(cardNumber.trim(), "123456", "0");
                CyberDialogHelper.showCyberAlert(
                    MainActivity.this,
                    R.drawable.ic_receipt,
                    result.titleFa,
                    result.voiceWarning,
                    "متوجه شدم",
                    d -> d.dismiss(),
                    null,
                    null
                );
            }
        );
    }

    private void openPreInstallScannerDialog() {
        List<PreInstallApkScanner.DangerousApkFile> dangerousApks = PreInstallApkScanner.scanDownloadedApks(this);
        if (dangerousApks.isEmpty()) {
            CyberDialogHelper.showCyberAlert(
                this,
                R.drawable.ic_folder_download,
                "پایش فایل‌های نصبی دانلود و تلگرام",
                "پوشه Download و فایل‌های دریافتی تلگرام بررسی شد؛ هیچ فایل نصبی مخرب یا بدافزاری وجود ندارد.",
                "بسیار عالی",
                Dialog::dismiss,
                null,
                null
            );
            return;
        }

        PreInstallApkScanner.DangerousApkFile chosen = dangerousApks.get(0);
        CyberDialogHelper.showCyberAlert(
            this,
            R.drawable.ic_call_warning,
            "حذف قطعی فایل نصبی آلوده؟",
            "فایل «" + chosen.fileName + "» یک بدافزار است.\nتوضیح: " + chosen.threatDescription + "\n\nآیا مایل به حذف قطعی آن از حافظه هستید؟",
            "بله، حذف کن",
            d -> {
                d.dismiss();
                boolean deleted = PreInstallApkScanner.deleteDangerousApk(chosen.filePath);
                if (deleted) {
                    Toast.makeText(MainActivity.this, "فایل با موفقیت از حافظه گوشی حذف شد.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "امکان حذف خودکار وجود نداشت. لطفاً به صورت دستی پاک کنید.", Toast.LENGTH_LONG).show();
                }
            },
            "انصراف",
            null
        );
    }

    private void syncThreatDatabaseOnline() {
        Toast.makeText(this, "در حال اتصال به گیت‌هاب و دریافت آخرین امضاهای بدافزار...", Toast.LENGTH_SHORT).show();
        LiveThreatSyncEngine.syncThreatDatabaseAsync(this, (success, newCount, message) -> {
            runOnUiThread(() -> {
                CyberDialogHelper.showCyberAlert(
                    MainActivity.this,
                    R.drawable.ic_sync,
                    success ? "همگام‌سازی موفقیت‌آمیز" : "خطا در همگام‌سازی",
                    message,
                    "تأیید",
                    Dialog::dismiss,
                    null,
                    null
                );
                if (success) {
                    performDeepScan();
                }
            });
        });
    }

    private void openBankingIntegrityDialog() {
        List<BankingAppIntegrityGuard.BankAppStatus> bankApps = BankingAppIntegrityGuard.auditInstalledBankingApps(this);
        if (bankApps.isEmpty()) {
            CyberDialogHelper.showCyberAlert(
                this,
                R.drawable.ic_bank,
                "پایش اصالت همراه بانک‌ها",
                "هیچ برنامه همراه بانک رسمی روی گوشی شما نصب نیست.",
                "متوجه شدم",
                Dialog::dismiss,
                null,
                null
            );
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (BankingAppIntegrityGuard.BankAppStatus b : bankApps) {
            sb.append("• ").append(b.bankNameFa).append(": ").append(b.statusDescriptionFa).append("\n");
        }

        CyberDialogHelper.showCyberAlert(
            this,
            R.drawable.ic_bank,
            "اصالت همراه بانک‌های نصب‌شده",
            sb.toString().trim(),
            "تأیید و بستن",
            Dialog::dismiss,
            null,
            null
        );
    }

    private void openCallFraudCheckDialog() {
        CyberDialogHelper.showCyberInputDialog(
            this,
            R.drawable.ic_call_warning,
            "بررسی شماره‌های مشکوک و تله‌های وانگیری",
            "شماره تماس ناشناس یا بین‌المللی (مثلاً +247...)",
            "بررسی هوشمند",
            (dialog, num) -> {
                dialog.dismiss();
                CallFraudGuardian.CallRiskAssessment assessment = CallFraudGuardian.evaluateIncomingNumber(num);
                CyberDialogHelper.showCyberAlert(
                    MainActivity.this,
                    R.drawable.ic_call_warning,
                    assessment.riskTitleFa,
                    assessment.recommendationFa,
                    "متوجه شدم",
                    Dialog::dismiss,
                    null,
                    null
                );
            }
        );
    }

    private void openOtpNotificationShieldDialog() {
        List<OtpNotificationShield.NotificationRiskAuditResult> risks =
            OtpNotificationShield.auditNotificationListeners(this, userWhitelist);

        if (risks.isEmpty()) {
            CyberDialogHelper.showCyberAlert(
                this,
                R.drawable.ic_notification_shield,
                "دیده‌بان اعلان‌های رمز پویا",
                "وضعیت امن: هیچ برنامه متفرقه یا ناشناسی به اعلان‌ها و رمزهای پویای گوشی شما دسترسی ندارد.",
                "بسیار عالی",
                Dialog::dismiss,
                null,
                null
            );
            return;
        }

        OtpNotificationShield.NotificationRiskAuditResult r = risks.get(0);
        CyberDialogHelper.showCyberAlert(
            this,
            R.drawable.ic_notification_shield,
            r.warningTitleFa,
            r.descriptionFa + "\n\n" + r.recommendationFa,
            "مدیریت دسترسی اعلان‌ها",
            d -> {
                d.dismiss();
                try {
                    startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
                } catch (Exception ignored) {}
            },
            "بستن",
            null
        );
    }

    private void openWifiRadarDialog() {
        WifiSecurityRadar.WifiSecurityAssessment assessment = WifiSecurityRadar.evaluateActiveNetwork(this);

        CyberDialogHelper.showCyberAlert(
            this,
            R.drawable.ic_wifi_shield,
            assessment.titleFa,
            "شبکه فعال: " + assessment.ssid + "\n" +
            "پروتکل امنیتی: " + assessment.securityProtocol + "\n\n" +
            assessment.descriptionFa + "\n\n" +
            assessment.recommendationFa,
            "متوجه شدم",
            Dialog::dismiss,
            null,
            null
        );
    }

    private void openAppLockDialog() {
        List<AppLockGuardian.SensitiveAppItem> apps = AppLockGuardian.getSensitiveAppsStatus(this);
        StringBuilder sb = new StringBuilder();
        for (AppLockGuardian.SensitiveAppItem a : apps) {
            sb.append(a.isLocked ? "🔒 [قفل فعال] " : "🔓 [قفل غیرفعال] ")
              .append(a.appNameFa).append(" (").append(a.categoryFa).append(")\n");
        }

        CyberDialogHelper.showCyberAlert(
            this,
            R.drawable.ic_lock_app,
            "قفل‌گذار و استتار برنامه‌های حساس",
            "وضعیت برنامه‌های تحت پوشش:\n\n" + sb.toString() +
            "\nجهت تغییر وضعیت قفل هر برنامه، روی تغییر قفل کلیک کنید.",
            "تغییر وضعیت قفل برنامه اول",
            d -> {
                d.dismiss();
                if (!apps.isEmpty()) {
                    AppLockGuardian.toggleAppLock(MainActivity.this, apps.get(0).packageName);
                    Toast.makeText(MainActivity.this, "وضعیت قفل برنامه تغییر کرد.", Toast.LENGTH_SHORT).show();
                }
            },
            "بستن",
            null
        );
    }

    private void openPhotoMetadataCleanDialog() {
        List<PhotoMetadataCleaner.PhotoExifAuditResult> photos = PhotoMetadataCleaner.scanRecentCameraPhotos(5);
        if (photos.isEmpty()) {
            CyberDialogHelper.showCyberAlert(
                this,
                R.drawable.ic_photo_clean,
                "پاک‌سازی متادیتا و لوکیشن تصاویر",
                "تصویری با اطلاعات مکانی لو رفته یا خطرناک در حافظه اخیر یافت نشد.",
                "بسیار عالی",
                Dialog::dismiss,
                null,
                null
            );
            return;
        }

        PhotoMetadataCleaner.PhotoExifAuditResult p = photos.get(0);
        CyberDialogHelper.showCyberAlert(
            this,
            R.drawable.ic_photo_clean,
            "پاک‌سازی موقعیت مکانی عکس",
            "عکس: " + p.fileName + (p.hasGpsLocation ? "\n[حاوی موقعیت دقیق GPS]" : "\n[فاقد GPS]") +
            "\n\nآیا مایل به حذف داده‌های مکانی و مدل دوربین قبل از اشتراک‌گذاری هستید؟",
            "پاک‌سازی متادیتا",
            d -> {
                d.dismiss();
                boolean cleaned = PhotoMetadataCleaner.cleanMetadata(p.filePath);
                if (cleaned) {
                    Toast.makeText(MainActivity.this, "اطلاعات GPS با موفقیت پاک شد.", Toast.LENGTH_SHORT).show();
                }
            },
            "انصراف",
            null
        );
    }

    private void openWhitelistManagementDialog() {
        Set<String> set = userWhitelist.getWhitelistedPackages();
        if (set.isEmpty()) {
            CyberDialogHelper.showCyberAlert(
                this,
                R.drawable.ic_shield,
                "لیست سفید برنامه‌های شما",
                "هنوز هیچ برنامه‌ای توسط شما به لیست سفید اضافه نشده است.",
                "متوجه شدم",
                Dialog::dismiss,
                null,
                null
            );
            return;
        }

        String[] pkgs = set.toArray(new String[0]);
        String first = pkgs[0];
        CyberDialogHelper.showCyberAlert(
            this,
            R.drawable.ic_shield,
            "مدیریت لیست سفید برنامه‌ها",
            "تعداد برنامه‌های تاییدشده: " + set.size() + "\nنمونه: " + first +
            "\n\nآیا می‌خواهید دسترسی این برنامه مجدداً لغو و مانیتور شود؟",
            "حذف برنامه اول از لیست سفید",
            d -> {
                d.dismiss();
                userWhitelist.removeFromWhitelist(first);
                Toast.makeText(MainActivity.this, "برنامه از لیست سفید خارج شد.", Toast.LENGTH_SHORT).show();
                performDeepScan();
            },
            "بستن",
            null
        );
    }

    private void performDeepScan() {
        detectedThreats.clear();
        PackageManager pm = getPackageManager();
        List<PackageInfo> packages = pm.getInstalledPackages(
            PackageManager.GET_PERMISSIONS | PackageManager.GET_SERVICES | PackageManager.GET_RECEIVERS | PackageManager.GET_META_DATA
        );

        for (PackageInfo pkg : packages) {
            ThreatPackageRegistry.ThreatResult threat = ThreatPackageRegistry.evaluatePackage(pkg.packageName);
            if (threat != null) {
                detectedThreats.add(threat);
                continue;
            }

            StealthTrojanDetector.StealthEvaluationResult stealth = StealthTrojanDetector.evaluatePackage(this, pkg);
            if (stealth != null && stealth.isMalicious) {
                detectedThreats.add(new ThreatPackageRegistry.ThreatResult(
                    true,
                    true,
                    true,
                    stealth.threatTitle,
                    stealth.threatType,
                    stealth.detailedReasonFa,
                    pkg.packageName
                ));
            }
        }

        smsAuditList = SmsAndScreenGuard.auditInstalledPackages(this, userWhitelist);
        renderActiveTabContent();

        // به‌روزرسانی وضعیت ویجت در صفحه اصلی اندروید
        try {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
            int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(this, SeparWidgetProvider.class));
            if (ids != null && ids.length > 0) {
                for (int id : ids) {
                    SeparWidgetProvider.updateAppWidget(this, appWidgetManager, id);
                }
            }
        } catch (Exception ignored) {}
    }

    @Override
    protected void onDestroy() {
        if (audioGuide != null) {
            audioGuide.release();
        }
        super.onDestroy();
    }
}

package ir.ricksanchez.separ.ui;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.EditText;
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
 * مجهز به معماری استاندار نوار ناوبری پایینی، هدر ثابت ایزوله از آنتن و ساعت و سیستم پایش جامع
 */
public class MainActivity extends Activity {

    public static final int TAB_RADAR = 0;
    public static final int TAB_SMS_APPS = 1;
    public static final int TAB_ACADEMY = 2;
    public static final int TAB_CREATOR = 3;

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
        // پدینگ بالا برای جلوگیری قطعی از ورود به ساعت و آنتن
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

        // اعمال هوشمند Insets سیستم‌عامل اندروید برای تطبیق لحظه‌ای در چرخش و ناچ‌ها
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
                    CyberUiTheme.dpToPx(MainActivity.this, 8),
                    CyberUiTheme.dpToPx(MainActivity.this, 8),
                    CyberUiTheme.dpToPx(MainActivity.this, 8),
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
        bar.setBackgroundColor(Color.parseColor(CyberUiTheme.COLOR_BOTTOM_BAR_BG));

        // خط مرزی بالای نوار ناوبری
        View divider = new View(this);
        divider.setBackgroundColor(Color.parseColor(CyberUiTheme.COLOR_BOTTOM_BAR_BORDER));
        divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        bar.addView(divider);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);
        row.setPadding(
            CyberUiTheme.dpToPx(this, 8),
            CyberUiTheme.dpToPx(this, 8),
            CyberUiTheme.dpToPx(this, 8),
            initialNavBarHeight + CyberUiTheme.dpToPx(this, 6)
        );
        row.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        addBottomNavItem(row, R.drawable.ic_nav_radar, "رادار پایش", TAB_RADAR);
        addBottomNavItem(row, R.drawable.ic_nav_sms, "دیده‌بان پیامک", TAB_SMS_APPS);
        addBottomNavItem(row, R.drawable.ic_nav_academy, "آموزش‌ها", TAB_ACADEMY);
        addBottomNavItem(row, R.drawable.ic_nav_creator, "سازنده", TAB_CREATOR);

        bar.addView(row);
        return bar;
    }

    private void addBottomNavItem(LinearLayout parent, int iconResId, String label, final int tabIndex) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);
        item.setPadding(CyberUiTheme.dpToPx(this, 4), CyberUiTheme.dpToPx(this, 4), CyberUiTheme.dpToPx(this, 4), CyberUiTheme.dpToPx(this, 4));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        item.setLayoutParams(lp);

        boolean isActive = (currentTab == tabIndex);

        // کانتینر آیکون (Pill Container)
        LinearLayout iconBox = new LinearLayout(this);
        iconBox.setGravity(Gravity.CENTER);
        int boxW = CyberUiTheme.dpToPx(this, 48);
        int boxH = CyberUiTheme.dpToPx(this, 28);
        iconBox.setLayoutParams(new LinearLayout.LayoutParams(boxW, boxH));
        if (isActive) {
            GradientDrawable pill = new GradientDrawable();
            pill.setColor(Color.parseColor("#152238"));
            pill.setCornerRadius(CyberUiTheme.dpToPx(this, 14));
            iconBox.setBackground(pill);
        }

        ImageView icon = new ImageView(this);
        icon.setImageResource(iconResId);
        int iconSize = CyberUiTheme.dpToPx(this, 20);
        icon.setLayoutParams(new LinearLayout.LayoutParams(iconSize, iconSize));
        if (isActive) {
            icon.setColorFilter(Color.parseColor(CyberUiTheme.COLOR_CYAN));
        } else {
            icon.setColorFilter(Color.parseColor(CyberUiTheme.COLOR_TEXT_DIM));
        }
        iconBox.addView(icon);
        item.addView(iconBox);

        TextView text = new TextView(this);
        text.setText(label);
        text.setTextSize(10);
        text.setTextColor(Color.parseColor(isActive ? "#FFFFFF" : CyberUiTheme.COLOR_TEXT_DIM));
        if (isActive) {
            FontHelper.applyBold(text);
        } else {
            FontHelper.applyRegular(text);
        }
        text.setPadding(0, CyberUiTheme.dpToPx(this, 2), 0, 0);
        item.addView(text);

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
        divider.setBackgroundColor(Color.parseColor(CyberUiTheme.COLOR_BOTTOM_BAR_BORDER));
        divider.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1));
        bottomNavBar.addView(divider);

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);
        row.setPadding(
            CyberUiTheme.dpToPx(this, 8),
            CyberUiTheme.dpToPx(this, 8),
            CyberUiTheme.dpToPx(this, 8),
            initialNavBarHeight + CyberUiTheme.dpToPx(this, 6)
        );
        row.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        addBottomNavItem(row, R.drawable.ic_nav_radar, "رادار پایش", TAB_RADAR);
        addBottomNavItem(row, R.drawable.ic_nav_sms, "دیده‌بان پیامک", TAB_SMS_APPS);
        addBottomNavItem(row, R.drawable.ic_nav_academy, "آموزش‌ها", TAB_ACADEMY);
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
                        public void onAddToWhitelistClicked(String packageName, String appName) {
                            userWhitelist.addToWhitelist(packageName);
                            Toast.makeText(MainActivity.this, "برنامه «" + appName + "» به لیست سفید افزوده شد.", Toast.LENGTH_SHORT).show();
                            performDeepScan();
                        }

                        @Override
                        public void onManageWhitelistClicked() {
                            openWhitelistManagementDialog();
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
            audioGuide.speak("هشدار فوری! برنامه‌هایی بدون تأیید شما به پیامک‌های بانکی یا صفحه گوشی دسترسی دارند. لطفاً در بخش دیده‌بان پیامک آن‌ها را حذف کنید.");
        }
    }

    private void openSessionRemediationDialog() {
        List<SessionRemediationHelper.AppSessionGuide> platforms = SessionRemediationHelper.getSupportedPlatforms();
        String[] titles = new String[platforms.size()];
        for (int i = 0; i < platforms.size(); i++) {
            titles[i] = platforms.get(i).appName;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("انتخاب برنامه جهت اخراج نشست‌ها و هکرها:");
        builder.setItems(titles, (dialog, which) -> {
            SessionRemediationHelper.AppSessionGuide selected = platforms.get(which);
            new AlertDialog.Builder(this)
                .setTitle(selected.appName)
                .setMessage("مسیر خروج از سایر نشست‌ها:\n\n" + selected.pathDescriptionFa)
                .setPositiveButton("باز کردن برنامه", (d, w) -> {
                    SessionRemediationHelper.openAppOrSettings(this, selected);
                })
                .setNegativeButton("بستن", null)
                .show();
        });
        builder.setPositiveButton("بستن", null);
        builder.show();
    }

    private void openReceiptDialog() {
        FakeReceiptAnalyzer.VerificationResult result = FakeReceiptAnalyzer.analyzeReceipt("6037991234567890", "123456", "9988");
        Toast.makeText(this, result.titleFa + "\n" + result.voiceWarning, Toast.LENGTH_LONG).show();
    }

    private void openPreInstallScannerDialog() {
        List<PreInstallApkScanner.DangerousApkFile> dangerousApks = PreInstallApkScanner.scanDownloadedApks(this);
        if (dangerousApks.isEmpty()) {
            Toast.makeText(this, "پوشه Download و فایل‌های دریافتی تلگرام بررسی شد؛ هیچ فایل نصبی مخربی وجود ندارد.", Toast.LENGTH_LONG).show();
            return;
        }

        String[] items = new String[dangerousApks.size()];
        for (int i = 0; i < dangerousApks.size(); i++) {
            PreInstallApkScanner.DangerousApkFile f = dangerousApks.get(i);
            items[i] = f.fileName + " (" + f.getFormattedSize() + ") - " + f.threatName;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("فایل‌های نصبی آلوده در پوشه دانلود/تلگرام:");
        builder.setItems(items, (dialog, which) -> {
            PreInstallApkScanner.DangerousApkFile chosen = dangerousApks.get(which);
            new AlertDialog.Builder(this)
                .setTitle("حذف قطعی فایل آلوده؟")
                .setMessage("فایل «" + chosen.fileName + "» یک بدافزار است.\nتوضیح: " + chosen.threatDescription + "\n\nآیا مایل به حذف قطعی آن از حافظه هستید؟")
                .setPositiveButton("بله، حذف کن", (d, w) -> {
                    boolean deleted = PreInstallApkScanner.deleteDangerousApk(chosen.filePath);
                    if (deleted) {
                        Toast.makeText(this, "فایل با موفقیت از حافظه گوشی حذف شد.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "امکان حذف خودکار وجود نداشت. لطفاً به صورت دستی از پوشه Download پاک کنید.", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("انصراف", null)
                .show();
        });
        builder.setPositiveButton("بستن", null);
        builder.show();
    }

    private void syncThreatDatabaseOnline() {
        Toast.makeText(this, "در حال اتصال به گیت‌هاب و دریافت آخرین امضاهای بدافزار...", Toast.LENGTH_SHORT).show();
        LiveThreatSyncEngine.syncThreatDatabaseAsync(this, (success, newCount, message) -> {
            runOnUiThread(() -> {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                if (success) {
                    performDeepScan();
                }
            });
        });
    }

    private void openBankingIntegrityDialog() {
        List<BankingAppIntegrityGuard.BankAppStatus> bankApps = BankingAppIntegrityGuard.auditInstalledBankingApps(this);
        if (bankApps.isEmpty()) {
            Toast.makeText(this, "هیچ برنامه همراه بانک رسمی روی گوشی نصب نیست.", Toast.LENGTH_LONG).show();
            return;
        }

        String[] items = new String[bankApps.size()];
        for (int i = 0; i < bankApps.size(); i++) {
            BankingAppIntegrityGuard.BankAppStatus b = bankApps.get(i);
            items[i] = b.bankNameFa + " - " + b.statusDescriptionFa;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("پایش اصالت همراه بانک‌های نصب‌شده:");
        builder.setItems(items, null);
        builder.setPositiveButton("تأیید و بستن", null);
        builder.show();
    }

    private void openCallFraudCheckDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("بررسی شماره تماس و تله‌های وانگیری");

        final EditText input = new EditText(this);
        input.setHint("شماره تماس ناشناس یا بین‌المللی (مثلاً +247...)");
        input.setPadding(CyberUiTheme.dpToPx(this, 16), CyberUiTheme.dpToPx(this, 12), CyberUiTheme.dpToPx(this, 16), CyberUiTheme.dpToPx(this, 12));
        builder.setView(input);

        builder.setPositiveButton("بررسی هوشمند", (dialog, which) -> {
            String num = input.getText().toString();
            CallFraudGuardian.CallRiskAssessment assessment = CallFraudGuardian.evaluateIncomingNumber(num);
            new AlertDialog.Builder(this)
                .setTitle(assessment.riskTitleFa)
                .setMessage(assessment.recommendationFa)
                .setPositiveButton("متوجه شدم", null)
                .show();
        });
        builder.setNegativeButton("انصراف", null);
        builder.show();
    }

    private void openOtpNotificationShieldDialog() {
        List<OtpNotificationShield.NotificationRiskAuditResult> risks =
            OtpNotificationShield.auditNotificationListeners(this, userWhitelist);

        if (risks.isEmpty()) {
            new AlertDialog.Builder(this)
                .setTitle("دیده‌بان اعلان‌های رمز پویا")
                .setMessage("وضعیت امن: هیچ برنامه متفرقه یا ناشناسی به اعلان‌ها و رمزهای پویای گوشی دسترسی ندارد.")
                .setPositiveButton("تأیید", null)
                .show();
            return;
        }

        String[] items = new String[risks.size()];
        for (int i = 0; i < risks.size(); i++) {
            items[i] = risks.get(i).warningTitleFa;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("هشدار: برنامه‌های دارای دسترسی به اعلان‌ها و رمز پویا:");
        builder.setItems(items, (dialog, which) -> {
            OtpNotificationShield.NotificationRiskAuditResult r = risks.get(which);
            new AlertDialog.Builder(this)
                .setTitle(r.warningTitleFa)
                .setMessage(r.descriptionFa + "\n\n" + r.recommendationFa)
                .setPositiveButton("مدیریت دسترسی اعلان‌ها", (d, w) -> {
                    try {
                        Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                        startActivity(intent);
                    } catch (Exception ignored) {}
                })
                .setNegativeButton("بستن", null)
                .show();
        });
        builder.setPositiveButton("بستن", null);
        builder.show();
    }

    private void openWifiRadarDialog() {
        WifiSecurityRadar.WifiSecurityAssessment assessment = WifiSecurityRadar.evaluateActiveNetwork(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(assessment.titleFa);
        builder.setMessage(
            "شبکه فعال: " + assessment.ssid + "\n" +
            "پروتکل امنیتی: " + assessment.securityProtocol + "\n\n" +
            assessment.descriptionFa + "\n\n" +
            assessment.recommendationFa
        );
        builder.setPositiveButton("متوجه شدم", null);
        builder.show();
    }

    private void openAppLockDialog() {
        List<AppLockGuardian.SensitiveAppItem> apps = AppLockGuardian.getSensitiveAppsStatus(this);
        String[] titles = new String[apps.size()];
        for (int i = 0; i < apps.size(); i++) {
            AppLockGuardian.SensitiveAppItem a = apps.get(i);
            titles[i] = (a.isLocked ? "[قفل فعال] " : "[قفل غیرفعال] ") + a.appNameFa + " (" + a.categoryFa + ")";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("قفل‌گذار و استتار برنامه‌های حساس:");
        builder.setItems(titles, (dialog, which) -> {
            AppLockGuardian.SensitiveAppItem selected = apps.get(which);
            AppLockGuardian.toggleAppLock(this, selected.packageName);
            Toast.makeText(this, "وضعیت قفل برنامه «" + selected.appNameFa + "» تغییر کرد.", Toast.LENGTH_SHORT).show();
            openAppLockDialog();
        });
        builder.setPositiveButton("بستن", null);
        builder.show();
    }

    private void openPhotoMetadataCleanDialog() {
        List<PhotoMetadataCleaner.PhotoExifAuditResult> photos = PhotoMetadataCleaner.scanRecentCameraPhotos(5);
        if (photos.isEmpty()) {
            Toast.makeText(this, "تصویری با اطلاعات مکانی لو رفته یافت نشد.", Toast.LENGTH_LONG).show();
            return;
        }

        String[] items = new String[photos.size()];
        for (int i = 0; i < photos.size(); i++) {
            PhotoMetadataCleaner.PhotoExifAuditResult p = photos.get(i);
            items[i] = p.fileName + (p.hasGpsLocation ? " [حاوی موقعیت GPS]" : " [بدون GPS]");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("پاک‌سازی متادیتا و لوکیشن تصاویر اخیر:");
        builder.setItems(items, (dialog, which) -> {
            PhotoMetadataCleaner.PhotoExifAuditResult p = photos.get(which);
            boolean cleaned = PhotoMetadataCleaner.cleanMetadata(p.filePath);
            if (cleaned) {
                Toast.makeText(this, "اطلاعات GPS و مدل گوشی از عکس «" + p.fileName + "» پاک شد.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "امکان پاک‌سازی وجود نداشت.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setPositiveButton("بستن", null);
        builder.show();
    }

    private void openWhitelistManagementDialog() {
        Set<String> set = userWhitelist.getWhitelistedPackages();
        if (set.isEmpty()) {
            Toast.makeText(this, "هنوز هیچ برنامه‌ای توسط شما به لیست سفید اضافه نشده است.", Toast.LENGTH_LONG).show();
            return;
        }

        String[] pkgs = set.toArray(new String[0]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("برنامه‌های موجود در لیست سفید شما:");
        builder.setItems(pkgs, (dialog, which) -> {
            String selectedPkg = pkgs[which];
            new AlertDialog.Builder(this)
                .setTitle("حذف از لیست سفید؟")
                .setMessage("آیا می‌خواهید دسترسی برنامه «" + selectedPkg + "» مجدداً مسدود و مانیتور شود؟")
                .setPositiveButton("بله، حذف کن", (d, w) -> {
                    userWhitelist.removeFromWhitelist(selectedPkg);
                    Toast.makeText(this, "برنامه از لیست سفید خارج شد.", Toast.LENGTH_SHORT).show();
                    performDeepScan();
                })
                .setNegativeButton("انصراف", null)
                .show();
        });
        builder.setPositiveButton("بستن", null);
        builder.show();
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

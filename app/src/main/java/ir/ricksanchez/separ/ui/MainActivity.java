package ir.ricksanchez.separ.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import ir.ricksanchez.separ.SeparApp;
import ir.ricksanchez.separ.audio.AudioGuideEngine;
import ir.ricksanchez.separ.billing.PromoAndAffiliateManager;
import ir.ricksanchez.separ.billing.UnifiedBillingManager;
import ir.ricksanchez.separ.data.FakeReceiptAnalyzer;
import ir.ricksanchez.separ.data.MediaPartnerRepository;
import ir.ricksanchez.separ.data.RescueLedgerRepository;
import ir.ricksanchez.separ.data.SmsAndScreenGuard;
import ir.ricksanchez.separ.data.StealthTrojanDetector;
import ir.ricksanchez.separ.data.ThreatPackageRegistry;
import ir.ricksanchez.separ.data.UserCustomWhitelistManager;
import ir.ricksanchez.separ.data.UserModeManager;
import ir.ricksanchez.separ.data.VideoTutorialRepository;
import ir.ricksanchez.separ.utils.CallForwardingAuditor;
import ir.ricksanchez.separ.utils.PrivacyPolicyHelper;
import ir.ricksanchez.separ.utils.ProminentDisclosureHelper;
import ir.ricksanchez.separ.utils.SessionRemediationHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * رابط کاربری اصلی و فوق‌پیشرفته سایبری «سپر» (Cyber-SOC HUD)
 * مجهز به معماری تب‌بندی‌شده ۴ لایه، رادار مدور، دیده‌بان پیامک، آکادمی آپارات و سیستم لایسنس
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

    private LinearLayout mainContainer;
    private LinearLayout tabsHeaderLayout;
    private LinearLayout tabContentContainer;

    private List<ThreatPackageRegistry.ThreatResult> detectedThreats = new ArrayList<>();
    private List<SmsAndScreenGuard.AppAuditResult> smsAuditList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // اعمال سیاست امنیتی ضد اسکرین‌شات
        SmsAndScreenGuard.enforceScreenSecurity(this);

        audioGuide = new AudioGuideEngine(this);
        billingManager = SeparApp.getInstance().getBillingManager();
        promoManager = SeparApp.getInstance().getPromoManager();
        rescueLedger = SeparApp.getInstance().getRescueLedger();
        userModeManager = SeparApp.getInstance().getUserModeManager();
        userWhitelist = SeparApp.getInstance().getUserWhitelist();

        // نمایش شفافیت دسترسی‌ها در اولین اجرا
        ProminentDisclosureHelper.showDisclosureIfNeeded(this, null);

        buildScaffoldUi();
        performDeepScan();
    }

    private void buildScaffoldUi() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.parseColor(CyberUiTheme.COLOR_BG));
        scrollView.setFillViewport(true);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));

        mainContainer = new LinearLayout(this);
        mainContainer.setOrientation(LinearLayout.VERTICAL);
        mainContainer.setPadding(28, 32, 28, 40);
        mainContainer.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        scrollView.addView(mainContainer);

        setContentView(scrollView);
        renderFullInterface();
    }

    private void renderFullInterface() {
        mainContainer.removeAllViews();

        // ۱. نوار وضعیت و برند برنامه در بالاترین نقطه
        LinearLayout topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setGravity(Gravity.CENTER_VERTICAL);
        topBar.setPadding(0, 0, 0, 16);
        topBar.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout brandCol = new LinearLayout(this);
        brandCol.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        brandCol.setLayoutParams(blp);

        TextView title = new TextView(this);
        title.setText("🛡️ سپر ضد هک");
        title.setTextSize(22);
        title.setTextColor(Color.WHITE);
        title.setTypeface(null, Typeface.BOLD);
        brandCol.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("۱۰۰٪ رایگان و عام‌المنفعه • برنامه‌نویس: ریک سانچز");
        subtitle.setTextSize(11);
        subtitle.setTextColor(Color.parseColor(CyberUiTheme.COLOR_NEON_GREEN));
        brandCol.addView(subtitle);
        topBar.addView(brandCol);

        // دکمه کوچک دسترسی سریع صدا
        Button quickVoice = new Button(this);
        quickVoice.setText("🔊 راهنما");
        quickVoice.setTextColor(Color.WHITE);
        quickVoice.setTextSize(11);
        quickVoice.setBackground(CyberUiTheme.createCardBg("#1E293B", CyberUiTheme.COLOR_CYAN, 12));
        quickVoice.setPadding(16, 8, 16, 8);
        quickVoice.setOnClickListener(v -> playVoiceBriefing());
        topBar.addView(quickVoice);

        mainContainer.addView(topBar);

        // ۲. تب‌های ناوبری مدرن افقی (Horizontal Cyber Tab Bar)
        HorizontalScrollView tabScroll = new HorizontalScrollView(this);
        tabScroll.setHorizontalScrollBarEnabled(false);
        tabScroll.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        tabsHeaderLayout = new LinearLayout(this);
        tabsHeaderLayout.setOrientation(LinearLayout.HORIZONTAL);
        tabsHeaderLayout.setPadding(0, 4, 0, 16);
        tabsHeaderLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        addTabButton("🛡️ رادار امنیت", TAB_RADAR);
        addTabButton("🔒 دیده‌بان پیامک", TAB_SMS_APPS);
        addTabButton("🎬 آکادمی آپارات", TAB_ACADEMY);
        addTabButton("👤 سازنده: ریک سانچز", TAB_CREATOR);

        tabScroll.addView(tabsHeaderLayout);
        mainContainer.addView(tabScroll);

        // ۳. کانتینر محتوای تب فعال
        tabContentContainer = new LinearLayout(this);
        tabContentContainer.setOrientation(LinearLayout.VERTICAL);
        tabContentContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mainContainer.addView(tabContentContainer);

        renderActiveTabContent();
    }

    private void addTabButton(String label, final int tabIndex) {
        Button btn = new Button(this);
        btn.setText(label);
        btn.setTextSize(12);
        btn.setTypeface(null, Typeface.BOLD);

        boolean isActive = (currentTab == tabIndex);
        btn.setTextColor(Color.parseColor(isActive ? "#FFFFFF" : CyberUiTheme.COLOR_TEXT_MUTED));
        btn.setBackground(CyberUiTheme.createCardBg(isActive ? "#1E293B" : "#0F172A", isActive ? CyberUiTheme.COLOR_CYAN : null, 14));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 10, 0);
        btn.setLayoutParams(lp);
        btn.setPadding(22, 14, 22, 14);

        btn.setOnClickListener(v -> {
            currentTab = tabIndex;
            renderFullInterface();
        });

        tabsHeaderLayout.addView(btn);
    }

    private void renderActiveTabContent() {
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
        renderFullInterface();
    }

    @Override
    protected void onDestroy() {
        if (audioGuide != null) {
            audioGuide.release();
        }
        super.onDestroy();
    }
}

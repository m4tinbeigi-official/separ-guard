package ir.ricksanchez.separ.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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
import ir.ricksanchez.separ.data.BankingAppIntegrityGuard;
import ir.ricksanchez.separ.data.FakeReceiptAnalyzer;
import ir.ricksanchez.separ.data.LiveThreatSyncEngine;
import ir.ricksanchez.separ.data.MediaPartnerRepository;
import ir.ricksanchez.separ.data.PreInstallApkScanner;
import ir.ricksanchez.separ.data.RescueLedgerRepository;
import ir.ricksanchez.separ.data.SmsAndScreenGuard;
import ir.ricksanchez.separ.data.StealthTrojanDetector;
import ir.ricksanchez.separ.data.ThreatPackageRegistry;
import ir.ricksanchez.separ.data.UserCustomWhitelistManager;
import ir.ricksanchez.separ.data.UserModeManager;
import ir.ricksanchez.separ.data.VideoTutorialRepository;
import ir.ricksanchez.separ.services.CallFraudGuardian;
import ir.ricksanchez.separ.utils.CallForwardingAuditor;
import ir.ricksanchez.separ.utils.FataIncidentReportExporter;
import ir.ricksanchez.separ.utils.PrivacyPolicyHelper;
import ir.ricksanchez.separ.utils.ProminentDisclosureHelper;
import ir.ricksanchez.separ.utils.SessionRemediationHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * رابط کاربری اصلی و فوق‌پیشرفته سایبری «سپر» (Cyber-SOC HUD)
 * مجهز به نوار ناوبری مدرن پایینی (Bottom Navigation Bar)، رادار متمرکز، دیده‌بان پیامک و تایپوگرافی وزیرمتن
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
    private ScrollView contentScrollView;
    private LinearLayout tabContentContainer;
    private LinearLayout bottomNavBar;

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
        // ۱. چیدمان عمودی کل صفحه
        rootAppLayout = new LinearLayout(this);
        rootAppLayout.setOrientation(LinearLayout.VERTICAL);
        rootAppLayout.setBackgroundColor(Color.parseColor(CyberUiTheme.COLOR_BG));
        rootAppLayout.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));

        // ۲. ناحیه محتوای پیمایشی بالا (وزن ۱.۰ برای پر کردن فضا تا بالای نوار ناوبری)
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
        scrollInner.setPadding(24, 28, 24, 28);
        scrollInner.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // هدر بالای صفحه
        scrollInner.addView(buildTopHeader());

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

        // ۳. نوار ناوبری مدرن پایینی (Fixed Bottom Navigation Bar)
        bottomNavBar = buildBottomNavBar();
        rootAppLayout.addView(bottomNavBar);

        setContentView(rootAppLayout);
        renderActiveTabContent();
    }

    private View buildTopHeader() {
        LinearLayout topBar = new LinearLayout(this);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setGravity(Gravity.CENTER_VERTICAL);
        topBar.setPadding(0, 0, 0, 18);
        topBar.setLayoutParams(new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // آیکون سپر در سمت راست
        ImageView logoView = new ImageView(this);
        logoView.setImageResource(R.drawable.ic_shield);
        int logoSizePx = 36;
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(logoSizePx, logoSizePx);
        llp.setMargins(14, 0, 0, 0);
        logoView.setLayoutParams(llp);
        topBar.addView(logoView);

        // ستون متن عنوان و زیرعنوان
        LinearLayout brandCol = new LinearLayout(this);
        brandCol.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        brandCol.setLayoutParams(blp);

        TextView title = new TextView(this);
        title.setText("سپر ضد هک");
        title.setTextSize(18);
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
        liveChip.setPadding(12, 6, 12, 6);
        liveChip.setBackground(CyberUiTheme.createCardBg("#0F2618", CyberUiTheme.COLOR_NEON_GREEN, 10));
        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        clp.setMargins(8, 0, 8, 0);
        liveChip.setLayoutParams(clp);
        topBar.addView(liveChip);

        // دکمه دستیار صوتی
        ImageView voiceBtn = new ImageView(this);
        voiceBtn.setImageResource(R.drawable.ic_volume_up);
        int vSizePx = 36;
        LinearLayout.LayoutParams vlp = new LinearLayout.LayoutParams(vSizePx, vSizePx);
        voiceBtn.setLayoutParams(vlp);
        voiceBtn.setPadding(6, 6, 6, 6);
        voiceBtn.setBackground(CyberUiTheme.createCardBg("#131D2E", CyberUiTheme.COLOR_CYAN, 12));
        voiceBtn.setOnClickListener(v -> playVoiceBriefing());
        topBar.addView(voiceBtn);

        return topBar;
    }

    private LinearLayout buildBottomNavBar() {
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER);
        bar.setBackgroundColor(Color.parseColor(CyberUiTheme.COLOR_BOTTOM_BAR_BG));
        bar.setPadding(10, 8, 10, 8);

        LinearLayout.LayoutParams barParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        bar.setLayoutParams(barParams);

        addBottomNavItem(bar, R.drawable.ic_nav_radar, "رادار پایش", TAB_RADAR);
        addBottomNavItem(bar, R.drawable.ic_nav_sms, "دیده‌بان پیامک", TAB_SMS_APPS);
        addBottomNavItem(bar, R.drawable.ic_nav_academy, "آموزش‌ها", TAB_ACADEMY);
        addBottomNavItem(bar, R.drawable.ic_nav_creator, "سازنده", TAB_CREATOR);

        return bar;
    }

    private void addBottomNavItem(LinearLayout parent, int iconResId, String label, final int tabIndex) {
        LinearLayout item = new LinearLayout(this);
        item.setOrientation(LinearLayout.VERTICAL);
        item.setGravity(Gravity.CENTER);
        item.setPadding(6, 6, 6, 6);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        item.setLayoutParams(lp);

        boolean isActive = (currentTab == tabIndex);

        ImageView icon = new ImageView(this);
        icon.setImageResource(iconResId);
        int iconSize = 22;
        LinearLayout.LayoutParams ilp = new LinearLayout.LayoutParams(iconSize, iconSize);
        icon.setLayoutParams(ilp);
        if (isActive) {
            icon.setColorFilter(Color.parseColor(CyberUiTheme.COLOR_CYAN));
        } else {
            icon.setColorFilter(Color.parseColor(CyberUiTheme.COLOR_TEXT_DIM));
        }
        item.addView(icon);

        TextView text = new TextView(this);
        text.setText(label);
        text.setTextSize(10);
        text.setTextColor(Color.parseColor(isActive ? "#FFFFFF" : CyberUiTheme.COLOR_TEXT_DIM));
        if (isActive) {
            FontHelper.applyBold(text);
        } else {
            FontHelper.applyRegular(text);
        }
        text.setPadding(0, 3, 0, 0);
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
        addBottomNavItem(bottomNavBar, R.drawable.ic_nav_radar, "رادار پایش", TAB_RADAR);
        addBottomNavItem(bottomNavBar, R.drawable.ic_nav_sms, "دیده‌بان پیامک", TAB_SMS_APPS);
        addBottomNavItem(bottomNavBar, R.drawable.ic_nav_academy, "آموزش‌ها", TAB_ACADEMY);
        addBottomNavItem(bottomNavBar, R.drawable.ic_nav_creator, "سازنده", TAB_CREATOR);
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
        input.setPadding(30, 24, 30, 24);
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
    }

    @Override
    protected void onDestroy() {
        if (audioGuide != null) {
            audioGuide.release();
        }
        super.onDestroy();
    }
}

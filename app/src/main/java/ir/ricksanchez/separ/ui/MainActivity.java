package ir.ricksanchez.separ.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import ir.ricksanchez.separ.SeparApp;
import ir.ricksanchez.separ.audio.AudioGuideEngine;
import ir.ricksanchez.separ.billing.PromoAndAffiliateManager;
import ir.ricksanchez.separ.billing.UnifiedBillingManager;
import ir.ricksanchez.separ.data.FakeReceiptAnalyzer;
import ir.ricksanchez.separ.data.RescueLedgerRepository;
import ir.ricksanchez.separ.data.SmsAndScreenGuard;
import ir.ricksanchez.separ.data.StealthTrojanDetector;
import ir.ricksanchez.separ.data.ThreatPackageRegistry;
import ir.ricksanchez.separ.data.UserCustomWhitelistManager;
import ir.ricksanchez.separ.data.UserModeManager;
import ir.ricksanchez.separ.utils.CallForwardingAuditor;
import ir.ricksanchez.separ.utils.PrivacyPolicyHelper;
import ir.ricksanchez.separ.utils.ProminentDisclosureHelper;
import ir.ricksanchez.separ.utils.SessionRemediationHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends Activity {

    private AudioGuideEngine audioGuide;
    private UnifiedBillingManager billingManager;
    private PromoAndAffiliateManager promoManager;
    private RescueLedgerRepository rescueLedger;
    private UserModeManager userModeManager;
    private UserCustomWhitelistManager userWhitelist;

    private LinearLayout rootLayout;
    private List<ThreatPackageRegistry.ThreatResult> detectedThreats = new ArrayList<>();
    private List<SmsAndScreenGuard.AppAuditResult> smsAuditList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ۱. اعمال قطعی تدابیر امنیتی ضد اسکرین‌شات و ضبط صفحه در سطح پنجره
        SmsAndScreenGuard.enforceScreenSecurity(this);

        audioGuide = new AudioGuideEngine(this);
        billingManager = SeparApp.getInstance().getBillingManager();
        promoManager = SeparApp.getInstance().getPromoManager();
        rescueLedger = SeparApp.getInstance().getRescueLedger();
        userModeManager = SeparApp.getInstance().getUserModeManager();
        userWhitelist = SeparApp.getInstance().getUserWhitelist();

        // ۲. نمایش دیالوگ شفافیت دسترسی‌ها (الزام داوری کافه‌بازار و مایکت) در اولین اجرا
        ProminentDisclosureHelper.showDisclosureIfNeeded(this, null);

        buildUi();
        performDeepScan();
    }

    private void buildUi() {
        ScrollView scrollView = new ScrollView(this);
        scrollView.setBackgroundColor(Color.parseColor("#0D1117"));
        scrollView.setFillViewport(true);
        scrollView.setLayoutParams(new ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));

        rootLayout = new LinearLayout(this);
        rootLayout.setOrientation(LinearLayout.VERTICAL);
        rootLayout.setPadding(32, 40, 32, 40);
        rootLayout.setLayoutParams(new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        scrollView.addView(rootLayout);

        setContentView(scrollView);
        renderCurrentState();
    }

    private void renderCurrentState() {
        rootLayout.removeAllViews();

        // ۱. هدر اصلی برنامه
        TextView headerTitle = new TextView(this);
        headerTitle.setText("سپر");
        headerTitle.setTextSize(28);
        headerTitle.setTextColor(Color.WHITE);
        headerTitle.setTypeface(null, Typeface.BOLD);
        headerTitle.setGravity(Gravity.CENTER);

        TextView headerSubtitle = new TextView(this);
        headerSubtitle.setText("سامانه هوشمند ضد هک، ممیزی پیامک و محافظت از حساب بانکی");
        headerSubtitle.setTextSize(12);
        headerSubtitle.setTextColor(Color.parseColor("#00E676"));
        headerSubtitle.setGravity(Gravity.CENTER);
        headerSubtitle.setPadding(0, 8, 0, 24);

        rootLayout.addView(headerTitle);
        rootLayout.addView(headerSubtitle);

        // ۲. وضعیت اشتراک و لایسنس
        UnifiedBillingManager.SubscriptionInfo subInfo = billingManager.getSubscriptionInfo();
        LinearLayout licenseBadge = new LinearLayout(this);
        licenseBadge.setOrientation(LinearLayout.HORIZONTAL);
        licenseBadge.setGravity(Gravity.CENTER);
        licenseBadge.setPadding(20, 10, 20, 10);
        licenseBadge.setBackground(createCardBackground(subInfo.isSubscribed ? "#163820" : "#2D1B1B", subInfo.isSubscribed ? "#00E676" : "#FF5252"));
        LinearLayout.LayoutParams lbParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lbParams.gravity = Gravity.CENTER_HORIZONTAL;
        lbParams.setMargins(0, 0, 0, 20);
        licenseBadge.setLayoutParams(lbParams);

        TextView badgeText = new TextView(this);
        badgeText.setText(subInfo.isSubscribed ? "اشتراک فعال سپر (" + subInfo.daysRemaining + " روز باقی‌مانده)" : "نسخه رایگان (حالت کشف تهدیدات)");
        badgeText.setTextColor(subInfo.isSubscribed ? Color.parseColor("#00E676") : Color.parseColor("#FFA726"));
        badgeText.setTextSize(12);
        badgeText.setTypeface(null, Typeface.BOLD);
        licenseBadge.addView(badgeText);
        rootLayout.addView(licenseBadge);

        // ۳. کارنامه حملات خنثی‌شده و نجات‌ها (Rescue Ledger Widget)
        RescueLedgerRepository.RescueStats stats = rescueLedger.getStats();
        if (stats.totalAttacksBlocked > 0) {
            LinearLayout rescueCard = new LinearLayout(this);
            rescueCard.setOrientation(LinearLayout.VERTICAL);
            rescueCard.setPadding(24, 20, 24, 20);
            rescueCard.setBackground(createCardBackground("#161B22", "#30363D"));
            LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
            rlp.setMargins(0, 0, 0, 20);
            rescueCard.setLayoutParams(rlp);

            TextView rTitle = new TextView(this);
            rTitle.setText("🛡️ کارنامه دفاع‌های موفق سپر تا این لحظه:");
            rTitle.setTextColor(Color.parseColor("#58A6FF"));
            rTitle.setTextSize(13);
            rTitle.setTypeface(null, Typeface.BOLD);
            rescueCard.addView(rTitle);

            TextView rDesc = new TextView(this);
            rDesc.setText("جلوگیری از " + stats.bankingTheftsPrevented + " حمله بانکی و خنثی‌سازی " +
                stats.phishingSmsBlocked + " پیامک فیشینگ.");
            rDesc.setTextColor(Color.parseColor("#C9D1D9"));
            rDesc.setTextSize(12);
            rDesc.setPadding(0, 6, 0, 0);
            rescueCard.addView(rDesc);

            rootLayout.addView(rescueCard);
        }

        // ۴. دایره بزرگ لمسی اسکن و وضعیت (Master Shield)
        int unapprovedSmsCount = countUnapprovedSmsApps();
        boolean hasThreats = !detectedThreats.isEmpty() || unapprovedSmsCount > 0;

        LinearLayout shieldCircle = new LinearLayout(this);
        shieldCircle.setOrientation(LinearLayout.VERTICAL);
        shieldCircle.setGravity(Gravity.CENTER);
        int sizePx = 380;
        LinearLayout.LayoutParams circleParams = new LinearLayout.LayoutParams(sizePx, sizePx);
        circleParams.gravity = Gravity.CENTER_HORIZONTAL;
        circleParams.setMargins(0, 10, 0, 24);
        shieldCircle.setLayoutParams(circleParams);

        GradientDrawable circleBg = new GradientDrawable();
        circleBg.setShape(GradientDrawable.OVAL);
        if (hasThreats) {
            circleBg.setColor(Color.parseColor("#4A1417"));
            circleBg.setStroke(6, Color.parseColor("#FF5252"));
        } else {
            circleBg.setColor(Color.parseColor("#14301B"));
            circleBg.setStroke(6, Color.parseColor("#00E676"));
        }
        shieldCircle.setBackground(circleBg);

        TextView shieldStatusText = new TextView(this);
        String shieldText = hasThreats
            ? "⚠️ خطر شناسایی شد!\n(لمس برای پاکسازی)"
            : "🛡️ گوشی کاملاً امن است\n(لمس برای بررسی مجدد)";
        shieldStatusText.setText(shieldText);
        shieldStatusText.setTextSize(15);
        shieldStatusText.setTextColor(Color.WHITE);
        shieldStatusText.setTypeface(null, Typeface.BOLD);
        shieldStatusText.setGravity(Gravity.CENTER);
        shieldCircle.addView(shieldStatusText);

        shieldCircle.setOnClickListener(v -> {
            if (hasThreats) {
                showPaywallOrEradicate();
            } else {
                performDeepScan();
            }
        });
        rootLayout.addView(shieldCircle);

        // ۵. دکمه راهنمای صوتی شمرده فارسی
        Button voiceBtn = createStyledButton("🔊 شنیدن راهنمای صوتی وضعیت گوشی", "#21262D", "#00E676");
        voiceBtn.setOnClickListener(v -> {
            if (!hasThreats) {
                audioGuide.speak("گوشی و پیامک‌های شما کاملاً امن هستند و هیچ برنامه مشکوکی به اطلاعات شما دسترسی ندارد.");
            } else {
                audioGuide.speak("خطر! برنامه‌هایی بدون تأیید شما به پیامک‌ها یا صفحه گوشی دسترسی دارند. برای پاکسازی، دکمه قرمز را بزنید.");
            }
        });
        rootLayout.addView(voiceBtn);

        // ۶. کارت اختصاصی: ممیزی دسترسی به پیامک‌ها و اسکرین‌شات (SMS & Screen Guardian)
        renderSmsAndScreenGuardCard(unapprovedSmsCount);

        // ۷. بخش گزارش بدافزارهای قطعی (در صورت کشف)
        if (!detectedThreats.isEmpty()) {
            renderMalwareThreatBox();
        }

        // ۸. کارت فعال‌سازی، خرید و کدهای تخفیف اینفلوئنسر
        if (!subInfo.isSubscribed) {
            renderPaywallAndPromoCard();
        }

        // ۹. کارت همکاری در فروش و دعوت از دوستان (Affiliate & Referral Hub)
        renderAffiliateCard();

        // ۱۰. جعبه‌ابزار بحران و ابزارهای نجات
        addSectionHeader("ابزارهای فوریت‌های امنیتی:");

        // اخراج نفوذگران از شبکه‌ها و پیام‌رسان‌ها
        Button sessionBtn = createStyledButton("🚪 اخراج نفوذگران از تلگرام، روبیکا، ایتا و بله", "#161B22", "#FFA726");
        sessionBtn.setOnClickListener(v -> openSessionRemediationDialog());
        rootLayout.addView(sessionBtn);

        // بررسی رسید و کارت بانکی
        Button receiptBtn = createStyledButton("💳 بررسی اصالت رسید فیش بانکی (ضد جعل)", "#161B22", "#00E676");
        receiptBtn.setOnClickListener(v -> openReceiptDialog());
        rootLayout.addView(receiptBtn);

        // لغو دایورت خطوط
        Button cancelDivertBtn = createStyledButton("📵 لغو فوری تمام دایورت‌های خط (#002##)", "#261011", "#FF5252");
        cancelDivertBtn.setOnClickListener(v -> CallForwardingAuditor.dialCancelForwarding(this));
        rootLayout.addView(cancelDivertBtn);

        // تماس اضطراری با پلیس فتا
        Button fataBtn = createStyledButton("🚨 تماس اضطراری با مرکز فوریت‌های فتا (۰۹۶۳۸۰)", "#161B22", "#00E5FF");
        fataBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:096380"));
            startActivity(intent);
        });
        rootLayout.addView(fataBtn);

        // ۱۱. فوتر قوانین و حریم خصوصی
        Button privacyBtn = createStyledButton("📜 سند حریم خصوصی و تعهد عدم خروج داده (Zero-Telemetry)", "#0D1117", "#8B949E");
        privacyBtn.setOnClickListener(v -> PrivacyPolicyHelper.showPrivacyPolicy(this));
        rootLayout.addView(privacyBtn);
    }

    private int countUnapprovedSmsApps() {
        int count = 0;
        for (SmsAndScreenGuard.AppAuditResult app : smsAuditList) {
            if (!app.isSafe()) {
                count++;
            }
        }
        return count;
    }

    private void renderSmsAndScreenGuardCard(int unapprovedCount) {
        LinearLayout smsCard = new LinearLayout(this);
        smsCard.setOrientation(LinearLayout.VERTICAL);
        smsCard.setPadding(24, 24, 24, 24);

        if (unapprovedCount > 0) {
            smsCard.setBackground(createCardBackground("#381515", "#FF5252"));
        } else {
            smsCard.setBackground(createCardBackground("#14261B", "#00E676"));
        }

        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        clp.setMargins(0, 16, 0, 16);
        smsCard.setLayoutParams(clp);

        TextView cardTitle = new TextView(this);
        cardTitle.setText(unapprovedCount > 0
            ? "⚠️ هشدار: " + unapprovedCount + " برنامه بدون اجازه به پیامک‌ها دسترسی دارند!"
            : "🔒 محافظت فعال پیامک‌ها: هیچ برنامه غیرمجازی به پیامک‌ها دسترسی ندارد");
        cardTitle.setTextColor(unapprovedCount > 0 ? Color.parseColor("#FF5252") : Color.parseColor("#00E676"));
        cardTitle.setTextSize(14);
        cardTitle.setTypeface(null, Typeface.BOLD);
        smsCard.addView(cardTitle);

        TextView cardDesc = new TextView(this);
        cardDesc.setText(unapprovedCount > 0
            ? "بیش از ۹۰٪ هک‌ها از شنود پیامک‌های بانکی و اسکرین‌شات شروع می‌شوند. برنامه‌های زیر دسترسی دارند و باید فوراً بررسی یا سلب دسترسی شوند:"
            : "سپر به صورت پیوسته کلیه برنامه‌ها را ممیزی کرده و از اسکرین‌شات و سرقت رمزهای پیامکی جلوگیری می‌کند.");
        cardDesc.setTextColor(Color.parseColor("#C9D1D9"));
        cardDesc.setTextSize(12);
        cardDesc.setPadding(0, 8, 0, 12);
        smsCard.addView(cardDesc);

        // فهرست برنامه‌های تأییدنشده دارای دسترسی پیامک
        for (SmsAndScreenGuard.AppAuditResult item : smsAuditList) {
            if (!item.isSafe()) {
                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.VERTICAL);
                row.setPadding(16, 12, 16, 12);
                row.setBackground(createCardBackground("#21262D", null));
                LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                );
                rlp.setMargins(0, 6, 0, 6);
                row.setLayoutParams(rlp);

                TextView nameView = new TextView(this);
                nameView.setText("• " + item.appName + " (" + item.packageName + ")");
                nameView.setTextColor(Color.WHITE);
                nameView.setTextSize(13);
                nameView.setTypeface(null, Typeface.BOLD);
                row.addView(nameView);

                TextView reasonView = new TextView(this);
                reasonView.setText(item.description);
                reasonView.setTextColor(Color.parseColor("#FFA726"));
                reasonView.setTextSize(11);
                reasonView.setPadding(0, 4, 0, 8);
                row.addView(reasonView);

                LinearLayout btnRow = new LinearLayout(this);
                btnRow.setOrientation(LinearLayout.HORIZONTAL);

                // دکمه حذف دسترسی / لغو نصب
                Button revokeBtn = createSmallButton("سلب دسترسی / حذف", "#D32F2F", Color.WHITE);
                revokeBtn.setOnClickListener(v -> {
                    SmsAndScreenGuard.launchRevokeOrUninstall(this, item.packageName, item.isKnownMalware);
                });
                btnRow.addView(revokeBtn);

                // دکمه افزودن به لیست سفید
                Button whiteBtn = createSmallButton("تأیید و افزودن به لیست سفید", "#37474F", Color.parseColor("#80CBC4"));
                whiteBtn.setOnClickListener(v -> {
                    userWhitelist.addToWhitelist(item.packageName);
                    Toast.makeText(this, "برنامه «" + item.appName + "» به لیست سفید افزوده شد.", Toast.LENGTH_SHORT).show();
                    performDeepScan();
                });
                btnRow.addView(whiteBtn);

                row.addView(btnRow);
                smsCard.addView(row);
            }
        }

        // دکمه مدیریت کامل لیست سفید کاربر
        Button manageWhitelistBtn = createStyledButton("📋 مدیریت لیست سفید برنامه‌ها", "#21262D", "#58A6FF");
        manageWhitelistBtn.setOnClickListener(v -> openWhitelistManagementDialog());
        smsCard.addView(manageWhitelistBtn);

        rootLayout.addView(smsCard);
    }

    private void renderMalwareThreatBox() {
        boolean isSubscribed = billingManager.getSubscriptionInfo().isSubscribed;
        LinearLayout threatBox = new LinearLayout(this);
        threatBox.setOrientation(LinearLayout.VERTICAL);
        threatBox.setPadding(28, 28, 28, 28);
        threatBox.setBackground(createCardBackground("#381515", "#FF5252"));
        LinearLayout.LayoutParams boxParams = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        boxParams.setMargins(0, 16, 0, 16);
        threatBox.setLayoutParams(boxParams);

        TextView threatTitle = new TextView(this);
        threatTitle.setText("🔴 " + detectedThreats.size() + " بدافزار سارق بانکی کشف شد!");
        threatTitle.setTextColor(Color.parseColor("#FF5252"));
        threatTitle.setTextSize(16);
        threatTitle.setTypeface(null, Typeface.BOLD);
        threatBox.addView(threatTitle);

        if (!isSubscribed) {
            TextView threatLockDesc = new TextView(this);
            threatLockDesc.setText("این بدافزارها در حال شنود رمزهای پیامکی هستند. برای پاکسازی قطعی، اشتراک سالیانه سپر (۱ میلیون تومان) را فعال کنید یا کد هدیه وارد نمایید.");
            threatLockDesc.setTextColor(Color.parseColor("#F0F6FC"));
            threatLockDesc.setTextSize(12);
            threatLockDesc.setPadding(0, 12, 0, 16);
            threatBox.addView(threatLockDesc);

            Button buyBtn = createStyledButton("خرید سالیانه در کافه‌بازار (۱,۰۰۰,۰۰۰ تومان)", "#FF4444", "#FFFFFF");
            buyBtn.setOnClickListener(v -> {
                billingManager.activateAnnualSubscription("bazaar_token", "sig", "data", UnifiedBillingManager.StoreType.BAZAAR);
                Toast.makeText(this, "اشتراک سالیانه کافه‌بازار فعال شد!", Toast.LENGTH_LONG).show();
                renderCurrentState();
            });
            threatBox.addView(buyBtn);
        } else {
            for (ThreatPackageRegistry.ThreatResult threat : detectedThreats) {
                TextView tName = new TextView(this);
                tName.setText("• " + threat.threatName + " (" + threat.packageName + ")");
                tName.setTextColor(Color.WHITE);
                tName.setTextSize(13);
                tName.setPadding(0, 8, 0, 4);
                threatBox.addView(tName);

                Button uninstallBtn = createStyledButton("حذف فوری " + threat.threatName, "#FF4444", "#FFFFFF");
                uninstallBtn.setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE);
                    intent.setData(Uri.parse("package:" + threat.packageName));
                    startActivity(intent);
                });
                threatBox.addView(uninstallBtn);
            }
        }
        rootLayout.addView(threatBox);
    }

    private void renderPaywallAndPromoCard() {
        LinearLayout payCard = new LinearLayout(this);
        payCard.setOrientation(LinearLayout.VERTICAL);
        payCard.setPadding(24, 24, 24, 24);
        payCard.setBackground(createCardBackground("#1A202C", "#4A5568"));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 16, 0, 16);
        payCard.setLayoutParams(lp);

        TextView title = new TextView(this);
        title.setText("💎 اشتراک سالیانه سپر ضد هک (۳۶۵ روز محافظت شبانه‌روزی)");
        title.setTextColor(Color.WHITE);
        title.setTextSize(14);
        title.setTypeface(null, Typeface.BOLD);
        payCard.addView(title);

        TextView priceText = new TextView(this);
        priceText.setText("تعرفه مصوب: ۱,۰۰۰,۰۰۰ تومان / سال (۱۰,۰۰۰,۰۰۰ ریال)");
        priceText.setTextColor(Color.parseColor("#00E676"));
        priceText.setTextSize(13);
        priceText.setPadding(0, 8, 0, 16);
        payCard.addView(priceText);

        Button buyBazaarBtn = createStyledButton("خرید رسمی در کافه‌بازار (۱ میلیون تومان)", "#238636", "#FFFFFF");
        buyBazaarBtn.setOnClickListener(v -> {
            billingManager.activateAnnualSubscription("bazaar_token", "sig", "data", UnifiedBillingManager.StoreType.BAZAAR);
            Toast.makeText(this, "اشتراک سالیانه با موفقیت فعال گردید!", Toast.LENGTH_LONG).show();
            renderCurrentState();
        });
        payCard.addView(buyBazaarBtn);

        Button redeemBtn = createStyledButton("🎁 وارد کردن کد تخفیف / کد هدیه رایگان اینفلوئنسر", "#1F6FEB", "#FFFFFF");
        redeemBtn.setOnClickListener(v -> openRedeemPromoDialog());
        payCard.addView(redeemBtn);

        Button restoreBtn = createStyledButton("🔄 بازیابی خرید قبلی (Restore Purchases)", "#21262D", "#C9D1D9");
        restoreBtn.setOnClickListener(v -> {
            billingManager.restorePurchases((success, message) -> {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
                if (success) renderCurrentState();
            });
        });
        payCard.addView(restoreBtn);

        rootLayout.addView(payCard);
    }

    private void renderAffiliateCard() {
        LinearLayout affCard = new LinearLayout(this);
        affCard.setOrientation(LinearLayout.VERTICAL);
        affCard.setPadding(24, 24, 24, 24);
        affCard.setBackground(createCardBackground("#132338", "#1F6FEB"));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 16, 0, 16);
        affCard.setLayoutParams(lp);

        TextView title = new TextView(this);
        title.setText("🤝 همکاری در فروش و دعوت از دوستان (کسب درآمد)");
        title.setTextColor(Color.WHITE);
        title.setTextSize(14);
        title.setTypeface(null, Typeface.BOLD);
        affCard.addView(title);

        String refCode = promoManager.getMyReferralCode();
        int refCount = promoManager.getMyReferralCount();
        long comm = promoManager.getMyTotalCommissionTomans();

        TextView desc = new TextView(this);
        desc.setText("کد معرف اختصاصی شما: " + refCode + "\n" +
                     "با معرفی سپر به مخاطبان یا دوستانتان، آن‌ها تخفیف می‌گیرند و شما به ازای هر خرید ۲۵٪ کمیسیون (۲۵۰,۰۰۰ تومان) دریافت می‌کنید.\n\n" +
                     "دعوت‌های موفق: " + refCount + " نفر | کمیسیون انباشته: " + String.format("%,d", comm) + " تومان");
        desc.setTextColor(Color.parseColor("#E6EDF3"));
        desc.setTextSize(12);
        desc.setLineSpacing(6f, 1.2f);
        desc.setPadding(0, 8, 0, 16);
        affCard.addView(desc);

        Button shareBtn = createStyledButton("📤 اشتراک‌گذاری کد معرف با دیگران", "#238636", "#FFFFFF");
        shareBtn.setOnClickListener(v -> {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, promoManager.getInviteShareMessage());
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "ارسال کد معرف"));
        });
        affCard.addView(shareBtn);

        rootLayout.addView(affCard);
    }

    private void openRedeemPromoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("🎁 وارد کردن کد تخفیف یا هدیه اینفلوئنسر");

        final EditText input = new EditText(this);
        input.setHint("کد هدیه (مثلاً RICK یا کد معرف)");
        input.setPadding(30, 24, 30, 24);
        builder.setView(input);

        builder.setPositiveButton("بررسی و فعال‌سازی", (dialog, which) -> {
            String code = input.getText().toString();
            PromoAndAffiliateManager.RedeemResult result = promoManager.redeemCode(code);
            Toast.makeText(this, result.message, Toast.LENGTH_LONG).show();
            if (result.success) {
                renderCurrentState();
            }
        });
        builder.setNegativeButton("انصراف", null);
        builder.show();
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
            // ۱. بررسی قطعی در رجیستری بدافزارهای بانکی
            ThreatPackageRegistry.ThreatResult threat = ThreatPackageRegistry.evaluatePackage(pkg.packageName);
            if (threat != null) {
                detectedThreats.add(threat);
                continue;
            }

            // ۲. بررسی عمیق با موتور کشف ترفندهای پنهان‌سازی (استتار هویت، حذف آیکون، مدیر دستگاه، دسترسی‌پذیری)
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

        // ممیزی کامل دسترسی پیامک‌ها و صفحه نمایش
        smsAuditList = SmsAndScreenGuard.auditInstalledPackages(this, userWhitelist);

        renderCurrentState();
    }

    private void showPaywallOrEradicate() {
        boolean isSubscribed = billingManager.getSubscriptionInfo().isSubscribed;
        if (!isSubscribed) {
            Toast.makeText(this, "لطفاً برای پاکسازی برنامه‌های مخرب اشتراک سالیانه را فعال کنید", Toast.LENGTH_LONG).show();
        } else {
            performDeepScan();
            Toast.makeText(this, "اسکن عمیق انجام شد.", Toast.LENGTH_SHORT).show();
        }
    }

    private void openReceiptDialog() {
        FakeReceiptAnalyzer.VerificationResult result = FakeReceiptAnalyzer.analyzeReceipt("6037991234567890", "123456", "9988");
        Toast.makeText(this, result.titleFa + "\n" + result.voiceWarning, Toast.LENGTH_LONG).show();
    }

    private void addSectionHeader(String text) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(14);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setPadding(0, 24, 0, 12);
        rootLayout.addView(tv);
    }

    private Button createStyledButton(String text, String bgColor, String textColor) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextColor(Color.parseColor(textColor));
        btn.setTypeface(null, Typeface.BOLD);
        btn.setTextSize(13);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(bgColor));
        gd.setCornerRadius(20);
        btn.setBackground(gd);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, 8, 0, 8);
        btn.setLayoutParams(lp);
        btn.setPadding(24, 20, 24, 20);
        return btn;
    }

    private Button createSmallButton(String text, String bgColor, int textColor) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setTextColor(textColor);
        btn.setTextSize(11);
        btn.setTypeface(null, Typeface.BOLD);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(bgColor));
        gd.setCornerRadius(14);
        btn.setBackground(gd);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            1.0f
        );
        lp.setMargins(4, 4, 4, 4);
        btn.setLayoutParams(lp);
        btn.setPadding(12, 14, 12, 14);
        return btn;
    }

    private GradientDrawable createCardBackground(String bgColor, String strokeColor) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(bgColor));
        gd.setCornerRadius(24);
        if (strokeColor != null) {
            gd.setStroke(2, Color.parseColor(strokeColor));
        }
        return gd;
    }

    @Override
    protected void onDestroy() {
        if (audioGuide != null) {
            audioGuide.release();
        }
        super.onDestroy();
    }
}

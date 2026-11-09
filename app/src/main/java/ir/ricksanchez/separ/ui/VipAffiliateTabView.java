package ir.ricksanchez.separ.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import ir.ricksanchez.separ.billing.PromoAndAffiliateManager;
import ir.ricksanchez.separ.billing.UnifiedBillingManager;

/**
 * رندر تب لایسنس، کدهای تخفیف اینفلوئنسرها و همکاری در فروش (VIP & Affiliate)
 */
public class VipAffiliateTabView {

    public interface VipAffiliateTabListener {
        void onBuyBazaarClicked();
        void onRedeemCodeClicked(String code);
        void onShareReferralClicked();
        void onRestorePurchasesClicked();
        void onPrivacyPolicyClicked();
    }

    public static LinearLayout buildView(Context context,
                                         UnifiedBillingManager.SubscriptionInfo subInfo,
                                         PromoAndAffiliateManager promoManager,
                                         final VipAffiliateTabListener listener) {

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // ۱. کارت طلایی اشتراک سالیانه
        LinearLayout vipCard = new LinearLayout(context);
        vipCard.setOrientation(LinearLayout.VERTICAL);
        vipCard.setPadding(24, 20, 24, 20);
        vipCard.setBackground(CyberUiTheme.createCardBg("#182238", CyberUiTheme.COLOR_GOLD, 20));
        LinearLayout.LayoutParams vlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vlp.setMargins(0, 0, 0, 16);
        vipCard.setLayoutParams(vlp);

        TextView vTitle = new TextView(context);
        vTitle.setText("💎 اشتراک سالیانه سپر ضد هک (۳۶۵ روز محافظت شبانه‌روزی)");
        vTitle.setTextColor(Color.WHITE);
        vTitle.setTextSize(14);
        vTitle.setTypeface(null, Typeface.BOLD);
        vipCard.addView(vTitle);

        TextView vPrice = new TextView(context);
        vPrice.setText("تعرفه مصوب: ۱,۰۰۰,۰۰۰ تومان / سال (۱۰,۰۰۰,۰۰۰ ریال)");
        vPrice.setTextColor(Color.parseColor(CyberUiTheme.COLOR_NEON_GREEN));
        vPrice.setTextSize(13);
        vPrice.setPadding(0, 6, 0, 12);
        vipCard.addView(vPrice);

        if (subInfo.isSubscribed) {
            TextView vActive = new TextView(context);
            vActive.setText("✅ اشتراک فعال شما به مدت " + subInfo.daysRemaining + " روز اعتبار دارد.");
            vActive.setTextColor(Color.parseColor(CyberUiTheme.COLOR_NEON_GREEN));
            vActive.setTextSize(12);
            vipCard.addView(vActive);
        } else {
            Button buyBtn = CyberUiTheme.createCyberButton(context, "خرید رسمی در کافه‌بازار (۱ میلیون تومان)", "#238636", "#FFFFFF", 12);
            buyBtn.setOnClickListener(v -> {
                if (listener != null) listener.onBuyBazaarClicked();
            });
            vipCard.addView(buyBtn);
        }

        // بخش وارد کردن کد هدیه یا تخفیف اینفلوئنسر
        TextView promoTitle = new TextView(context);
        promoTitle.setText("🎁 کد هدیه رایگان یا تخفیف اینفلوئنسر داری؟");
        promoTitle.setTextColor(Color.parseColor(CyberUiTheme.COLOR_CYAN));
        promoTitle.setTextSize(12);
        promoTitle.setTypeface(null, Typeface.BOLD);
        promoTitle.setPadding(0, 14, 0, 6);
        vipCard.addView(promoTitle);

        LinearLayout inputRow = new LinearLayout(context);
        inputRow.setOrientation(LinearLayout.HORIZONTAL);
        inputRow.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        final EditText promoInput = new EditText(context);
        promoInput.setHint("مثلاً RICK یا SEPAR100");
        promoInput.setHintTextColor(Color.parseColor("#64748B"));
        promoInput.setTextColor(Color.WHITE);
        promoInput.setTextSize(12);
        promoInput.setBackground(CyberUiTheme.createCardBg("#0F172A", "#334155", 14));
        promoInput.setPadding(16, 14, 16, 14);
        LinearLayout.LayoutParams iplp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        iplp.setMargins(0, 0, 8, 0);
        promoInput.setLayoutParams(iplp);
        inputRow.addView(promoInput);

        Button applyBtn = new Button(context);
        applyBtn.setText("ثبت کد");
        applyBtn.setTextColor(Color.WHITE);
        applyBtn.setTextSize(11);
        applyBtn.setBackground(CyberUiTheme.createCardBg("#1F6FEB", null, 14));
        applyBtn.setOnClickListener(v -> {
            String code = promoInput.getText().toString();
            if (listener != null) listener.onRedeemCodeClicked(code);
        });
        inputRow.addView(applyBtn);
        vipCard.addView(inputRow);

        container.addView(vipCard);

        // ۲. کارت همکاری در فروش و درآمدزایی (Affiliate Hub)
        LinearLayout affCard = new LinearLayout(context);
        affCard.setOrientation(LinearLayout.VERTICAL);
        affCard.setPadding(24, 20, 24, 20);
        affCard.setBackground(CyberUiTheme.createCardBg("#132338", "#1F6FEB", 20));
        LinearLayout.LayoutParams alp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alp.setMargins(0, 0, 0, 16);
        affCard.setLayoutParams(alp);

        TextView aTitle = new TextView(context);
        aTitle.setText("🤝 همکاری در فروش و دعوت از دوستان (کسب درآمد)");
        aTitle.setTextColor(Color.WHITE);
        aTitle.setTextSize(14);
        aTitle.setTypeface(null, Typeface.BOLD);
        affCard.addView(aTitle);

        String refCode = promoManager.getMyReferralCode();
        int refCount = promoManager.getMyReferralCount();
        long comm = promoManager.getMyTotalCommissionTomans();

        TextView aDesc = new TextView(context);
        aDesc.setText("کد معرف شما: " + refCode + "\n" +
                     "با دعوت دوستانتان، آن‌ها ۲۰٪ تخفیف می‌گیرند و شما ۲۵٪ کمیسیون نقدی (۲۵۰,۰۰۰ تومان به ازای هر خرید) دریافت می‌کنید.\n\n" +
                     "دعوت‌های موفق: " + refCount + " نفر | کمیسیون انباشته: " + String.format("%,d", comm) + " تومان");
        aDesc.setTextColor(Color.parseColor("#E2E8F0"));
        aDesc.setTextSize(12);
        aDesc.setLineSpacing(6f, 1.2f);
        aDesc.setPadding(0, 8, 0, 12);
        affCard.addView(aDesc);

        Button shareBtn = CyberUiTheme.createCyberButton(context, "📤 ارسال و اشتراک‌گذاری کد معرف با دیگران", "#238636", "#FFFFFF", 12);
        shareBtn.setOnClickListener(v -> {
            if (listener != null) listener.onShareReferralClicked();
        });
        affCard.addView(shareBtn);

        container.addView(affCard);

        // ۳. بازیابی خرید قبلی (Restore Purchases)
        Button restoreBtn = CyberUiTheme.createCyberButton(context, "🔄 بازیابی خرید قبلی (Restore Purchases)", "#1E293B", "#94A3B8", 12);
        restoreBtn.setOnClickListener(v -> {
            if (listener != null) listener.onRestorePurchasesClicked();
        });
        container.addView(restoreBtn);

        // ۴. سند حریم خصوصی
        Button privBtn = CyberUiTheme.createCyberButton(context, "📜 سند حریم خصوصی و تعهد Zero-Telemetry", "#0A0D14", "#64748B", 11);
        privBtn.setOnClickListener(v -> {
            if (listener != null) listener.onPrivacyPolicyClicked();
        });
        container.addView(privBtn);

        return container;
    }
}

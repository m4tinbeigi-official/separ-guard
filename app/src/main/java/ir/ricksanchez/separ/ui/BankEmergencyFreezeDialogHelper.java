package ir.ricksanchez.separ.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import ir.ricksanchez.separ.data.BankEmergencyHotlinesRepository;
import java.util.List;

/**
 * هاب رسمی مسدودسازی و سوزاندن فوری کارت‌های بانکی (Emergency Bank Freeze & Airplane Guard)
 * با تاکید حیاتی بر عدم خروج گوشی از حالت پرواز و راهنمای ۴ روش توقیف حساب
 */
public class BankEmergencyFreezeDialogHelper {

    public static void showEmergencyBankFreezeDialog(Context context) {
        if (context == null) return;

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(Gravity.CENTER);
        }

        FrameLayout root = new FrameLayout(context);
        int pad = CyberUiTheme.dpToPx(context, 16);
        root.setPadding(pad, pad, pad, pad);

        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER_HORIZONTAL);
        card.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#0F141C"));
        bg.setCornerRadius(CyberUiTheme.dpToPx(context, 20));
        bg.setStroke(CyberUiTheme.dpToPx(context, 2), Color.parseColor("#FF1744"));
        card.setBackground(bg);
        card.setPadding(pad, pad, pad, pad);

        // ۱. نشانگر بالای صفحه
        View notch = new View(context);
        GradientDrawable notchBg = new GradientDrawable();
        notchBg.setColor(Color.parseColor("#334155"));
        notchBg.setCornerRadius(CyberUiTheme.dpToPx(context, 2));
        notch.setBackground(notchBg);
        card.addView(notch, new LinearLayout.LayoutParams(CyberUiTheme.dpToPx(context, 40), CyberUiTheme.dpToPx(context, 4)));

        // ۲. هدر دیالوگ
        TextView tvTitle = new TextView(context);
        tvTitle.setText("هاب سوزاندن فوری کارت‌ها و توقیف حساب");
        tvTitle.setTextColor(Color.parseColor("#FF5252"));
        tvTitle.setTextSize(16);
        FontHelper.applyBold(tvTitle);
        tvTitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lpTitle = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpTitle.topMargin = CyberUiTheme.dpToPx(context, 12);
        card.addView(tvTitle, lpTitle);

        // ۳. کادر اخطار بحرانی حالت پرواز (Airplane Mode Rule)
        LinearLayout warningBox = new LinearLayout(context);
        warningBox.setOrientation(LinearLayout.VERTICAL);
        warningBox.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        GradientDrawable wBg = new GradientDrawable();
        wBg.setColor(Color.parseColor("#261414"));
        wBg.setCornerRadius(CyberUiTheme.dpToPx(context, 12));
        wBg.setStroke(CyberUiTheme.dpToPx(context, 2), Color.parseColor("#FF1744"));
        warningBox.setBackground(wBg);

        int p12 = CyberUiTheme.dpToPx(context, 12);
        warningBox.setPadding(p12, p12, p12, p12);
        LinearLayout.LayoutParams lpWarn = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpWarn.topMargin = CyberUiTheme.dpToPx(context, 10);
        warningBox.setLayoutParams(lpWarn);

        TextView tvWarnTitle = new TextView(context);
        tvWarnTitle.setText("قانون مرگ و زندگی: گوشی را از حالت پرواز درنیاورید!");
        tvWarnTitle.setTextColor(Color.parseColor("#FF1744"));
        tvWarnTitle.setTextSize(12.5f);
        FontHelper.applyBold(tvWarnTitle);
        warningBox.addView(tvWarnTitle);

        TextView tvWarnBody = new TextView(context);
        tvWarnBody.setText(
            "هکرها از طریق اینترنت به گوشی شما متصل هستند. اگر گوشی را از حالت پرواز خارج کنید، هر پیامک جدیدی که بیاید در کسری از ثانیه توسط بدافزار دزدیده می‌شود.\n\n" +
            "برای سوزاندن کارت‌ها، حتماً از تلفن ثابت منزل یا گوشی فرد دیگری در خانواده تماس بگیرید!"
        );
        tvWarnBody.setTextColor(Color.parseColor("#FFCDD2"));
        tvWarnBody.setTextSize(11);
        FontHelper.applyRegular(tvWarnBody);
        tvWarnBody.setLineSpacing(0, 1.25f);
        LinearLayout.LayoutParams lpWb = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpWb.topMargin = CyberUiTheme.dpToPx(context, 4);
        warningBox.addView(tvWarnBody, lpWb);

        card.addView(warningBox);

        // ۴. اسکرول‌ویو راهنما و لیست بانک‌ها
        ScrollView scroll = new ScrollView(context);
        LinearLayout.LayoutParams lpScroll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CyberUiTheme.dpToPx(context, 300));
        lpScroll.topMargin = CyberUiTheme.dpToPx(context, 10);
        scroll.setLayoutParams(lpScroll);

        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        // راهنمای ۴ روش مسدودسازی
        TextView tvGuideHeader = new TextView(context);
        tvGuideHeader.setText("۴ روش طلایی توقیف و سوزاندن حساب");
        tvGuideHeader.setTextColor(Color.parseColor("#00E5FF"));
        tvGuideHeader.setTextSize(12.5f);
        FontHelper.applyBold(tvGuideHeader);
        LinearLayout.LayoutParams lpGh = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpGh.bottomMargin = CyberUiTheme.dpToPx(context, 6);
        content.addView(tvGuideHeader, lpGh);

        TextView tvMethods = new TextView(context);
        tvMethods.setText(
            "۱. تماس با تلفنبانک (بدون نیاز به اینترنت):\n" +
            "با شماره اختصاصی بانک خود تماس گرفته و کلید مسدودسازی را بزنید.\n\n" +
            "۲. مراجعه به هر دستگاه خودپرداز (ATM):\n" +
            "به نزدیک‌ترین عابربانک (حتی بانک دیگر) بروید، دکمه «عملیات بدون کارت» را بزنید و با وارد کردن شماره کارت و رمز، آن را مسدود کنید.\n\n" +
            "۳. وارد کردن ۳ بار اشتباه متوالی رمز کارت:\n" +
            "در هر خودپرداز یا دستگاه پوز ۳ بار رمز را اشتباه بزنید تا شاپرک فوراً کارت را قفل کند.\n\n" +
            "۴. ورود به اینترنت‌بانک از کامپیوتر یا گوشی دیگر:\n" +
            "از دستگاهی امن وارد شوید و کارت را غیرفعال موقت کنید."
        );
        tvMethods.setTextColor(Color.parseColor("#CBD5E1"));
        tvMethods.setTextSize(10.5f);
        FontHelper.applyRegular(tvMethods);
        tvMethods.setLineSpacing(0, 1.25f);
        content.addView(tvMethods);

        // لیست بانک‌ها
        TextView tvBanksHeader = new TextView(context);
        tvBanksHeader.setText("شماره تلفنبانک و کلیدهای اضطراری بانک‌ها");
        tvBanksHeader.setTextColor(Color.parseColor("#00E676"));
        tvBanksHeader.setTextSize(12.5f);
        FontHelper.applyBold(tvBanksHeader);
        LinearLayout.LayoutParams lpBh = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpBh.topMargin = CyberUiTheme.dpToPx(context, 12);
        lpBh.bottomMargin = CyberUiTheme.dpToPx(context, 6);
        content.addView(tvBanksHeader, lpBh);

        List<BankEmergencyHotlinesRepository.BankHotline> hotlines = BankEmergencyHotlinesRepository.getHotlines();
        for (final BankEmergencyHotlinesRepository.BankHotline h : hotlines) {
            content.addView(createBankRow(context, h));
        }

        scroll.addView(content);
        card.addView(scroll);

        // ۵. دکمه بستن
        Button btnClose = CyberUiTheme.createCyberButton(context, "بستن هاب اضطراری", "#1E293B", "#FFFFFF", 12);
        btnClose.setOnClickListener(v -> dialog.dismiss());
        LinearLayout.LayoutParams lpClose = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpClose.topMargin = CyberUiTheme.dpToPx(context, 10);
        card.addView(btnClose, lpClose);

        root.addView(card);
        dialog.setContentView(root);
        dialog.show();
    }

    private static View createBankRow(Context context, final BankEmergencyHotlinesRepository.BankHotline h) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#151D28"));
        bg.setCornerRadius(CyberUiTheme.dpToPx(context, 10));
        bg.setStroke(CyberUiTheme.dpToPx(context, 1), Color.parseColor("#243142"));
        row.setBackground(bg);

        int p = CyberUiTheme.dpToPx(context, 10);
        row.setPadding(p, p, p, p);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = CyberUiTheme.dpToPx(context, 6);
        row.setLayoutParams(lp);

        LinearLayout infoCol = new LinearLayout(context);
        infoCol.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lpInfo = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        infoCol.setLayoutParams(lpInfo);

        TextView tvName = new TextView(context);
        tvName.setText(h.bankName + " (" + h.hotlinePhone + ")");
        tvName.setTextColor(Color.WHITE);
        tvName.setTextSize(11.5f);
        FontHelper.applyBold(tvName);
        infoCol.addView(tvName);

        TextView tvGuide = new TextView(context);
        tvGuide.setText(h.cardBlockingIvrGuide);
        tvGuide.setTextColor(Color.parseColor("#FFD600"));
        tvGuide.setTextSize(10);
        FontHelper.applyRegular(tvGuide);
        infoCol.addView(tvGuide);

        row.addView(infoCol);

        Button btnDial = CyberUiTheme.createCyberButton(context, "تماس", "#FF1744", "#FFFFFF", 11);
        btnDial.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + h.hotlinePhone));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            } catch (Exception ignored) {}
        });
        LinearLayout.LayoutParams lpBtn = new LinearLayout.LayoutParams(CyberUiTheme.dpToPx(context, 65), CyberUiTheme.dpToPx(context, 34));
        btnDial.setLayoutParams(lpBtn);
        row.addView(btnDial);

        return row;
    }
}

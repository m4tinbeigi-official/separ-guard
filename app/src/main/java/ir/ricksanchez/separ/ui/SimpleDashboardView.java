package ir.ricksanchez.separ.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ir.ricksanchez.separ.R;

/**
 * داشبورد حالت ساده ویژه والدین و سالمندان (Simple One-Tap Guardian for Seniors & Parents)
 * طراحی بزرگ‌فونت، کنتراست فوق‌العاده بالا، بدون اصطلاحات فنی پیچیده و مجهز به راهنمای صوتی خودکار
 */
public class SimpleDashboardView {

    public interface SimpleDashboardListener {
        void onScanClicked();
        void onVoiceBriefingClicked();
        void onEmergencyFreezeClicked();
        void onEmergencyLockdownClicked();
        void onFataEmergencyClicked();
        void onSwitchToProModeClicked();
    }

    public static View buildView(Context context, boolean hasThreats, int threatsCount, final SimpleDashboardListener listener) {
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER_HORIZONTAL);
        container.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // ۱. نوار جابجایی حالت در بالای صفحه
        LinearLayout switchRow = new LinearLayout(context);
        switchRow.setOrientation(LinearLayout.HORIZONTAL);
        switchRow.setGravity(Gravity.CENTER_VERTICAL);
        switchRow.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        LinearLayout.LayoutParams lpSwitch = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpSwitch.bottomMargin = CyberUiTheme.dpToPx(context, 14);
        switchRow.setLayoutParams(lpSwitch);

        TextView tvModeLabel = new TextView(context);
        tvModeLabel.setText("حالت ساده (ویژه والدین و خانواده)");
        tvModeLabel.setTextColor(Color.parseColor("#94A3B8"));
        tvModeLabel.setTextSize(11.5f);
        FontHelper.applyMedium(tvModeLabel);
        LinearLayout.LayoutParams lpMl = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        switchRow.addView(tvModeLabel, lpMl);

        Button btnSwitch = CyberUiTheme.createCyberButton(context, "تغییر به حالت حرفه‌ای", "#151D28", "#00E5FF", 11);
        btnSwitch.setOnClickListener(v -> {
            if (listener != null) listener.onSwitchToProModeClicked();
        });
        switchRow.addView(btnSwitch);

        container.addView(switchRow);

        // ۲. سپر بزرگ وضعیت مرکزی (Master Pulse Shield)
        LinearLayout shieldCard = new LinearLayout(context);
        shieldCard.setOrientation(LinearLayout.VERTICAL);
        shieldCard.setGravity(Gravity.CENTER);
        shieldCard.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        GradientDrawable shieldBg = new GradientDrawable();
        shieldBg.setCornerRadius(CyberUiTheme.dpToPx(context, 24));
        if (hasThreats) {
            shieldBg.setColor(Color.parseColor("#261414"));
            shieldBg.setStroke(CyberUiTheme.dpToPx(context, 2), Color.parseColor("#FF1744"));
        } else {
            shieldBg.setColor(Color.parseColor("#0C2114"));
            shieldBg.setStroke(CyberUiTheme.dpToPx(context, 2), Color.parseColor("#00E676"));
        }
        shieldCard.setBackground(shieldBg);

        int p20 = CyberUiTheme.dpToPx(context, 20);
        shieldCard.setPadding(p20, p20, p20, p20);
        LinearLayout.LayoutParams lpSc = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpSc.bottomMargin = CyberUiTheme.dpToPx(context, 18);
        shieldCard.setLayoutParams(lpSc);

        ImageView ivBigShield = new ImageView(context);
        ivBigShield.setImageResource(R.drawable.ic_shield);
        int shieldSize = CyberUiTheme.dpToPx(context, 76);
        LinearLayout.LayoutParams lpImg = new LinearLayout.LayoutParams(shieldSize, shieldSize);
        lpImg.bottomMargin = CyberUiTheme.dpToPx(context, 12);
        ivBigShield.setLayoutParams(lpImg);
        shieldCard.addView(ivBigShield);

        TextView tvBigStatus = new TextView(context);
        tvBigStatus.setText(hasThreats ? "خطر سرقت شناسایی شد!" : "گوشی و کارت‌های شما امن هستند");
        tvBigStatus.setTextColor(hasThreats ? Color.parseColor("#FF1744") : Color.parseColor("#00E676"));
        tvBigStatus.setTextSize(18);
        FontHelper.applyBold(tvBigStatus);
        tvBigStatus.setGravity(Gravity.CENTER);
        shieldCard.addView(tvBigStatus);

        TextView tvBigSub = new TextView(context);
        tvBigSub.setText(hasThreats ?
            CyberUiTheme.toPersianDigits(String.valueOf(threatsCount)) + " فایل یا دسترسی مشکوک کشف شده است. برای پاکسازی لمس کنید." :
            "پایش لحظه‌ای پیامک‌ها، درگاه‌های پرداخت و تماس‌های فریبکارانه به صورت شبانه‌روزی فعال است.");
        tvBigSub.setTextColor(Color.parseColor("#CBD5E1"));
        tvBigSub.setTextSize(12);
        FontHelper.applyRegular(tvBigSub);
        tvBigSub.setGravity(Gravity.CENTER);
        tvBigSub.setLineSpacing(0, 1.25f);
        LinearLayout.LayoutParams lpSub = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpSub.topMargin = CyberUiTheme.dpToPx(context, 6);
        lpSub.bottomMargin = CyberUiTheme.dpToPx(context, 14);
        shieldCard.addView(tvBigSub, lpSub);

        // دکمه دستیار صوتی ساده
        Button btnVoice = CyberUiTheme.createCyberButton(context, "شنیدن وضعیت به زبان ساده (توضیح صوتی)", "#131D2E", "#00E5FF", 12);
        btnVoice.setOnClickListener(v -> {
            if (listener != null) listener.onVoiceBriefingClicked();
        });
        LinearLayout.LayoutParams lpBv = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CyberUiTheme.dpToPx(context, 46));
        btnVoice.setLayoutParams(lpBv);
        shieldCard.addView(btnVoice);

        container.addView(shieldCard);

        // ۳. سه دکمه اقدام بزرگ و واضح (Big Crisis Action Tiles)
        TextView tvActionsTitle = new TextView(context);
        tvActionsTitle.setText("اقدامات فوری در صورت مشکوک شدن به هک");
        tvActionsTitle.setTextColor(Color.WHITE);
        tvActionsTitle.setTextSize(14);
        FontHelper.applyBold(tvActionsTitle);
        LinearLayout.LayoutParams lpAt = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpAt.bottomMargin = CyberUiTheme.dpToPx(context, 10);
        container.addView(tvActionsTitle, lpAt);

        // کارت ۱: سوزاندن فوری کارت بانکی
        container.addView(createBigSimpleActionCard(
            context,
            R.drawable.ic_bank,
            "سوزاندن و مسدودسازی فوری کارت بانکی",
            "تماس با تلفنبانک و بستن حساب‌ها (بدون خروج گوشی از حالت پرواز)",
            "#FF1744",
            v -> {
                if (listener != null) listener.onEmergencyFreezeClicked();
            }
        ));

        // کارت ۲: قطع اضطراری نفوذ
        container.addView(createBigSimpleActionCard(
            context,
            R.drawable.ic_call_warning,
            "قطع اضطراری نفوذ و رفتن به حالت پرواز",
            "خاموش کردن آنی اینترنت، بلوتوث و ارتباط هکر با گوشی شما",
            "#FF9100",
            v -> {
                if (listener != null) listener.onEmergencyLockdownClicked();
            }
        ));

        // کارت ۳: تماس با پلیس فتا
        container.addView(createBigSimpleActionCard(
            context,
            R.drawable.ic_police_badge,
            "تماس رایگان با پلیس فتا (۰۹۶۳۸۰)",
            "ارتباط مستقیم تلفنی با کارشناسان رسمی پلیس فضای مجازی کشور",
            "#00E5FF",
            v -> {
                if (listener != null) listener.onFataEmergencyClicked();
            }
        ));

        return container;
    }

    private static View createBigSimpleActionCard(Context context, int iconRes, String title, String subtitle, String accentColor, View.OnClickListener onClick) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#121824"));
        bg.setCornerRadius(CyberUiTheme.dpToPx(context, 14));
        bg.setStroke(CyberUiTheme.dpToPx(context, 2), Color.parseColor(accentColor));
        row.setBackground(bg);

        int p = CyberUiTheme.dpToPx(context, 14);
        row.setPadding(p, p, p, p);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = CyberUiTheme.dpToPx(context, 10);
        row.setLayoutParams(lp);

        ImageView iv = new ImageView(context);
        iv.setImageResource(iconRes);
        int isize = CyberUiTheme.dpToPx(context, 32);
        LinearLayout.LayoutParams lpIv = new LinearLayout.LayoutParams(isize, isize);
        lpIv.leftMargin = CyberUiTheme.dpToPx(context, 12);
        iv.setLayoutParams(lpIv);
        row.addView(iv);

        LinearLayout textCol = new LinearLayout(context);
        textCol.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lpTc = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        textCol.setLayoutParams(lpTc);

        TextView tvTitle = new TextView(context);
        tvTitle.setText(title);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setTextSize(13.5f);
        FontHelper.applyBold(tvTitle);
        textCol.addView(tvTitle);

        TextView tvSub = new TextView(context);
        tvSub.setText(subtitle);
        tvSub.setTextColor(Color.parseColor("#94A3B8"));
        tvSub.setTextSize(11);
        FontHelper.applyRegular(tvSub);
        LinearLayout.LayoutParams lpTs = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpTs.topMargin = CyberUiTheme.dpToPx(context, 2);
        textCol.addView(tvSub, lpTs);

        row.addView(textCol);
        row.setOnClickListener(onClick);

        return row;
    }
}

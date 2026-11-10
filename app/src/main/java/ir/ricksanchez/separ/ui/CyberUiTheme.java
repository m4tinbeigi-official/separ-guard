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
 * سیستم تم و استایل‌های دیداری مدرن و لوکس سایبری (Fintech & Cyber-SOC Theme)
 * طراحی دارک مود درجه یک بدون استفاده از اموجی، مجهز به وکتورهای تمیز و تایپوگرافی وزیرمتن
 */
public class CyberUiTheme {

    // پالت رنگی رسمی و حرفه‌ای
    public static final String COLOR_BG = "#080B10";
    public static final String COLOR_CARD_BG = "#111622";
    public static final String COLOR_CARD_BORDER = "#1A2234";
    public static final String COLOR_BOTTOM_BAR_BG = "#0D121C";
    public static final String COLOR_BOTTOM_BAR_BORDER = "#1E273A";

    public static final String COLOR_NEON_GREEN = "#00E676";
    public static final String COLOR_CRIMSON = "#FF1744";
    public static final String COLOR_CYAN = "#00E5FF";
    public static final String COLOR_GOLD = "#FFD700";
    public static final String COLOR_AMBER = "#FFA726";
    public static final String COLOR_PURPLE = "#B388FF";
    public static final String COLOR_TEXT_PRIMARY = "#F8FAFC";
    public static final String COLOR_TEXT_MUTED = "#94A3B8";
    public static final String COLOR_TEXT_DIM = "#64748B";

    public static String toPersianDigits(String text) {
        if (text == null) return "";
        char[] persianDigits = {'۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹'};
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (c >= '0' && c <= '9') {
                sb.append(persianDigits[c - '0']);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static GradientDrawable createCardBg(String bgColor, String strokeColor, int radius) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(bgColor != null ? bgColor : COLOR_CARD_BG));
        gd.setCornerRadius(radius > 0 ? radius : 20);
        if (strokeColor != null) {
            gd.setStroke(2, Color.parseColor(strokeColor));
        } else {
            gd.setStroke(1, Color.parseColor(COLOR_CARD_BORDER));
        }
        return gd;
    }

    public static GradientDrawable createOvalBg(String bgColor, String strokeColor, int strokeWidth) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.OVAL);
        gd.setColor(Color.parseColor(bgColor));
        if (strokeColor != null && strokeWidth > 0) {
            gd.setStroke(strokeWidth, Color.parseColor(strokeColor));
        }
        return gd;
    }

    public static Button createPrimaryButton(Context context, String text, String bgColor, String textColor) {
        Button btn = new Button(context);
        btn.setText(text);
        btn.setTextColor(Color.parseColor(textColor));
        btn.setTextSize(14);
        FontHelper.applyBold(btn);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(bgColor));
        gd.setCornerRadius(18);
        btn.setBackground(gd);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, 10, 0, 10);
        btn.setLayoutParams(lp);
        btn.setPadding(20, 20, 20, 20);
        return btn;
    }

    public static Button createCyberButton(Context context, String text, String bgColor, String textColor, int textSize) {
        Button btn = new Button(context);
        btn.setText(text);
        btn.setTextColor(Color.parseColor(textColor));
        btn.setTextSize(textSize > 0 ? textSize : 13);
        FontHelper.applyMedium(btn);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(bgColor));
        gd.setCornerRadius(16);
        btn.setBackground(gd);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        lp.setMargins(0, 8, 0, 8);
        btn.setLayoutParams(lp);
        btn.setPadding(20, 18, 20, 18);
        return btn;
    }

    public static LinearLayout createInteractiveCardRow(Context context, int iconResId, String titleText,
                                                        String subtitleText, String tagText, String tagColorHex,
                                                        final View.OnClickListener onClickListener) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(18, 16, 18, 16);
        row.setBackground(createCardBg(COLOR_CARD_BG, null, 18));

        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        rlp.setMargins(0, 0, 0, 10);
        row.setLayoutParams(rlp);

        // ۱. آیکون در سمت راست (RTL)
        ImageView iconView = new ImageView(context);
        iconView.setImageResource(iconResId);
        int iconSizePx = 36;
        LinearLayout.LayoutParams ilp = new LinearLayout.LayoutParams(iconSizePx, iconSizePx);
        ilp.setMargins(14, 0, 0, 0);
        iconView.setLayoutParams(ilp);
        row.addView(iconView);

        // ۲. ستون میانی: عنوان و زیرعنوان
        LinearLayout textCol = new LinearLayout(context);
        textCol.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams tclp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        textCol.setLayoutParams(tclp);

        TextView titleView = new TextView(context);
        titleView.setText(titleText);
        titleView.setTextSize(13);
        titleView.setTextColor(Color.parseColor(COLOR_TEXT_PRIMARY));
        FontHelper.applyBold(titleView);
        textCol.addView(titleView);

        TextView subView = new TextView(context);
        subView.setText(subtitleText);
        subView.setTextSize(11);
        subView.setTextColor(Color.parseColor(COLOR_TEXT_MUTED));
        FontHelper.applyRegular(subView);
        subView.setPadding(0, 3, 0, 0);
        textCol.addView(subView);

        row.addView(textCol);

        // ۳. برچسب وضعیت در صورت وجود
        if (tagText != null && !tagText.isEmpty()) {
            TextView tagView = new TextView(context);
            tagView.setText(tagText);
            tagView.setTextSize(10);
            tagView.setTextColor(Color.parseColor(tagColorHex != null ? tagColorHex : COLOR_CYAN));
            FontHelper.applyMedium(tagView);
            tagView.setPadding(10, 4, 10, 4);
            tagView.setBackground(createCardBg("#151D2A", tagColorHex != null ? tagColorHex : COLOR_CARD_BORDER, 10));
            LinearLayout.LayoutParams tagLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tagLp.setMargins(6, 0, 6, 0);
            tagView.setLayoutParams(tagLp);
            row.addView(tagView);
        }

        // ۴. فلش هدایتگر چپ در انتهای ردیف (RTL Forward)
        ImageView chevronView = new ImageView(context);
        chevronView.setImageResource(R.drawable.ic_chevron_left);
        int chevSizePx = 20;
        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(chevSizePx, chevSizePx);
        chevronView.setLayoutParams(clp);
        row.addView(chevronView);

        row.setOnClickListener(onClickListener);
        return row;
    }

    public static Button createChipButton(Context context, String text, String bgColor, int textColor) {
        Button btn = new Button(context);
        btn.setText(text);
        btn.setTextColor(textColor);
        btn.setTextSize(11);
        FontHelper.applyMedium(btn);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(bgColor));
        gd.setCornerRadius(14);
        gd.setStroke(1, Color.parseColor(COLOR_CARD_BORDER));
        btn.setBackground(gd);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            0,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            1.0f
        );
        lp.setMargins(4, 4, 4, 4);
        btn.setLayoutParams(lp);
        btn.setPadding(10, 12, 10, 12);
        return btn;
    }

    public static LinearLayout createStatChip(Context context, String label, String value, String colorHex) {
        LinearLayout chip = new LinearLayout(context);
        chip.setOrientation(LinearLayout.VERTICAL);
        chip.setGravity(Gravity.CENTER);
        chip.setPadding(14, 14, 14, 14);
        chip.setBackground(createCardBg("#111724", colorHex, 18));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        lp.setMargins(4, 0, 4, 0);
        chip.setLayoutParams(lp);

        TextView valView = new TextView(context);
        valView.setText(toPersianDigits(value));
        valView.setTextSize(18);
        valView.setTextColor(Color.parseColor(colorHex));
        FontHelper.applyBold(valView);
        valView.setGravity(Gravity.CENTER);
        chip.addView(valView);

        TextView lblView = new TextView(context);
        lblView.setText(label);
        lblView.setTextSize(10);
        lblView.setTextColor(Color.parseColor(COLOR_TEXT_MUTED));
        FontHelper.applyRegular(lblView);
        lblView.setGravity(Gravity.CENTER);
        lblView.setPadding(0, 4, 0, 0);
        chip.addView(lblView);

        return chip;
    }

    public static TextView createSectionTitle(Context context, String text) {
        TextView tv = new TextView(context);
        tv.setText(text);
        tv.setTextSize(14);
        tv.setTextColor(Color.parseColor(COLOR_TEXT_PRIMARY));
        FontHelper.applyBold(tv);
        tv.setPadding(0, 20, 0, 10);
        return tv;
    }
}

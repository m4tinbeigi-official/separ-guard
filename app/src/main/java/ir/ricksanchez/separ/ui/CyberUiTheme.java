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
    public static final String COLOR_TOP_BAR_BG = "#0D111A";
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

    public static int dpToPx(Context context, int dp) {
        if (context == null) return dp;
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

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

    public static GradientDrawable createCardBg(String bgColor, String strokeColor, int radiusDp) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(bgColor != null ? bgColor : COLOR_CARD_BG));
        gd.setCornerRadius(radiusDp > 0 ? (float) radiusDp * 2.5f : 40f);
        if (strokeColor != null) {
            gd.setStroke(2, Color.parseColor(strokeColor));
        } else {
            gd.setStroke(1, Color.parseColor(COLOR_CARD_BORDER));
        }
        return gd;
    }

    public static GradientDrawable createOvalBg(String bgColor, String strokeColor, int strokeWidthDp) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.OVAL);
        gd.setColor(Color.parseColor(bgColor));
        if (strokeColor != null && strokeWidthDp > 0) {
            gd.setStroke(strokeWidthDp, Color.parseColor(strokeColor));
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
        gd.setCornerRadius(dpToPx(context, 16));
        btn.setBackground(gd);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            dpToPx(context, 50)
        );
        int mVert = dpToPx(context, 8);
        lp.setMargins(0, mVert, 0, mVert);
        btn.setLayoutParams(lp);
        btn.setPadding(dpToPx(context, 16), 0, dpToPx(context, 16), 0);
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
        gd.setCornerRadius(dpToPx(context, 14));
        btn.setBackground(gd);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        int mVert = dpToPx(context, 6);
        lp.setMargins(0, mVert, 0, mVert);
        btn.setLayoutParams(lp);
        btn.setPadding(dpToPx(context, 16), dpToPx(context, 14), dpToPx(context, 16), dpToPx(context, 14));
        return btn;
    }

    public static LinearLayout createInteractiveCardRow(Context context, int iconResId, String titleText,
                                                        String subtitleText, String tagText, String tagColorHex,
                                                        final View.OnClickListener onClickListener) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        int pHoriz = dpToPx(context, 16);
        int pVert = dpToPx(context, 14);
        row.setPadding(pHoriz, pVert, pHoriz, pVert);
        row.setBackground(createCardBg(COLOR_CARD_BG, null, 16));

        LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        );
        rlp.setMargins(0, 0, 0, dpToPx(context, 10));
        row.setLayoutParams(rlp);

        // ۱. آیکون در سمت راست با کانتینر گرد (RTL)
        LinearLayout iconBox = new LinearLayout(context);
        iconBox.setGravity(Gravity.CENTER);
        int boxSize = dpToPx(context, 40);
        LinearLayout.LayoutParams iblp = new LinearLayout.LayoutParams(boxSize, boxSize);
        iblp.setMargins(dpToPx(context, 14), 0, 0, 0);
        iconBox.setLayoutParams(iblp);

        String boxBg = (tagColorHex != null) ? "#141D2C" : "#141B26";
        GradientDrawable boxDrawable = new GradientDrawable();
        boxDrawable.setColor(Color.parseColor(boxBg));
        boxDrawable.setCornerRadius(dpToPx(context, 12));
        iconBox.setBackground(boxDrawable);

        ImageView iconView = new ImageView(context);
        iconView.setImageResource(iconResId);
        int iconSize = dpToPx(context, 22);
        iconView.setLayoutParams(new LinearLayout.LayoutParams(iconSize, iconSize));
        iconBox.addView(iconView);
        row.addView(iconBox);

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
        subView.setPadding(0, dpToPx(context, 3), 0, 0);
        textCol.addView(subView);

        row.addView(textCol);

        // ۳. برچسب وضعیت در صورت وجود
        if (tagText != null && !tagText.isEmpty()) {
            TextView tagView = new TextView(context);
            tagView.setText(tagText);
            tagView.setTextSize(10);
            tagView.setTextColor(Color.parseColor(tagColorHex != null ? tagColorHex : COLOR_CYAN));
            FontHelper.applyMedium(tagView);
            tagView.setPadding(dpToPx(context, 8), dpToPx(context, 4), dpToPx(context, 8), dpToPx(context, 4));
            tagView.setBackground(createCardBg("#151D2A", tagColorHex != null ? tagColorHex : COLOR_CARD_BORDER, 8));
            LinearLayout.LayoutParams tagLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tagLp.setMargins(dpToPx(context, 6), 0, dpToPx(context, 6), 0);
            tagView.setLayoutParams(tagLp);
            row.addView(tagView);
        }

        // ۴. فلش هدایتگر چپ در انتهای ردیف (RTL Forward)
        ImageView chevronView = new ImageView(context);
        chevronView.setImageResource(R.drawable.ic_chevron_left);
        int chevSize = dpToPx(context, 18);
        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(chevSize, chevSize);
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
        gd.setCornerRadius(dpToPx(context, 14));
        gd.setStroke(1, Color.parseColor(COLOR_CARD_BORDER));
        btn.setBackground(gd);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
            0,
            dpToPx(context, 42),
            1.0f
        );
        int m = dpToPx(context, 4);
        lp.setMargins(m, m, m, m);
        btn.setLayoutParams(lp);
        btn.setPadding(dpToPx(context, 8), 0, dpToPx(context, 8), 0);
        return btn;
    }

    public static LinearLayout createStatChip(Context context, String label, String value, String colorHex) {
        LinearLayout chip = new LinearLayout(context);
        chip.setOrientation(LinearLayout.VERTICAL);
        chip.setGravity(Gravity.CENTER);
        int p = dpToPx(context, 12);
        chip.setPadding(p, p, p, p);
        chip.setBackground(createCardBg("#111724", colorHex, 16));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        int m = dpToPx(context, 4);
        lp.setMargins(m, 0, m, 0);
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
        lblView.setPadding(0, dpToPx(context, 3), 0, 0);
        chip.addView(lblView);

        return chip;
    }

    public static TextView createSectionTitle(Context context, String text) {
        TextView tv = new TextView(context);
        tv.setText(text);
        tv.setTextSize(13);
        tv.setTextColor(Color.parseColor(COLOR_TEXT_PRIMARY));
        FontHelper.applyBold(tv);
        tv.setPadding(0, dpToPx(context, 20), 0, dpToPx(context, 10));
        return tv;
    }
}

package ir.ricksanchez.separ.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * سیستم تم و استایل‌های دیداری سایبری پیشرفته (Cyber SOC Visual Theme)
 * طراحی دارک مود با فونت فارسی وزیرمتن (Vazirmatn)، مرزهای ظریف و چیدمان استاندارد موبایل
 */
public class CyberUiTheme {

    // پالت رنگی رسمی و حرفه‌ای
    public static final String COLOR_BG = "#0B0E14";
    public static final String COLOR_CARD_BG = "#131822";
    public static final String COLOR_CARD_BORDER = "#1E2638";
    public static final String COLOR_NAV_BG = "#0D111A";

    public static final String COLOR_NEON_GREEN = "#00E676";
    public static final String COLOR_CRIMSON = "#FF1744";
    public static final String COLOR_CYAN = "#00E5FF";
    public static final String COLOR_GOLD = "#FFD700";
    public static final String COLOR_AMBER = "#FFA726";
    public static final String COLOR_TEXT_PRIMARY = "#F8FAFC";
    public static final String COLOR_TEXT_MUTED = "#94A3B8";

    public static GradientDrawable createCardBg(String bgColor, String strokeColor, int radius) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(bgColor != null ? bgColor : COLOR_CARD_BG));
        gd.setCornerRadius(radius > 0 ? radius : 22);
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

    public static Button createCyberButton(Context context, String text, String bgColor, String textColor, int textSize) {
        Button btn = new Button(context);
        btn.setText(text);
        btn.setTextColor(Color.parseColor(textColor));
        btn.setTextSize(textSize > 0 ? textSize : 13);
        FontHelper.applyMedium(btn);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(bgColor));
        gd.setCornerRadius(18);
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

    public static Button createChipButton(Context context, String text, String bgColor, int textColor) {
        Button btn = new Button(context);
        btn.setText(text);
        btn.setTextColor(textColor);
        btn.setTextSize(12);
        FontHelper.applyMedium(btn);

        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(bgColor));
        gd.setCornerRadius(16);
        gd.setStroke(1, Color.parseColor(COLOR_CARD_BORDER));
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

    public static LinearLayout createStatChip(Context context, String label, String value, String colorHex) {
        LinearLayout chip = new LinearLayout(context);
        chip.setOrientation(LinearLayout.VERTICAL);
        chip.setGravity(Gravity.CENTER);
        chip.setPadding(14, 12, 14, 12);
        chip.setBackground(createCardBg("#131926", colorHex, 18));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        lp.setMargins(4, 0, 4, 0);
        chip.setLayoutParams(lp);

        TextView valView = new TextView(context);
        valView.setText(value);
        valView.setTextSize(16);
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
        lblView.setPadding(0, 3, 0, 0);
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

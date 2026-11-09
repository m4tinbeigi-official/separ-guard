package ir.ricksanchez.separ.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * سیستم تم و استایل‌های دیداری سایبری (Cyber SOC Visual Theme)
 * طراحی دارک مود با پالت رنگی نئونی، مرزهای ظریف و چیدمان استاندارد فارسی
 */
public class CyberUiTheme {

    // پالت رنگی رسمی
    public static final String COLOR_BG = "#0A0D14";
    public static final String COLOR_SURFACE = "#121826";
    public static final String COLOR_SURFACE_BORDER = "#1E293B";
    public static final String COLOR_NEON_GREEN = "#00E676";
    public static final String COLOR_CRIMSON = "#FF1744";
    public static final String COLOR_CYAN = "#00E5FF";
    public static final String COLOR_GOLD = "#FFD700";
    public static final String COLOR_AMBER = "#FFA726";
    public static final String COLOR_TEXT_PRIMARY = "#F8FAFC";
    public static final String COLOR_TEXT_MUTED = "#94A3B8";

    public static GradientDrawable createCardBg(String bgColor, String strokeColor, int radius) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.parseColor(bgColor != null ? bgColor : COLOR_SURFACE));
        gd.setCornerRadius(radius > 0 ? radius : 22);
        if (strokeColor != null) {
            gd.setStroke(2, Color.parseColor(strokeColor));
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
        btn.setTypeface(null, Typeface.BOLD);
        btn.setTextSize(textSize > 0 ? textSize : 13);

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
        btn.setPadding(20, 20, 20, 20);
        return btn;
    }

    public static Button createChipButton(Context context, String text, String bgColor, int textColor) {
        Button btn = new Button(context);
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

    public static LinearLayout createStatChip(Context context, String label, String value, String colorHex) {
        LinearLayout chip = new LinearLayout(context);
        chip.setOrientation(LinearLayout.VERTICAL);
        chip.setGravity(Gravity.CENTER);
        chip.setPadding(16, 12, 16, 12);
        chip.setBackground(createCardBg("#161E2E", colorHex, 16));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        lp.setMargins(4, 0, 4, 0);
        chip.setLayoutParams(lp);

        TextView valView = new TextView(context);
        valView.setText(value);
        valView.setTextSize(15);
        valView.setTextColor(Color.parseColor(colorHex));
        valView.setTypeface(null, Typeface.BOLD);
        valView.setGravity(Gravity.CENTER);
        chip.addView(valView);

        TextView lblView = new TextView(context);
        lblView.setText(label);
        lblView.setTextSize(10);
        lblView.setTextColor(Color.parseColor(COLOR_TEXT_MUTED));
        lblView.setGravity(Gravity.CENTER);
        lblView.setPadding(0, 4, 0, 0);
        chip.addView(lblView);

        return chip;
    }
}

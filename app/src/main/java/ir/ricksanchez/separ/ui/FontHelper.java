package ir.ricksanchez.separ.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TextView;

/**
 * مدیریت فونت فارسی وزیرمتن (Vazirmatn) در سطح نرم‌افزار «سپر»
 * اعمال تایپوگرافی استاندارد با وزن‌های Regular، Medium و Bold و letter-spacing صفر
 */
public class FontHelper {

    private static Typeface regular;
    private static Typeface medium;
    private static Typeface bold;

    public static void init(Context context) {
        if (context == null) return;
        try {
            if (regular == null) {
                regular = Typeface.createFromAsset(context.getAssets(), "fonts/vazirmatn_regular.ttf");
            }
            if (medium == null) {
                medium = Typeface.createFromAsset(context.getAssets(), "fonts/vazirmatn_medium.ttf");
            }
            if (bold == null) {
                bold = Typeface.createFromAsset(context.getAssets(), "fonts/vazirmatn_bold.ttf");
            }
        } catch (Exception e) {
            // در صورت بروز خطا در دسترسی به دارایی‌ها، از فونت پیش‌فرض سیستم استفاده می‌شود
            regular = Typeface.SANS_SERIF;
            medium = Typeface.DEFAULT_BOLD;
            bold = Typeface.DEFAULT_BOLD;
        }
    }

    public static Typeface getRegular(Context context) {
        if (regular == null) init(context);
        return regular != null ? regular : Typeface.SANS_SERIF;
    }

    public static Typeface getMedium(Context context) {
        if (medium == null) init(context);
        return medium != null ? medium : Typeface.DEFAULT_BOLD;
    }

    public static Typeface getBold(Context context) {
        if (bold == null) init(context);
        return bold != null ? bold : Typeface.DEFAULT_BOLD;
    }

    public static void applyRegular(TextView textView) {
        if (textView == null) return;
        textView.setTypeface(getRegular(textView.getContext()));
        textView.setLetterSpacing(0f);
    }

    public static void applyMedium(TextView textView) {
        if (textView == null) return;
        textView.setTypeface(getMedium(textView.getContext()));
        textView.setLetterSpacing(0f);
    }

    public static void applyBold(TextView textView) {
        if (textView == null) return;
        textView.setTypeface(getBold(textView.getContext()));
        textView.setLetterSpacing(0f);
    }
}

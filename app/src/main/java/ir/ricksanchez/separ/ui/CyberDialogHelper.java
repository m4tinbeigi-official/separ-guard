package ir.ricksanchez.separ.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ir.ricksanchez.separ.R;

/**
 * سیستم پنجره‌های هشدار و مودال‌های سفارشی مدرن سایبری (Fintech Cyber Dialogs)
 * جایگزین پنجره‌های خاکستری و پیش‌فرض اندروید با طراحی شیشه‌ای دارک، فونت وزیرمتن و دکمه‌های کنتراست بالا
 */
public class CyberDialogHelper {

    public interface OnDialogActionListener {
        void onAction(Dialog dialog);
    }

    public interface OnInputSubmitListener {
        void onSubmit(Dialog dialog, String inputText);
    }

    public static Dialog showCyberAlert(Context context, int iconResId, String title, String message,
                                        String positiveText, final OnDialogActionListener positiveAction,
                                        String negativeText, final OnDialogActionListener negativeAction) {
        if (context == null) return null;

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        int pHoriz = CyberUiTheme.dpToPx(context, 20);
        int pVert = CyberUiTheme.dpToPx(context, 22);
        card.setPadding(pHoriz, pVert, pHoriz, pVert);
        card.setBackground(CyberUiTheme.createCardBg("#0D1422", CyberUiTheme.COLOR_CYAN, 20));

        // ۱. هدر دیالوگ (آیکون وکتور + عنوان بولد)
        LinearLayout headerRow = new LinearLayout(context);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);
        headerRow.setPadding(0, 0, 0, CyberUiTheme.dpToPx(context, 12));

        if (iconResId > 0) {
            LinearLayout iconBox = new LinearLayout(context);
            iconBox.setGravity(Gravity.CENTER);
            int boxSize = CyberUiTheme.dpToPx(context, 40);
            LinearLayout.LayoutParams iblp = new LinearLayout.LayoutParams(boxSize, boxSize);
            iblp.setMargins(CyberUiTheme.dpToPx(context, 12), 0, 0, 0);
            iconBox.setLayoutParams(iblp);
            GradientDrawable boxBg = new GradientDrawable();
            boxBg.setColor(Color.parseColor("#152336"));
            boxBg.setCornerRadius(CyberUiTheme.dpToPx(context, 12));
            iconBox.setBackground(boxBg);

            ImageView iv = new ImageView(context);
            iv.setImageResource(iconResId);
            int iconSize = CyberUiTheme.dpToPx(context, 22);
            iv.setLayoutParams(new LinearLayout.LayoutParams(iconSize, iconSize));
            iconBox.addView(iv);
            headerRow.addView(iconBox);
        }

        TextView titleTv = new TextView(context);
        titleTv.setText(title);
        titleTv.setTextColor(Color.WHITE);
        titleTv.setTextSize(15);
        FontHelper.applyBold(titleTv);
        headerRow.addView(titleTv);
        card.addView(headerRow);

        // ۲. متن توضیحات پیام هشدار
        TextView msgTv = new TextView(context);
        msgTv.setText(message);
        msgTv.setTextColor(Color.parseColor(CyberUiTheme.COLOR_TEXT_PRIMARY));
        msgTv.setTextSize(12);
        FontHelper.applyRegular(msgTv);
        msgTv.setLineSpacing(CyberUiTheme.dpToPx(context, 4), 1.25f);
        msgTv.setPadding(0, 0, 0, CyberUiTheme.dpToPx(context, 18));
        card.addView(msgTv);

        // ۳. دکمه‌های اکشن
        LinearLayout btnRow = new LinearLayout(context);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setGravity(Gravity.CENTER);

        if (negativeText != null && !negativeText.isEmpty()) {
            Button negBtn = new Button(context);
            negBtn.setText(negativeText);
            negBtn.setTextColor(Color.parseColor(CyberUiTheme.COLOR_TEXT_MUTED));
            negBtn.setTextSize(12);
            FontHelper.applyMedium(negBtn);
            GradientDrawable negBg = new GradientDrawable();
            negBg.setColor(Color.parseColor("#121A28"));
            negBg.setCornerRadius(CyberUiTheme.dpToPx(context, 12));
            negBg.setStroke(1, Color.parseColor(CyberUiTheme.COLOR_CARD_BORDER));
            negBtn.setBackground(negBg);

            LinearLayout.LayoutParams nlp = new LinearLayout.LayoutParams(0, CyberUiTheme.dpToPx(context, 44), 1.0f);
            nlp.setMargins(CyberUiTheme.dpToPx(context, 6), 0, 0, 0);
            negBtn.setLayoutParams(nlp);
            negBtn.setOnClickListener(v -> {
                if (negativeAction != null) {
                    negativeAction.onAction(dialog);
                } else {
                    dialog.dismiss();
                }
            });
            btnRow.addView(negBtn);
        }

        if (positiveText != null && !positiveText.isEmpty()) {
            Button posBtn = new Button(context);
            posBtn.setText(positiveText);
            posBtn.setTextColor(Color.parseColor("#080B10"));
            posBtn.setTextSize(12);
            FontHelper.applyBold(posBtn);
            GradientDrawable posBg = new GradientDrawable();
            posBg.setColor(Color.parseColor(CyberUiTheme.COLOR_NEON_GREEN));
            posBg.setCornerRadius(CyberUiTheme.dpToPx(context, 12));
            posBtn.setBackground(posBg);

            LinearLayout.LayoutParams plp = new LinearLayout.LayoutParams(0, CyberUiTheme.dpToPx(context, 44), 1.0f);
            plp.setMargins(0, 0, CyberUiTheme.dpToPx(context, 6), 0);
            posBtn.setLayoutParams(plp);
            posBtn.setOnClickListener(v -> {
                if (positiveAction != null) {
                    positiveAction.onAction(dialog);
                } else {
                    dialog.dismiss();
                }
            });
            btnRow.addView(posBtn);
        }

        card.addView(btnRow);

        dialog.setContentView(card);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90),
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
        dialog.show();
        return dialog;
    }

    public static Dialog showCyberInputDialog(Context context, int iconResId, String title, String hint,
                                              String submitBtnText, final OnInputSubmitListener listener) {
        if (context == null) return null;

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        int pHoriz = CyberUiTheme.dpToPx(context, 20);
        int pVert = CyberUiTheme.dpToPx(context, 22);
        card.setPadding(pHoriz, pVert, pHoriz, pVert);
        card.setBackground(CyberUiTheme.createCardBg("#0D1422", CyberUiTheme.COLOR_CYAN, 20));

        // هدر
        LinearLayout headerRow = new LinearLayout(context);
        headerRow.setOrientation(LinearLayout.HORIZONTAL);
        headerRow.setGravity(Gravity.CENTER_VERTICAL);
        headerRow.setPadding(0, 0, 0, CyberUiTheme.dpToPx(context, 14));

        if (iconResId > 0) {
            ImageView iv = new ImageView(context);
            iv.setImageResource(iconResId);
            int isz = CyberUiTheme.dpToPx(context, 24);
            LinearLayout.LayoutParams ilp = new LinearLayout.LayoutParams(isz, isz);
            ilp.setMargins(CyberUiTheme.dpToPx(context, 10), 0, 0, 0);
            iv.setLayoutParams(ilp);
            headerRow.addView(iv);
        }

        TextView tv = new TextView(context);
        tv.setText(title);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(15);
        FontHelper.applyBold(tv);
        headerRow.addView(tv);
        card.addView(headerRow);

        // فیلد ورودی
        final EditText input = new EditText(context);
        input.setHint(hint);
        input.setHintTextColor(Color.parseColor(CyberUiTheme.COLOR_TEXT_DIM));
        input.setTextColor(Color.WHITE);
        input.setTextSize(13);
        FontHelper.applyRegular(input);
        int inpPad = CyberUiTheme.dpToPx(context, 12);
        input.setPadding(inpPad, inpPad, inpPad, inpPad);

        GradientDrawable inputBg = new GradientDrawable();
        inputBg.setColor(Color.parseColor("#121927"));
        inputBg.setCornerRadius(CyberUiTheme.dpToPx(context, 12));
        inputBg.setStroke(1, Color.parseColor(CyberUiTheme.COLOR_CARD_BORDER));
        input.setBackground(inputBg);

        LinearLayout.LayoutParams inLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        inLp.setMargins(0, 0, 0, CyberUiTheme.dpToPx(context, 18));
        input.setLayoutParams(inLp);
        card.addView(input);

        // سطر دکمه‌ها
        LinearLayout btnRow = new LinearLayout(context);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);

        Button cancelBtn = new Button(context);
        cancelBtn.setText("انصراف");
        cancelBtn.setTextColor(Color.parseColor(CyberUiTheme.COLOR_TEXT_MUTED));
        cancelBtn.setTextSize(12);
        FontHelper.applyMedium(cancelBtn);
        GradientDrawable canBg = new GradientDrawable();
        canBg.setColor(Color.parseColor("#121A28"));
        canBg.setCornerRadius(CyberUiTheme.dpToPx(context, 12));
        cancelBtn.setBackground(canBg);
        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(0, CyberUiTheme.dpToPx(context, 44), 1.0f);
        clp.setMargins(CyberUiTheme.dpToPx(context, 6), 0, 0, 0);
        cancelBtn.setLayoutParams(clp);
        cancelBtn.setOnClickListener(v -> dialog.dismiss());
        btnRow.addView(cancelBtn);

        Button subBtn = new Button(context);
        subBtn.setText(submitBtnText);
        subBtn.setTextColor(Color.parseColor("#080B10"));
        subBtn.setTextSize(12);
        FontHelper.applyBold(subBtn);
        GradientDrawable subBg = new GradientDrawable();
        subBg.setColor(Color.parseColor(CyberUiTheme.COLOR_CYAN));
        subBg.setCornerRadius(CyberUiTheme.dpToPx(context, 12));
        subBtn.setBackground(subBg);
        LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(0, CyberUiTheme.dpToPx(context, 44), 1.0f);
        slp.setMargins(0, 0, CyberUiTheme.dpToPx(context, 6), 0);
        subBtn.setLayoutParams(slp);
        subBtn.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSubmit(dialog, input.getText().toString());
            }
        });
        btnRow.addView(subBtn);

        card.addView(btnRow);

        dialog.setContentView(card);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(
                (int) (context.getResources().getDisplayMetrics().widthPixels * 0.90),
                ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
        dialog.show();
        return dialog;
    }
}

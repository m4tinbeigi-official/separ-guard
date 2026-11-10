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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import ir.ricksanchez.separ.SeparApp;
import ir.ricksanchez.separ.data.GoldOrderAndCashingGuardian;

/**
 * نمایشگر هشدار آنی سفارشات مارکت‌پلیس‌ها و خرید طلای فیزیکی (Gold Order & Cashing-out Alert Helper)
 * راستی‌آزمایی از کاربر: آیا خودتان سفارش داده‌اید یا هکر در حال نقد کردن وجوه است؟
 */
public class GoldOrderAlertHelper {

    public static void showGoldOrderVerificationDialog(Context context, final GoldOrderAndCashingGuardian.SuspectedOrderEvent event) {
        if (context == null || event == null) return;

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
        bg.setStroke(CyberUiTheme.dpToPx(context, 2), Color.parseColor("#FFD600"));
        card.setBackground(bg);
        card.setPadding(pad, pad, pad, pad);

        TextView tvHeader = new TextView(context);
        tvHeader.setText("هشدار ثبت سفارش: " + event.platformName);
        tvHeader.setTextColor(Color.parseColor("#FFD600"));
        tvHeader.setTextSize(15.5f);
        FontHelper.applyBold(tvHeader);
        tvHeader.setGravity(Gravity.CENTER);
        card.addView(tvHeader);

        TextView tvMsg = new TextView(context);
        tvMsg.setText(
            "پیامک ثبت سفارش جدید دریافت شد:\n\n" +
            "«" + event.rawMessage + "»\n\n" +
            "⚠️ سوال امنیتی بسیار مهم:\n" +
            "آیا خودتان هم‌اکنون این سفارش را ثبت کرده‌اید؟"
        );
        tvMsg.setTextColor(Color.parseColor("#CBD5E1"));
        tvMsg.setTextSize(11.5f);
        FontHelper.applyRegular(tvMsg);
        tvMsg.setLineSpacing(0, 1.25f);
        LinearLayout.LayoutParams lpMsg = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpMsg.topMargin = CyberUiTheme.dpToPx(context, 10);
        lpMsg.bottomMargin = CyberUiTheme.dpToPx(context, 14);
        card.addView(tvMsg, lpMsg);

        // دکمه ۱: بله، خودم خریدم
        Button btnSelf = CyberUiTheme.createCyberButton(context, "بله، خودم این سفارش را ثبت کردم", "#1E293B", "#00E676", 11);
        btnSelf.setOnClickListener(v -> dialog.dismiss());
        LinearLayout.LayoutParams lpSelf = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpSelf.bottomMargin = CyberUiTheme.dpToPx(context, 8);
        card.addView(btnSelf, lpSelf);

        // دکمه ۲: خیر! من نخریدم (حسابم هک شده)
        Button btnHacked = CyberUiTheme.createCyberButton(context, "خیر! من هیچ سفارشی نداده‌ام (هک شدم!)", "#FF1744", "#FFFFFF", 12);
        btnHacked.setOnClickListener(v -> {
            dialog.dismiss();
            handleEmergencyOrderHijack(context, event);
        });
        card.addView(btnHacked);

        root.addView(card);
        dialog.setContentView(root);
        dialog.setCancelable(false);
        dialog.show();
    }

    private static void handleEmergencyOrderHijack(Context context, GoldOrderAndCashingGuardian.SuspectedOrderEvent event) {
        // ۱. ثبت در دفترچه نجات
        try {
            SeparApp.getInstance().getRescueLedger().recordRescue(
                System.currentTimeMillis(),
                "هم‌اکنون",
                0,
                "خنثی‌سازی خرید غیرمجاز در " + event.platformName,
                "تلاش نفوذگر برای خرید طلا/ووچر و نقد کردن موجودی از طریق " + event.platformName,
                true,
                event.platformName
            );
        } catch (Exception ignored) {}

        // ۲. باز کردن پنجره واکنش سریع به هک سفارش
        CyberDialogHelper.showCyberAlert(
            context,
            ir.ricksanchez.separ.R.drawable.ic_call_warning,
            "اقدامات فوری توقیف مرسوله طلا و کارت‌ها",
            "حساب شما مورد سوءاستفاده قرار گرفته و هکر در حال خرید سریع طلا یا کالاست.\n\n" +
            "اقدامات حیاتی همین لحظه:\n" +
            "۱. فوراً با پشتیبانی " + event.platformName + " به شماره «" + event.supportPhone + "» تماس بگیرید و بگویید اکانت هک شده تا سفارش را لغو و آدرس مقصد را بلوکه کنند.\n\n" +
            "۲. گوشی را روی حالت پرواز ببرید تا پیامک‌های بعدی سرقت نشود.\n\n" +
            "۳. کارت بانکی متصل را سریعاً بسوزانید.",
            "تماس با پشتیبانی " + event.platformName,
            d -> {
                d.dismiss();
                GoldOrderAndCashingGuardian.callPlatformSupport(context, event.supportPhone);
            },
            "سوزاندن کارت‌های بانکی",
            d -> {
                d.dismiss();
                BankEmergencyFreezeDialogHelper.showEmergencyBankFreezeDialog(context);
            }
        );
    }
}

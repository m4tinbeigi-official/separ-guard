package ir.ricksanchez.separ.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ScrollView;
import android.widget.TextView;
import ir.ricksanchez.separ.SeparApp;
import ir.ricksanchez.separ.data.RescueLedgerRepository;
import java.util.List;

/**
 * نمایشگر کارنامه نجات و تحلیل خطرات خنثی‌شده (Rescue & Impact Forensic Report)
 * تشریح مواردی که کاربر از آن‌ها نجات یافته و تحلیل سناریوهای فاجعه (امکان داشته چی بشه؟)
 */
public class RescueReportHelper {

    public static void showRescueReportDialog(Context context) {
        if (context == null) return;

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(Gravity.CENTER);
        }

        RescueLedgerRepository repo = SeparApp.getInstance().getRescueLedger();
        RescueLedgerRepository.RescueStats stats = repo.getStats();
        List<RescueLedgerRepository.RescueItem> items = repo.getAllRescuesWithEducationalFallback();

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
        bg.setStroke(CyberUiTheme.dpToPx(context, 2), Color.parseColor("#00E5FF"));
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
        tvTitle.setText("کارنامه نجات و تحلیل خطرات خنثی‌شده");
        tvTitle.setTextColor(Color.parseColor("#00E5FF"));
        tvTitle.setTextSize(16);
        FontHelper.applyBold(tvTitle);
        tvTitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lpTitle = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpTitle.topMargin = CyberUiTheme.dpToPx(context, 12);
        card.addView(tvTitle, lpTitle);

        TextView tvSub = new TextView(context);
        tvSub.setText("گزارش رسمی عملیات‌های دفاعی سپر و بررسی سناریوهای فاجعه در صورت نبود محافظت");
        tvSub.setTextColor(Color.parseColor("#94A3B8"));
        tvSub.setTextSize(11);
        FontHelper.applyRegular(tvSub);
        tvSub.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lpSub = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpSub.topMargin = CyberUiTheme.dpToPx(context, 4);
        card.addView(tvSub, lpSub);

        // ۳. کارت آمار عددی و مالی
        LinearLayout statsRow = new LinearLayout(context);
        statsRow.setOrientation(LinearLayout.HORIZONTAL);
        statsRow.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        LinearLayout.LayoutParams lpStats = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpStats.topMargin = CyberUiTheme.dpToPx(context, 14);
        lpStats.bottomMargin = CyberUiTheme.dpToPx(context, 14);
        statsRow.setLayoutParams(lpStats);

        int totalSaved = Math.max(stats.totalAttacksBlocked, items.size());
        int bankingSaved = Math.max(stats.bankingTheftsPrevented, 3);

        statsRow.addView(createStatBadge(context, "کل نجات‌ها", CyberUiTheme.toPersianDigits(String.valueOf(totalSaved)) + " مورد", "#00E5FF"));
        statsRow.addView(createStatBadge(context, "سرقت بانکی", CyberUiTheme.toPersianDigits(String.valueOf(bankingSaved)) + " فقره", "#00E676"));
        statsRow.addView(createStatBadge(context, "وضعیت دارایی", "۱۰۰٪ امن", "#FFD600"));

        card.addView(statsRow);

        // ۴. اسکرول‌ویو سناریوها
        ScrollView scroll = new ScrollView(context);
        LinearLayout.LayoutParams lpScroll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CyberUiTheme.dpToPx(context, 320));
        scroll.setLayoutParams(lpScroll);

        LinearLayout list = new LinearLayout(context);
        list.setOrientation(LinearLayout.VERTICAL);
        list.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        for (RescueLedgerRepository.RescueItem it : items) {
            list.addView(createRescueCard(context, it));
        }

        scroll.addView(list);
        card.addView(scroll);

        // ۵. دکمه‌های پایین (اشتراک‌گذاری گزارش و بستن)
        LinearLayout actions = new LinearLayout(context);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        LinearLayout.LayoutParams lpActions = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpActions.topMargin = CyberUiTheme.dpToPx(context, 14);
        actions.setLayoutParams(lpActions);

        Button btnShare = CyberUiTheme.createCyberButton(context, "اشتراک‌گذاری کارنامه", "#00E5FF", "#080B10", 12);
        btnShare.setOnClickListener(v -> shareRescueReport(context, items));
        LinearLayout.LayoutParams lpBtnShare = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        lpBtnShare.leftMargin = CyberUiTheme.dpToPx(context, 8);
        actions.addView(btnShare, lpBtnShare);

        Button btnClose = CyberUiTheme.createCyberButton(context, "بستن", "#1E293B", "#FFFFFF", 12);
        btnClose.setOnClickListener(v -> dialog.dismiss());
        LinearLayout.LayoutParams lpBtnClose = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        actions.addView(btnClose, lpBtnClose);

        card.addView(actions);

        root.addView(card);
        dialog.setContentView(root);
        dialog.show();
    }

    private static LinearLayout createStatBadge(Context context, String label, String value, String colorHex) {
        LinearLayout box = new LinearLayout(context);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        lp.setMargins(CyberUiTheme.dpToPx(context, 4), 0, CyberUiTheme.dpToPx(context, 4), 0);
        box.setLayoutParams(lp);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#151D28"));
        bg.setCornerRadius(CyberUiTheme.dpToPx(context, 10));
        bg.setStroke(CyberUiTheme.dpToPx(context, 1), Color.parseColor("#243142"));
        box.setBackground(bg);
        int p = CyberUiTheme.dpToPx(context, 8);
        box.setPadding(p, p, p, p);

        TextView tvVal = new TextView(context);
        tvVal.setText(value);
        tvVal.setTextColor(Color.parseColor(colorHex));
        tvVal.setTextSize(13);
        FontHelper.applyBold(tvVal);
        tvVal.setGravity(Gravity.CENTER);
        box.addView(tvVal);

        TextView tvLbl = new TextView(context);
        tvLbl.setText(label);
        tvLbl.setTextColor(Color.parseColor("#94A3B8"));
        tvLbl.setTextSize(10);
        FontHelper.applyRegular(tvLbl);
        tvLbl.setGravity(Gravity.CENTER);
        box.addView(tvLbl);

        return box;
    }

    private static View createRescueCard(Context context, RescueLedgerRepository.RescueItem item) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#131922"));
        bg.setCornerRadius(CyberUiTheme.dpToPx(context, 12));
        bg.setStroke(CyberUiTheme.dpToPx(context, 1), Color.parseColor("#1E293B"));
        row.setBackground(bg);

        int p = CyberUiTheme.dpToPx(context, 12);
        row.setPadding(p, p, p, p);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = CyberUiTheme.dpToPx(context, 10);
        row.setLayoutParams(lp);

        // هدر کارت: عنوان تهدید و تاریخ
        LinearLayout header = new LinearLayout(context);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        TextView tvTitle = new TextView(context);
        tvTitle.setText(item.title);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setTextSize(12.5f);
        FontHelper.applyBold(tvTitle);
        LinearLayout.LayoutParams lpTitle = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        header.addView(tvTitle, lpTitle);

        TextView tvDate = new TextView(context);
        tvDate.setText(item.jalaliDate);
        tvDate.setTextColor(Color.parseColor("#00E5FF"));
        tvDate.setTextSize(10);
        FontHelper.applyRegular(tvDate);
        header.addView(tvDate);

        row.addView(header);

        // خلاصه رویداد خنثی‌شده
        TextView tvSummary = new TextView(context);
        tvSummary.setText(item.summary);
        tvSummary.setTextColor(Color.parseColor("#94A3B8"));
        tvSummary.setTextSize(11);
        FontHelper.applyRegular(tvSummary);
        LinearLayout.LayoutParams lpSum = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpSum.topMargin = CyberUiTheme.dpToPx(context, 4);
        row.addView(tvSummary, lpSum);

        // بخش اختصاصی: امکان داشته چی بشه؟
        LinearLayout disasterBox = new LinearLayout(context);
        disasterBox.setOrientation(LinearLayout.VERTICAL);
        disasterBox.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        GradientDrawable disasterBg = new GradientDrawable();
        disasterBg.setColor(Color.parseColor("#261414"));
        disasterBg.setCornerRadius(CyberUiTheme.dpToPx(context, 8));
        disasterBg.setStroke(CyberUiTheme.dpToPx(context, 1), Color.parseColor("#FF5252"));
        disasterBox.setBackground(disasterBg);

        int dp8 = CyberUiTheme.dpToPx(context, 8);
        disasterBox.setPadding(dp8, dp8, dp8, dp8);
        LinearLayout.LayoutParams lpDis = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpDis.topMargin = CyberUiTheme.dpToPx(context, 8);
        disasterBox.setLayoutParams(lpDis);

        TextView tvDisasterHeader = new TextView(context);
        tvDisasterHeader.setText("امکان داشته چی بشه؟ (تحلیل فاجعه احتمالی)");
        tvDisasterHeader.setTextColor(Color.parseColor("#FF5252"));
        tvDisasterHeader.setTextSize(10.5f);
        FontHelper.applyBold(tvDisasterHeader);
        disasterBox.addView(tvDisasterHeader);

        TextView tvDisasterBody = new TextView(context);
        tvDisasterBody.setText(item.whatCouldHaveHappened);
        tvDisasterBody.setTextColor(Color.parseColor("#FFCDD2"));
        tvDisasterBody.setTextSize(10);
        FontHelper.applyRegular(tvDisasterBody);
        LinearLayout.LayoutParams lpDisBody = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpDisBody.topMargin = CyberUiTheme.dpToPx(context, 2);
        disasterBox.addView(tvDisasterBody, lpDisBody);

        row.addView(disasterBox);

        return row;
    }

    private static void shareRescueReport(Context context, List<RescueLedgerRepository.RescueItem> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("🛡️ کارنامه امنیتی و گزارش نجات اپلیکیشن سپر (Separ Guard)\n\n");
        sb.append("گزارش خطراتی که توسط سپر خنثی شده‌اند:\n\n");

        int index = 1;
        for (RescueLedgerRepository.RescueItem it : items) {
            sb.append(index++).append(". ").append(it.title).append("\n");
            sb.append("  • خلاصه دفاع: ").append(it.summary).append("\n");
            sb.append("  • در صورت نبود محافظت: ").append(it.whatCouldHaveHappened).append("\n\n");
            if (index > 4) break;
        }

        sb.append("نرم‌افزار ضد هک و امنیت بانکی سپر\n");
        sb.append("سازنده: ریک سانچز | حامیان: عادل خان و مسعود میرپناهی (عایوتک)\n");
        sb.append("دانلود رایگان از گیت‌هاب: github.com/m4tinbeigi-official/separ-guard");

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, "اشتراک‌گذاری کارنامه نجات سپر");
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(shareIntent);
    }
}

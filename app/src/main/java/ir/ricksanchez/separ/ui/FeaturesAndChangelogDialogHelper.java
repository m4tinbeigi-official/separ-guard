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
import android.widget.ScrollView;
import android.widget.TextView;
import ir.ricksanchez.separ.data.AppFeaturesAndChangelogRepository;
import java.util.List;

/**
 * نمایشگر شناسنامه قابلیت‌ها و گزارش تاریخچه بروزرسانی‌ها (Features & Changelog Hub)
 * نمایش کلیه امکانات سامانه به تفکیک دسته‌بندی و تغییرات هر نسخه
 */
public class FeaturesAndChangelogDialogHelper {

    public static void showFeaturesAndChangelogDialog(Context context) {
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
        tvTitle.setText("شناسنامه قابلیت‌ها و گزارش بروزرسانی‌ها");
        tvTitle.setTextColor(Color.parseColor("#00E5FF"));
        tvTitle.setTextSize(15.5f);
        FontHelper.applyBold(tvTitle);
        tvTitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lpTitle = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpTitle.topMargin = CyberUiTheme.dpToPx(context, 12);
        card.addView(tvTitle, lpTitle);

        TextView tvSub = new TextView(context);
        tvSub.setText("مجموعه امکانات فعال سپر و تاریخچه ارتقای سیستم در هر نگارش");
        tvSub.setTextColor(Color.parseColor("#94A3B8"));
        tvSub.setTextSize(11);
        FontHelper.applyRegular(tvSub);
        tvSub.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lpSub = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpSub.topMargin = CyberUiTheme.dpToPx(context, 2);
        card.addView(tvSub, lpSub);

        // ۳. محتوای اسکرول‌شونده
        ScrollView scroll = new ScrollView(context);
        LinearLayout.LayoutParams lpScroll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CyberUiTheme.dpToPx(context, 340));
        lpScroll.topMargin = CyberUiTheme.dpToPx(context, 12);
        scroll.setLayoutParams(lpScroll);

        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        // عنوان بخش تاریخچه نگارش‌ها
        TextView tvVerHeader = new TextView(context);
        tvVerHeader.setText("آخرین بروزرسانی‌ها (Changelog)");
        tvVerHeader.setTextColor(Color.parseColor("#00E676"));
        tvVerHeader.setTextSize(13);
        FontHelper.applyBold(tvVerHeader);
        LinearLayout.LayoutParams lpVer = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpVer.topMargin = CyberUiTheme.dpToPx(context, 6);
        lpVer.bottomMargin = CyberUiTheme.dpToPx(context, 8);
        content.addView(tvVerHeader, lpVer);

        for (AppFeaturesAndChangelogRepository.VersionRelease rel : AppFeaturesAndChangelogRepository.getChangelog()) {
            content.addView(createVersionCard(context, rel));
        }

        // عنوان بخش تمام قابلیت‌های فعال
        TextView tvFeatHeader = new TextView(context);
        tvFeatHeader.setText("کاتالوگ کلیه امکانات فعال سامانه");
        tvFeatHeader.setTextColor(Color.parseColor("#00E5FF"));
        tvFeatHeader.setTextSize(13);
        FontHelper.applyBold(tvFeatHeader);
        LinearLayout.LayoutParams lpFeat = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpFeat.topMargin = CyberUiTheme.dpToPx(context, 14);
        lpFeat.bottomMargin = CyberUiTheme.dpToPx(context, 8);
        content.addView(tvFeatHeader, lpFeat);

        for (AppFeaturesAndChangelogRepository.FeatureItem item : AppFeaturesAndChangelogRepository.getAllFeatures()) {
            content.addView(createFeatureCard(context, item));
        }

        scroll.addView(content);
        card.addView(scroll);

        // ۴. دکمه بستن
        Button btnClose = CyberUiTheme.createCyberButton(context, "بستن شناسنامه", "#1E293B", "#FFFFFF", 12);
        btnClose.setOnClickListener(v -> dialog.dismiss());
        LinearLayout.LayoutParams lpClose = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpClose.topMargin = CyberUiTheme.dpToPx(context, 12);
        card.addView(btnClose, lpClose);

        root.addView(card);
        dialog.setContentView(root);
        dialog.show();
    }

    private static View createVersionCard(Context context, AppFeaturesAndChangelogRepository.VersionRelease rel) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#151D28"));
        bg.setCornerRadius(CyberUiTheme.dpToPx(context, 12));
        bg.setStroke(CyberUiTheme.dpToPx(context, 1), Color.parseColor("#243142"));
        row.setBackground(bg);

        int p = CyberUiTheme.dpToPx(context, 10);
        row.setPadding(p, p, p, p);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = CyberUiTheme.dpToPx(context, 8);
        row.setLayoutParams(lp);

        LinearLayout header = new LinearLayout(context);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        TextView tvTitle = new TextView(context);
        tvTitle.setText(rel.versionName + " • " + rel.milestoneTitle);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setTextSize(12);
        FontHelper.applyBold(tvTitle);
        LinearLayout.LayoutParams lpT = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        header.addView(tvTitle, lpT);

        TextView tvDate = new TextView(context);
        tvDate.setText(rel.releaseDate);
        tvDate.setTextColor(Color.parseColor("#00E676"));
        tvDate.setTextSize(10);
        FontHelper.applyRegular(tvDate);
        header.addView(tvDate);

        row.addView(header);

        StringBuilder sb = new StringBuilder();
        for (String h : rel.highlights) {
            sb.append("• ").append(h).append("\n");
        }

        TextView tvDesc = new TextView(context);
        tvDesc.setText(sb.toString().trim());
        tvDesc.setTextColor(Color.parseColor("#94A3B8"));
        tvDesc.setTextSize(10.5f);
        FontHelper.applyRegular(tvDesc);
        LinearLayout.LayoutParams lpD = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpD.topMargin = CyberUiTheme.dpToPx(context, 4);
        row.addView(tvDesc, lpD);

        return row;
    }

    private static View createFeatureCard(Context context, AppFeaturesAndChangelogRepository.FeatureItem item) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#131922"));
        bg.setCornerRadius(CyberUiTheme.dpToPx(context, 10));
        bg.setStroke(CyberUiTheme.dpToPx(context, 1), Color.parseColor("#1E293B"));
        row.setBackground(bg);

        int p = CyberUiTheme.dpToPx(context, 9);
        row.setPadding(p, p, p, p);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = CyberUiTheme.dpToPx(context, 6);
        row.setLayoutParams(lp);

        LinearLayout header = new LinearLayout(context);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        TextView tvTitle = new TextView(context);
        tvTitle.setText(item.title);
        tvTitle.setTextColor(Color.parseColor("#00E5FF"));
        tvTitle.setTextSize(11.5f);
        FontHelper.applyBold(tvTitle);
        LinearLayout.LayoutParams lpT = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        header.addView(tvTitle, lpT);

        TextView tvTag = new TextView(context);
        tvTag.setText(item.tag);
        tvTag.setTextColor(Color.parseColor("#94A3B8"));
        tvTag.setTextSize(9.5f);
        FontHelper.applyRegular(tvTag);
        header.addView(tvTag);

        row.addView(header);

        TextView tvDesc = new TextView(context);
        tvDesc.setText(item.description);
        tvDesc.setTextColor(Color.parseColor("#CBD5E1"));
        tvDesc.setTextSize(10);
        FontHelper.applyRegular(tvDesc);
        LinearLayout.LayoutParams lpD = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpD.topMargin = CyberUiTheme.dpToPx(context, 2);
        row.addView(tvDesc, lpD);

        return row;
    }
}

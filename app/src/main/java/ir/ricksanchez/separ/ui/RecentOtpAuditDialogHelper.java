package ir.ricksanchez.separ.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import ir.ricksanchez.separ.R;
import ir.ricksanchez.separ.data.RecentOtpAuditRepository;
import java.util.List;

/**
 * نمایشگر دیده‌بان ورودهای پنهان و تاریخچه کدهای تایید (Ghost Login & Recent OTP Audit Dialog)
 * رصد حساب‌هایی که شاید کاربر برنامه‌شان را روی گوشی ندارد اما نفوذگر با شماره او وارد شده است.
 */
public class RecentOtpAuditDialogHelper {

    public static void showRecentOtpAuditDialog(Context context, RecentOtpAuditRepository repository) {
        if (context == null || repository == null) return;

        // اسکن پیامک‌های صندوق ورودی
        repository.scanDeviceSmsInboxIfPermitted();

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
        tvTitle.setText("دیده‌بان ورودهای پنهان: کدهای اخیری که برایتان آمده");
        tvTitle.setTextColor(Color.parseColor("#00E5FF"));
        tvTitle.setTextSize(15);
        FontHelper.applyBold(tvTitle);
        tvTitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lpTitle = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpTitle.topMargin = CyberUiTheme.dpToPx(context, 12);
        card.addView(tvTitle, lpTitle);

        TextView tvSub = new TextView(context);
        tvSub.setText("شناسایی پلتفرم‌هایی که شاید برنامه‌شان روی گوشی شما نیست اما هکر با شماره شما لاگین کرده است");
        tvSub.setTextColor(Color.parseColor("#94A3B8"));
        tvSub.setTextSize(11);
        FontHelper.applyRegular(tvSub);
        tvSub.setGravity(Gravity.CENTER);
        tvSub.setLineSpacing(0, 1.2f);
        LinearLayout.LayoutParams lpSub = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpSub.topMargin = CyberUiTheme.dpToPx(context, 4);
        card.addView(tvSub, lpSub);

        // ۳. کادر اخطار آموزشی
        LinearLayout infoBox = new LinearLayout(context);
        infoBox.setOrientation(LinearLayout.VERTICAL);
        infoBox.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        GradientDrawable ibBg = new GradientDrawable();
        ibBg.setColor(Color.parseColor("#151D28"));
        ibBg.setCornerRadius(CyberUiTheme.dpToPx(context, 10));
        ibBg.setStroke(CyberUiTheme.dpToPx(context, 1), Color.parseColor("#243142"));
        infoBox.setBackground(ibBg);

        int p10 = CyberUiTheme.dpToPx(context, 10);
        infoBox.setPadding(p10, p10, p10, p10);
        LinearLayout.LayoutParams lpIb = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpIb.topMargin = CyberUiTheme.dpToPx(context, 10);
        lpIb.bottomMargin = CyberUiTheme.dpToPx(context, 10);
        infoBox.setLayoutParams(lpIb);

        TextView tvInfo = new TextView(context);
        tvInfo.setText("💡 اگر برای برنامه‌ای که در گوشی خود نصب ندارید کد پیامک شده است، بلافاصله روی دکمه «خروج نفوذگر» کلیک کنید تا متوجه شوید آیا شخصی با هویت شما وارد شده یا خیر.");
        tvInfo.setTextColor(Color.parseColor("#FFD600"));
        tvInfo.setTextSize(10.5f);
        FontHelper.applyRegular(tvInfo);
        tvInfo.setLineSpacing(0, 1.25f);
        infoBox.addView(tvInfo);

        card.addView(infoBox);

        // ۴. اسکرول‌ویو لیست کدها
        ScrollView scroll = new ScrollView(context);
        LinearLayout.LayoutParams lpScroll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CyberUiTheme.dpToPx(context, 320));
        scroll.setLayoutParams(lpScroll);

        LinearLayout list = new LinearLayout(context);
        list.setOrientation(LinearLayout.VERTICAL);
        list.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        List<RecentOtpAuditRepository.OtpAuditItem> items = repository.getAllRecentOtps();
        for (RecentOtpAuditRepository.OtpAuditItem it : items) {
            list.addView(createOtpHistoryCard(context, it));
        }

        scroll.addView(list);
        card.addView(scroll);

        // ۵. دکمه‌های پایین
        LinearLayout actions = new LinearLayout(context);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        LinearLayout.LayoutParams lpAct = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpAct.topMargin = CyberUiTheme.dpToPx(context, 12);
        actions.setLayoutParams(lpAct);

        Button btnRefresh = CyberUiTheme.createCyberButton(context, "پویش مجدد پیامک‌ها", "#151D28", "#00E5FF", 11);
        btnRefresh.setOnClickListener(v -> {
            repository.scanDeviceSmsInboxIfPermitted();
            dialog.dismiss();
            showRecentOtpAuditDialog(context, repository);
            Toast.makeText(context, "پویش پیامک‌های اخیر انجام شد.", Toast.LENGTH_SHORT).show();
        });
        LinearLayout.LayoutParams lpRef = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        lpRef.leftMargin = CyberUiTheme.dpToPx(context, 6);
        actions.addView(btnRefresh, lpRef);

        Button btnClose = CyberUiTheme.createCyberButton(context, "بستن", "#1E293B", "#FFFFFF", 12);
        btnClose.setOnClickListener(v -> dialog.dismiss());
        LinearLayout.LayoutParams lpClose = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        actions.addView(btnClose, lpClose);

        card.addView(actions);

        root.addView(card);
        dialog.setContentView(root);
        dialog.show();
    }

    private static View createOtpHistoryCard(Context context, final RecentOtpAuditRepository.OtpAuditItem item) {
        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#131922"));
        bg.setCornerRadius(CyberUiTheme.dpToPx(context, 12));
        if (!item.isAppInstalled) {
            // هشدار: برنامه روی گوشی نصب نیست!
            bg.setStroke(CyberUiTheme.dpToPx(context, 2), Color.parseColor("#FF9100"));
        } else {
            bg.setStroke(CyberUiTheme.dpToPx(context, 1), Color.parseColor("#1E293B"));
        }
        row.setBackground(bg);

        int p = CyberUiTheme.dpToPx(context, 12);
        row.setPadding(p, p, p, p);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = CyberUiTheme.dpToPx(context, 8);
        row.setLayoutParams(lp);

        // هدر کارت: نام پلتفرم و زمان دریافت
        LinearLayout header = new LinearLayout(context);
        header.setOrientation(LinearLayout.HORIZONTAL);
        header.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        TextView tvPlatform = new TextView(context);
        tvPlatform.setText(item.platformName);
        tvPlatform.setTextColor(Color.WHITE);
        tvPlatform.setTextSize(12.5f);
        FontHelper.applyBold(tvPlatform);
        LinearLayout.LayoutParams lpPf = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        header.addView(tvPlatform, lpPf);

        TextView tvDate = new TextView(context);
        tvDate.setText(item.dateTimeFa);
        tvDate.setTextColor(Color.parseColor("#00E5FF"));
        tvDate.setTextSize(10);
        FontHelper.applyRegular(tvDate);
        header.addView(tvDate);

        row.addView(header);

        // نشان وضعیت نصب بودن برنامه روی گوشی
        TextView tvInstallStatus = new TextView(context);
        if (!item.isAppInstalled) {
            tvInstallStatus.setText("⚠️ هشدار: این برنامه روی گوشی شما نصب نیست! (خطر ورود از راه دور)");
            tvInstallStatus.setTextColor(Color.parseColor("#FF9100"));
        } else {
            tvInstallStatus.setText("✓ این برنامه روی گوشی شما نصب است");
            tvInstallStatus.setTextColor(Color.parseColor("#00E676"));
        }
        tvInstallStatus.setTextSize(10.5f);
        FontHelper.applyBold(tvInstallStatus);
        LinearLayout.LayoutParams lpIs = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpIs.topMargin = CyberUiTheme.dpToPx(context, 4);
        row.addView(tvInstallStatus, lpIs);

        // متن پیامک
        TextView tvBody = new TextView(context);
        tvBody.setText("متن پیامک: «" + item.rawBody + "»");
        tvBody.setTextColor(Color.parseColor("#94A3B8"));
        tvBody.setTextSize(10.5f);
        FontHelper.applyRegular(tvBody);
        tvBody.setLineSpacing(0, 1.2f);
        LinearLayout.LayoutParams lpB = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpB.topMargin = CyberUiTheme.dpToPx(context, 4);
        lpB.bottomMargin = CyberUiTheme.dpToPx(context, 8);
        row.addView(tvBody, lpB);

        // دکمه‌های اقدام جهت خروج نفوذگر یا تماس
        LinearLayout btnRow = new LinearLayout(context);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        if (item.portalUrl != null && !item.portalUrl.isEmpty()) {
            Button btnPortal = CyberUiTheme.createCyberButton(context, "ورود به پورتال وب و خروج نفوذگر", "#151D28", "#00E5FF", 10);
            btnPortal.setOnClickListener(v -> {
                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.portalUrl));
                    browserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(browserIntent);
                } catch (Exception ignored) {}
            });
            LinearLayout.LayoutParams lpP = new LinearLayout.LayoutParams(0, CyberUiTheme.dpToPx(context, 36), 1f);
            lpP.leftMargin = CyberUiTheme.dpToPx(context, 6);
            btnRow.addView(btnPortal, lpP);
        }

        if (item.supportPhone != null && !item.supportPhone.isEmpty()) {
            Button btnPhone = CyberUiTheme.createCyberButton(context, "تماس با پشتیبانی", "#1E293B", "#FFD600", 10);
            btnPhone.setOnClickListener(v -> {
                try {
                    Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + item.supportPhone));
                    dialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(dialIntent);
                } catch (Exception ignored) {}
            });
            LinearLayout.LayoutParams lpPh = new LinearLayout.LayoutParams(0, CyberUiTheme.dpToPx(context, 36), 1f);
            btnRow.addView(btnPhone, lpPh);
        }

        if (btnRow.getChildCount() > 0) {
            row.addView(btnRow);
        }

        return row;
    }
}

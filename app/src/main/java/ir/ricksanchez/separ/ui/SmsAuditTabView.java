package ir.ricksanchez.separ.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import ir.ricksanchez.separ.data.SmsAndScreenGuard;
import ir.ricksanchez.separ.data.ThreatPackageRegistry;
import java.util.List;

/**
 * رندر تب ممیزی دسترسی به پیامک‌ها، ضبط صفحه و بدافزارهای استتاریافته (SMS & Apps SOC)
 * مجهز به فونت فارسی وزیرمتن (Vazirmatn)
 */
public class SmsAuditTabView {

    public interface SmsAuditTabListener {
        void onRevokeOrUninstallClicked(String packageName, boolean isMalware);
        void onAddToWhitelistClicked(String packageName, String appName);
        void onManageWhitelistClicked();
    }

    public static LinearLayout buildView(Context context,
                                         List<ThreatPackageRegistry.ThreatResult> threats,
                                         List<SmsAndScreenGuard.AppAuditResult> smsApps,
                                         final SmsAuditTabListener listener) {

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // ۱. کارت هشدار بدافزارهای قطعی (در صورت وجود)
        if (!threats.isEmpty()) {
            LinearLayout threatCard = new LinearLayout(context);
            threatCard.setOrientation(LinearLayout.VERTICAL);
            threatCard.setPadding(24, 20, 24, 20);
            threatCard.setBackground(CyberUiTheme.createCardBg("#381515", CyberUiTheme.COLOR_CRIMSON, 20));
            LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tlp.setMargins(0, 0, 0, 16);
            threatCard.setLayoutParams(tlp);

            TextView tTitle = new TextView(context);
            tTitle.setText("🔴 " + threats.size() + " بدافزار سارق و استتاریافته شناسایی شد!");
            tTitle.setTextColor(Color.parseColor(CyberUiTheme.COLOR_CRIMSON));
            tTitle.setTextSize(14);
            FontHelper.applyBold(tTitle);
            threatCard.addView(tTitle);

            for (ThreatPackageRegistry.ThreatResult t : threats) {
                TextView tItem = new TextView(context);
                tItem.setText("• " + t.threatName + "\n  " + t.description);
                tItem.setTextColor(Color.WHITE);
                tItem.setTextSize(12);
                FontHelper.applyRegular(tItem);
                tItem.setPadding(0, 6, 0, 8);
                threatCard.addView(tItem);

                Button delBtn = CyberUiTheme.createCyberButton(context, "🗑️ حذف فوری " + t.threatName, "#D32F2F", "#FFFFFF", 12);
                delBtn.setOnClickListener(v -> {
                    if (listener != null) listener.onRevokeOrUninstallClicked(t.packageName, true);
                });
                threatCard.addView(delBtn);
            }
            container.addView(threatCard);
        }

        // ۲. کارت ممیزی دسترسی پیامک‌ها
        int unapprovedCount = 0;
        for (SmsAndScreenGuard.AppAuditResult a : smsApps) {
            if (!a.isSafe()) unapprovedCount++;
        }

        LinearLayout auditCard = new LinearLayout(context);
        auditCard.setOrientation(LinearLayout.VERTICAL);
        auditCard.setPadding(24, 20, 24, 20);
        String auditBg = unapprovedCount > 0 ? "#2D181A" : "#102318";
        String auditBorder = unapprovedCount > 0 ? CyberUiTheme.COLOR_CRIMSON : CyberUiTheme.COLOR_NEON_GREEN;
        auditCard.setBackground(CyberUiTheme.createCardBg(auditBg, auditBorder, 20));
        LinearLayout.LayoutParams alp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alp.setMargins(0, 0, 0, 16);
        auditCard.setLayoutParams(alp);

        TextView aTitle = new TextView(context);
        aTitle.setText(unapprovedCount > 0
            ? "⚠️ " + unapprovedCount + " برنامه بدون اجازه به پیامک‌ها دسترسی دارند!"
            : "🔒 محافظت فعال: هیچ برنامه ناشناسی به پیامک‌ها دسترسی ندارد");
        aTitle.setTextColor(Color.parseColor(unapprovedCount > 0 ? CyberUiTheme.COLOR_CRIMSON : CyberUiTheme.COLOR_NEON_GREEN));
        aTitle.setTextSize(14);
        FontHelper.applyBold(aTitle);
        auditCard.addView(aTitle);

        TextView aDesc = new TextView(context);
        aDesc.setText("بیش از ۹۰٪ حملات بانکی از شنود پیامک‌های رمز پویا شروع می‌شوند. برنامه‌های زیر دسترسی دارند:");
        aDesc.setTextColor(Color.parseColor(CyberUiTheme.COLOR_TEXT_MUTED));
        aDesc.setTextSize(11);
        FontHelper.applyRegular(aDesc);
        aDesc.setPadding(0, 4, 0, 12);
        auditCard.addView(aDesc);

        for (SmsAndScreenGuard.AppAuditResult item : smsApps) {
            if (!item.isSafe()) {
                LinearLayout row = new LinearLayout(context);
                row.setOrientation(LinearLayout.VERTICAL);
                row.setPadding(16, 12, 16, 12);
                row.setBackground(CyberUiTheme.createCardBg("#1E2536", null, 14));
                LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rlp.setMargins(0, 4, 0, 6);
                row.setLayoutParams(rlp);

                TextView nView = new TextView(context);
                nView.setText("• " + item.appName + " (" + item.packageName + ")");
                nView.setTextColor(Color.WHITE);
                nView.setTextSize(13);
                FontHelper.applyBold(nView);
                row.addView(nView);

                TextView dView = new TextView(context);
                dView.setText(item.description);
                dView.setTextColor(Color.parseColor(CyberUiTheme.COLOR_AMBER));
                dView.setTextSize(11);
                FontHelper.applyRegular(dView);
                dView.setPadding(0, 2, 0, 6);
                row.addView(dView);

                LinearLayout btnRow = new LinearLayout(context);
                btnRow.setOrientation(LinearLayout.HORIZONTAL);

                Button revBtn = CyberUiTheme.createChipButton(context, "سلب دسترسی / حذف", "#D32F2F", Color.WHITE);
                revBtn.setOnClickListener(v -> {
                    if (listener != null) listener.onRevokeOrUninstallClicked(item.packageName, item.isKnownMalware);
                });
                btnRow.addView(revBtn);

                Button addBtn = CyberUiTheme.createChipButton(context, "افزودن به لیست سفید", "#263238", Color.parseColor("#80CBC4"));
                addBtn.setOnClickListener(v -> {
                    if (listener != null) listener.onAddToWhitelistClicked(item.packageName, item.appName);
                });
                btnRow.addView(addBtn);

                row.addView(btnRow);
                auditCard.addView(row);
            }
        }

        Button manageBtn = CyberUiTheme.createCyberButton(context, "📋 مدیریت لیست سفید برنامه‌های من", "#1A2638", CyberUiTheme.COLOR_CYAN, 12);
        manageBtn.setOnClickListener(v -> {
            if (listener != null) listener.onManageWhitelistClicked();
        });
        auditCard.addView(manageBtn);

        container.addView(auditCard);
        return container;
    }
}

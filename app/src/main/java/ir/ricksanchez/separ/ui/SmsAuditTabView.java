package ir.ricksanchez.separ.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import ir.ricksanchez.separ.R;
import ir.ricksanchez.separ.data.SmsAndScreenGuard;
import ir.ricksanchez.separ.data.ThreatPackageRegistry;
import java.util.List;

/**
 * رندر تب ممیزی دسترسی به پیامک‌ها، اسکنر بدافزارها و مدیریت مجوزهای اپلیکیشن‌ها (SMS & Apps SOC)
 * امکان کشف فوری بدافزارها، حذف قطعی با ۱ کلیک یا لغو و سلب دسترسی‌ها در تنظیمات اندروید
 */
public class SmsAuditTabView {

    public interface SmsAuditTabListener {
        void onRevokeOrUninstallClicked(String packageName, boolean isMalware);
        void onManageAppPermissionsClicked(String packageName);
        void onAddToWhitelistClicked(String packageName, String appName);
        void onManageWhitelistClicked();
        void onRescanAppsRequested();
    }

    public static LinearLayout buildView(Context context,
                                         List<ThreatPackageRegistry.ThreatResult> threats,
                                         List<SmsAndScreenGuard.AppAuditResult> smsApps,
                                         final SmsAuditTabListener listener) {

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // ۱. دکمه و خلاصه اسکن زنده اپلیکیشن‌ها
        LinearLayout scanHeader = new LinearLayout(context);
        scanHeader.setOrientation(LinearLayout.VERTICAL);
        int p = CyberUiTheme.dpToPx(context, 16);
        scanHeader.setPadding(p, p, p, p);
        scanHeader.setBackground(CyberUiTheme.createCardBg("#0D1624", CyberUiTheme.COLOR_CYAN, 18));
        LinearLayout.LayoutParams shlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        shlp.setMargins(0, 0, 0, CyberUiTheme.dpToPx(context, 14));
        scanHeader.setLayoutParams(shlp);

        TextView shTitle = new TextView(context);
        shTitle.setText("اسکنر و ممیزی جامع برنامه‌های نصب‌شده");
        shTitle.setTextColor(Color.WHITE);
        shTitle.setTextSize(14);
        FontHelper.applyBold(shTitle);
        scanHeader.addView(shTitle);

        TextView shDesc = new TextView(context);
        shDesc.setText("تمام برنامه‌های گوشی شما از نظر دسترسی به پیامک‌های بانکی، اعلان‌های رمز پویا، همپوشانی صفحه و امضاهای مخرب ممیزی شدند.");
        shDesc.setTextColor(Color.parseColor(CyberUiTheme.COLOR_TEXT_MUTED));
        shDesc.setTextSize(11);
        FontHelper.applyRegular(shDesc);
        shDesc.setLineSpacing(CyberUiTheme.dpToPx(context, 3), 1.25f);
        shDesc.setPadding(0, CyberUiTheme.dpToPx(context, 4), 0, CyberUiTheme.dpToPx(context, 10));
        scanHeader.addView(shDesc);

        Button rescanBtn = CyberUiTheme.createPrimaryButton(context, "اسکن مجدد و بررسی آنی برنامه‌ها", "#00E5FF", "#080B10");
        rescanBtn.setOnClickListener(v -> {
            if (listener != null) listener.onRescanAppsRequested();
        });
        scanHeader.addView(rescanBtn);

        container.addView(scanHeader);

        // ۲. کارت بدافزارهای قطعی (در صورت شناسایی)
        if (!threats.isEmpty()) {
            LinearLayout threatCard = new LinearLayout(context);
            threatCard.setOrientation(LinearLayout.VERTICAL);
            int tp = CyberUiTheme.dpToPx(context, 18);
            threatCard.setPadding(tp, tp, tp, tp);
            threatCard.setBackground(CyberUiTheme.createCardBg("#381515", CyberUiTheme.COLOR_CRIMSON, 18));
            LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            tlp.setMargins(0, 0, 0, CyberUiTheme.dpToPx(context, 14));
            threatCard.setLayoutParams(tlp);

            TextView tTitle = new TextView(context);
            tTitle.setText(CyberUiTheme.toPersianDigits(String.valueOf(threats.size())) + " بدافزار سارق و خطرناک شناسایی شد");
            tTitle.setTextColor(Color.parseColor(CyberUiTheme.COLOR_CRIMSON));
            tTitle.setTextSize(14);
            FontHelper.applyBold(tTitle);
            threatCard.addView(tTitle);

            for (ThreatPackageRegistry.ThreatResult t : threats) {
                LinearLayout itemBox = new LinearLayout(context);
                itemBox.setOrientation(LinearLayout.VERTICAL);
                itemBox.setPadding(CyberUiTheme.dpToPx(context, 12), CyberUiTheme.dpToPx(context, 10), CyberUiTheme.dpToPx(context, 12), CyberUiTheme.dpToPx(context, 10));
                itemBox.setBackground(CyberUiTheme.createCardBg("#251012", CyberUiTheme.COLOR_CRIMSON, 12));
                LinearLayout.LayoutParams ilp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                ilp.setMargins(0, CyberUiTheme.dpToPx(context, 8), 0, 0);
                itemBox.setLayoutParams(ilp);

                TextView tName = new TextView(context);
                tName.setText(t.threatName + " (" + t.packageName + ")");
                tName.setTextColor(Color.WHITE);
                tName.setTextSize(13);
                FontHelper.applyBold(tName);
                itemBox.addView(tName);

                TextView tDesc = new TextView(context);
                tDesc.setText(t.description);
                tDesc.setTextColor(Color.parseColor("#FFCDD2"));
                tDesc.setTextSize(11);
                FontHelper.applyRegular(tDesc);
                tDesc.setPadding(0, CyberUiTheme.dpToPx(context, 3), 0, CyberUiTheme.dpToPx(context, 8));
                itemBox.addView(tDesc);

                LinearLayout btnRow = new LinearLayout(context);
                btnRow.setOrientation(LinearLayout.HORIZONTAL);

                Button delBtn = CyberUiTheme.createChipButton(context, "حذف قطعی بدافزار", "#D32F2F", Color.WHITE);
                delBtn.setOnClickListener(v -> {
                    if (listener != null) listener.onRevokeOrUninstallClicked(t.packageName, true);
                });
                btnRow.addView(delBtn);

                Button manageBtn = CyberUiTheme.createChipButton(context, "لغو دسترسی‌ها", "#1E2738", Color.parseColor(CyberUiTheme.COLOR_CYAN));
                manageBtn.setOnClickListener(v -> {
                    if (listener != null) listener.onManageAppPermissionsClicked(t.packageName);
                });
                btnRow.addView(manageBtn);

                itemBox.addView(btnRow);
                threatCard.addView(itemBox);
            }
            container.addView(threatCard);
        }

        // ۳. کارت ممیزی دسترسی پیامک‌ها و نوتیفیکیشن‌ها
        int unapprovedCount = 0;
        for (SmsAndScreenGuard.AppAuditResult a : smsApps) {
            if (!a.isSafe()) unapprovedCount++;
        }

        LinearLayout auditCard = new LinearLayout(context);
        auditCard.setOrientation(LinearLayout.VERTICAL);
        int ap = CyberUiTheme.dpToPx(context, 18);
        auditCard.setPadding(ap, ap, ap, ap);
        String auditBg = unapprovedCount > 0 ? "#2D181A" : "#102318";
        String auditBorder = unapprovedCount > 0 ? CyberUiTheme.COLOR_CRIMSON : CyberUiTheme.COLOR_NEON_GREEN;
        auditCard.setBackground(CyberUiTheme.createCardBg(auditBg, auditBorder, 18));
        LinearLayout.LayoutParams alp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        alp.setMargins(0, 0, 0, CyberUiTheme.dpToPx(context, 14));
        auditCard.setLayoutParams(alp);

        TextView aTitle = new TextView(context);
        aTitle.setText(unapprovedCount > 0
            ? CyberUiTheme.toPersianDigits(String.valueOf(unapprovedCount)) + " برنامه دارای دسترسی حساس به پیامک و صفحه"
            : "محافظت فعال: کلیه برنامه‌های دارای دسترسی در لیست سفید تایید شده‌اند");
        aTitle.setTextColor(Color.parseColor(unapprovedCount > 0 ? CyberUiTheme.COLOR_CRIMSON : CyberUiTheme.COLOR_NEON_GREEN));
        aTitle.setTextSize(14);
        FontHelper.applyBold(aTitle);
        auditCard.addView(aTitle);

        TextView aDesc = new TextView(context);
        aDesc.setText("بیش از ۹۰٪ سرقت‌های بانکی از طریق شنود پیامک‌های رمز پویا انجام می‌شود. شما می‌توانید با ۱ کلیک برنامه را حذف یا دسترسی آن را سلب کنید:");
        aDesc.setTextColor(Color.parseColor(CyberUiTheme.COLOR_TEXT_MUTED));
        aDesc.setTextSize(11);
        FontHelper.applyRegular(aDesc);
        aDesc.setPadding(0, CyberUiTheme.dpToPx(context, 4), 0, CyberUiTheme.dpToPx(context, 10));
        auditCard.addView(aDesc);

        for (SmsAndScreenGuard.AppAuditResult item : smsApps) {
            if (!item.isSafe()) {
                LinearLayout row = new LinearLayout(context);
                row.setOrientation(LinearLayout.VERTICAL);
                row.setPadding(CyberUiTheme.dpToPx(context, 14), CyberUiTheme.dpToPx(context, 12), CyberUiTheme.dpToPx(context, 14), CyberUiTheme.dpToPx(context, 12));
                row.setBackground(CyberUiTheme.createCardBg("#1E2536", null, 14));
                LinearLayout.LayoutParams rlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                rlp.setMargins(0, 0, 0, CyberUiTheme.dpToPx(context, 8));
                row.setLayoutParams(rlp);

                TextView nView = new TextView(context);
                nView.setText(item.appName + " (" + item.packageName + ")");
                nView.setTextColor(Color.WHITE);
                nView.setTextSize(13);
                FontHelper.applyBold(nView);
                row.addView(nView);

                TextView dView = new TextView(context);
                dView.setText(item.description);
                dView.setTextColor(Color.parseColor(CyberUiTheme.COLOR_AMBER));
                dView.setTextSize(11);
                FontHelper.applyRegular(dView);
                dView.setPadding(0, CyberUiTheme.dpToPx(context, 3), 0, CyberUiTheme.dpToPx(context, 8));
                row.addView(dView);

                LinearLayout btnRow = new LinearLayout(context);
                btnRow.setOrientation(LinearLayout.HORIZONTAL);

                Button revBtn = CyberUiTheme.createChipButton(context, "سلب دسترسی / تنظیمات", "#1E293B", Color.parseColor(CyberUiTheme.COLOR_CYAN));
                revBtn.setOnClickListener(v -> {
                    if (listener != null) listener.onManageAppPermissionsClicked(item.packageName);
                });
                btnRow.addView(revBtn);

                Button delBtn = CyberUiTheme.createChipButton(context, "حذف برنامه", "#D32F2F", Color.WHITE);
                delBtn.setOnClickListener(v -> {
                    if (listener != null) listener.onRevokeOrUninstallClicked(item.packageName, item.isKnownMalware);
                });
                btnRow.addView(delBtn);

                Button addBtn = CyberUiTheme.createChipButton(context, "لیست سفید", "#15241D", Color.parseColor(CyberUiTheme.COLOR_NEON_GREEN));
                addBtn.setOnClickListener(v -> {
                    if (listener != null) listener.onAddToWhitelistClicked(item.packageName, item.appName);
                });
                btnRow.addView(addBtn);

                row.addView(btnRow);
                auditCard.addView(row);
            }
        }

        Button manageBtn = CyberUiTheme.createCyberButton(context, "مدیریت لیست سفید برنامه‌های من", "#1A2638", CyberUiTheme.COLOR_CYAN, 12);
        manageBtn.setOnClickListener(v -> {
            if (listener != null) listener.onManageWhitelistClicked();
        });
        auditCard.addView(manageBtn);

        container.addView(auditCard);
        return container;
    }
}

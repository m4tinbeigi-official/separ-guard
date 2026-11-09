package ir.ricksanchez.separ.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import ir.ricksanchez.separ.data.RescueLedgerRepository;

/**
 * رندر تب رادار امنیت و داشبورد مرکزی (Cyber Radar Dashboard)
 */
public class RadarDashboardView {

    public interface RadarDashboardListener {
        void onScanRequested();
        void onVoiceRequested();
        void onCancelDivertClicked();
        void onFataEmergencyClicked();
        void onSessionKillClicked();
        void onReceiptVerifyClicked();
    }

    public static View buildView(Context context, boolean hasThreats, int threatsCount,
                                 RescueLedgerRepository.RescueStats stats,
                                 final RadarDashboardListener listener) {

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // ۱. نوار چیپ‌های وضعیت زنده (Live Telemetry Row)
        LinearLayout statsRow = new LinearLayout(context);
        statsRow.setOrientation(LinearLayout.HORIZONTAL);
        statsRow.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        statsRow.setPadding(0, 0, 0, 20);

        statsRow.addView(CyberUiTheme.createStatChip(context, "دفاع موفق", String.valueOf(stats.totalAttacksBlocked), CyberUiTheme.COLOR_CYAN));
        statsRow.addView(CyberUiTheme.createStatChip(context, "سرقت بانکی خنثی‌شده", String.valueOf(stats.bankingTheftsPrevented), CyberUiTheme.COLOR_NEON_GREEN));
        statsRow.addView(CyberUiTheme.createStatChip(context, "تهدید فعال", String.valueOf(threatsCount), hasThreats ? CyberUiTheme.COLOR_CRIMSON : CyberUiTheme.COLOR_NEON_GREEN));
        container.addView(statsRow);

        // ۲. رادار مدور سایبری (Concentric Cyber Radar Core)
        LinearLayout outerRadar = new LinearLayout(context);
        outerRadar.setOrientation(LinearLayout.VERTICAL);
        outerRadar.setGravity(Gravity.CENTER);
        int outerSizePx = 360;
        LinearLayout.LayoutParams outerParams = new LinearLayout.LayoutParams(outerSizePx, outerSizePx);
        outerParams.gravity = Gravity.CENTER_HORIZONTAL;
        outerParams.setMargins(0, 10, 0, 24);
        outerRadar.setLayoutParams(outerParams);

        String radarBg = hasThreats ? "#3A1418" : "#0D2618";
        String radarBorder = hasThreats ? CyberUiTheme.COLOR_CRIMSON : CyberUiTheme.COLOR_NEON_GREEN;
        outerRadar.setBackground(CyberUiTheme.createOvalBg(radarBg, radarBorder, 4));

        LinearLayout innerRadar = new LinearLayout(context);
        innerRadar.setOrientation(LinearLayout.VERTICAL);
        innerRadar.setGravity(Gravity.CENTER);
        int innerSizePx = 310;
        LinearLayout.LayoutParams innerParams = new LinearLayout.LayoutParams(innerSizePx, innerSizePx);
        innerParams.gravity = Gravity.CENTER;
        innerRadar.setLayoutParams(innerParams);

        String innerBg = hasThreats ? "#5A1E24" : "#133824";
        innerRadar.setBackground(CyberUiTheme.createOvalBg(innerBg, radarBorder, 2));

        TextView iconView = new TextView(context);
        iconView.setText(hasThreats ? "⛔" : "🛡️");
        iconView.setTextSize(36);
        iconView.setGravity(Gravity.CENTER);
        innerRadar.addView(iconView);

        TextView statusTitle = new TextView(context);
        statusTitle.setText(hasThreats ? "خطر شناسایی شد!" : "سیستم کاملاً ایمن است");
        statusTitle.setTextSize(16);
        statusTitle.setTextColor(Color.WHITE);
        statusTitle.setTypeface(null, Typeface.BOLD);
        statusTitle.setGravity(Gravity.CENTER);
        statusTitle.setPadding(0, 6, 0, 2);
        innerRadar.addView(statusTitle);

        TextView subTitle = new TextView(context);
        subTitle.setText(hasThreats ? "لمس برای پاکسازی فوری" : "لمس برای رصد و اسکن عمیق");
        subTitle.setTextSize(11);
        subTitle.setTextColor(Color.parseColor(hasThreats ? "#FF8A80" : "#A7F3D0"));
        subTitle.setGravity(Gravity.CENTER);
        innerRadar.addView(subTitle);

        outerRadar.addView(innerRadar);
        outerRadar.setOnClickListener(v -> {
            if (listener != null) listener.onScanRequested();
        });
        container.addView(outerRadar);

        // ۳. دکمه شنیدن دستیار صوتی
        Button voiceBtn = CyberUiTheme.createCyberButton(context, "🔊 شنیدن تحلیل و وضعیت صوتی گوشی", "#162032", CyberUiTheme.COLOR_NEON_GREEN, 12);
        voiceBtn.setOnClickListener(v -> {
            if (listener != null) listener.onVoiceRequested();
        });
        container.addView(voiceBtn);

        // ۴. شبکه ابزارهای بحران و واکنش سریع (۴ کاشی عملیاتی)
        TextView quickHeader = new TextView(context);
        quickHeader.setText("⚡ ابزارهای فوریت‌های امنیتی و واکنش سریع:");
        quickHeader.setTextSize(13);
        quickHeader.setTextColor(Color.parseColor(CyberUiTheme.COLOR_TEXT_PRIMARY));
        quickHeader.setTypeface(null, Typeface.BOLD);
        quickHeader.setPadding(0, 20, 0, 10);
        container.addView(quickHeader);

        // سطر ۱ کاشی‌ها
        LinearLayout row1 = new LinearLayout(context);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        row1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        Button divertBtn = CyberUiTheme.createChipButton(context, "📵 لغو دایورت (##002#)", "#2A1418", Color.parseColor("#FF5252"));
        divertBtn.setOnClickListener(v -> {
            if (listener != null) listener.onCancelDivertClicked();
        });
        row1.addView(divertBtn);

        Button fataBtn = CyberUiTheme.createChipButton(context, "🚨 پلیس فتا (۰۹۶۳۸۰)", "#122638", Color.parseColor("#00E5FF"));
        fataBtn.setOnClickListener(v -> {
            if (listener != null) listener.onFataEmergencyClicked();
        });
        row1.addView(fataBtn);
        container.addView(row1);

        // سطر ۲ کاشی‌ها
        LinearLayout row2 = new LinearLayout(context);
        row2.setOrientation(LinearLayout.HORIZONTAL);
        row2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        Button sessionBtn = CyberUiTheme.createChipButton(context, "🚪 اخراج نفوذگران سشن‌ها", "#261D12", Color.parseColor("#FFA726"));
        sessionBtn.setOnClickListener(v -> {
            if (listener != null) listener.onSessionKillClicked();
        });
        row2.addView(sessionBtn);

        Button receiptBtn = CyberUiTheme.createChipButton(context, "💳 بررسی اصالت فیش بانکی", "#122A1E", Color.parseColor("#00E676"));
        receiptBtn.setOnClickListener(v -> {
            if (listener != null) listener.onReceiptVerifyClicked();
        });
        row2.addView(receiptBtn);
        container.addView(row2);

        return container;
    }
}

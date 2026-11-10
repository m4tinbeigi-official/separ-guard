package ir.ricksanchez.separ.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import ir.ricksanchez.separ.R;
import ir.ricksanchez.separ.utils.PrivacyPolicyHelper;

/**
 * صفحه معرفی رسمی سازنده و برنامه‌نویس نرم‌افزار «سپر»: ریک سانچز (Rick Sanchez)
 * طراحی دارک مود درجه یک بدون استفاده از اموجی، مجهز به وکتورهای تمیز و تایپوگرافی وزیرمتن
 */
public class CreatorProfileTabView {

    public interface CreatorProfileListener {
        void onTelegramClicked();
        void onGithubClicked();
        void onShareAppClicked();
        void onPrivacyClicked();
    }

    public static LinearLayout buildView(final Context context, final CreatorProfileListener listener) {
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // ۱. کارت شناسنامه و هویت توسعه‌دهنده
        LinearLayout idCard = new LinearLayout(context);
        idCard.setOrientation(LinearLayout.VERTICAL);
        idCard.setGravity(Gravity.CENTER);
        idCard.setPadding(24, 28, 24, 28);
        idCard.setBackground(CyberUiTheme.createCardBg("#111827", CyberUiTheme.COLOR_NEON_GREEN, 22));
        LinearLayout.LayoutParams idLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        idLp.setMargins(0, 0, 0, 16);
        idCard.setLayoutParams(idLp);

        // نشان گرافیکی حروف اختصاری سازنده (RS)
        TextView avatarBadge = new TextView(context);
        avatarBadge.setText("RS");
        avatarBadge.setTextSize(20);
        avatarBadge.setTextColor(Color.WHITE);
        FontHelper.applyBold(avatarBadge);
        avatarBadge.setGravity(Gravity.CENTER);
        int avatarSizePx = 54;
        LinearLayout.LayoutParams alp = new LinearLayout.LayoutParams(avatarSizePx, avatarSizePx);
        avatarBadge.setLayoutParams(alp);
        avatarBadge.setBackground(CyberUiTheme.createOvalBg("#1E293B", CyberUiTheme.COLOR_NEON_GREEN, 2));
        idCard.addView(avatarBadge);

        TextView devName = new TextView(context);
        devName.setText("ریک سانچز (Rick Sanchez)");
        devName.setTextColor(Color.WHITE);
        devName.setTextSize(20);
        FontHelper.applyBold(devName);
        devName.setGravity(Gravity.CENTER);
        devName.setPadding(0, 12, 0, 4);
        idCard.addView(devName);

        TextView devRole = new TextView(context);
        devRole.setText("معمار ارشد امنیت سایبری و مهندسی معکوس بدافزارها");
        devRole.setTextColor(Color.parseColor(CyberUiTheme.COLOR_CYAN));
        devRole.setTextSize(12);
        FontHelper.applyRegular(devRole);
        devRole.setGravity(Gravity.CENTER);
        idCard.addView(devRole);

        container.addView(idCard);

        // ۲. بیانیه نرم‌افزار عام‌المنفعه و ۱۰۰٪ رایگان
        LinearLayout missionCard = new LinearLayout(context);
        missionCard.setOrientation(LinearLayout.VERTICAL);
        missionCard.setPadding(20, 18, 20, 18);
        missionCard.setBackground(CyberUiTheme.createCardBg("#131D2C", CyberUiTheme.COLOR_CYAN, 18));
        LinearLayout.LayoutParams mlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mlp.setMargins(0, 0, 0, 16);
        missionCard.setLayoutParams(mlp);

        TextView missionTitle = new TextView(context);
        missionTitle.setText("ویرایش عام‌المنفعه، ۱۰۰٪ رایگان و بدون تبلیغات");
        missionTitle.setTextColor(Color.parseColor(CyberUiTheme.COLOR_CYAN));
        missionTitle.setTextSize(13);
        FontHelper.applyBold(missionTitle);
        missionCard.addView(missionTitle);

        TextView missionDesc = new TextView(context);
        missionDesc.setText(
            "نرم‌افزار «سپر» با هدف نجات دارایی‌های بانکی مردم از تروجان‌های مخرب پیامکی (مانند ولورا، آلبوم یادگاری و صیغه‌یاب) ساخته شده است.\n\n" +
            "• هیچ‌گونه خرید درون‌برنامه‌ای، پلن پولی یا تبلیغاتی در سپر وجود ندارد.\n" +
            "• تمامی سپرهای محافظت درگاه شاپرک، مسدودسازی پیامک‌های فیشینگ و پاکسازی بدافزارها برای همیشه رایگان است.\n" +
            "• پردازش‌ها به صورت کاملاً آفلاین روی گوشی شما انجام شده و هیچ دیتایی از پیامک‌ها یا مخاطبین خارج نمی‌شود (Zero-Telemetry)."
        );
        missionDesc.setTextColor(Color.parseColor(CyberUiTheme.COLOR_TEXT_PRIMARY));
        missionDesc.setTextSize(11);
        FontHelper.applyRegular(missionDesc);
        missionDesc.setLineSpacing(6f, 1.25f);
        missionDesc.setPadding(0, 8, 0, 0);
        missionCard.addView(missionDesc);

        container.addView(missionCard);

        // ۳. کانال‌ها و راه‌های ارتباطی رسمی با سازنده
        TextView linksHeader = CyberUiTheme.createSectionTitle(context, "راه‌های ارتباطی رسمی با ریک سانچز");
        container.addView(linksHeader);

        // کانال تلگرام
        container.addView(CyberUiTheme.createInteractiveCardRow(
            context,
            R.drawable.ic_shield,
            "کانال رسمی تلگرام: @M4tinBeigi",
            "انتشار تحلیل‌های امنیتی، هشدارهای فیشینگ و آخرین نسخه‌های سپر",
            "تلگرام",
            "#0088CC",
            v -> {
                if (listener != null) listener.onTelegramClicked();
            }
        ));

        // صفحه گیت‌هاب
        container.addView(CyberUiTheme.createInteractiveCardRow(
            context,
            R.drawable.ic_nav_creator,
            "مخزن کدهای منبع در گیت‌هاب",
            "صفحه رسمی پروژه‌ها و کدهای منبع m4tinbeigi-official",
            "گیت‌هاب",
            "#94A3B8",
            v -> {
                if (listener != null) listener.onGithubClicked();
            }
        ));

        // ایمیل ارتباط مستقیم
        container.addView(CyberUiTheme.createInteractiveCardRow(
            context,
            R.drawable.ic_file_report,
            "مکاتبه و ارسال نمونه بدافزار مشکوک",
            "ارسال گزارش یا فایل‌های آلوده کشف‌شده: m4tinbeigi@gmail.com",
            "ایمیل",
            CyberUiTheme.COLOR_CYAN,
            v -> {
                try {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:m4tinbeigi@gmail.com"));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "گزارش بدافزار / پیام به ریک سانچز درباره نرم‌افزار سپر");
                    context.startActivity(emailIntent);
                } catch (Exception ignored) {}
            }
        ));

        // دکمه اشتراک‌گذاری رایگان نرم‌افزار با خانواده و دوستان
        Button shareBtn = CyberUiTheme.createPrimaryButton(
            context,
            "ارسال سپر برای دوستان و خانواده (نجات از کلاهبرداری)",
            "#238636",
            "#FFFFFF"
        );
        shareBtn.setOnClickListener(v -> {
            if (listener != null) listener.onShareAppClicked();
        });
        container.addView(shareBtn);

        // سند حریم خصوصی
        Button privBtn = CyberUiTheme.createCyberButton(
            context,
            "سند حریم خصوصی و تعهد آفلاین بودن سپر (Zero-Telemetry)",
            "#0A0D14",
            "#64748B",
            11
        );
        privBtn.setOnClickListener(v -> {
            if (listener != null) listener.onPrivacyClicked();
        });
        container.addView(privBtn);

        return container;
    }

    public static String getShareAppMessage() {
        return "نرم‌افزار ضد هک و محافظ حساب بانکی «سپر»\n" +
               "برنامه‌نویس: ریک سانچز (Rick Sanchez)\n\n" +
               "این نرم‌افزار کاملاً رایگان و بدون تبلیغات است و جلوی پیامک‌های جعلی سرقت رمز دوم (ثنا، سهام عدالت، وام‌چی) و برنامه‌های سارق پیامک (ولورا و آلبوم یادگاری) را می‌گیرد.\n\n" +
               "کانال رسمی تلگرام: @M4tinBeigi\n" +
               "دریافت رایگان: https://t.me/M4tinBeigi";
    }
}

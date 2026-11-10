package ir.ricksanchez.separ.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ir.ricksanchez.separ.R;
import ir.ricksanchez.separ.utils.PrivacyPolicyHelper;

/**
 * صفحه معرفی رسمی سازنده و برنامه‌نویس نرم‌افزار «سپر»: ریک سانچز (Rick Sanchez)
 * مجهز به تصویر پرتره رسمی سازنده، تایپوگرافی وزیرمتن و لینک‌های ارتباط مستقیم
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

        // ۱. کارت شناسنامه و پرتره رسمی توسعه‌دهنده
        LinearLayout idCard = new LinearLayout(context);
        idCard.setOrientation(LinearLayout.VERTICAL);
        idCard.setGravity(Gravity.CENTER);
        int p = CyberUiTheme.dpToPx(context, 24);
        idCard.setPadding(p, p, p, p);
        idCard.setBackground(CyberUiTheme.createCardBg("#0F172A", CyberUiTheme.COLOR_NEON_GREEN, 24));
        LinearLayout.LayoutParams idLp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        idLp.setMargins(0, 0, 0, CyberUiTheme.dpToPx(context, 16));
        idCard.setLayoutParams(idLp);

        // تصویر پرتره رسمی ریک سانچز با برش مدور بدون بیرون‌زدگی
        LinearLayout avatarRing = new LinearLayout(context);
        avatarRing.setGravity(Gravity.CENTER);
        int ringSize = CyberUiTheme.dpToPx(context, 86);
        LinearLayout.LayoutParams alp = new LinearLayout.LayoutParams(ringSize, ringSize);
        avatarRing.setLayoutParams(alp);
        avatarRing.setBackground(CyberUiTheme.createOvalBg("#1E293B", CyberUiTheme.COLOR_NEON_GREEN, 3));

        ImageView avatarImg = new ImageView(context);
        avatarImg.setImageDrawable(CyberUiTheme.createCircularDrawable(context, R.drawable.rick_sanchez));
        int imgSize = CyberUiTheme.dpToPx(context, 80);
        avatarImg.setLayoutParams(new LinearLayout.LayoutParams(imgSize, imgSize));
        avatarImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        avatarRing.addView(avatarImg);
        idCard.addView(avatarRing);

        TextView devName = new TextView(context);
        devName.setText("ریک سانچز (Rick Sanchez)");
        devName.setTextColor(Color.WHITE);
        devName.setTextSize(20);
        FontHelper.applyBold(devName);
        devName.setGravity(Gravity.CENTER);
        devName.setPadding(0, CyberUiTheme.dpToPx(context, 12), 0, CyberUiTheme.dpToPx(context, 4));
        idCard.addView(devName);

        TextView devRole = new TextView(context);
        devRole.setText("معمار ارشد امنیت سایبری، مهندسی معکوس و هوش مصنوعی");
        devRole.setTextColor(Color.parseColor(CyberUiTheme.COLOR_CYAN));
        devRole.setTextSize(12);
        FontHelper.applyRegular(devRole);
        devRole.setGravity(Gravity.CENTER);
        idCard.addView(devRole);

        // آیکون‌های ارتباط مستقیم تلگرام، گیت‌هاب و ایمیل درون کارت هویتی
        LinearLayout devSocialRow = new LinearLayout(context);
        devSocialRow.setOrientation(LinearLayout.HORIZONTAL);
        devSocialRow.setGravity(Gravity.CENTER);
        devSocialRow.setPadding(0, CyberUiTheme.dpToPx(context, 12), 0, 0);

        // ۱. تلگرام
        LinearLayout tgBtn = new LinearLayout(context);
        tgBtn.setGravity(Gravity.CENTER);
        int dBtnSize = CyberUiTheme.dpToPx(context, 42);
        LinearLayout.LayoutParams tblp = new LinearLayout.LayoutParams(dBtnSize, dBtnSize);
        tblp.setMargins(CyberUiTheme.dpToPx(context, 6), 0, CyberUiTheme.dpToPx(context, 6), 0);
        tgBtn.setLayoutParams(tblp);
        GradientDrawable tgBg = new GradientDrawable();
        tgBg.setColor(Color.parseColor("#152438"));
        tgBg.setCornerRadius(CyberUiTheme.dpToPx(context, 12));
        tgBg.setStroke(1, Color.parseColor("#0088CC"));
        tgBtn.setBackground(tgBg);
        ImageView tgIv = new ImageView(context);
        tgIv.setImageResource(R.drawable.ic_social_telegram);
        tgIv.setLayoutParams(new LinearLayout.LayoutParams(CyberUiTheme.dpToPx(context, 22), CyberUiTheme.dpToPx(context, 22)));
        tgBtn.addView(tgIv);
        tgBtn.setOnClickListener(v -> {
            if (listener != null) listener.onTelegramClicked();
        });
        devSocialRow.addView(tgBtn);

        // ۲. گیت‌هاب
        LinearLayout ghBtn = new LinearLayout(context);
        ghBtn.setGravity(Gravity.CENTER);
        ghBtn.setLayoutParams(tblp);
        GradientDrawable ghBg = new GradientDrawable();
        ghBg.setColor(Color.parseColor("#152438"));
        ghBg.setCornerRadius(CyberUiTheme.dpToPx(context, 12));
        ghBg.setStroke(1, Color.parseColor("#FFFFFF"));
        ghBtn.setBackground(ghBg);
        ImageView ghIv = new ImageView(context);
        ghIv.setImageResource(R.drawable.ic_social_github);
        ghIv.setLayoutParams(new LinearLayout.LayoutParams(CyberUiTheme.dpToPx(context, 22), CyberUiTheme.dpToPx(context, 22)));
        ghBtn.addView(ghIv);
        ghBtn.setOnClickListener(v -> {
            if (listener != null) listener.onGithubClicked();
        });
        devSocialRow.addView(ghBtn);

        // ۳. ایمیل
        LinearLayout emBtn = new LinearLayout(context);
        emBtn.setGravity(Gravity.CENTER);
        emBtn.setLayoutParams(tblp);
        GradientDrawable emBg = new GradientDrawable();
        emBg.setColor(Color.parseColor("#152438"));
        emBg.setCornerRadius(CyberUiTheme.dpToPx(context, 12));
        emBg.setStroke(1, Color.parseColor(CyberUiTheme.COLOR_CYAN));
        emBtn.setBackground(emBg);
        ImageView emIv = new ImageView(context);
        emIv.setImageResource(R.drawable.ic_file_report);
        emIv.setColorFilter(Color.parseColor(CyberUiTheme.COLOR_CYAN));
        emIv.setLayoutParams(new LinearLayout.LayoutParams(CyberUiTheme.dpToPx(context, 22), CyberUiTheme.dpToPx(context, 22)));
        emBtn.addView(emIv);
        emBtn.setOnClickListener(v -> {
            try {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:m4tinbeigi@gmail.com"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "گزارش بدافزار / پیام به ریک سانچز");
                context.startActivity(emailIntent);
            } catch (Exception ignored) {}
        });
        devSocialRow.addView(emBtn);

        idCard.addView(devSocialRow);

        container.addView(idCard);

        // ۲. بیانیه نرم‌افزار عام‌المنفعه و ۱۰۰٪ رایگان
        LinearLayout missionCard = new LinearLayout(context);
        missionCard.setOrientation(LinearLayout.VERTICAL);
        int mp = CyberUiTheme.dpToPx(context, 18);
        missionCard.setPadding(mp, mp, mp, mp);
        missionCard.setBackground(CyberUiTheme.createCardBg("#131D2C", CyberUiTheme.COLOR_CYAN, 18));
        LinearLayout.LayoutParams mlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mlp.setMargins(0, 0, 0, CyberUiTheme.dpToPx(context, 16));
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
        missionDesc.setLineSpacing(CyberUiTheme.dpToPx(context, 4), 1.25f);
        missionDesc.setPadding(0, CyberUiTheme.dpToPx(context, 8), 0, 0);
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

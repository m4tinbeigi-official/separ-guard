package ir.ricksanchez.separ.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import ir.ricksanchez.separ.R;
import ir.ricksanchez.separ.data.SocialInstallBlockerGuard;

/**
 * ویزارد مرحله‌به‌مرحله اعطای دسترسی‌های امنیتی و منشور ضد هک ورود (Onboarding Permission Wizard)
 * تشریح دقیق دلایل نیاز به دسترسی‌ها و تضمین پردازش ۱۰۰٪ آفلاین
 */
public class PermissionOnboardingWizardHelper {

    private static final String PREF_NAME = "separ_onboarding_prefs";
    private static final String KEY_COMPLETED = "onboarding_wizard_completed";

    public interface OnboardingCompletionListener {
        void onCompleted();
    }

    public static boolean isOnboardingCompleted(Context context) {
        if (context == null) return true;
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(KEY_COMPLETED, false);
    }

    public static void setOnboardingCompleted(Context context) {
        if (context == null) return;
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        sp.edit().putBoolean(KEY_COMPLETED, true).apply();
    }

    public static void startWizard(Context context, OnboardingCompletionListener listener) {
        showStepDialog(context, 1, listener);
    }

    private static void showStepDialog(Context context, int step, OnboardingCompletionListener listener) {
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

        // ۱. نشانگر مرحله (Step Chip)
        TextView tvStep = new TextView(context);
        tvStep.setText("گام راهبری امنیتی " + CyberUiTheme.toPersianDigits(String.valueOf(step)) + " از " + CyberUiTheme.toPersianDigits("4"));
        tvStep.setTextColor(Color.parseColor("#00E5FF"));
        tvStep.setTextSize(11);
        FontHelper.applyBold(tvStep);
        tvStep.setGravity(Gravity.CENTER);

        GradientDrawable chipBg = new GradientDrawable();
        chipBg.setColor(Color.parseColor("#151D28"));
        chipBg.setCornerRadius(CyberUiTheme.dpToPx(context, 8));
        chipBg.setStroke(CyberUiTheme.dpToPx(context, 1), Color.parseColor("#243142"));
        tvStep.setBackground(chipBg);
        int cp = CyberUiTheme.dpToPx(context, 6);
        tvStep.setPadding(cp * 2, cp, cp * 2, cp);
        card.addView(tvStep);

        // ۲. متون متغیر متناسب با هر مرحله
        String title;
        String whyText;
        String actionBtnText;
        Runnable actionRunnable;

        if (step == 1) {
            title = "پایش پیامک‌های فیشینگ و رمزهای پویا";
            whyText = "چرا این دسترسی لازم است؟\n\n" +
                "۹۰٪ سرقت‌های اینترنتی کشور با پیامک‌های جعلی شکوائیه دادسرا (ثنا) یا سود سهام عدالت صورت می‌پذیرد.\n\n" +
                "سپر پیامک‌ها را به صورت ۱۰۰٪ آفلاین و محلی در گوشی بررسی می‌کند تا در صورت دریافت پیامک فیشینگ، فوراً هشدار دهد. هیچ داده‌ای به بیرون از گوشی شما ارسال نمی‌شود.";
            actionBtnText = "درخواست مجوز پیامک";
            actionRunnable = () -> {
                // هدایت به صفحه اطلاعات برنامه جهت اعطای دسترسی
                try {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Exception ignored) {}
            };
        } else if (step == 2) {
            title = "دیده‌بان اعلان‌ها (Notification Shield)";
            whyText = "چرا این دسترسی لازم است؟\n\n" +
                "بسیاری از بدافزارها بدون دسترسی پیامک، رمزهای پویای بانکی را از نوار اعلان‌های بالای صفحه می‌دزدند.\n\n" +
                "سپر با این دسترسی، اعلان‌های بانکی را زیر چتر امنیتی قرار داده و مانع از سرقت کدهای یکبارمصرف توسط سایر برنامه‌ها می‌شود.";
            actionBtnText = "فعال‌سازی در تنظیمات اعلان";
            actionRunnable = () -> {
                try {
                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Exception ignored) {}
            };
        } else if (step == 3) {
            title = "دیده‌بان درگاه جعلی شاپرک (Accessibility Guard)";
            whyText = "چرا این دسترسی لازم است؟\n\n" +
                "هنگام خرید اینترنتی، سپر آدرس درگاه را با دامنه رسمی شاپرک (shaparak.ir) تطبیق می‌دهد.\n\n" +
                "اگر وارد درگاه فیک شوید، سپر در کسری از ثانیه صفحه قرمز هشدار را باز کرده و مانع از وارد کردن اطلاعات کارت بانکی شما می‌شود.";
            actionBtnText = "فعال‌سازی دیده‌بان شاپرک";
            actionRunnable = () -> {
                try {
                    Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Exception ignored) {}
            };
        } else {
            title = "سد نفوذ: قفل اجازه نصب در پیام‌رسان‌ها";
            whyText = "چرا این کار حیاتی است؟\n\n" +
                "کانال ورود ۹۹٪ بدافزارها (مانند آلبوم یادگاری و ثنا)، فعال بودن اجازه نصب برنامه ناشناخته در تلگرام، واتساپ یا روبیکاست.\n\n" +
                "سپر به شما کمک می‌کند این مسیر را با یک لمس خاموش کنید تا هیچ بدافزاری نتواند ناخواسته روی گوشی شما نصب شود.";
            actionBtnText = "بررسی و قفل نصب ناشناخته";
            actionRunnable = () -> {
                SocialInstallBlockerGuard.launchManageUnknownSources(context, "org.telegram.messenger");
            };
        }

        TextView tvTitle = new TextView(context);
        tvTitle.setText(title);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setTextSize(15);
        FontHelper.applyBold(tvTitle);
        tvTitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lpTitle = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpTitle.topMargin = CyberUiTheme.dpToPx(context, 12);
        card.addView(tvTitle, lpTitle);

        TextView tvWhy = new TextView(context);
        tvWhy.setText(whyText);
        tvWhy.setTextColor(Color.parseColor("#94A3B8"));
        tvWhy.setTextSize(11.5f);
        FontHelper.applyRegular(tvWhy);
        tvWhy.setLineSpacing(0, 1.25f);
        LinearLayout.LayoutParams lpWhy = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpWhy.topMargin = CyberUiTheme.dpToPx(context, 10);
        lpWhy.bottomMargin = CyberUiTheme.dpToPx(context, 16);
        card.addView(tvWhy, lpWhy);

        // دکمه اقدام اصلی
        Button btnAction = CyberUiTheme.createCyberButton(context, actionBtnText, "#00E5FF", "#080B10", 12);
        btnAction.setOnClickListener(v -> actionRunnable.run());
        LinearLayout.LayoutParams lpAction = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpAction.bottomMargin = CyberUiTheme.dpToPx(context, 8);
        card.addView(btnAction, lpAction);

        // دکمه مرحله بعد
        String nextLabel = (step < 4) ? "گام بعدی" : "تکمیل و مشاهده منشور ضد هک";
        Button btnNext = CyberUiTheme.createCyberButton(context, nextLabel, "#1E293B", "#FFFFFF", 12);
        btnNext.setOnClickListener(v -> {
            dialog.dismiss();
            if (step < 4) {
                showStepDialog(context, step + 1, listener);
            } else {
                setOnboardingCompleted(context);
                showSecurityMasterBriefing(context, listener);
            }
        });
        card.addView(btnNext);

        root.addView(card);
        dialog.setContentView(root);
        dialog.setCancelable(false);
        dialog.show();
    }

    /**
     * نمایش منشور رسمی دفاع شخصی سایبری (پست آموزشی خوش‌آمدگویی)
     */
    public static void showSecurityMasterBriefing(Context context, OnboardingCompletionListener listener) {
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
        bg.setStroke(CyberUiTheme.dpToPx(context, 2), Color.parseColor("#00E676"));
        card.setBackground(bg);
        card.setPadding(pad, pad, pad, pad);

        TextView tvHeader = new TextView(context);
        tvHeader.setText("منشور ضد هک سپر: ۳ اصل طلایی امنیت شخصی");
        tvHeader.setTextColor(Color.parseColor("#00E676"));
        tvHeader.setTextSize(15);
        FontHelper.applyBold(tvHeader);
        tvHeader.setGravity(Gravity.CENTER);
        card.addView(tvHeader);

        TextView tvRules = new TextView(context);
        tvRules.setText(
            "به جمع کاربران امن سپر خوش آمدید!\n\n" +
            "برای اینکه حساب‌های بانکی و شبکه‌های اجتماعی شما هرگز هک نشوند، این ۳ اصل را همیشه به خاطر بسپارید:\n\n" +
            "۱. اصل رازبان: کدهای پیامکی و رمزهای دوم پویا را برای هیچ فردی (حتی کارمند بانک یا مامور پلیس) نخوانید.\n\n" +
            "۲. اصل تله‌گریز: فایل‌های نصبی (با پسوند apk) ارسالی در تلگرام، ایتا و واتساپ را تحت هیچ شرایطی باز نکنید.\n\n" +
            "۳. اصل شاپرک: پرداخت‌های اینترنتی خود را منحصراً در آدرس‌های معتبر به پسوند shaparak.ir انجام دهید.\n\n" +
            "سپر همیشه در پس‌زمینه گوشی بیدار است و از دارایی‌های شما محافظت می‌کند."
        );
        tvRules.setTextColor(Color.parseColor("#CBD5E1"));
        tvRules.setTextSize(11.5f);
        FontHelper.applyRegular(tvRules);
        tvRules.setLineSpacing(0, 1.25f);
        LinearLayout.LayoutParams lpRules = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpRules.topMargin = CyberUiTheme.dpToPx(context, 12);
        lpRules.bottomMargin = CyberUiTheme.dpToPx(context, 16);
        card.addView(tvRules, lpRules);

        Button btnEnter = CyberUiTheme.createCyberButton(context, "شروع استفاده از سپر امن", "#00E676", "#080B10", 12);
        btnEnter.setOnClickListener(v -> {
            dialog.dismiss();
            if (listener != null) listener.onCompleted();
        });
        card.addView(btnEnter);

        root.addView(card);
        dialog.setContentView(root);
        dialog.show();
    }
}

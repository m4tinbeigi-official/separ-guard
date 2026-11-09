package ir.ricksanchez.separ.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * نمایش پنجره رسمی افشای برجسته مجوزها (Prominent Disclosure)
 * پیش از درخواست مجوزهای حساس پیامک و دسترسی‌پذیری جهت تطابق ۱۰۰٪ با قوانین کافه‌بازار، مایکت و گوگل‌پلی
 */
public class ProminentDisclosureHelper {

    private static final String PREF_NAME = "separ_disclosure_prefs";
    private static final String KEY_SHOWN = "has_shown_disclosure";

    public interface DisclosureCallback {
        void onAccepted();
    }

    public static boolean hasShown(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_SHOWN, false);
    }

    public static void setShown(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_SHOWN, true).apply();
    }

    public static void showDisclosureIfNeeded(Context context, DisclosureCallback callback) {
        if (hasShown(context)) {
            if (callback != null) callback.onAccepted();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("🛡️ شفافیت و تعهد حریم خصوصی «سپر»");
        builder.setMessage(
            "نرم‌افزار امنیتی سپر برای محافظت از حساب‌های بانکی شما در برابر بدافزارها و فیشینگ، به دسترسی‌های زیر نیاز دارد:\n\n" +
            "۱. دریافت پیامک‌ها (RECEIVE_SMS): صرفاً جهت کشف لحظه‌ای پیامک‌های فریبنده و سرقت رمز پویا.\n" +
            "۲. سرویس دسترسی‌پذیری (Accessibility): صرفاً جهت پایش نوار آدرس مرورگر و مسدودسازی درگاه‌های جعلی پرداخت شاپرک.\n\n" +
            "🔒 تعهد ۱۰۰٪ پردازش آفلاین:\n" +
            "تمام بررسی‌ها روی پردازنده گوشی شما انجام می‌شود. هیچ پیامک، شماره یا داده شخصی هرگز ذخیره یا به سروری ارسال نمی‌شود (Zero-Telemetry)."
        );
        builder.setPositiveButton("متوجه شدم و قبول دارم", (dialog, which) -> {
            setShown(context);
            if (callback != null) callback.onAccepted();
        });
        builder.setCancelable(false);
        builder.show();
    }
}

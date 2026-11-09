package ir.ricksanchez.separ.utils;

import android.app.AlertDialog;
import android.content.Context;

/**
 * نمایش سند رسمی حریم خصوصی و اطلاعات توسعه‌دهنده درون اپلیکیشن
 */
public class PrivacyPolicyHelper {

    public static void showPrivacyPolicy(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("📜 سند رسمی حریم خصوصی و شرایط استفاده");
        builder.setMessage(
            "«سپر» سامانه امنیتی بومی، اختصاصی و ضد هک توسعه‌داده شده توسط ریک سانچز (Rick Sanchez) است.\n\n" +
            "• اصل عدم خروج داده (Zero-Telemetry):\n" +
            "هیچ داده‌ای از پیامک‌ها، لیست مخاطبین، مشخصات کارت‌های بانکی یا نرم‌افزارهای نصب‌شده از دیوایس خارج نمی‌شود.\n\n" +
            "• شرایط اشتراک و لایسنس:\n" +
            "اشتراک سالیانه به قیمت مصوب ۱,۰۰۰,۰۰۰ تومان (۱۰,۰۰۰,۰۰۰ ریال) به مدت ۳۶۵ روز محافظت شبانه‌روزی را فعال می‌کند.\n\n" +
            "• بازیابی خرید (Restore Purchases):\n" +
            "خریداران محترم در صورت حذف و نصب مجدد، می‌توانند با لمس دکمه «بازیابی خرید» بدون پرداخت مجدد اشتراک خود را فعال کنند.\n\n" +
            "• تماس و پشتیبانی رسمی:\n" +
            "ایمیل توسعه‌دهنده: m4tinbeigi@gmail.com"
        );
        builder.setPositiveButton("بستن", null);
        builder.show();
    }
}

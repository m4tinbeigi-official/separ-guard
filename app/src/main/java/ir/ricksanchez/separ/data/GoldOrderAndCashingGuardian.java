package ir.ricksanchez.separ.data;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * دیده‌بان نقد کردن وجوه و خرید طلای فیزیکی در مارکت‌پلیس‌ها (Marketplace Gold & BNPL Cashing-out Guardian)
 * ردیابی شگرد هکرها در خرید شمش، سکه و طلا از دیجی‌کالا، دیجی‌پی، اسنپ‌پی و صرافی‌های طلا پس از نفوذ به حساب
 */
public class GoldOrderAndCashingGuardian {

    public static class SuspectedOrderEvent {
        public final String platformName;
        public final String rawMessage;
        public final String supportPhone;
        public final String cancelUrl;
        public final boolean isHighRiskGoldOrVoucher;

        public SuspectedOrderEvent(String platform, String msg, String phone, String cancelUrl, boolean isGold) {
            this.platformName = platform;
            this.rawMessage = msg;
            this.supportPhone = phone;
            this.cancelUrl = cancelUrl;
            this.isHighRiskGoldOrVoucher = isGold;
        }
    }

    private static final List<String> GOLD_AND_CASHING_KEYWORDS = Arrays.asList(
        "طلا", "سکه", "شمش", "پارسیان", "بهار آزادی", "کارت هدیه", "ووچر",
        "دیجی‌پی", "دیجی پی", "اسنپ‌پی", "اسنپ پی", "اقساطی", "اعتبار",
        "gold", "coin", "voucher", "digipay", "snappay", "milli", "taline"
    );

    private static final List<String> ORDER_KEYWORDS = Arrays.asList(
        "سفارش شما", "ثبت سفارش", "کد سفارش", "سفارش جدید", "فاکتور",
        "تایید خرید", "تحویل مرسوله", "order confirmed", "order placed"
    );

    /**
     * ارزیابی پیامک یا نوتیفیکیشن دریافتی برای کشف سفارشات مشکوک دیجی‌کالا و طلا
     */
    public static SuspectedOrderEvent evaluateIncomingMessage(String sender, String body) {
        if (body == null) return null;
        String clean = body.toLowerCase();

        boolean hasOrderKeyword = false;
        for (String ok : ORDER_KEYWORDS) {
            if (clean.contains(ok)) {
                hasOrderKeyword = true;
                break;
            }
        }

        // ۱. دیجی‌کالا
        if (clean.contains("دیجی‌کالا") || clean.contains("دیجیکالا") || clean.contains("digikala") ||
            (sender != null && (sender.contains("digikala") || sender.contains("30006193") || sender.contains("10006193")))) {

            boolean isGold = false;
            for (String gk : GOLD_AND_CASHING_KEYWORDS) {
                if (clean.contains(gk)) {
                    isGold = true;
                    break;
                }
            }
            return new SuspectedOrderEvent(
                "دیجی‌کالا (Digikala)",
                body,
                "02161930000",
                "https://www.digikala.com/profile/orders/",
                isGold
            );
        }

        // ۲. دیجی‌پی (خرید اعتباری یا شارژ کیف پول)
        if (clean.contains("دیجی‌پی") || clean.contains("دیجی پی") || clean.contains("mydigipay")) {
            return new SuspectedOrderEvent(
                "دیجی‌پی (DigiPay)",
                body,
                "02158474000",
                "https://www.mydigipay.com/",
                true
            );
        }

        // ۳. اسنپ‌پی (اعتبار اقساطی BNPL)
        if (clean.contains("اسنپ‌پی") || clean.contains("اسنپ پی") || clean.contains("snappay")) {
            return new SuspectedOrderEvent(
                "اسنپ‌پی (SnappPay)",
                body,
                "02196864000",
                "https://snapppay.ir/",
                true
            );
        }

        // ۴. سامانه‌های خرید آنلاین طلا (میلی، طلاین)
        if (clean.contains("میلی") || clean.contains("milli.gold") || clean.contains("طلاین") || clean.contains("taline")) {
            return new SuspectedOrderEvent(
                "سامانه معامله طلا (Milli / Taline)",
                body,
                "02191200250",
                "https://milli.gold/",
                true
            );
        }

        // ۵. سفارشات عمومی همراه با کلیدواژه‌های حساس طلا و کارت هدیه
        if (hasOrderKeyword) {
            for (String gk : GOLD_AND_CASHING_KEYWORDS) {
                if (clean.contains(gk)) {
                    return new SuspectedOrderEvent(
                        "فروشگاه اینترنتی / خرید طلا و ووچر",
                        body,
                        "096380",
                        "",
                        true
                    );
                }
            }
        }

        return null;
    }

    /**
     * تماس مستقیم با پشتیبانی ۲۴ ساعته دیجی‌کالا یا پلتفرم مربوطه جهت توقیف مرسوله
     */
    public static void callPlatformSupport(Context context, String phone) {
        if (context == null || phone == null) return;
        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception ignored) {}
    }
}

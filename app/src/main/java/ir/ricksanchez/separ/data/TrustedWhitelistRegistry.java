package ir.ricksanchez.separ.data;

import java.util.HashSet;
import java.util.Set;

public class TrustedWhitelistRegistry {

    private static final Set<String> CERTIFIED_WHITELIST = new HashSet<>();

    static {
        // ۱. خود سامانه امنیتی سپر
        CERTIFIED_WHITELIST.add("ir.ricksanchez.separ");

        // ۲. مارکت‌های رسمی
        CERTIFIED_WHITELIST.add("com.farsitel.bazaar");
        CERTIFIED_WHITELIST.add("ir.mservices.market");
        CERTIFIED_WHITELIST.add("com.android.vending");
        CERTIFIED_WHITELIST.add("com.google.android.gms");

        // ۳. اپراتورهای مخابراتی
        CERTIFIED_WHITELIST.add("ir.mci.ecareapp");
        CERTIFIED_WHITELIST.add("ir.mtn.irancell");
        CERTIFIED_WHITELIST.add("ir.rightel.myrightel");

        // ۴. پلتفرم‌های پرکاربرد و معتبر کشور
        CERTIFIED_WHITELIST.add("cab.snapp.passenger");
        CERTIFIED_WHITELIST.add("net.tapsi.passenger");
        CERTIFIED_WHITELIST.add("com.digikala.mobile");
        CERTIFIED_WHITELIST.add("ir.divar");

        // ۵. پیام‌رسان‌های معتبر رسمی
        CERTIFIED_WHITELIST.add("org.telegram.messenger");
        CERTIFIED_WHITELIST.add("com.whatsapp");
        CERTIFIED_WHITELIST.add("ir.rubika.app");
        CERTIFIED_WHITELIST.add("ir.eitaa.messenger");
        CERTIFIED_WHITELIST.add("ir.bale.messenger");
        CERTIFIED_WHITELIST.add("mobi.mmdt.ottplus");
        CERTIFIED_WHITELIST.add("com.google.android.apps.messaging");

        // ۶. ابزارهای معتبر بین‌المللی و کاربری
        CERTIFIED_WHITELIST.add("com.openai.chatgpt");
        CERTIFIED_WHITELIST.add("com.arlosoft.macrodroid");
        CERTIFIED_WHITELIST.add("com.palette.panel");
        CERTIFIED_WHITELIST.add("com.mahsanet.mahsang");
        CERTIFIED_WHITELIST.add("zank.remote");
        CERTIFIED_WHITELIST.add("com.nothing.launcher");
    }

    public static boolean isWhitelisted(String packageName) {
        if (packageName == null) return false;
        String lower = packageName.trim().toLowerCase();

        // معافیت سیستم‌عامل رسمی گوگل و اندروید
        if (lower.startsWith("com.android.") || lower.startsWith("android.") || lower.startsWith("com.google.android.")) {
            if (!lower.contains("ui") || lower.equals("com.android.systemui")) {
                return true;
            }
        }

        // معافیت سیستم Nothing OS
        if (lower.startsWith("com.nothing.")) {
            return true;
        }

        return CERTIFIED_WHITELIST.contains(lower);
    }
}

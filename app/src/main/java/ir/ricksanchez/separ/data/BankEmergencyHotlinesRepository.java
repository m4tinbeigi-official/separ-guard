package ir.ricksanchez.separ.data;

import java.util.ArrayList;
import java.util.List;

/**
 * پایگاه داده رسمی تلفنبانک‌ها و خطوط مسدودسازی فوری کارت‌های شبکه بانکی ایران (Bank Emergency Hotlines)
 * امکان سوزاندن و ابطال سریع کارت‌های بانکی بدون نیاز به اینترنت هنگام مواجهه با فیشینگ یا سرقت حساب
 */
public class BankEmergencyHotlinesRepository {

    public static class BankHotline {
        public final String bankName;
        public final String hotlinePhone;
        public final String cardBlockingIvrGuide;
        public final String cardPrefixBin;

        public BankHotline(String name, String phone, String guide, String bin) {
            this.bankName = name;
            this.hotlinePhone = phone;
            this.cardBlockingIvrGuide = guide;
            this.cardPrefixBin = bin;
        }
    }

    private static final List<BankHotline> BANK_HOTLINES = new ArrayList<>();

    static {
        BANK_HOTLINES.add(new BankHotline("بانک ملی ایران", "02164140", "کلید ۳ (مسدودسازی کارت مفقودی/سرقتی)", "603799"));
        BANK_HOTLINES.add(new BankHotline("بانک ملت", "02182488", "کلید ۴ (خدمات کارت و انسداد آنی)", "610433"));
        BANK_HOTLINES.add(new BankHotline("بانک صادرات ایران", "0219602", "کلید ۲ (مسدود کردن کارت سپهر)", "603769"));
        BANK_HOTLINES.add(new BankHotline("بانک تجارت", "0211554", "کلید ۳ (انسداد کارت و بستن رمز پویا)", "585983"));
        BANK_HOTLINES.add(new BankHotline("بانک سپه", "0211557", "کلید ۲ (مسدودسازی کارت عابربانک)", "589210"));
        BANK_HOTLINES.add(new BankHotline("بانک سامان / بلوبانک", "0216422", "کلید ۳ (امور کارت و انسداد) • بلوبانک: 02187641", "621986"));
        BANK_HOTLINES.add(new BankHotline("بانک پاسارگاد / ویپاد", "02182890", "کلید ۲ (مسدود کردن کارت) • ویپاد: 02124598000", "502229"));
        BANK_HOTLINES.add(new BankHotline("بانک قرض‌الحسنه رسالت", "0214747", "کلید ۴ (مسدودسازی کارت و اینترنت‌بانک)", "504172"));
        BANK_HOTLINES.add(new BankHotline("بانک آینده", "02127663200", "کلید ۳ (انسداد آنی کارت و توقیف حساب)", "636214"));
        BANK_HOTLINES.add(new BankHotline("بانک پارسیان", "02181151", "کلید ۴ (مسدودسازی کارت و رمز دوم)", "622106"));
        BANK_HOTLINES.add(new BankHotline("بانک کشاورزی", "02181301", "کلید ۳ (انسداد کارت مهر)", "603770"));
        BANK_HOTLINES.add(new BankHotline("بانک مسکن", "02161088", "کلید ۳ (ابطال کارت مسکن)", "628023"));
        BANK_HOTLINES.add(new BankHotline("بانک رفاه کارگران", "0218525", "کلید ۲ (مسدودسازی کارت رفاه)", "589463"));
        BANK_HOTLINES.add(new BankHotline("بانک شهر", "02187611", "کلید ۳ (انسداد کارت شهروندی)", "502806"));
        BANK_HOTLINES.add(new BankHotline("بانک اقتصاد نوین", "02148031000", "کلید ۳ (انسداد فوری کارت)", "627412"));
        BANK_HOTLINES.add(new BankHotline("بانک سینا", "02141731", "کلید ۲ (امور کارت و مسدودسازی)", "639346"));
    }

    public static List<BankHotline> getHotlines() {
        return new ArrayList<>(BANK_HOTLINES);
    }
}

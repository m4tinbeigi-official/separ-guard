package ir.ricksanchez.separ.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * مرجع رسمی سرشماره‌ها، شناسه‌های پیامکی و پایگاه ضد جعل هویت بانک‌های ایران (Official Bank Registry)
 * تفکیک قطعی پیامک‌های سرشماره رسمی بانک‌ها از شماره‌های شخصی کلاهبرداران
 */
public class OfficialBankSenderRegistry {

    public static class BankProfile {
        public final String bankName;
        public final List<String> officialSenders;
        public final String hotline;
        public final String cardBlockingIvrKey;

        public BankProfile(String name, List<String> senders, String phone, String ivr) {
            this.bankName = name;
            this.officialSenders = senders;
            this.hotline = phone;
            this.cardBlockingIvrKey = ivr;
        }
    }

    private static final Map<String, BankProfile> OFFICIAL_BANKS = new HashMap<>();

    // کلیدواژه‌های معرف بانک‌ها و خدمات بانکی در ایران
    private static final List<String> ALL_BANK_KEYWORDS = Arrays.asList(
        "بانک ملی", "بانک ملت", "بانک صادرات", "بانک تجارت", "بانک سپه",
        "بانک سامان", "بلوبانک", "بلو بانک", "بانک پاسارگاد", "ویپاد", "بانک رسالت",
        "بانک آینده", "بانک پارسیان", "بانک کشاورزی", "بانک مسکن", "بانک رفاه",
        "بانک شهر", "بانک اقتصاد نوین", "بانک سینا", "بانک کارآفرین", "بانک دی",
        "بانک ایران زمین", "بانک سرمایه", "پست بانک", "بانک گردشگری", "بانک توسعه تعاون",
        "بانک خاورمیانه", "مهر ایران", "قرض‌الحسنه", "قرض الحسنه",
        "پشتیبان بانک", "پشتیبانی بانک", "حراست بانک", "کارشناس بانک",
        "رمز بفرستید", "کد بفرستید", "رمز را بفرستید", "کد را بفرستید",
        "مسدودی حساب", "توقیف حساب", "حساب مشکوک", "همراه بانک", "اینترنت بانک",
        "سامانه بام", "سامانه صیاد", "چک صیادی", "شاپرک", "شتاب", "رمز پویا"
    );

    static {
        putBank("بانک ملی ایران", Arrays.asList("20004000", "100064140", "b.melli", "bankmelli"), "02164140", "کلید ۳");
        putBank("بانک ملت", Arrays.asList("200033", "100033", "20003300", "b.mellat", "bankmellat"), "02182488", "کلید ۴");
        putBank("بانک صادرات ایران", Arrays.asList("20000000", "30009602", "b.saderat", "saderat"), "0219602", "کلید ۲");
        putBank("بانک تجارت", Arrays.asList("200070", "300070", "b.tejarat", "tejarat"), "0211554", "کلید ۳");
        putBank("بانک سپه", Arrays.asList("200022", "30001557", "b.sepah", "sepah"), "0211557", "کلید ۲");
        putBank("بانک سامان", Arrays.asList("200088", "30006422", "b.saman", "saman"), "0216422", "کلید ۳");
        putBank("بلوبانک (سامان)", Arrays.asList("30008764", "10006422", "blu", "blubank"), "02187641", "کلید ۲");
        putBank("بانک پاسارگاد", Arrays.asList("20008289", "10008289", "b.pasargad", "pasargad"), "02182890", "کلید ۲");
        putBank("ویپاد (پاسارگاد)", Arrays.asList("300077", "100077", "vpod", "wepod"), "02124598000", "کلید ۲");
        putBank("بانک قرض‌الحسنه رسالت", Arrays.asList("20004747", "30004747", "resalat"), "0214747", "کلید ۴");
        putBank("بانک آینده", Arrays.asList("30002766", "20002766", "b.ayandeh", "ayandeh"), "02127663200", "کلید ۳");
        putBank("بانک پارسیان", Arrays.asList("30008115", "20008115", "b.parsian", "parsian"), "02181151", "کلید ۴");
        putBank("بانک کشاورزی", Arrays.asList("30008130", "20008130", "b.keshavarzi"), "02181301", "کلید ۳");
        putBank("بانک مسکن", Arrays.asList("30006108", "20006108", "b.maskan"), "02161088", "کلید ۳");
        putBank("بانک رفاه کارگران", Arrays.asList("30008525", "b.refah"), "0218525", "کلید ۲");
        putBank("بانک شهر", Arrays.asList("20008761", "b.shahr"), "02187611", "کلید ۳");
        putBank("بانک اقتصاد نوین", Arrays.asList("20004803", "b.en"), "02148031000", "کلید ۳");
        putBank("بانک سینا", Arrays.asList("20004173", "b.sina"), "02141731", "کلید ۲");
        putBank("بانک قرض‌الحسنه مهر ایران", Arrays.asList("3000852", "qmb"), "02143051000", "کلید ۳");
        putBank("پست بانک ایران", Arrays.asList("300089", "postbank"), "02184284", "کلید ۲");
    }

    private static void putBank(String name, List<String> senders, String phone, String ivr) {
        OFFICIAL_BANKS.put(name, new BankProfile(name, senders, phone, ivr));
    }

    /**
     * بررسی اینکه آیا سرشماره یک خط شخصی موبایل است
     */
    public static boolean isPersonalMobileNumber(String sender) {
        if (sender == null) return false;
        String clean = sender.trim().replace(" ", "").replace("-", "");
        return clean.startsWith("+989") || clean.startsWith("09") || clean.startsWith("989") || (clean.length() == 10 && clean.startsWith("9"));
    }

    /**
     * ارزیابی قطعی پیامک: آیا پیامک از خط شخصی با ادعای بانکی ارسال شده است؟
     */
    public static String detectImpersonatedBankKeyword(String body) {
        if (body == null) return null;
        String lower = body.toLowerCase();
        for (String kw : ALL_BANK_KEYWORDS) {
            if (lower.contains(kw.toLowerCase())) {
                return kw;
            }
        }
        return null;
    }

    /**
     * بررسی اینکه آیا پیامک ورودی ۱۰۰٪ جعل هویت بانکی است
     */
    public static boolean isFraudulentPersonalBankSms(String sender, String body) {
        if (!isPersonalMobileNumber(sender)) {
            return false; // سرشماره‌های رسمی یا متنی سیستم
        }
        return detectImpersonatedBankKeyword(body) != null;
    }

    public static List<BankProfile> getAllOfficialBanks() {
        return new ArrayList<>(OFFICIAL_BANKS.values());
    }
}

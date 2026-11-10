package ir.ricksanchez.separ.data;

import java.util.ArrayList;
import java.util.List;

/**
 * پایگاه داده شبیه‌ساز و آزمون‌های هوشیاری ضد فیشینگ (Phishing Simulator & Awareness Quiz)
 * سناریوهای واقعی مهندسی اجتماعی در ایران به همراه آموزش تحلیل شگردها
 */
public class PhishingQuizRepository {

    public static class QuizQuestion {
        public final String id;
        public final String scenarioTitle;
        public final String simulatedMessage;
        public final boolean isMalicious;
        public final String explanation;

        public QuizQuestion(String id, String title, String msg, boolean isMal, String exp) {
            this.id = id;
            this.scenarioTitle = title;
            this.simulatedMessage = msg;
            this.isMalicious = isMal;
            this.explanation = exp;
        }
    }

    private static final List<QuizQuestion> QUESTIONS = new ArrayList<>();

    static {
        QUESTIONS.add(new QuizQuestion(
            "quiz_sana",
            "پیامک شکوائیه سامانه ثنا",
            "سرشماره: 09351234567\nمتن: «ابلاغیه الکترونیکی شماره ۱۴۰۵/۲۸ در شعبه دهم دادسرا صادر شد. جهت مشاهده شکوائیه به آدرس adliran-eblagh.top مراجعه نمایید.»",
            true,
            "این پیامک ۱۰۰٪ فیشینگ و کلاهبرداری است!\n\nدلایل رد قطعی:\n۱. قوه قضاییه هرگز از شماره‌های شخصی (۰۹...) پیامک نمی‌زند، بلکه با سرشماره متنی عدل ایران پیامک ارسال می‌کند.\n۲. دامنه‌های رسمی دولتی منحصراً با پسوند .ir هستند، نه .top یا .com."
        ));

        QUESTIONS.add(new QuizQuestion(
            "quiz_seham",
            "پیامک سود سهام عدالت",
            "سرشماره: 09129876543\nمتن: «مبلغ ۲,۴۵۰,۰۰۰ تومان سود سهام عدالت به حساب شما تخصیص یافت. جهت تایید و واریز فوری به سامانه sahamedalat-fa.com وارد شوید.»",
            true,
            "این پیامک کلاهبرداری و تله سرقت رمز پویاست!\n\nسود سهام عدالت به صورت کاملاً خودکار و بدون نیاز به هیچ ثبت‌نام، تایید یا پرداخت وجهی به حساب شبای بانکی شما واریز می‌شود."
        ));

        QUESTIONS.add(new QuizQuestion(
            "quiz_bank_call",
            "تماس تلفنی حراست و پشتیبانی بانک",
            "تماس صوتی: «سلام، از حراست بانک تماس می‌گیرم. فردی در حال ورود به حساب شماست؛ کدی که الان برایتان پیامک شد را بخوانید تا مسدودش کنیم!»",
            true,
            "این خطرناک‌ترین شگرد مهندسی اجتماعی است!\n\nهیچ کارمند بانکی، پشتیبان یا حتی مامور پلیسی حق پرسیدن کدهای پیامکی شما را ندارد. با خواندن کد، هکر وارد اکانت شما می‌شود."
        ));

        QUESTIONS.add(new QuizQuestion(
            "quiz_yadegari_file",
            "فایل عکس‌های یادگاری در تلگرام",
            "فایل ارسالی: «البوم_عکس_یادگاری_سفر.apk» با حجم ۱ مگابایت",
            true,
            "این یک بدافزار و تروجان بانکی قطعی است!\n\nعکس‌ها هرگز با پسوند apk (فایل برنامه‌های اندروید) ارسال نمی‌شوند. پسوند عکس‌ها jpg یا png است. این فایل بدافزار Velora برای خالی کردن کارت‌هاست."
        ));

        QUESTIONS.add(new QuizQuestion(
            "quiz_real_shaparak",
            "درگاه پرداخت اینترنتی با دامنه شاپرک",
            "آدرس‌بار مرورگر: https://bpm.shaparak.ir/pgwchannel/startpay.mellat",
            false,
            "این درگاه کاملاً معتبر و رسمی است!\n\nدلیل: آدرس دقیقاً به دامنه ملی shaparak.ir ختم شده و پروتکل امن HTTPS فعال است."
        ));
    }

    public static List<QuizQuestion> getQuestions() {
        return new ArrayList<>(QUESTIONS);
    }
}

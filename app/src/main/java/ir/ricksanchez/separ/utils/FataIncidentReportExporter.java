package ir.ricksanchez.separ.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import ir.ricksanchez.separ.SeparApp;
import ir.ricksanchez.separ.data.RescueLedgerRepository;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * سامانه تدوین و صدور گزارش مستند فنی جرایم سایبری جهت ارائه به پلیس فتا و دادسرای جرایم رایانه‌ای
 * مستندسازی متهم/شماره تماس، پیامک‌های فیشینگ رهگیری‌شده، بسته‌های مخرب و جلوگیری از تضرر مالی
 */
public class FataIncidentReportExporter {

    public static String generateFullIncidentReport(Context context) {
        StringBuilder sb = new StringBuilder();

        String nowJalali = new SimpleDateFormat("yyyy/MM/dd - HH:mm:ss", Locale.getDefault()).format(new Date());

        sb.append("═══════════════════════════════════════════════\n");
        sb.append("📋 گزارش مستند فنی جرایم رایانه‌ای و استعلام سایبری\n");
        sb.append("موضوع: استناد به مواد ۱، ۶ و ۱۳ قانون جرایم رایانه‌ای\n");
        sb.append("مرجع رسیدگی: پلیس فضای تولید و تبادل اطلاعات (فتا) / دادسرای عمومی\n");
        sb.append("═══════════════════════════════════════════════\n\n");

        sb.append("۱. مشخصات فنی دستگاه شاکی:\n");
        sb.append("• مدل و برند گوشی: ").append(Build.MANUFACTURER).append(" ").append(Build.MODEL).append("\n");
        sb.append("• نسخه اندروید: ").append(Build.VERSION.RELEASE).append(" (API ").append(Build.VERSION.SDK_INT).append(")\n");
        sb.append("• تاریخ و ساعت تهیه گزارش: ").append(nowJalali).append("\n");
        sb.append("• سامانه پایشگر امنیتی: نرم‌افزار ضد هک سپر (نسخه رسمی ریک سانچز)\n\n");

        sb.append("۲. کارنامه رخدادهای سایبری خنثی‌شده (مستندات پرونده):\n");

        RescueLedgerRepository repo = SeparApp.getInstance().getRescueLedger();
        SQLiteDatabase db = repo.getReadableDatabase();

        int counter = 1;
        Cursor c = db.rawQuery("SELECT timestamp, jalali_date, threat_title, summary_fa, source_package_or_domain FROM rescued_attacks ORDER BY id DESC LIMIT 15", null);
        if (c != null) {
            while (c.moveToNext()) {
                long ts = c.getLong(0);
                String jalali = c.getString(1);
                String title = c.getString(2);
                String summary = c.getString(3);
                String source = c.getString(4);

                sb.append(counter++).append(") رخداد: ").append(title).append("\n");
                sb.append("   • تاریخ ثبت: ").append(jalali).append(" (زمان یونیکس: ").append(ts).append(")\n");
                sb.append("   • منبع حمله / سرشماره / دامنه مظنون: ").append(source).append("\n");
                sb.append("   • خلاصه واقعه و مداخله سپر: ").append(summary).append("\n\n");
            }
            c.close();
        }

        if (counter == 1) {
            sb.append("• هنوز حمله موفقی علیه دستگاه ثبت نشده و سامانه در وضعیت پاک قرار دارد.\n\n");
        }

        sb.append("۳. تقاضای شاکی از مرجع قضایی و پلیس فتا:\n");
        sb.append("خواهشمند است با عنایت به مستندات فنی و لاگ‌های استخراج‌شده از سامانه، نسبت به رهگیری سرشماره‌ها/درگاه‌های جعلی مذکور، انسداد حساب‌های مقصد و تعقیب کیفری گردانندگان شبکه فیشینگ و انتشار بدافزار اقدام لازم مبذول فرمایید.\n\n");
        sb.append("امضا و تایید گزارش فنی دیجیتال • سامانه امنیتی سپر\n");

        return sb.toString();
    }

    public static void shareReport(Context context) {
        String report = generateFullIncidentReport(context);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "گزارش مستند تخلف سایبری جهت ارائه به پلیس فتا");
        intent.putExtra(Intent.EXTRA_TEXT, report);
        context.startActivity(Intent.createChooser(intent, "ارسال یا ذخیره گزارش پلیس فتا"));
    }
}

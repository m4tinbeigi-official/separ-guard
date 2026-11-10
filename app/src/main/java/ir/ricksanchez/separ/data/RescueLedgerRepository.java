package ir.ricksanchez.separ.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class RescueLedgerRepository extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "separ_rescue_ledger.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_NAME = "rescued_attacks";
    private static final String COL_ID = "id";
    private static final String COL_TIMESTAMP = "timestamp";
    private static final String COL_JALALI_DATE = "jalali_date";
    private static final String COL_TYPE = "attack_type";
    private static final String COL_TITLE = "threat_title";
    private static final String COL_SUMMARY = "summary_fa";
    private static final String COL_FINANCIAL = "financial_protection";
    private static final String COL_SOURCE = "source_package_or_domain";
    private static final String COL_DISASTER = "what_could_have_happened";

    public static class RescueStats {
        public final int totalAttacksBlocked;
        public final int bankingTheftsPrevented;
        public final int phishingSmsBlocked;
        public final int maliciousAppsDestroyed;

        public RescueStats(int total, int banking, int sms, int apps) {
            this.totalAttacksBlocked = total;
            this.bankingTheftsPrevented = banking;
            this.phishingSmsBlocked = sms;
            this.maliciousAppsDestroyed = apps;
        }
    }

    public static class RescueItem {
        public final long id;
        public final long timestamp;
        public final String jalaliDate;
        public final int type;
        public final String title;
        public final String summary;
        public final boolean isFinancial;
        public final String source;
        public final String whatCouldHaveHappened;

        public RescueItem(long id, long ts, String jDate, int type, String title, String summary, boolean fin, String source, String disaster) {
            this.id = id;
            this.timestamp = ts;
            this.jalaliDate = jDate;
            this.type = type;
            this.title = title;
            this.summary = summary;
            this.isFinancial = fin;
            this.source = source;
            this.whatCouldHaveHappened = disaster;
        }
    }

    public RescueLedgerRepository(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
            "CREATE TABLE " + TABLE_NAME + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_TIMESTAMP + " INTEGER NOT NULL, " +
            COL_JALALI_DATE + " TEXT NOT NULL, " +
            COL_TYPE + " INTEGER NOT NULL, " +
            COL_TITLE + " TEXT NOT NULL, " +
            COL_SUMMARY + " TEXT NOT NULL, " +
            COL_FINANCIAL + " INTEGER NOT NULL, " +
            COL_SOURCE + " TEXT NOT NULL, " +
            COL_DISASTER + " TEXT" +
            ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long recordRescue(long timestamp, String jalaliDate, int type, String title, String summary, boolean isFinancial, String source) {
        String disaster = deduceDisaster(title, summary);
        ContentValues values = new ContentValues();
        values.put(COL_TIMESTAMP, timestamp);
        values.put(COL_JALALI_DATE, jalaliDate);
        values.put(COL_TYPE, type);
        values.put(COL_TITLE, title);
        values.put(COL_SUMMARY, summary);
        values.put(COL_FINANCIAL, isFinancial ? 1 : 0);
        values.put(COL_SOURCE, source);
        values.put(COL_DISASTER, disaster);
        return getWritableDatabase().insert(TABLE_NAME, null, values);
    }

    public List<RescueItem> getAllRescuesWithEducationalFallback() {
        List<RescueItem> list = new ArrayList<>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COL_TIMESTAMP + " DESC");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
                    long ts = cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIMESTAMP));
                    String jDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_JALALI_DATE));
                    int type = cursor.getInt(cursor.getColumnIndexOrThrow(COL_TYPE));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE));
                    String summary = cursor.getString(cursor.getColumnIndexOrThrow(COL_SUMMARY));
                    boolean fin = cursor.getInt(cursor.getColumnIndexOrThrow(COL_FINANCIAL)) == 1;
                    String src = cursor.getString(cursor.getColumnIndexOrThrow(COL_SOURCE));
                    String disaster = cursor.getString(cursor.getColumnIndexOrThrow(COL_DISASTER));
                    if (disaster == null || disaster.trim().isEmpty()) {
                        disaster = deduceDisaster(title, summary);
                    }
                    list.add(new RescueItem(id, ts, jDate, type, title, summary, fin, src, disaster));
                }
                cursor.close();
            }
        } catch (Exception ignored) {}

        // در صورت نو بودن اپلیکیشن و خالی بودن لاگ، ۴ سناریوی محافظتی سپر نمایش داده می‌شود
        if (list.isEmpty()) {
            list.add(new RescueItem(
                1, System.currentTimeMillis(), "هم‌اکنون فعال", 0,
                "تروجان پوشه یادگاری / آلبوم یادگاری (Velora)",
                "مسدودسازی نفوذ بدافزار Jiagu و شنود خودکار پیامک‌های حاوی رمز پویا",
                true, "com.velora.cloud",
                "هکرها در کسری از ثانیه رمز پویای شما را می‌دزدیدند، تمام موجودی حسابتان را کارت‌به‌کارت می‌کردند و با خط شما برای کل مخاطبانتان پیامک اسپم می‌فرستادند که منجر به مسدودی خط تلفن و شکایت قضایی می‌شد."
            ));
            list.add(new RescueItem(
                2, System.currentTimeMillis(), "هم‌اکنون فعال", 1,
                "درگاه جعلی شاپرک و شکوائیه ابلاغیه ثنا",
                "شناسایی و قطع ارتباط با درگاه جعلی پرداخت ۲ هزار تومانی ابلاغیه دادسرا",
                true, "shaparak.fake-payment.ir",
                "شماره کارت، رمز دوم، تاریخ انقضا و CVV2 شما به سرقت می‌رفت و علاوه بر خالی شدن حساب، شماره کارت شما به عنوان حساب واسط (قاطر پولشویی) برای جرایم قمار استفاده می‌شد."
            ));
            list.add(new RescueItem(
                3, System.currentTimeMillis(), "هم‌اکنون فعال", 2,
                "بمباران و طوفان پیامکی ورود (Credential Stuffing)",
                "به صدا درآمدن آژیر خطر هنگام هجوم درخواست‌های متوالی کد ورود به تلگرام و روبیکا",
                true, "OTP Bombing Siren",
                "تسخیر کامل تلگرام و روبیکای شما؛ هکر با تماس فریبکارانه به عنوان پشتیبان، کد را دریافت و با دسترسی به چت‌های خصوصی، از آشنایانتان تقاضای واریز پول دستی می‌کرد."
            ));
            list.add(new RescueItem(
                4, System.currentTimeMillis(), "هم‌اکنون فعال", 2,
                "اجازه نصب برنامه‌های ناشناخته از تلگرام (Anti-Sideload)",
                "هشدار و قفل اجازه نصب مستقیم فایل‌های APK از داخل چت‌های تلگرام و پیام‌رسان‌ها",
                false, "org.telegram.messenger",
                "نصب بدون مجوز باج‌افزارها و استالکروارهای جاسوسی که می‌توانستند کل عکس‌ها، مخاطبان و اسناد محرمانه گوشی شما را به سرورهای ناشناس ارسال کنند."
            ));
        }

        return list;
    }

    private static String deduceDisaster(String title, String summary) {
        String combined = (title + " " + summary).toLowerCase();
        if (combined.contains("شاپرک") || combined.contains("درگاه") || combined.contains("کارت")) {
            return "تخلیه کامل موجودی حساب شما در درگاه جعلی و ثبت شماره حساب شما به عنوان متهم پولشویی قمار در محاکم قضایی.";
        }
        if (combined.contains("ثنا") || combined.contains("یادگاری") || combined.contains("پیامک")) {
            return "سرقت پیامک‌های رمز دوم، ارسال هزاران پیامک اسپم به مخاطبانتان از جیب شما و شکایت بستگان از سیم‌کارتتان.";
        }
        if (combined.contains("بمباران") || combined.contains("آژیر") || combined.contains("ورود")) {
            return "تسخیر کامل اکانت‌های تلگرام، واتساپ یا روبیکا با ترغیب شما به خواندن کد تایید برای هکر نفوذگر.";
        }
        return "نفوذ پنهانی به دستگاه، ضبط صدا با میکروفون و سرقت فایل‌ها و عکس‌های خصوصی خانوادگی شما.";
    }

    public RescueStats getStats() {
        SQLiteDatabase db = getReadableDatabase();
        int total = 0;
        int banking = 0;
        int sms = 0;
        int apps = 0;

        try {
            Cursor cursor = db.rawQuery("SELECT " + COL_TYPE + ", " + COL_FINANCIAL + " FROM " + TABLE_NAME, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    total++;
                    int type = cursor.getInt(0);
                    boolean isFinancial = cursor.getInt(1) == 1;
                    if (isFinancial || type == 0) banking++;
                    if (type == 1) sms++;
                    if (type == 2) apps++;
                }
                cursor.close();
            }
        } catch (Exception ignored) {}

        return new RescueStats(total, banking, sms, apps);
    }

    public String generateVoiceReport() {
        RescueStats stats = getStats();
        if (stats.totalAttacksBlocked == 0) {
            return "سپر شما بیدار و آماده است. تا این لحظه هیچ نفوذی ثبت نشده و دستگاه شما در بالاترین ضریب امنیتی قرار دارد.";
        } else {
            return "سپر شما بیدار بوده و تا الان جلوی " + stats.bankingTheftsPrevented + " سرقت بانکی و " + stats.phishingSmsBlocked + " پیامک کلاهبرداری را گرفته است. خیالتان راحت باشد.";
        }
    }
}

package ir.ricksanchez.separ.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RescueLedgerRepository extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "separ_rescue_ledger.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "rescued_attacks";
    private static final String COL_ID = "id";
    private static final String COL_TIMESTAMP = "timestamp";
    private static final String COL_JALALI_DATE = "jalali_date";
    private static final String COL_TYPE = "attack_type";
    private static final String COL_TITLE = "threat_title";
    private static final String COL_SUMMARY = "summary_fa";
    private static final String COL_FINANCIAL = "financial_protection";
    private static final String COL_SOURCE = "source_package_or_domain";

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
            COL_SOURCE + " TEXT NOT NULL" +
            ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long recordRescue(long timestamp, String jalaliDate, int type, String title, String summary, boolean isFinancial, String source) {
        ContentValues values = new ContentValues();
        values.put(COL_TIMESTAMP, timestamp);
        values.put(COL_JALALI_DATE, jalaliDate);
        values.put(COL_TYPE, type);
        values.put(COL_TITLE, title);
        values.put(COL_SUMMARY, summary);
        values.put(COL_FINANCIAL, isFinancial ? 1 : 0);
        values.put(COL_SOURCE, source);
        return getWritableDatabase().insert(TABLE_NAME, null, values);
    }

    public RescueStats getStats() {
        SQLiteDatabase db = getReadableDatabase();
        int total = 0;
        int banking = 0;
        int sms = 0;
        int apps = 0;

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

        return new RescueStats(total, banking, sms, apps);
    }

    public String generateVoiceReport() {
        RescueStats stats = getStats();
        if (stats.totalAttacksBlocked == 0) {
            return "سپر شما فعال است. تا این لحظه هیچ حمله ای به گوشی شما انجام نشده و دستگاه کاملاً امن است.";
        } else {
            return "سپر شما بیدار بوده و تا الان جلوی " + stats.bankingTheftsPrevented + " سرقت بانکی و " + stats.phishingSmsBlocked + " پیامک کلاهبرداری را گرفته است. خیالتان راحت باشد.";
        }
    }
}

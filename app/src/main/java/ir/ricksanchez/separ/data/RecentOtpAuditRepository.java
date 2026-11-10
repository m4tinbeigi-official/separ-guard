package ir.ricksanchez.separ.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * پایگاه داده دیده‌بان ورودهای پنهان و تاریخچه کدهای تایید (Ghost Login & Recent OTP Audit Hub)
 * شناسایی حساب‌هایی که شاید کاربر اصلاً برنامه آن‌ها را روی گوشی ندارد اما هکر با شماره او لاگین کرده است.
 */
public class RecentOtpAuditRepository extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "separ_otp_audit.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "recent_otp_history";
    private static final String COL_ID = "id";
    private static final String COL_TIMESTAMP = "timestamp";
    private static final String COL_DATE_TIME = "date_time_fa";
    private static final String COL_PLATFORM = "platform_name";
    private static final String COL_PACKAGE = "expected_package";
    private static final String COL_SENDER = "sender";
    private static final String COL_OTP_CODE = "otp_code";
    private static final String COL_RAW_BODY = "raw_body";
    private static final String COL_PORTAL_URL = "portal_url";
    private static final String COL_SUPPORT_PHONE = "support_phone";

    public static class OtpAuditItem {
        public final long id;
        public final long timestamp;
        public final String dateTimeFa;
        public final String platformName;
        public final String expectedPackage;
        public final String sender;
        public final String otpCode;
        public final String rawBody;
        public final String portalUrl;
        public final String supportPhone;
        public final boolean isAppInstalled;

        public OtpAuditItem(long id, long ts, String dt, String platform, String pkg,
                            String sender, String otp, String raw, String portal,
                            String phone, boolean installed) {
            this.id = id;
            this.timestamp = ts;
            this.dateTimeFa = dt;
            this.platformName = platform;
            this.expectedPackage = pkg;
            this.sender = sender;
            this.otpCode = otp;
            this.rawBody = raw;
            this.portalUrl = portal;
            this.supportPhone = phone;
            this.isAppInstalled = installed;
        }
    }

    public static class PlatformMetadata {
        public final String name;
        public final String packageName;
        public final String portalUrl;
        public final String supportPhone;

        public PlatformMetadata(String name, String pkg, String url, String phone) {
            this.name = name;
            this.packageName = pkg;
            this.portalUrl = url;
            this.supportPhone = phone;
        }
    }

    private final Context context;

    public RecentOtpAuditRepository(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
            "CREATE TABLE " + TABLE_NAME + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_TIMESTAMP + " INTEGER NOT NULL, " +
            COL_DATE_TIME + " TEXT NOT NULL, " +
            COL_PLATFORM + " TEXT NOT NULL, " +
            COL_PACKAGE + " TEXT, " +
            COL_SENDER + " TEXT, " +
            COL_OTP_CODE + " TEXT, " +
            COL_RAW_BODY + " TEXT NOT NULL, " +
            COL_PORTAL_URL + " TEXT, " +
            COL_SUPPORT_PHONE + " TEXT" +
            ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * ثبت بلادرنگ پیامک حاوی کد ورود
     */
    public synchronized long recordOtpMessage(String sender, String body, long timestamp) {
        if (body == null || body.trim().isEmpty()) return -1;

        PlatformMetadata meta = identifyPlatform(body);
        String extractedCode = extractOtpDigits(body);
        String dtFa = formatTimestampFa(timestamp > 0 ? timestamp : System.currentTimeMillis());

        ContentValues cv = new ContentValues();
        cv.put(COL_TIMESTAMP, timestamp > 0 ? timestamp : System.currentTimeMillis());
        cv.put(COL_DATE_TIME, dtFa);
        cv.put(COL_PLATFORM, meta.name);
        cv.put(COL_PACKAGE, meta.packageName);
        cv.put(COL_SENDER, sender != null ? sender : "سامانه پیامک");
        cv.put(COL_OTP_CODE, extractedCode);
        cv.put(COL_RAW_BODY, body);
        cv.put(COL_PORTAL_URL, meta.portalUrl);
        cv.put(COL_SUPPORT_PHONE, meta.supportPhone);

        try {
            return getWritableDatabase().insert(TABLE_NAME, null, cv);
        } catch (Exception e) {
            return -1;
        }
    }

    /**
     * پویش تاریخچه پیامک‌های اخیر دستگاه جهت استخراج کدهای ارسالی روزهای گذشته
     */
    public void scanDeviceSmsInboxIfPermitted() {
        if (context == null) return;
        try {
            PackageManager pm = context.getPackageManager();
            if (pm.checkPermission(android.Manifest.permission.READ_SMS, context.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            Uri inboxUri = Uri.parse("content://sms/inbox");
            Cursor cursor = context.getContentResolver().query(
                inboxUri,
                new String[]{"address", "body", "date"},
                null,
                null,
                "date DESC LIMIT 50"
            );

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String address = cursor.getString(0);
                    String body = cursor.getString(1);
                    long date = cursor.getLong(2);

                    if (body != null && isLoginOtpMessage(body)) {
                        if (!isBodyAlreadyRecorded(body)) {
                            recordOtpMessage(address, body, date);
                        }
                    }
                }
                cursor.close();
            }
        } catch (Exception ignored) {}
    }

    private boolean isBodyAlreadyRecorded(String body) {
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor c = db.rawQuery("SELECT " + COL_ID + " FROM " + TABLE_NAME + " WHERE " + COL_RAW_BODY + " = ? LIMIT 1", new String[]{body});
            boolean exists = (c != null && c.getCount() > 0);
            if (c != null) c.close();
            return exists;
        } catch (Exception e) {
            return false;
        }
    }

    public List<OtpAuditItem> getAllRecentOtps() {
        List<OtpAuditItem> list = new ArrayList<>();
        PackageManager pm = context != null ? context.getPackageManager() : null;

        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, COL_TIMESTAMP + " DESC LIMIT 40");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID));
                    long ts = cursor.getLong(cursor.getColumnIndexOrThrow(COL_TIMESTAMP));
                    String dt = cursor.getString(cursor.getColumnIndexOrThrow(COL_DATE_TIME));
                    String platform = cursor.getString(cursor.getColumnIndexOrThrow(COL_PLATFORM));
                    String pkg = cursor.getString(cursor.getColumnIndexOrThrow(COL_PACKAGE));
                    String sender = cursor.getString(cursor.getColumnIndexOrThrow(COL_SENDER));
                    String otp = cursor.getString(cursor.getColumnIndexOrThrow(COL_OTP_CODE));
                    String raw = cursor.getString(cursor.getColumnIndexOrThrow(COL_RAW_BODY));
                    String portal = cursor.getString(cursor.getColumnIndexOrThrow(COL_PORTAL_URL));
                    String phone = cursor.getString(cursor.getColumnIndexOrThrow(COL_SUPPORT_PHONE));

                    boolean isInstalled = false;
                    if (pm != null && pkg != null && !pkg.isEmpty()) {
                        try {
                            pm.getPackageInfo(pkg, 0);
                            isInstalled = true;
                        } catch (Exception e) {
                            isInstalled = false;
                        }
                    }

                    list.add(new OtpAuditItem(id, ts, dt, platform, pkg, sender, otp, raw, portal, phone, isInstalled));
                }
                cursor.close();
            }
        } catch (Exception ignored) {}

        // نمونه‌های آموزشی و سناریوهای مرجع در صورت خالی بودن دیتابیس
        if (list.isEmpty()) {
            list.add(new OtpAuditItem(
                1, System.currentTimeMillis() - 7200000, "امروز • ساعت ۱۱:۲۰",
                "روبیکا (Rubika)", "ir.rubika.app",
                "3000", "۵۴۳۲۱",
                "کد ورود به روبیکا: ۵۴۳۲۱. این کد را در اختیار هیچ فردی قرار ندهید.",
                "https://web.rubika.ir/", "02191079000",
                false // برنامه روی گوشی نصب نیست -> خطر لاگین پنهان
            ));
            list.add(new OtpAuditItem(
                2, System.currentTimeMillis() - 18000000, "امروز • ساعت ۰۸:۱۵",
                "اسنپ‌پی / اعتبار اقساطی", "cab.snapp.passenger",
                "SnappPay", "۸۹۰۱۲",
                "کد فعال‌سازی اعتبار اقساطی اسنپ‌پی: ۸۹۰۱۲",
                "https://snapppay.ir/", "02196864000",
                false // خطر ایجاد بدهی اقساطی توسط هکر
            ));
            list.add(new OtpAuditItem(
                3, System.currentTimeMillis() - 86400000, "دیروز • ساعت ۱۶:۴۰",
                "همراه من (پورتال همراه اول)", "ir.mci.ecareapp",
                "MCI", "۳۷۱۲۰",
                "رمز یکبار مصرف ورود به همراه من: ۳۷۱۲۰",
                "https://my.mci.ir/", "9990",
                true // نصب است
            ));
        }

        return list;
    }

    public static boolean isLoginOtpMessage(String body) {
        if (body == null) return false;
        String clean = body.toLowerCase();
        return clean.contains("کد ورود") || clean.contains("رمز ورود") || clean.contains("کد تایید")
            || clean.contains("کد فعال‌سازی") || clean.contains("کد فعالسازی") || clean.contains("رمز یکبار مصرف")
            || clean.contains("رمز پویا") || clean.contains("login code") || clean.contains("verification code");
    }

    public static PlatformMetadata identifyPlatform(String text) {
        String lower = text.toLowerCase();

        if (lower.contains("rubika") || lower.contains("روبیکا")) {
            return new PlatformMetadata("روبیکا (Rubika)", "ir.rubika.app", "https://web.rubika.ir/", "02191079000");
        }
        if (lower.contains("eitaa") || lower.contains("ایتا")) {
            return new PlatformMetadata("ایتا (Eitaa)", "ir.eitaa.messenger", "https://web.eitaa.com/", "02531020");
        }
        if (lower.contains("bale") || lower.contains("بله")) {
            return new PlatformMetadata("بله (Bale)", "ir.bale.messenger", "https://web.bale.ai/", "02142299");
        }
        if (lower.contains("telegram") || lower.contains("تلگرام")) {
            return new PlatformMetadata("تلگرام (Telegram)", "org.telegram.messenger", "https://web.telegram.org/", "");
        }
        if (lower.contains("whatsapp") || lower.contains("واتساپ")) {
            return new PlatformMetadata("واتساپ (WhatsApp)", "com.whatsapp", "https://web.whatsapp.com/", "");
        }
        if (lower.contains("همراه من") || lower.contains("mci")) {
            return new PlatformMetadata("همراه من (پورتال همراه اول)", "ir.mci.ecareapp", "https://my.mci.ir/", "9990");
        }
        if (lower.contains("ایرانسل من") || lower.contains("irancell")) {
            return new PlatformMetadata("ایرانسل من (پورتال ایرانسل)", "ir.mtnirancell.myirancell", "https://my.irancell.ir/", "700");
        }
        if (lower.contains("اسنپ‌پی") || lower.contains("اسنپ پی") || lower.contains("snapppay")) {
            return new PlatformMetadata("اسنپ‌پی (خرید اعتباری اقساطی)", "cab.snapp.passenger", "https://snapppay.ir/", "02196864000");
        }
        if (lower.contains("دیجی‌پی") || lower.contains("دیجی پی") || lower.contains("digipay")) {
            return new PlatformMetadata("دیجی‌پی (کیف پول و وام)", "com.mydigipay.app", "https://www.mydigipay.com/", "02158474000");
        }
        if (lower.contains("دیجی‌کالا") || lower.contains("دیجیکالا") || lower.contains("digikala")) {
            return new PlatformMetadata("دیجی‌کالا (Digikala)", "com.digikala.mobile", "https://www.digikala.com/profile/orders/", "02161930000");
        }
        if (lower.contains("دیوار") || lower.contains("divar")) {
            return new PlatformMetadata("دیوار (Divar)", "ir.divar", "https://divar.ir/my-divar/devices", "02179458000");
        }
        if (lower.contains("شیپور") || lower.contains("sheypoor")) {
            return new PlatformMetadata("شیپور (Sheypoor)", "com.sheypoor.mobile", "https://www.sheypoor.com/", "02154587");
        }
        if (lower.contains("نوبیتکس") || lower.contains("nobitex")) {
            return new PlatformMetadata("نوبیتکس (صرافی رمزارز)", "ir.nobitex.app", "https://nobitex.ir/security/", "02191009900");
        }
        if (lower.contains("والکس") || lower.contains("wallex")) {
            return new PlatformMetadata("والکس (صرافی رمزارز)", "ir.wallex.app", "https://wallex.ir/", "02191002275");
        }
        if (lower.contains("میلی") || lower.contains("milli")) {
            return new PlatformMetadata("میلی گلد (خرید طلا)", "gold.milli.app", "https://milli.gold/", "02191200250");
        }
        if (lower.contains("طلاین") || lower.contains("taline")) {
            return new PlatformMetadata("طلاین (معامله طلا)", "ir.taline.app", "https://taline.ir/", "02191070123");
        }
        if (lower.contains("آپ") || lower.contains("آسان پرداخت")) {
            return new PlatformMetadata("آپ (آسان پرداخت)", "com.asanpardakht.app", "https://asanpardakht.ir/", "02183333");
        }
        if (lower.contains("تپسی") || lower.contains("tapsi")) {
            return new PlatformMetadata("تپسی (Tapsi)", "tapsi.passenger.cab", "https://tapsi.ir/", "1630");
        }

        return new PlatformMetadata("سرویس آنلاین / سامانه وب", "", "", "096380");
    }

    public static String extractOtpDigits(String body) {
        if (body == null) return "";
        // استخراج ۴ تا ۸ رقم پیوسته
        Pattern pattern = Pattern.compile("\\b(\\d{4,8})\\b");
        Matcher matcher = pattern.matcher(body);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }

    public static String formatTimestampFa(long ts) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd • HH:mm", Locale.getDefault());
        return sdf.format(new Date(ts));
    }
}

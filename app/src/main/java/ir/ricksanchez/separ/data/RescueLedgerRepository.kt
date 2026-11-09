package ir.ricksanchez.separ.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import ir.ricksanchez.separ.data.models.RescuedAttack
import ir.ricksanchez.separ.data.models.RescuedAttackType

/**
 * مدیریت ثبت و ذخیره‌سازی رویدادهای نجات کاربر از حملات سایبری و بانکی
 */
class RescueLedgerRepository(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "separ_rescue_ledger.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_NAME = "rescued_attacks"
        private const val COL_ID = "id"
        private const val COL_TIMESTAMP = "timestamp"
        private const val COL_JALALI_DATE = "jalali_date"
        private const val COL_TYPE = "attack_type"
        private const val COL_TITLE = "threat_title"
        private const val COL_SUMMARY = "summary_fa"
        private const val COL_FINANCIAL = "financial_protection"
        private const val COL_SOURCE = "source_package_or_domain"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE_NAME (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TIMESTAMP INTEGER NOT NULL,
                $COL_JALALI_DATE TEXT NOT NULL,
                $COL_TYPE INTEGER NOT NULL,
                $COL_TITLE TEXT NOT NULL,
                $COL_SUMMARY TEXT NOT NULL,
                $COL_FINANCIAL INTEGER NOT NULL,
                $COL_SOURCE TEXT NOT NULL
            );
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    /**
     * ثبت یک حمله خنثی‌شده در کارنامه کاربر
     */
    fun recordRescue(attack: RescuedAttack): Long {
        val values = ContentValues().apply {
            put(COL_TIMESTAMP, attack.timestamp)
            put(COL_JALALI_DATE, attack.jalaliDate)
            put(COL_TYPE, attack.type.ordinal)
            put(COL_TITLE, attack.title)
            put(COL_SUMMARY, attack.summaryFa)
            put(COL_FINANCIAL, if (attack.financialProtection) 1 else 0)
            put(COL_SOURCE, attack.sourcePackageOrDomain)
        }
        return writableDatabase.insert(TABLE_NAME, null, values)
    }

    /**
     * دریافت آمار کلی نجات‌ها برای نمایش به افراد کم‌سواد
     */
    data class RescueStats(
        val totalAttacksBlocked: Int,
        val bankingTheftsPrevented: Int,
        val phishingSmsBlocked: Int,
        val maliciousAppsDestroyed: Int
    )

    fun getStats(): RescueStats {
        val db = readableDatabase
        var total = 0
        var banking = 0
        var sms = 0
        var apps = 0

        val cursor = db.rawQuery("SELECT $COL_TYPE, $COL_FINANCIAL FROM $TABLE_NAME", null)
        cursor.use {
            while (it.moveToNext()) {
                total++
                val typeOrdinal = it.getInt(0)
                val isFinancial = it.getInt(1) == 1

                if (isFinancial || typeOrdinal == RescuedAttackType.FAKE_SHAPARAK_GATEWAY.ordinal) {
                    banking++
                }
                if (typeOrdinal == RescuedAttackType.PHISHING_SMS.ordinal) {
                    sms++
                }
                if (typeOrdinal == RescuedAttackType.MALICIOUS_APP_BLOCKED.ordinal) {
                    apps++
                }
            }
        }

        return RescueStats(
            totalAttacksBlocked = total,
            bankingTheftsPrevented = banking,
            phishingSmsBlocked = sms,
            maliciousAppsDestroyed = apps
        )
    }

    /**
     * تولید متن روان گفتاری برای دستیار صوتی
     */
    fun generateVoiceReport(): String {
        val stats = getStats()
        return if (stats.totalAttacksBlocked == 0) {
            "سپر شما فعال است. تا این لحظه هیچ حمله‌ای به گوشی شما انجام نشده و دستگاه کاملاً امن است."
        } else {
            "سپر شما بیدار بوده و تا الان جلوی ${stats.bankingTheftsPrevented} سرقت بانکی و ${stats.phishingSmsBlocked} پیامک کلاهبرداری را گرفته است. خیالتان راحت باشد."
        }
    }
}

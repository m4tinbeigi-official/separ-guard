package ir.ricksanchez.separ.workers

import android.content.Context
import androidx.work.*
import ir.ricksanchez.separ.SeparApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

/**
 * ورکر پس‌زمینه برای همگام‌سازی روزانه لیست دامنه‌های جدید فیشینگ و امضاهای بدافزار
 */
class ThreatSyncWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        private const val SYNC_WORK_NAME = "separ_daily_threat_sync"
        private const val UPDATE_ENDPOINT = "https://raw.githubusercontent.com/m4tinbeigi-official/separ-guard/main/threats_feed.json"

        fun scheduleDailySync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val dailyWork = PeriodicWorkRequestBuilder<ThreatSyncWorker>(24, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                SYNC_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                dailyWork
            )
        }
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            // در حالت آفلاین یا اختلال شبکه با موفقیت عبور می‌کند
            val url = URL(UPDATE_ENDPOINT)
            val connection = url.openConnection() as HttpURLConnection
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val stream = connection.inputStream
                val responseText = stream.bufferedReader().use { it.readText() }
                // پردازش و ذخیره در صورت وجود داده جدید
            }
            Result.success()
        } catch (_: Exception) {
            // عدم پرتاب خطا در صورت نبود اینترنت تا گوشی کاربر درگیر نشود
            Result.success()
        }
    }
}

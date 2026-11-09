package ir.ricksanchez.separ.security

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * خزانه‌داری رمزنگاری‌شده و ضد دستکاری اشتراک کاربر
 * جلوگیری از باز کردن قفل برنامه با تغییر SharedPreferences یا ویرایش دستی دیتابیس در گوشی‌های روت‌شده
 */
class CryptoSubscriptionVault(private val context: Context) {

    companion object {
        private const val PREF_NAME = "separ_sec_vault"
        private const val KEY_EXPIRE = "v_exp"
        private const val KEY_SEAL = "v_seal"
        private const val SECRET_SALT = "RickSanchez_Separ_Secured_Vault_Seal_2026_Key"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /**
     * محاسبه مهر دیجیتال رمزنگاری‌شده برای بستن دسترسی مودرها
     */
    private fun computeSeal(expireTime: Long, deviceId: String): String {
        return try {
            val data = "$expireTime:$deviceId:$SECRET_SALT"
            val keySpec = SecretKeySpec(SECRET_SALT.toByteArray(Charsets.UTF_8), "HmacSHA256")
            val mac = Mac.getInstance("HmacSHA256")
            mac.init(keySpec)
            val hmacBytes = mac.doFinal(data.toByteArray(Charsets.UTF_8))
            hmacBytes.joinToString("") { "%02x".format(it) }
        } catch (_: Exception) {
            ""
        }
    }

    private fun getDeviceId(): String {
        return try {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "generic_dev"
        } catch (_: Exception) {
            "generic_dev"
        }
    }

    /**
     * ذخیره امن اشتراک با مهر دیجیتال
     */
    fun saveSubscription(expireTime: Long) {
        val deviceId = getDeviceId()
        val seal = computeSeal(expireTime, deviceId)

        prefs.edit()
            .putLong(KEY_EXPIRE, expireTime)
            .putString(KEY_SEAL, seal)
            .apply()
    }

    /**
     * بررسی اعتبارسنجی دوطرفه: هم تاریخ منقضی نشده باشد و هم مهر دیجیتال دستکاری نشده باشد
     */
    fun isSubscriptionValid(): Boolean {
        val expireTime = prefs.getLong(KEY_EXPIRE, 0L)
        val savedSeal = prefs.getString(KEY_SEAL, "") ?: ""

        if (expireTime <= System.currentTimeMillis() || savedSeal.isEmpty()) {
            return false
        }

        val deviceId = getDeviceId()
        val expectedSeal = computeSeal(expireTime, deviceId)

        // اگر فایل دستکاری شده باشد یا مهر نخواند، قفل برنامه باز نمی‌شود
        return savedSeal.equals(expectedSeal, ignoreCase = true)
    }

    fun getDaysRemaining(): Int {
        val expireTime = prefs.getLong(KEY_EXPIRE, 0L)
        val diff = expireTime - System.currentTimeMillis()
        return if (diff > 0 && isSubscriptionValid()) {
            (diff / (1000 * 60 * 60 * 24)).toInt()
        } else {
            0
        }
    }
}

package ir.ricksanchez.separ.auth

import android.content.Context
import android.content.SharedPreferences

/**
 * مدیریت ساده و مستقیم ورود با شماره موبایل و کد یکبارمصرف پیامکی
 */
class AuthManager(context: Context) {

    companion object {
        private const val PREF_AUTH = "separ_auth_prefs"
        private const val KEY_PHONE_NUMBER = "user_phone"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_AUTH_TOKEN = "auth_token"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_AUTH, Context.MODE_PRIVATE)

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getPhoneNumber(): String? {
        return prefs.getString(KEY_PHONE_NUMBER, null)
    }

    /**
     * ورود موفق با شماره موبایل
     */
    fun saveLogin(phoneNumber: String, token: String = "token_local") {
        prefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_PHONE_NUMBER, phoneNumber.trim())
            .putString(KEY_AUTH_TOKEN, token)
            .apply()
    }

    /**
     * خروج از حساب
     */
    fun logout() {
        prefs.edit().clear().apply()
    }
}

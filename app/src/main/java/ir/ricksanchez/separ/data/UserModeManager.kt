package ir.ricksanchez.separ.data

import android.content.Context
import android.content.SharedPreferences

/**
 * مدیریت دوحالته نرم‌افزار: کاربر عادی (ساده و صدامحور) در برابر کاربر متخصص (حرفه‌ای و فنی)
 */
class UserModeManager(context: Context) {

    enum class UserMode {
        NOT_SET,
        SIMPLE, // حالت ساده و تصویرمحور برای افراد عادی، سالمندان و کم‌سواد
        PRO     // حالت تخصصی و پنل امنیت سایبری برای افراد حرفه‌ای و تکنسین‌ها
    }

    companion object {
        private const val PREF_NAME = "separ_user_mode_pref"
        private const val KEY_MODE = "user_mode"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun getUserMode(): UserMode {
        val modeStr = prefs.getString(KEY_MODE, UserMode.NOT_SET.name) ?: UserMode.NOT_SET.name
        return try {
            UserMode.valueOf(modeStr)
        } catch (_: Exception) {
            UserMode.NOT_SET
        }
    }

    fun setUserMode(mode: UserMode) {
        prefs.edit().putString(KEY_MODE, mode.name).apply()
    }

    fun isSimpleMode(): Boolean = getUserMode() == UserMode.SIMPLE

    fun isProMode(): Boolean = getUserMode() == UserMode.PRO

    fun isFirstLaunch(): Boolean = getUserMode() == UserMode.NOT_SET
}

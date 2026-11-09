package ir.ricksanchez.separ.data;

import android.content.Context;
import android.content.SharedPreferences;

public class UserModeManager {

    public enum UserMode {
        NOT_SET,
        SIMPLE,
        PRO
    }

    private static final String PREF_NAME = "separ_user_mode_pref";
    private static final String KEY_MODE = "user_mode";

    private final SharedPreferences prefs;

    public UserModeManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public UserMode getUserMode() {
        String modeStr = prefs.getString(KEY_MODE, UserMode.NOT_SET.name());
        try {
            return UserMode.valueOf(modeStr);
        } catch (Exception e) {
            return UserMode.NOT_SET;
        }
    }

    public void setUserMode(UserMode mode) {
        prefs.edit().putString(KEY_MODE, mode.name()).apply();
    }

    public boolean isSimpleMode() {
        return getUserMode() == UserMode.SIMPLE;
    }

    public boolean isProMode() {
        return getUserMode() == UserMode.PRO;
    }

    public boolean isFirstLaunch() {
        return getUserMode() == UserMode.NOT_SET;
    }
}

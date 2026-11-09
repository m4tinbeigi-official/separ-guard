package ir.ricksanchez.separ.services;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import ir.ricksanchez.separ.ui.MainActivity;

/**
 * کاشی تنظیمات سریع اندروید (Quick Settings Tile) در نوار اعلان‌ها
 * به کاربر اجازه می‌دهد وضعیت فعال بودن محافظت سپر را همواره مشاهده و با ۱ لمس وارد برنامه شود.
 */
public class SeparQuickSettingsTileService extends TileService {

    @Override
    public void onStartListening() {
        super.onStartListening();
        Tile tile = getQsTile();
        if (tile != null) {
            tile.setState(Tile.STATE_ACTIVE);
            tile.setLabel("سپر ضد هک: فعال");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                tile.setSubtitle("محافظت پیامک و شاپرک");
            }
            tile.updateTile();
        }
    }

    @Override
    public void onClick() {
        super.onClick();
        try {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivityAndCollapse(intent);
        } catch (Exception ignored) {}
    }
}

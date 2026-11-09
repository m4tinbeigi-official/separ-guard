package ir.ricksanchez.separ.services

import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import ir.ricksanchez.separ.ui.MainActivity

/**
 * سرویس کاشی تنظیمات سریع اندروید (Quick Settings Tile)
 * نمایش وضعیت زنده سپر در نوار اعلان‌ها و بالای گوشی کاربر
 */
@RequiresApi(Build.VERSION_CODES.N)
class SeparQuickSettingsTileService : TileService() {

    override fun onStartListening() {
        super.onStartListening()
        val tile = qsTile ?: return
        tile.state = Tile.STATE_ACTIVE
        tile.label = "سپر ضد هک"
        tile.updateTile()
    }

    override fun onClick() {
        super.onClick()
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivityAndCollapse(intent)
    }
}

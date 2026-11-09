package ir.ricksanchez.separ.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView

/**
 * سرویس نمایش پوسته مسدودساز تمام‌صفحه به رنگ قرمز روی درگاه‌های جعلی پرداخت
 */
class AlertOverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val domain = intent?.getStringExtra("threat_domain")
        val customTitle = intent?.getStringExtra("alert_title")
        val customMessage = intent?.getStringExtra("alert_message")

        val title = customTitle ?: "⚠️ درگاه جعلی پرداخت بانکی!"
        val message = customMessage ?: "سپر اجازه نداد اطلاعات کارت شما دزدیده شود.\nآدرس این درگاه جعلی است: ${domain ?: "ناشناس"}\nهرگز اطلاعات کارت بانکی خود را وارد نکنید."

        showRedOverlay(title, message)
        return START_NOT_STICKY
    }

    private fun showRedOverlay(title: String, message: String) {
        if (overlayView != null) return

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        val context = this
        val root = android.widget.LinearLayout(context).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setBackgroundColor(android.graphics.Color.parseColor("#E6B71B1B"))
            gravity = Gravity.CENTER
            setPadding(40, 40, 40, 40)
        }

        val titleView = TextView(context).apply {
            text = title
            textSize = 20f
            setTextColor(android.graphics.Color.WHITE)
            gravity = Gravity.CENTER
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val descView = TextView(context).apply {
            text = message
            textSize = 14f
            setTextColor(android.graphics.Color.WHITE)
            gravity = Gravity.CENTER
            setPadding(0, 20, 0, 40)
            setLineSpacing(10f, 1f)
        }

        val closeBtn = Button(context).apply {
            text = "متوجه شدم و بستن هشدار"
            setBackgroundColor(android.graphics.Color.WHITE)
            setTextColor(android.graphics.Color.parseColor("#B71B1B"))
            setPadding(40, 20, 40, 20)
            setOnClickListener {
                removeOverlay()
                stopSelf()
            }
        }

        root.addView(titleView)
        root.addView(descView)
        root.addView(closeBtn)

        overlayView = root

        try {
            windowManager?.addView(overlayView, params)
        } catch (_: Exception) {}
    }

    private fun removeOverlay() {
        if (overlayView != null) {
            try {
                windowManager?.removeView(overlayView)
            } catch (_: Exception) {}
            overlayView = null
        }
    }

    override fun onDestroy() {
        removeOverlay()
        super.onDestroy()
    }
}

package ir.ricksanchez.separ.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings

/**
 * مدیریت سازگاری همه‌جانبه با تمام برندهای اندروید (شیائومی، سامسونگ، هواوی و ...)
 * شناسایی خودکار برند و سیستم‌عامل و هدایت ۱ کلیکی به تنظیمات اختصاصی همان گوشی
 */
object OemPermissionHelper {

    enum class OemBrand(val displayNameFa: String) {
        XIAOMI("شیائومی (Xiaomi / POCO / Redmi)"),
        SAMSUNG("سامسونگ (Samsung)"),
        HUAWEI("هواوی / آنر (Huawei / Honor)"),
        OPPO_REALME_VIVO("اوپو / ریلمی / ویوو (Realme / Oppo)"),
        STOCK_GENERIC("اندروید استاندارد (Pixel / Nokia / Motorola)")
    }

    data class OemStep(
        val stepNumber: String,
        val textFa: String,
        val highlight: Boolean = false
    )

    data class OemGuide(
        val brand: OemBrand,
        val brandNameFa: String,
        val modelName: String,
        val titleFa: String,
        val steps: List<OemStep>,
        val voiceScript: String,
        val specialWarningFa: String? = null
    )

    /**
     * تشخیص هوشمند برند و کارخانه سازنده گوشی
     */
    fun detectBrand(): OemBrand {
        val manufacturer = Build.MANUFACTURER.lowercase()
        val brand = Build.BRAND.lowercase()

        return when {
            manufacturer.contains("xiaomi") || brand.contains("xiaomi") ||
                    manufacturer.contains("redmi") || brand.contains("redmi") ||
                    manufacturer.contains("poco") || brand.contains("poco") -> OemBrand.XIAOMI

            manufacturer.contains("samsung") || brand.contains("samsung") -> OemBrand.SAMSUNG

            manufacturer.contains("huawei") || brand.contains("huawei") ||
                    manufacturer.contains("honor") || brand.contains("honor") -> OemBrand.HUAWEI

            manufacturer.contains("oppo") || brand.contains("oppo") ||
                    manufacturer.contains("realme") || brand.contains("realme") ||
                    manufacturer.contains("vivo") || brand.contains("vivo") -> OemBrand.OPPO_REALME_VIVO

            else -> OemBrand.STOCK_GENERIC
        }
    }

    /**
     * دریافت مدل دقیق و نسخه اندروید برای نمایش به کاربر
     */
    fun getDeviceModelInfo(): String {
        return "${Build.BRAND.uppercase()} ${Build.MODEL} (اندروید ${Build.VERSION.RELEASE})"
    }

    /**
     * تولید راهنمای صوتی و تصویری فعال‌سازی سرویس دسترسی‌پذیری بر اساس مدل گوشی
     */
    fun getAccessibilityGuide(context: Context): OemGuide {
        val brand = detectBrand()
        val model = getDeviceModelInfo()

        return when (brand) {
            OemBrand.XIAOMI -> OemGuide(
                brand = brand,
                brandNameFa = brand.displayNameFa,
                modelName = model,
                titleFa = "راهنمای فعال‌سازی در گوشی‌های شیائومی",
                steps = listOf(
                    OemStep("۱", "روی دکمه سبز 'رفتن به تنظیمات' در پایین بزنید."),
                    OemStep("۲", "گزینه 'برنامه‌های دانلود شده' (Downloaded apps) را انتخاب کنید.", highlight = true),
                    OemStep("۳", "برنامه 'سپر' را پیدا کرده و کلید آن را روشن کنید."),
                    OemStep("۴", "در صورت نمایش هشدار امنیتی ۱۰ ثانیه‌ای شیائومی، تیک تأیید را بزنید و قبول کنید.")
                ),
                voiceScript = "گوشی شما شیائومی است. در صفحه بعد، روی برنامه‌های دانلود شده بزنید و کلید سپر را روشن کنید. اگر هشدار ده ثانیه‌ای آمد، آن را تأیید کنید.",
                specialWarningFa = if (Build.VERSION.SDK_INT >= 33) {
                    "نکته شیائومی در اندروید ۱۳ و ۱۴: اگر با پیام 'تنظیمات محدود شده' مواجه شدید، به اطلاعات برنامه سپر بروید، ۳ نقطه بالای صفحه را بزنید و گزینه 'مجاز کردن تنظیمات محدود شده' را فعال کنید."
                } else null
            )

            OemBrand.SAMSUNG -> OemGuide(
                brand = brand,
                brandNameFa = brand.displayNameFa,
                modelName = model,
                titleFa = "راهنمای فعال‌سازی در گوشی‌های سامسونگ",
                steps = listOf(
                    OemStep("۱", "روی دکمه سبز 'رفتن به تنظیمات' در پایین بزنید."),
                    OemStep("۲", "گزینه 'برنامه‌های نصب‌شده' (Installed apps) را انتخاب کنید.", highlight = true),
                    OemStep("۳", "برنامه 'سپر' را انتخاب کرده و کلید فعال‌سازی را روشن کنید."),
                    OemStep("۴", "دکمه 'اجازه دادن' (Allow) را لمس کنید.")
                ),
                voiceScript = "گوشی شما سامسونگ است. در صفحه بعد، گزینه برنامه‌های نصب شده را انتخاب کنید و سپر را روشن کنید."
            )

            OemBrand.HUAWEI -> OemGuide(
                brand = brand,
                brandNameFa = brand.displayNameFa,
                modelName = model,
                titleFa = "راهنمای فعال‌سازی در گوشی‌های هواوی و آنر",
                steps = listOf(
                    OemStep("۱", "روی دکمه سبز 'رفتن به تنظیمات' در پایین بزنید."),
                    OemStep("۲", "گزینه 'سرویس‌های دانلود شده' یا 'دسترسی‌پذیری' را انتخاب کنید.", highlight = true),
                    OemStep("۳", "گزینه 'سپر' را لمس کرده و آن را روشن کنید.")
                ),
                voiceScript = "گوشی شما هواوی است. در صفحه تنظیمات، گزینه سپر را در بخش دسترسی‌پذیری پیدا کرده و روشن کنید."
            )

            OemBrand.OPPO_REALME_VIVO -> OemGuide(
                brand = brand,
                brandNameFa = brand.displayNameFa,
                modelName = model,
                titleFa = "راهنمای فعال‌سازی در گوشی‌های ریلمی و اوپو",
                steps = listOf(
                    OemStep("۱", "وارد تنظیمات دسترسی‌پذیری شوید."),
                    OemStep("۲", "برنامه 'سپر' را پیدا کرده و کلید آن را روشن کنید."),
                    OemStep("۳", "پنجره تأیید دسترسی را تأیید کنید.")
                ),
                voiceScript = "در صفحه بعد، برنامه سپر را انتخاب کرده و کلید روشن شدن را بزنید."
            )

            OemBrand.STOCK_GENERIC -> OemGuide(
                brand = brand,
                brandNameFa = brand.displayNameFa,
                modelName = model,
                titleFa = "راهنمای فعال‌سازی در اندروید",
                steps = listOf(
                    OemStep("۱", "روی دکمه 'رفتن به تنظیمات' بزنید."),
                    OemStep("۲", "گزینه 'سپر' را در لیست پیدا کنید."),
                    OemStep("۳", "کلید استفاده از سپر را روشن کنید.")
                ),
                voiceScript = "در صفحه تنظیمات، سپر را پیدا کرده و کلید آن را روشن کنید."
            )
        }
    }

    /**
     * باز کردن مستقیم صفحه تنظیمات اختصاصی سازنده برای شروع خودکار (Autostart)
     */
    fun openOemAutostartSettings(context: Context): Boolean {
        val brand = detectBrand()

        val intentList = when (brand) {
            OemBrand.XIAOMI -> listOf(
                Intent().setComponent(ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
                Intent("miui.intent.action.OP_AUTO_START").addCategory(Intent.CATEGORY_DEFAULT)
            )

            OemBrand.HUAWEI -> listOf(
                Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")),
                Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.bootstart.BootStartActivity"))
            )

            OemBrand.OPPO_REALME_VIVO -> listOf(
                Intent().setComponent(ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
                Intent().setComponent(ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
                Intent().setComponent(ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"))
            )

            OemBrand.SAMSUNG -> listOf(
                Intent().setComponent(ComponentName("com.samsung.android.lool", "com.samsung.android.sm.battery.ui.BatteryActivity")),
                Intent().setComponent(ComponentName("com.samsung.android.sm", "com.samsung.android.sm.battery.ui.BatteryActivity"))
            )

            OemBrand.STOCK_GENERIC -> emptyList()
        }

        for (intent in intentList) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return true
            } catch (_: Exception) {}
        }

        // باز کردن صفحه جزئیات برنامه به عنوان راهکار پیش‌فرض
        return try {
            val appDetailsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(appDetailsIntent)
            true
        } catch (_: Exception) {
            false
        }
    }
}

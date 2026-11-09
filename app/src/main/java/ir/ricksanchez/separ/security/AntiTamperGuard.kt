package ir.ricksanchez.separ.security

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Debug
import android.os.Process
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.net.Socket
import java.security.MessageDigest

/**
 * گارد جامع ضد دستکاری، ضد مود، ضد هوک (Anti-Frida / Anti-Xposed) و اعتبارسنجی یکپارچگی APK
 */
object AntiTamperGuard {

    // اثر انگشت SHA-256 مجاز کلید امضای رسمی ریک سانچز (در بیلد ریلیز نهایی مقایسه می‌شود)
    // اگر مودر فایل APK را دستکاری و با کلید خودش امضا کند، هش تغییر کرده و برنامه قفل می‌شود
    private const val OFFICIAL_RELEASE_SIGNATURE_SHA256 = "A3F02E0EF46BF5C246F0AAFFC8E10934AEF808A1FF0864515DDBBAF04C8C5454"

    data class TamperCheckResult(
        val isTampered: Boolean,
        val reason: String?
    )

    /**
     * اجرای تست‌های امنیتی چندلایه برای کشف محیط‌های کرک و ابزارهای هوک
     */
    fun verifyAppIntegrity(context: Context): TamperCheckResult {
        // ۱. بررسی اتصال دیباگر فعال
        if (Debug.isDebuggerConnected() || Debug.waitingForDebugger()) {
            return TamperCheckResult(true, "اتصال دیباگر فعال شناسایی شد.")
        }

        // ۲. بررسی فرآیند TracerPid در سیستم‌عامل لینوکس (Anti-Ptrace)
        if (isTracedByDebugger()) {
            return TamperCheckResult(true, "رهگیری پروسس توسط ابزار مهندسی معکوس شناسایی شد.")
        }

        // ۳. بررسی حضور ابزارهای هوک و تزریق کد پویا (Frida / Xposed / LSPosed)
        if (isHookingFrameworkLoaded()) {
            return TamperCheckResult(true, "ابزارهای تزریق کد (Frida/Xposed) شناسایی شدند.")
        }

        // ۴. بررسی پورت‌های سرور فریدا (Frida Server Ports)
        if (isFridaServerListening()) {
            return TamperCheckResult(true, "سرور Frida فعال روی دستگاه شناسایی شد.")
        }

        // ۵. بررسی امضای دیجیتال بسته و کشف ری‌ساین شدن (Resigned APK)
        val sigCheck = verifyCertificateSignature(context)
        if (sigCheck.isTampered) {
            return sigCheck
        }

        return TamperCheckResult(false, null)
    }

    private fun isTracedByDebugger(): Boolean {
        return try {
            val reader = BufferedReader(FileReader("/proc/self/status"))
            var line: String?
            var isTraced = false
            while (reader.readLine().also { line = it } != null) {
                if (line?.startsWith("TracerPid:") == true) {
                    val pidStr = line?.substring(10)?.trim()
                    val pid = pidStr?.toIntOrNull() ?: 0
                    if (pid > 0) isTraced = true
                    break
                }
            }
            reader.close()
            isTraced
        } catch (_: Exception) {
            false
        }
    }

    private fun isHookingFrameworkLoaded(): Boolean {
        return try {
            val reader = BufferedReader(FileReader("/proc/self/maps"))
            var line: String?
            var hookFound = false
            val dangerousLibraries = listOf(
                "frida",
                "gadget",
                "xposed",
                "lsposed",
                "substrate",
                "edxposed",
                "sandhook"
            )

            while (reader.readLine().also { line = it } != null) {
                val lower = line?.lowercase() ?: ""
                if (dangerousLibraries.any { lower.contains(it) }) {
                    hookFound = true
                    break
                }
            }
            reader.close()
            hookFound
        } catch (_: Exception) {
            false
        }
    }

    private fun isFridaServerListening(): Boolean {
        return try {
            // پورت‌های پیش‌فرض ارتباط Frida Server
            val socket = Socket("127.0.0.1", 27042)
            socket.close()
            true
        } catch (_: Exception) {
            try {
                val socket = Socket("127.0.0.1", 27043)
                socket.close()
                true
            } catch (_: Exception) {
                false
            }
        }
    }

    private fun verifyCertificateSignature(context: Context): TamperCheckResult {
        return try {
            val pm = context.packageManager
            val pkgName = context.packageName

            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val signingInfo = pm.getPackageInfo(pkgName, PackageManager.GET_SIGNING_CERTIFICATES).signingInfo
                signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(pkgName, PackageManager.GET_SIGNATURES).signatures
            }

            if (signatures.isNullOrEmpty()) {
                return TamperCheckResult(false, null)
            }

            val certBytes = signatures[0].toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val currentCertHash = md.digest(certBytes).joinToString("") { "%02X".format(it) }

            // در صورتی که کلید رسمی تنظیم شده باشد و با کلید فعلی نخواند، برنامه ری‌ساین شده است
            if (OFFICIAL_RELEASE_SIGNATURE_SHA256 != "AUTHORIZED_RICK_SANCHEZ_SIGNATURE_HASH_HERE" &&
                !currentCertHash.equals(OFFICIAL_RELEASE_SIGNATURE_SHA256, ignoreCase = true)
            ) {
                return TamperCheckResult(true, "امضای دیجیتال برنامه تغییر یافته و فایل دستکاری شده است.")
            }

            TamperCheckResult(false, null)
        } catch (_: Exception) {
            TamperCheckResult(false, null)
        }
    }
}

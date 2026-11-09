package ir.ricksanchez.separ.utils

import android.content.Context
import android.content.pm.PackageManager
import java.io.File
import java.security.MessageDigest

/**
 * ابزارهای کار با پکیج‌ها و محاسبه هش باینری برای شناسایی بدافزارها
 */
object PackageUtils {

    fun calculateApkSha256(apkPath: String): String? {
        return try {
            val file = File(apkPath)
            if (!file.exists()) return null
            val digest = MessageDigest.getInstance("SHA-256")
            file.inputStream().use { stream ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (stream.read(buffer).also { bytesRead = it } != -1) {
                    digest.update(buffer, 0, bytesRead)
                }
            }
            digest.digest().joinToString("") { "%02x".format(it) }
        } catch (_: Exception) {
            null
        }
    }

    fun isSystemApp(context: Context, packageName: String): Boolean {
        return try {
            val pm = context.packageManager
            pm.checkSignatures(packageName, "android") == PackageManager.SIGNATURE_MATCH
        } catch (_: Exception) {
            false
        }
    }
}

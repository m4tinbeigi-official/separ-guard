package ir.ricksanchez.separ.security

/**
 * سیستم رمزنگاری و پنهان‌سازی رشته‌های کلیدی در حافظه (Runtime String Obfuscation)
 * برای جلوگیری از کشف کلیدواژه‌ها، متغیرهای اشتراک و شناسه‌ها توسط ابزارهای دیکامپایل (JADX و APKTool)
 */
object ObfuscatedStrings {

    private const val DEFAULT_XOR_KEY = 0x5A.toByte()

    /**
     * رمزگشایی در حافظه با عملیات بیتی XOR
     */
    fun decrypt(bytes: ByteArray, key: Byte = DEFAULT_XOR_KEY): String {
        val result = ByteArray(bytes.size)
        for (i in bytes.indices) {
            result[i] = (bytes[i].toInt() xor key.toInt()).toByte()
        }
        return String(result, Charsets.UTF_8)
    }

    // آرایه‌های بایت رمزنگاری‌شده با کلید 0x5A
    // مقدار دیکدشده: "separ_annual_subscription"
    val SKU_ANNUAL: ByteArray = byteArrayOf(
        0x29.toByte(), 0x3F.toByte(), 0x2A.toByte(), 0x3B.toByte(), 0x28.toByte(), 0x05.toByte(),
        0x3B.toByte(), 0x34.toByte(), 0x34.toByte(), 0x2F.toByte(), 0x3B.toByte(), 0x36.toByte(),
        0x05.toByte(), 0x29.toByte(), 0x2F.toByte(), 0x38.toByte(), 0x29.toByte(), 0x39.toByte(),
        0x28.toByte(), 0x33.toByte(), 0x2A.toByte(), 0x2E.toByte(), 0x33.toByte(), 0x35.toByte(),
        0x34.toByte()
    )

    // مقدار دیکدشده: "is_subscribed"
    val KEY_SUB_STATE: ByteArray = byteArrayOf(
        0x33.toByte(), 0x29.toByte(), 0x05.toByte(), 0x29.toByte(), 0x2F.toByte(), 0x38.toByte(),
        0x29.toByte(), 0x39.toByte(), 0x28.toByte(), 0x33.toByte(), 0x38.toByte(), 0x3F.toByte(),
        0x3E.toByte()
    )

    // مقدار دیکدشده: "com.velora.cloud"
    val THREAT_VELORA_PKG: ByteArray = byteArrayOf(
        0x39.toByte(), 0x35.toByte(), 0x37.toByte(), 0x74.toByte(), 0x2C.toByte(), 0x3F.toByte(),
        0x36.toByte(), 0x35.toByte(), 0x28.toByte(), 0x3B.toByte(), 0x74.toByte(), 0x39.toByte(),
        0x36.toByte(), 0x35.toByte(), 0x2F.toByte(), 0x3E.toByte()
    )
}

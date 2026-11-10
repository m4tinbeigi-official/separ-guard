package ir.ricksanchez.separ.security;

/**
 * سیستم مبهم‌سازی رشته‌ها در سطح حافظه (In-Memory XOR String Obfuscator)
 * جلوگیری از کشف متن‌ها و شناسه‌ها توسط ابزارهای تحلیل استاتیک نظیر strings، JADX، Ghidra و IDA Pro
 */
public class StringObfuscator {

    private static final byte MASTER_XOR_KEY = 0x5C;

    /**
     * رمزگشایی در حافظه رم هنگام فراخوانی (Zero Static Visibility)
     */
    public static String decrypt(byte[] encryptedBytes) {
        if (encryptedBytes == null || encryptedBytes.length == 0) return "";
        char[] chars = new char[encryptedBytes.length];
        for (int i = 0; i < encryptedBytes.length; i++) {
            chars[i] = (char) (encryptedBytes[i] ^ MASTER_XOR_KEY);
        }
        return new String(chars);
    }

    /**
     * رمزنگاری رشته برای تولید آرایه‌های بایت در زمان کامپایل
     */
    public static byte[] encrypt(String clearText) {
        if (clearText == null) return new byte[0];
        byte[] bytes = new byte[clearText.length()];
        for (int i = 0; i < clearText.length(); i++) {
            bytes[i] = (byte) (clearText.charAt(i) ^ MASTER_XOR_KEY);
        }
        return bytes;
    }
}

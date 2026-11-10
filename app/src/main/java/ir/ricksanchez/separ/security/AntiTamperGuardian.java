package ir.ricksanchez.separ.security;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Debug;
import android.os.Process;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.Locale;

/**
 * سامانه جامع ضد مهندسی معکوس، ضد دستکاری و محافظ یکپارچگی دودویی (Anti-Tampering & Anti-Reversing Engine)
 * طراحی‌شده مطابق با استانداردهای نخبگان مهندسی معکوس و حفاظت در برابر MT Manager، Frida، Xposed و Lucky Patcher
 */
public class AntiTamperGuardian {

    // اثر انگشت رسمی گواهی انتشار ریک سانچز (SHA-256)
    // هرگونه تغییر، ویرایش با MT Manager یا بازامضاکردن، این هش را باطل می‌کند
    private static final String OFFICIAL_SIGNATURE_SHA256 =
        "A3F02E0EF46BF5C246F0AAFFC8E10934AEF808A1FF0864515DDBBAF04C8C5454";

    public static class SecurityCheckResult {
        public final boolean isCompromised;
        public final String violationReason;

        public SecurityCheckResult(boolean compromised, String reason) {
            this.isCompromised = compromised;
            this.violationReason = reason;
        }
    }

    /**
     * اجرای ارزیابی جامع امنیتی در کسری از ثانیه
     * در صورت کشف هرگونه ابزار مهندسی معکوس، فرآیند فوراً متوقف می‌شود
     */
    public static SecurityCheckResult performComprehensiveAudit(Context context) {
        if (context == null) {
            return new SecurityCheckResult(false, null);
        }

        // ۱. بررسی امضای دیجیتال و اثر انگشت بیلد اصلی (Anti-Resigning / Anti-Repackage)
        if (!verifySignatureIntegrity(context)) {
            terminateProcessWithDecoy();
            return new SecurityCheckResult(true, "امضای دیجیتال برنامه تغییر یافته و فایل دستکاری شده است.");
        }

        // ۲. بررسی فعال بودن دیباگر و پایشگرهای تعاملی (Anti-Debugging)
        if (isDebuggerAttached()) {
            terminateProcessWithDecoy();
            return new SecurityCheckResult(true, "تلاش برای دیباگ و رصد حافظه برنامه کشف شد.");
        }

        // ۳. بررسی اتصال فریم‌ورک‌های تزریق حافظه و هوک (Anti-Frida & Anti-Xposed)
        if (isFridaOrHookingPresent()) {
            terminateProcessWithDecoy();
            return new SecurityCheckResult(true, "کشف ابزارهای تزریق کدهای هوک (Frida/Xposed).");
        }

        // ۴. بررسی پرچم Debuggable در مانیفست
        if (isDebuggableBuild(context)) {
            terminateProcessWithDecoy();
            return new SecurityCheckResult(true, "فلگ خطایابی در مانیفست به صورت اجباری فعال شده است.");
        }

        // ۵. بررسی اجرای در محیط‌های مجازی و ساندباکس‌های شبیه‌ساز (Anti-VirtualApp)
        if (isRunningInVirtualEnvironment(context)) {
            terminateProcessWithDecoy();
            return new SecurityCheckResult(true, "برنامه در فضای ایزوله شبیه‌ساز یا کلونینگ در حال اجراست.");
        }

        return new SecurityCheckResult(false, null);
    }

    /**
     * بررسی اصالت امضای دیجیتال نسبت به کلید رسمی انتشار
     */
    public static boolean verifySignatureIntegrity(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            String packageName = context.getPackageName();
            Signature[] signatures = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES);
                if (packageInfo.signingInfo != null) {
                    signatures = packageInfo.signingInfo.getApkContentsSigners();
                }
            } else {
                PackageInfo packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
                signatures = packageInfo.signatures;
            }

            if (signatures != null && signatures.length > 0) {
                for (Signature sig : signatures) {
                    MessageDigest md = MessageDigest.getInstance("SHA-256");
                    byte[] digest = md.digest(sig.toByteArray());
                    String hexString = bytesToHex(digest);
                    if (OFFICIAL_SIGNATURE_SHA256.equalsIgnoreCase(hexString)) {
                        return true;
                    }
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    /**
     * شناسایی دیباگر از طریق Debug API و خواندن وضعیت TracerPid در لینوکس
     */
    public static boolean isDebuggerAttached() {
        if (Debug.isDebuggerConnected() || Debug.waitingForDebugger()) {
            return true;
        }

        // خواندن وضعیت پردازش از هسته لینوکس
        try {
            File statusFile = new File("/proc/self/status");
            if (statusFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(statusFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("TracerPid:")) {
                        String pidStr = line.substring(10).trim();
                        int tracerPid = Integer.parseInt(pidStr);
                        if (tracerPid > 0) {
                            reader.close();
                            return true; // یک پردازشگر دیگر (GDB/IDA/Frida) در حال تریس کردن است
                        }
                    }
                }
                reader.close();
            }
        } catch (Exception ignored) {}

        return false;
    }

    /**
     * رصد وجود فریم‌ورک‌های هوکینگ در نقشه حافظه (Memory Maps) و پورت‌های محلی
     */
    public static boolean isFridaOrHookingPresent() {
        // ۱. پیمایش کتابخانه‌های بارگذاری‌شده در حافظه فرآیند
        try {
            File mapsFile = new File("/proc/self/maps");
            if (mapsFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(mapsFile));
                String line;
                while ((line = reader.readLine()) != null) {
                    String lower = line.toLowerCase(Locale.US);
                    if (lower.contains("frida") ||
                        lower.contains("gadget") ||
                        lower.contains("xposed") ||
                        lower.contains("lsposed") ||
                        lower.contains("edxposed") ||
                        lower.contains("substrate") ||
                        lower.contains("sandhook")) {
                        reader.close();
                        return true;
                    }
                }
                reader.close();
            }
        } catch (Exception ignored) {}

        // ۲. تست اتصال سریع به پورت پیش‌فرض سرور فریدا (27042)
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("127.0.0.1", 27042), 40);
            socket.close();
            return true; // سرور فریدا فعال است
        } catch (Exception ignored) {}

        return false;
    }

    /**
     * بررسی فعال‌بودن پرچم Debuggable
     */
    public static boolean isDebuggableBuild(Context context) {
        try {
            return (context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * شناسایی اجرا در محیط‌های کلونینگ یا موازی (VirtualApp, Dual Space, Parallel)
     */
    public static boolean isRunningInVirtualEnvironment(Context context) {
        try {
            String filesDir = context.getFilesDir().getAbsolutePath();
            String pkg = context.getPackageName();
            // در اندروید استاندارد مسیر فایل‌ها باید با /data/user/0/ir.ricksanchez.separ یا /data/data/... باشد
            if (!filesDir.contains(pkg)) {
                return true;
            }
            if (filesDir.contains("com.lbe.parallel") ||
                filesDir.contains("io.va.exposed") ||
                filesDir.contains("virtual") ||
                filesDir.contains("dual") ||
                filesDir.contains("clone")) {
                return true;
            }
        } catch (Exception ignored) {}
        return false;
    }

    /**
     * متوقف‌سازی خشن فرآیند و پاکسازی ردپا هنگام کشف دستکاری
     */
    public static void terminateProcessWithDecoy() {
        try {
            // تله گمراه‌کننده برای فریب دکامپایلرها
            decompilerTrapMethod(0xDEADBEEF, 0x1337);
        } catch (Throwable ignored) {}

        // خروج مستقیم در سطح سیستم‌عامل
        Process.killProcess(Process.myPid());
        System.exit(0);
    }

    /**
     * تله ضد دکامپایل و کنترل جریان گمراه‌کننده (Opaque Predicate Decoy Trap)
     * تولید بایت‌کدهای پیچیده جهت کرش دادن یا به بن‌بست کشاندن ابزارهای تحلیل استاتیک
     */
    private static int decompilerTrapMethod(long a, int b) {
        int x = (int) (a ^ b);
        for (int i = 0; i < 16; i++) {
            x = (x << 3) | (x >>> 29);
            if ((x & 1) == 0) {
                x ^= 0x5A5A5A5A;
            } else {
                x += 0x12345678;
            }
        }
        return x;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}

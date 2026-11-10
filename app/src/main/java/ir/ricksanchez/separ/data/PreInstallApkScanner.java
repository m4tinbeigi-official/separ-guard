package ir.ricksanchez.separ.data;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * اسکنر فایل‌های نصبی (APK) دانلودشده در پوشه Download و پیام‌رسان‌ها (تلگرام، روبیکا، ایتا، بله)
 * این ماژول قبل از اینکه کاربر روی فایل نصبی بزند، بدافزار بودن آن را در حافظه استخراج و کشف می‌کند.
 */
public class PreInstallApkScanner {

    public static class DangerousApkFile {
        public final String filePath;
        public final String fileName;
        public final String packageName;
        public final String threatName;
        public final String threatDescription;
        public final long fileSizeBytes;
        public final boolean isCritical;

        public DangerousApkFile(String path, String name, String pkg, String threat, String desc, long size, boolean crit) {
            this.filePath = path;
            this.fileName = name;
            this.packageName = pkg;
            this.threatName = threat;
            this.threatDescription = desc;
            this.fileSizeBytes = size;
            this.isCritical = crit;
        }

        public String getFormattedSize() {
            if (fileSizeBytes < 1024) return fileSizeBytes + " B";
            int z = (63 - Long.numberOfLeadingZeros(fileSizeBytes)) / 10;
            return String.format("%.1f %sB", (double) fileSizeBytes / (1L << (z * 10)), " KMGTPE".charAt(z));
        }
    }

    public static List<DangerousApkFile> scanDownloadedApks(Context context) {
        List<DangerousApkFile> dangerousApks = new ArrayList<>();
        PackageManager pm = context.getPackageManager();

        List<File> targetDirectories = getCandidateDownloadDirs();
        Set<String> scannedPaths = new HashSet<>();

        for (File dir : targetDirectories) {
            if (dir == null || !dir.exists() || !dir.isDirectory()) continue;
            File[] files = dir.listFiles();
            if (files == null) continue;

            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".apk")) {
                    String path = file.getAbsolutePath();
                    if (scannedPaths.contains(path)) continue;
                    scannedPaths.add(path);

                    DangerousApkFile threat = evaluateApkFile(pm, file);
                    if (threat != null) {
                        dangerousApks.add(threat);
                    }
                }
            }
        }

        return dangerousApks;
    }

    private static DangerousApkFile evaluateApkFile(PackageManager pm, File apkFile) {
        try {
            PackageInfo info = pm.getPackageArchiveInfo(apkFile.getAbsolutePath(), PackageManager.GET_PERMISSIONS);
            if (info == null || info.packageName == null) {
                // فایلی که نامش بدافزاری است اما آسیب دیده
                String name = apkFile.getName().toLowerCase();
                if (name.contains("yadegari") || name.contains("velora") || name.contains("sana") || name.contains("edalat") || name.contains("sighe")) {
                    return new DangerousApkFile(
                        apkFile.getAbsolutePath(),
                        apkFile.getName(),
                        "unknown.corrupted.malware",
                        "فایل نصبی مشکوک و مخرب با عنوان تله",
                        "این فایل نصبی حاوی نام کلیدواژه‌های تله بدافزارهای بانکی است.",
                        apkFile.length(),
                        true
                    );
                }
                return null;
            }

            String pkg = info.packageName.toLowerCase();

            // ۱. بررسی قطعی در رجیستری تهدیدات
            ThreatPackageRegistry.ThreatResult tr = ThreatPackageRegistry.evaluatePackage(pkg);
            if (tr != null) {
                return new DangerousApkFile(
                    apkFile.getAbsolutePath(),
                    apkFile.getName(),
                    pkg,
                    tr.threatName,
                    tr.description,
                    apkFile.length(),
                    tr.isCritical
                );
            }

            // ۲. بررسی کلیدواژه‌های فیشینگ در نام بسته و فایل
            String fileNameLower = apkFile.getName().toLowerCase();
            if (pkg.contains("yadegari") || pkg.contains("velora") || pkg.contains("sigheh") || pkg.contains("sana")
                || fileNameLower.contains("yadegari") || fileNameLower.contains("velora") || fileNameLower.contains("sighe")) {
                return new DangerousApkFile(
                    apkFile.getAbsolutePath(),
                    apkFile.getName(),
                    pkg,
                    "بدافزار آلبوم یادگاری / صیغه‌یاب (دانلودشده)",
                    "این فایل نصبی یک تروجان شنود پیامک بانکی است که از تلگرام یا اینترنت دریافت شده است.",
                    apkFile.length(),
                    true
                );
            }

            // ۳. دسترسی‌های خطرناک پیامک در برنامه‌های نامعتبر
            if (info.requestedPermissions != null) {
                boolean hasSms = false;
                for (String p : info.requestedPermissions) {
                    if (p.contains("RECEIVE_SMS") || p.contains("READ_SMS") || p.contains("SEND_SMS")) {
                        hasSms = true;
                        break;
                    }
                }
                if (hasSms && !TrustedWhitelistRegistry.isWhitelisted(pkg)) {
                    return new DangerousApkFile(
                        apkFile.getAbsolutePath(),
                        apkFile.getName(),
                        pkg,
                        "فایل نصبی ناشناس با دسترسی شنود پیامک",
                        "این برنامه قبل از نصب درخواست شنود رمزهای پیامکی دارد و باید فوراً پاک شود.",
                        apkFile.length(),
                        true
                    );
                }
            }
        } catch (Exception ignored) {}
        return null;
    }

    public static boolean deleteDangerousApk(String filePath) {
        if (filePath == null) return false;
        try {
            File f = new File(filePath);
            if (f.exists()) {
                return f.delete();
            }
        } catch (Exception ignored) {}
        return false;
    }

    private static List<File> getCandidateDownloadDirs() {
        List<File> list = new ArrayList<>();

        // پوشه رسمی دانلود
        try {
            File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (downloadDir != null) list.add(downloadDir);
        } catch (Exception ignored) {}

        // مسیرهای رایج حافظه داخلی
        String base = Environment.getExternalStorageDirectory().getAbsolutePath();
        list.add(new File(base, "Download"));
        list.add(new File(base, "Downloads"));
        list.add(new File(base, "Telegram/Telegram Documents"));
        list.add(new File(base, "Telegram"));
        list.add(new File(base, "Rubika"));
        list.add(new File(base, "Eitaa"));
        list.add(new File(base, "Bale"));
        list.add(new File(base, "WhatsApp/Media/WhatsApp Documents"));

        return list;
    }
}

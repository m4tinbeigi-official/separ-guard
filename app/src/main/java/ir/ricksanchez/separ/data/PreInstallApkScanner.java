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
 * اسکنر هوشمند و عمیق فایل‌های نصبی (APK) دانلودشده در پوشه Download و پیام‌رسان‌ها (تلگرام، روبیکا، ایتا، بله)
 * این ماژول پوشه‌ها و زیرپوشه‌ها را تا عمق ۳ سطح به صورت بازگشتی جستجو کرده و قبل از اقدام به نصب، بدافزار را کشف می‌کند.
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

    private static final String[] PERSIAN_LURE_KEYWORDS = {
        "یادگاری", "البوم", "آلبوم", "صیغه", "صیغه‌یاب", "همدم", "سهام", "عدالت", "سود",
        "ابلاغیه", "ثنا", "عدلیران", "وام", "تسهیلات", "کالابرگ", "یارانه", "عروسی", "کارت",
        "پوشه", "velora", "yadegari", "sigheh", "sighe", "sana", "edalat", "vamchi"
    };

    public static List<DangerousApkFile> scanDownloadedApks(Context context) {
        List<DangerousApkFile> dangerousApks = new ArrayList<>();
        if (context == null) return dangerousApks;

        PackageManager pm = context.getPackageManager();
        List<File> targetDirectories = getCandidateDownloadDirs();
        Set<String> scannedPaths = new HashSet<>();

        for (File dir : targetDirectories) {
            scanDirectoryRecursively(pm, dir, 0, 3, scannedPaths, dangerousApks);
        }

        return dangerousApks;
    }

    private static void scanDirectoryRecursively(PackageManager pm, File dir, int currentDepth, int maxDepth,
                                                Set<String> scannedPaths, List<DangerousApkFile> results) {
        if (dir == null || !dir.exists() || !dir.isDirectory() || currentDepth > maxDepth) return;

        File[] files = null;
        try {
            files = dir.listFiles();
        } catch (Exception ignored) {}
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectoryRecursively(pm, file, currentDepth + 1, maxDepth, scannedPaths, results);
            } else if (file.isFile() && file.getName().toLowerCase().endsWith(".apk")) {
                String path = file.getAbsolutePath();
                if (scannedPaths.contains(path)) continue;
                scannedPaths.add(path);

                DangerousApkFile threat = evaluateApkFile(pm, file);
                if (threat != null) {
                    results.add(threat);
                }
            }
        }
    }

    private static DangerousApkFile evaluateApkFile(PackageManager pm, File apkFile) {
        String fileName = apkFile.getName();
        String fileNameLower = fileName.toLowerCase();

        // بررسی ۱: آیا نام فایل حاوی کلیدواژه‌های تله فیشینگ و بدافزاری است؟
        boolean hasLureFileName = false;
        String detectedLureWord = "";
        for (String kw : PERSIAN_LURE_KEYWORDS) {
            if (fileNameLower.contains(kw.toLowerCase())) {
                hasLureFileName = true;
                detectedLureWord = kw;
                break;
            }
        }

        PackageInfo info = null;
        try {
            info = pm.getPackageArchiveInfo(apkFile.getAbsolutePath(), PackageManager.GET_PERMISSIONS);
        } catch (Exception ignored) {}

        if (info == null || info.packageName == null) {
            try {
                info = pm.getPackageArchiveInfo(apkFile.getAbsolutePath(), 0);
            } catch (Exception ignored) {}
        }

        if (info == null || info.packageName == null) {
            // فایلی که نامش بدافزاری است اما هدرهایش دستکاری شده یا آسیب دیده است
            if (hasLureFileName) {
                return new DangerousApkFile(
                    apkFile.getAbsolutePath(),
                    fileName,
                    "malware.corrupted." + detectedLureWord,
                    "بدافزار فیشینگ استتاریافته («" + fileName + "»)",
                    "این فایل نصبی یک تروجان مخرب شنود پیامک است که هدرهای آن برای فرار از آنتی‌ویروس‌ها دستکاری شده است.",
                    apkFile.length(),
                    true
                );
            }
            return null;
        }

        String pkg = info.packageName.toLowerCase();

        // بررسی ۲: بررسی قطعی در رجیستری تهدیدات کشوری (مثل com.velora.cloud)
        ThreatPackageRegistry.ThreatResult tr = ThreatPackageRegistry.evaluatePackage(pkg);
        if (tr != null) {
            return new DangerousApkFile(
                apkFile.getAbsolutePath(),
                fileName,
                pkg,
                tr.threatName,
                tr.description,
                apkFile.length(),
                tr.isCritical
            );
        }

        // بررسی ۳: بررسی نام بسته یا فایل با کلیدواژه‌های تله
        if (hasLureFileName || pkg.contains("velora") || pkg.contains("yadegari") || pkg.contains("sigheh") || pkg.contains("sana")) {
            return new DangerousApkFile(
                apkFile.getAbsolutePath(),
                fileName,
                pkg,
                "بدافزار آلبوم یادگاری / ولورا (دانلودشده)",
                "این فایل نصبی یک تروجان سارق رمزهای پویا و شنود پیامک‌های بانکی است که از تلگرام یا وب دانلود شده است.",
                apkFile.length(),
                true
            );
        }

        // بررسی ۴: دسترسی‌های خطرناک پیامک در برنامه‌های غیررسمی
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
                    fileName,
                    pkg,
                    "فایل نصبی ناشناس با دسترسی شنود پیامک",
                    "این برنامه قبل از نصب درخواست شنود رمزهای پیامکی دارد و باید فوراً پاک شود.",
                    apkFile.length(),
                    true
                );
            }
        }

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

        // پوشه رسمی دانلود استاندارد
        try {
            File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (downloadDir != null && downloadDir.exists()) {
                list.add(downloadDir);
                // زیرپوشه تلگرام در دانلود اندروید ۱۱ به بعد
                File tgInDownload = new File(downloadDir, "Telegram");
                if (tgInDownload.exists()) list.add(tgInDownload);
                File tgDocsInDownload = new File(downloadDir, "Telegram Documents");
                if (tgDocsInDownload.exists()) list.add(tgDocsInDownload);
            }
        } catch (Exception ignored) {}

        // مسیرهای رایج حافظه داخلی
        try {
            File base = Environment.getExternalStorageDirectory();
            if (base != null && base.exists()) {
                list.add(new File(base, "Download"));
                list.add(new File(base, "Downloads"));
                list.add(new File(base, "Download/Telegram"));
                list.add(new File(base, "Download/Telegram Documents"));
                list.add(new File(base, "Telegram"));
                list.add(new File(base, "Telegram/Telegram Documents"));
                list.add(new File(base, "Rubika"));
                list.add(new File(base, "Eitaa"));
                list.add(new File(base, "Bale"));
                list.add(new File(base, "WhatsApp/Media/WhatsApp Documents"));
                list.add(new File(base, "Android/data/org.telegram.messenger/files/Telegram/Telegram Documents"));
            }
        } catch (Exception ignored) {}

        return list;
    }
}

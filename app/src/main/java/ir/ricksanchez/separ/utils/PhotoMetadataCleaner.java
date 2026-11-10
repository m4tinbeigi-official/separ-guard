package ir.ricksanchez.separ.utils;

import android.content.Context;
import android.media.ExifInterface;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * ابزار پاک‌سازی هوشمند متادیتا و اطلاعات مکانی (GPS) عکس‌ها قبل از اشتراک‌گذاری در شبکه‌های اجتماعی
 * جلوگیری از ردیابی مکان زندگی، مدل گوشی و حریم خصوصی کاربر
 */
public class PhotoMetadataCleaner {

    public static class PhotoExifAuditResult {
        public final String filePath;
        public final String fileName;
        public final boolean hasGpsLocation;
        public final String cameraModel;
        public final String dateTime;
        public final long fileSizeBytes;

        public PhotoExifAuditResult(String filePath, String fileName, boolean hasGpsLocation,
                                    String cameraModel, String dateTime, long fileSizeBytes) {
            this.filePath = filePath;
            this.fileName = fileName;
            this.hasGpsLocation = hasGpsLocation;
            this.cameraModel = cameraModel;
            this.dateTime = dateTime;
            this.fileSizeBytes = fileSizeBytes;
        }
    }

    /**
     * بررسی اینکه آیا یک فایل عکس دارای اطلاعات مختصات جغرافیایی (GPS) است
     */
    public static boolean hasGpsLocation(String filePath) {
        if (filePath == null) return false;
        try {
            ExifInterface exif = new ExifInterface(filePath);
            float[] latLong = new float[2];
            return exif.getLatLong(latLong);
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * واکاوی جزئیات متادیتای یک تصویر
     */
    public static PhotoExifAuditResult auditPhotoFile(File file) {
        if (file == null || !file.exists() || !file.isFile()) return null;
        try {
            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
            float[] latLong = new float[2];
            boolean hasGps = exif.getLatLong(latLong);
            String model = exif.getAttribute(ExifInterface.TAG_MODEL);
            String date = exif.getAttribute(ExifInterface.TAG_DATETIME);

            return new PhotoExifAuditResult(
                file.getAbsolutePath(),
                file.getName(),
                hasGps,
                model != null ? model : "نامشخص",
                date != null ? date : "نامشخص",
                file.length()
            );
        } catch (Exception ignored) {
            return null;
        }
    }

    /**
     * حذف کامل اطلاعات جغرافیایی، نام دستگاه و متادیتای ردیابی از فایل تصویر
     */
    public static boolean cleanMetadata(String filePath) {
        if (filePath == null) return false;
        try {
            ExifInterface exif = new ExifInterface(filePath);

            // پاک‌سازی تمامی فیلدهای حساس موقعیت مکانی
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, null);
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, null);
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, null);
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, null);
            exif.setAttribute(ExifInterface.TAG_GPS_ALTITUDE, null);
            exif.setAttribute(ExifInterface.TAG_GPS_ALTITUDE_REF, null);
            exif.setAttribute(ExifInterface.TAG_GPS_DATESTAMP, null);
            exif.setAttribute(ExifInterface.TAG_GPS_TIMESTAMP, null);
            exif.setAttribute(ExifInterface.TAG_GPS_PROCESSING_METHOD, null);

            // پاک‌سازی مشخصات دوربین و دستگاه
            exif.setAttribute(ExifInterface.TAG_MAKE, null);
            exif.setAttribute(ExifInterface.TAG_MODEL, null);
            exif.setAttribute(ExifInterface.TAG_SOFTWARE, null);
            exif.setAttribute(ExifInterface.TAG_USER_COMMENT, null);

            exif.saveAttributes();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * جستجوی سریع در تصاویر اخیر پوشه Camera جهت یافتن عکس‌های دارای لوکیشن لو رفته
     */
    public static List<PhotoExifAuditResult> scanRecentCameraPhotos(int maxFiles) {
        List<PhotoExifAuditResult> results = new ArrayList<>();
        try {
            File dcimDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");
            if (!dcimDir.exists() || !dcimDir.isDirectory()) {
                dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            }
            if (dcimDir == null || !dcimDir.exists()) return results;

            File[] files = dcimDir.listFiles();
            if (files == null) return results;

            int checked = 0;
            for (File f : files) {
                if (checked >= maxFiles) break;
                String lower = f.getName().toLowerCase();
                if (lower.endsWith(".jpg") || lower.endsWith(".jpeg")) {
                    PhotoExifAuditResult audit = auditPhotoFile(f);
                    if (audit != null) {
                        results.add(audit);
                        checked++;
                    }
                }
            }
        } catch (Exception ignored) {}
        return results;
    }
}

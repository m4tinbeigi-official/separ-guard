# ===================================================================
# Separ Guard - Proprietary Commercial Obfuscation & Security Rules
# Copyright (C) 2026 Rick Sanchez. All rights reserved.
# ===================================================================

# به حداکثر رساندن مبهم‌سازی کدهای تجاری و توابع تحلیل بدافزار
-repackageclasses ''
-allowaccessmodification
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

# حذف لاگ‌های حساس سیستم
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}

# حفاظت از امضاهای دسترسی‌پذیری و گیرنده‌های سیستمی اندروید
-keep class ir.ricksanchez.separ.SeparApp { *; }
-keep class ir.ricksanchez.separ.ui.MainActivity { *; }
-keep class ir.ricksanchez.separ.services.** { *; }

# حفاظت از ساختار داده‌های دیتابیس لوکال
-keep class ir.ricksanchez.separ.data.models.** { *; }

# جلوگیری از دستکاری توابع اعتبارسنجی پرداخت درون‌برنامه‌ای بازار و مایکت
-keep class ir.ricksanchez.separ.billing.UnifiedBillingManager {
    public boolean verifyPurchaseSignature(java.lang.String, java.lang.String, java.lang.String);
}

# قوانین کتابخانه‌های استاندارد و کامپوز
-dontwarn androidx.**
-dontwarn kotlin.**
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}

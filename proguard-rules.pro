# ===================================================================
# قوانین پیشرفته بهینه‌سازی، مبهم‌سازی و ضد مهندسی معکوس پروگارد / R8
# ===================================================================

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# مبهم‌سازی تهاجمی اسامی کلاس‌ها، متدها و فیلدها
-repackageclasses ''
-allowaccessmodification

# حذف کامل اطلاعات خطایابی، شماره خطوط و اسامی متغیرهای محلی
-renamesourcefileattribute ''
-keepattributes SourceFile,LineNumberTable

# حفظ صرفاً نقاط ورود رسمی سیستم‌عامل اندروید در مانیفست
-keep public class ir.ricksanchez.separ.SeparApp {
    public <init>();
    public void onCreate();
}

-keep public class ir.ricksanchez.separ.ui.MainActivity {
    public <init>();
    public void onCreate(android.os.Bundle);
}

-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.appwidget.AppWidgetProvider

# حفظ متدهای سازنده استاندارد برای ویوها
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# مبهم‌سازی عمیق کلیه کلاس‌های دیتابیس، اسکنرها، تحلیل‌گرها و امنیت
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

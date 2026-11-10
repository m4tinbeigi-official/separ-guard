package ir.ricksanchez.separ.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import java.io.File;
import java.io.FileOutputStream;

/**
 * سامانه تولید پوستر و استوری هشدار بدافزار (Security Alert Story & Viral Sharing Exporter)
 * تولید گرافیک‌های باکیفیت ۱۰۸۰x۱۹۲۰ استاندارد استوری اینستاگرام و شبکه‌های اجتماعی
 */
public class SecurityStoryExporter {

    public static void exportAndShareThreatStory(Context context, String threatTitle, String packageName, String dangerSummary) {
        if (context == null) return;

        try {
            int width = 1080;
            int height = 1920;
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            // ۱. پس‌زمینه دارک اولد
            Paint bgPaint = new Paint();
            bgPaint.setColor(Color.parseColor("#080B10"));
            canvas.drawRect(0, 0, width, height, bgPaint);

            // ۲. کادر نئونی سایبری
            Paint framePaint = new Paint();
            framePaint.setColor(Color.parseColor("#152238"));
            framePaint.setStyle(Paint.Style.STROKE);
            framePaint.setStrokeWidth(6);
            RectF frame = new RectF(50, 60, width - 50, height - 60);
            canvas.drawRoundRect(frame, 40, 40, framePaint);

            // خط نئونی بالای صفحه
            Paint glowLine = new Paint();
            glowLine.setColor(Color.parseColor("#FF1744"));
            glowLine.setStrokeWidth(12);
            canvas.drawLine(150, 100, width - 150, 100, glowLine);

            // ۳. متن برند و هدر
            Paint headerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            headerPaint.setColor(Color.parseColor("#00E5FF"));
            headerPaint.setTextSize(46);
            headerPaint.setTextAlign(Paint.Align.CENTER);
            headerPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("سپر • آزمایشگاه ملی دفاع سایبری", width / 2f, 220, headerPaint);

            // ۴. دایره قرمز نشان خطر
            Paint circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            circlePaint.setColor(Color.parseColor("#261414"));
            canvas.drawCircle(width / 2f, 440, 140, circlePaint);

            Paint circleBorder = new Paint(Paint.ANTI_ALIAS_FLAG);
            circleBorder.setColor(Color.parseColor("#FF1744"));
            circleBorder.setStyle(Paint.Style.STROKE);
            circleBorder.setStrokeWidth(8);
            canvas.drawCircle(width / 2f, 440, 140, circleBorder);

            Paint iconPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            iconPaint.setColor(Color.parseColor("#FF1744"));
            iconPaint.setTextSize(90);
            iconPaint.setTextAlign(Paint.Align.CENTER);
            iconPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("⚠️", width / 2f, 470, iconPaint);

            // ۵. عنوان هشدار بدافزار
            Paint titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            titlePaint.setColor(Color.WHITE);
            titlePaint.setTextSize(54);
            titlePaint.setTextAlign(Paint.Align.CENTER);
            titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("کشف بدافزار سارق حساب بانکی!", width / 2f, 680, titlePaint);

            // ۶. نام بدافزار و پکیج
            Paint threatNamePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            threatNamePaint.setColor(Color.parseColor("#FFD600"));
            threatNamePaint.setTextSize(44);
            threatNamePaint.setTextAlign(Paint.Align.CENTER);
            threatNamePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText(threatTitle != null ? threatTitle : "بدافزار آلبوم یادگاری", width / 2f, 780, threatNamePaint);

            if (packageName != null && !packageName.isEmpty()) {
                Paint pkgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                pkgPaint.setColor(Color.parseColor("#94A3B8"));
                pkgPaint.setTextSize(32);
                pkgPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("شناسه بسته: " + packageName, width / 2f, 840, pkgPaint);
            }

            // ۷. کادر هشدار مرکزی
            Paint alertCardBg = new Paint(Paint.ANTI_ALIAS_FLAG);
            alertCardBg.setColor(Color.parseColor("#121826"));
            RectF alertCard = new RectF(90, 940, width - 90, 1420);
            canvas.drawRoundRect(alertCard, 30, 30, alertCardBg);

            Paint alertCardStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
            alertCardStroke.setColor(Color.parseColor("#243142"));
            alertCardStroke.setStyle(Paint.Style.STROKE);
            alertCardStroke.setStrokeWidth(3);
            canvas.drawRoundRect(alertCard, 30, 30, alertCardStroke);

            Paint bodyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            bodyPaint.setColor(Color.parseColor("#CBD5E1"));
            bodyPaint.setTextSize(36);
            bodyPaint.setTextAlign(Paint.Align.CENTER);

            canvas.drawText("این فایل خطرناک را برای من فرستادند!", width / 2f, 1030, bodyPaint);
            canvas.drawText("اپلیکیشن ضد هک «سپر» کدهای مخرب آن را کشف کرد.", width / 2f, 1100, bodyPaint);
            canvas.drawText("در صورت باز کردن، رمز پویای بانکی شما سرقت شده", width / 2f, 1200, bodyPaint);
            canvas.drawText("و موجودی حسابتان در کسری از ثانیه خالی می‌شود.", width / 2f, 1270, bodyPaint);
            canvas.drawText("به هیچ وجه فایل‌های ارسالی ناشناس را باز نکنید!", width / 2f, 1360, bodyPaint);

            // ۸. فوتر و کردیت سازنده و حامیان
            Paint footerTitle = new Paint(Paint.ANTI_ALIAS_FLAG);
            footerTitle.setColor(Color.parseColor("#00E676"));
            footerTitle.setTextSize(38);
            footerTitle.setTextAlign(Paint.Align.CENTER);
            footerTitle.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
            canvas.drawText("دانلود رایگان نرم‌افزار سپر از بازار و مایکت", width / 2f, 1560, footerTitle);

            Paint footerCredits = new Paint(Paint.ANTI_ALIAS_FLAG);
            footerCredits.setColor(Color.parseColor("#94A3B8"));
            footerCredits.setTextSize(30);
            footerCredits.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("سازنده: ریک سانچز • حامیان: عادل خان و مسعود میرپناهی (عایوتک)", width / 2f, 1630, footerCredits);
            canvas.drawText("گیت‌هاب رسمی: github.com/m4tinbeigi-official/separ-guard", width / 2f, 1690, footerCredits);

            // ذخیره در کش اپلیکیشن
            File cacheDir = context.getExternalCacheDir();
            if (cacheDir == null) cacheDir = context.getCacheDir();
            File storyFile = new File(cacheDir, "separ_threat_story.png");
            FileOutputStream fos = new FileOutputStream(storyFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 95, fos);
            fos.flush();
            fos.close();

            // فراخوانی پنجره اشتراک‌گذاری
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "هشدار کشف بدافزار بانکی توسط سپر");
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                "🚨 هشدار امنیتی مهم: بدافزار «" + threatTitle + "» کشف شد!\n\n" +
                "این فایل به اسم ابلاغیه، عکس یادگاری یا سود سهام ارسال می‌شود و رمز پویای بانکی را می‌دزدد. به هیچ عنوان بازش نکنید!\n\n" +
                "دانلود رایگان اپلیکیشن ضد هک سپر از کافه بازار و مایکت:\n" +
                "https://github.com/m4tinbeigi-official/separ-guard"
            );
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(Intent.createChooser(shareIntent, "اشتراک‌گذاری هشدار بدافزار"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

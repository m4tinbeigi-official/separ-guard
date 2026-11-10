package ir.ricksanchez.separ.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import ir.ricksanchez.separ.R;
import ir.ricksanchez.separ.data.MediaPartnerRepository;
import ir.ricksanchez.separ.data.VideoTutorialRepository;
import java.util.List;

/**
 * رندر آکادمی جامع آموزش امنیت سایبری (Cyber Security Academy & Media Hub)
 * بخش‌های سه‌گانه مجزا: ۱. پادکست‌ها و راهنماهای صوتی ۲. مقالات و چک‌لیست‌های متنی ۳. ویدیوهای آپارات ۴. حامیان رسانه‌ای
 */
public class AcademyMediaTabView {

    public interface AcademyMediaTabListener {
        void onVideoClicked(String aparatUrl);
        void onPartnerClicked(String partnerUrl);
        void onVoicePlayRequested(String title, String audioText);
        void onArticleReadRequested(String title, String fullContent);
    }

    public static LinearLayout buildView(Context context, final AcademyMediaTabListener listener) {
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // ۱. پادکست‌ها و راهنماهای صوتی ضد هک (Audio Guides & Podcasts)
        TextView voiceHeader = CyberUiTheme.createSectionTitle(context, "پادکست‌ها و راهنماهای صوتی ضد هک (شنیداری)");
        voiceHeader.setTextColor(Color.parseColor(CyberUiTheme.COLOR_CYAN));
        container.addView(voiceHeader);

        container.addView(createAudioGuideCard(
            context,
            "شگرد جدید کلاهبرداری پیامکی ثنا و سود سهام عدالت",
            "روش فریب هکرها با ارسال پیامک ابلاغیه الکترونیک و تکنیک‌های پیشگیری",
            "مدت: ۱ دقیقه و ۳۰ ثانیه • ریک سانچز",
            "هشدار مهم امنیتی: کلاهبرداران پیامک‌هایی با عنوان ابلاغیه قضایی سامانه ثنا، سود سهام عدالت یا وام فوری ارسال می‌کنند. سرشماره این پیامک‌ها معمولاً شماره شخصی موبایل است. به یاد داشته باشید سامانه‌های دولتی و قوه قضاییه هرگز با شماره موبایل شخصی پیامک ارسال نمی‌کنند و دامنه‌های رسمی همگی به دات آی‌آر ختم می‌شوند. در صورت باز کردن اشتباهی لینک، بلافاصله در صفحه اصلی سپر دکمه قطع اضطراری نفوذ را بزنید و گوشی را روی حالت پرواز قرار دهید.",
            listener
        ));

        container.addView(createAudioGuideCard(
            context,
            "آموزش خروج فوری از نشست‌های فعال تلگرام و ایتا",
            "چگونه متوجه شویم شخص دیگری به پیام‌های ما دسترسی دارد؟",
            "مدت: ۱ دقیقه و ۴۵ ثانیه • پشتیبانی فنی",
            "برای بررسی اینکه آیا هکر در حساب تلگرام یا ایتا شما حضور دارد یا خیر: وارد تنظیمات برنامه شوید، بخش حریم خصوصی و امنیت یا Devices را باز کنید. در این بخش لیست تمام گوشی‌ها و کامپیوترهایی که به حساب شما متصل هستند را مشاهده می‌کنید. اگر هر دستگاه ناشناسی دیدید، فوراً دکمه Terminate all other sessions یا خروج از سایر نشست‌ها را بزنید و تایید دومرحله‌ای با رمز عبور قوی فعال کنید.",
            listener
        ));

        container.addView(createAudioGuideCard(
            context,
            "تشخیص درگاه جعلی پرداخت شاپرک در ۳ ثانیه",
            "تکنیک دقیق بررسی آدرس‌بار قبل از وارد کردن اطلاعات کارت بانکی",
            "مدت: ۱ دقیقه و ۱۵ ثانیه • امنیت فین‌تک",
            "مهم‌ترین قانون خریدهای اینترنتی در ایران: هر درگاه پرداختی در کشور باید دقیقاً به پسوند دات شاپَرَک دات آی‌آر ختم شود (shaparak.ir). هکرها از دامنه‌های گمراه‌کننده مثل shaparak-pay.com استفاده می‌کنند. سپر به صورت خودکار با سرویس پایش لحظه‌ای، به محض ورود شما به هر درگاهی آدرس را بررسی کرده و در صورت جعلی بودن یک کاور قرمز تمام‌صفحه باز می‌کند.",
            listener
        ));

        // ۲. آموزش‌ها و مقالات تحلیلی متنی (Security Masterclass)
        TextView articlesHeader = CyberUiTheme.createSectionTitle(context, "مقالات و چک‌لیست‌های متنی آموزش امنیت");
        articlesHeader.setTextColor(Color.parseColor(CyberUiTheme.COLOR_NEON_GREEN));
        container.addView(articlesHeader);

        container.addView(CyberUiTheme.createInteractiveCardRow(
            context,
            R.drawable.ic_file_report,
            "چک‌لیست ۱۰ گانه ایمن‌سازی گوشی در برابر تروجان‌ها",
            "تنظیمات الزامی برای ضد هک کردن اندروید، فایروال و رمزهای یکبار مصرف",
            "چک‌لیست متنی",
            CyberUiTheme.COLOR_NEON_GREEN,
            v -> {
                if (listener != null) {
                    listener.onArticleReadRequested(
                        "چک‌لیست ۱۰ گانه ایمن‌سازی گوشی در برابر تروجان‌ها",
                        "۱. هرگز فایل‌های نصبی (APK) ارسالی در تلگرام، ایتا و واتساپ را نصب نکنید.\n\n" +
                        "۲. دسترسی پیامک را تنها به برنامه‌های رسمی و تاییدشده بدهید.\n\n" +
                        "۳. قابلیت تأیید دومرحله‌ای (Two-Step Verification) را در تمام پیام‌رسان‌ها فعال کنید.\n\n" +
                        "۴. به پیامک‌های اخطار قطع یارانه، ابلاغیه ثنا یا وام فوری که با شماره‌های شخصی (۰۹...) ارسال می‌شوند اعتماد نکنید.\n\n" +
                        "۵. در صورت دانلود تصادفی هر فایل، در بخش اسکنر سپر آن را قبل از باز کردن بررسی و حذف قطعی کنید.\n\n" +
                        "۶. در کافه‌ها و فرودگاه‌ها، قبل از اتصال به وای‌فای عمومی از رادار امنیت وای‌فای سپر استفاده کنید.\n\n" +
                        "۷. همواره نشست‌های فعال تلگرام و روبیکا را هفتگی بررسی کنید.\n\n" +
                        "۸. رمز عبور مجزا و پین‌کد روی برنامه‌های بانکی خود تنظیم نمایید.\n\n" +
                        "۹. پیش از انتشار عکس‌ها در فضای مجازی، متادیتای لوکیشن GPS آن‌ها را پاک کنید.\n\n" +
                        "۱۰. در صورت احساس نفوذ، فوراً گزینه «قطع اضطراری نفوذ» را در سپر بزنید و گوشی را روی حالت پرواز قرار دهید."
                    );
                }
            }
        ));

        container.addView(CyberUiTheme.createInteractiveCardRow(
            context,
            R.drawable.ic_lock_app,
            "تروجان‌های مخفی فاقد آیکون چگونه عمل می‌کنند؟",
            "کالبدشکافی نحوه استتار بدافزارها و راهکارهای حذف برنامه‌های قفل‌شده",
            "تحلیل فنی",
            CyberUiTheme.COLOR_AMBER,
            v -> {
                if (listener != null) {
                    listener.onArticleReadRequested(
                        "تروجان‌های مخفی فاقد آیکون چگونه عمل می‌کنند؟",
                        "تروجان‌های نسل جدید ایرانی پس از نصب، دستور حذف آیکون میانبر از صفحه اصلی گوشی (Launcher Icon Suppression) را صادر می‌کنند. بنابراین کاربر تصور می‌کند برنامه نصب نشد یا خراب بود!\n\n" +
                        "این برنامه‌ها در پس‌زمینه گوشی باقی مانده و با دسترسی به پیامک‌ها، رمزهای پویای بانکی را به ربات‌های تلگرامی هکرها مخابره می‌کنند.\n\n" +
                        "سپر با الگوریتم پویش بسته‌های پنهان، کلیه برنامه‌های بدون میانبر را در تب برنامه‌ها استخراج کرده و امکان حذف مستقیم آن‌ها را با یک کلیک فراهم می‌سازد."
                    );
                }
            }
        ));

        // ۳. ویدیوهای آموزشی در آپارات (Aparat Video Hub)
        TextView vidHeader = CyberUiTheme.createSectionTitle(context, "ویدیوهای آموزشی امنیت و ضد هک در آپارات (اینترنت نیم‌بها)");
        vidHeader.setTextColor(Color.parseColor("#FF4081"));
        container.addView(vidHeader);

        List<VideoTutorialRepository.VideoTutorial> tutorials = VideoTutorialRepository.getAllTutorials();
        for (VideoTutorialRepository.VideoTutorial v : tutorials) {
            container.addView(CyberUiTheme.createInteractiveCardRow(
                context,
                R.drawable.ic_social_aparat,
                v.titleFa,
                v.descriptionFa + " (" + v.durationFa + ")",
                v.categoryFa,
                "#FF4081",
                view -> {
                    if (listener != null) listener.onVideoClicked(v.aparatUrl);
                }
            ));
        }

        // ۴. تالار حامیان رسانه‌ای و چهره‌های مطرح فناوری
        TextView partHeader = CyberUiTheme.createSectionTitle(context, "تالار حامیان رسانه‌ای و چهره‌های فناوری");
        partHeader.setTextColor(Color.parseColor(CyberUiTheme.COLOR_GOLD));
        container.addView(partHeader);

        List<MediaPartnerRepository.PartnerInfo> partners = MediaPartnerRepository.getOfficialPartners();
        for (MediaPartnerRepository.PartnerInfo p : partners) {
            if (p.imageResId > 0) {
                container.addView(createFeaturedPartnerCard(context, p, listener));
            } else {
                container.addView(CyberUiTheme.createInteractiveCardRow(
                    context,
                    R.drawable.ic_shield,
                    p.nameFa,
                    p.roleDescriptionFa,
                    p.typeFa,
                    p.badgeColorHex,
                    view -> {
                        if (listener != null) listener.onPartnerClicked(p.url);
                    }
                ));
            }
        }

        return container;
    }

    private static LinearLayout createAudioGuideCard(Context context, String title, String subtitle,
                                                     String durationMeta, final String audioText,
                                                     final AcademyMediaTabListener listener) {
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        int p = CyberUiTheme.dpToPx(context, 16);
        card.setPadding(p, p, p, p);
        card.setBackground(CyberUiTheme.createCardBg("#0D1624", CyberUiTheme.COLOR_CYAN, 16));

        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        clp.setMargins(0, 0, 0, CyberUiTheme.dpToPx(context, 12));
        card.setLayoutParams(clp);

        LinearLayout topRow = new LinearLayout(context);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);

        ImageView iv = new ImageView(context);
        iv.setImageResource(R.drawable.ic_volume_up);
        iv.setColorFilter(Color.parseColor(CyberUiTheme.COLOR_CYAN));
        int isz = CyberUiTheme.dpToPx(context, 24);
        LinearLayout.LayoutParams ilp = new LinearLayout.LayoutParams(isz, isz);
        ilp.setMargins(CyberUiTheme.dpToPx(context, 10), 0, 0, 0);
        iv.setLayoutParams(ilp);
        topRow.addView(iv);

        LinearLayout textCol = new LinearLayout(context);
        textCol.setOrientation(LinearLayout.VERTICAL);
        textCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

        TextView tv = new TextView(context);
        tv.setText(title);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(13);
        FontHelper.applyBold(tv);
        textCol.addView(tv);

        TextView stv = new TextView(context);
        stv.setText(subtitle);
        stv.setTextColor(Color.parseColor(CyberUiTheme.COLOR_TEXT_MUTED));
        stv.setTextSize(11);
        FontHelper.applyRegular(stv);
        stv.setPadding(0, CyberUiTheme.dpToPx(context, 2), 0, 0);
        textCol.addView(stv);

        topRow.addView(textCol);
        card.addView(topRow);

        // سطر کنترل پخش صوت
        LinearLayout playRow = new LinearLayout(context);
        playRow.setOrientation(LinearLayout.HORIZONTAL);
        playRow.setGravity(Gravity.CENTER_VERTICAL);
        playRow.setPadding(0, CyberUiTheme.dpToPx(context, 10), 0, 0);

        TextView metaTv = new TextView(context);
        metaTv.setText(durationMeta);
        metaTv.setTextColor(Color.parseColor(CyberUiTheme.COLOR_CYAN));
        metaTv.setTextSize(10);
        FontHelper.applyMedium(metaTv);
        LinearLayout.LayoutParams mlp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f);
        metaTv.setLayoutParams(mlp);
        playRow.addView(metaTv);

        Button playBtn = CyberUiTheme.createChipButton(context, "شنیدن راهنما (پخش صوتی)", "#16283E", Color.parseColor(CyberUiTheme.COLOR_CYAN));
        LinearLayout.LayoutParams plp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, CyberUiTheme.dpToPx(context, 38));
        playBtn.setLayoutParams(plp);
        playBtn.setPadding(CyberUiTheme.dpToPx(context, 14), 0, CyberUiTheme.dpToPx(context, 14), 0);
        playBtn.setOnClickListener(v -> {
            if (listener != null) listener.onVoicePlayRequested(title, audioText);
        });
        playRow.addView(playBtn);

        card.addView(playRow);

        return card;
    }

    private static LinearLayout createFeaturedPartnerCard(Context context, final MediaPartnerRepository.PartnerInfo partner,
                                                          final AcademyMediaTabListener listener) {
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        int p = CyberUiTheme.dpToPx(context, 16);
        card.setPadding(p, p, p, p);
        card.setBackground(CyberUiTheme.createCardBg(CyberUiTheme.COLOR_CARD_BG, partner.badgeColorHex, 18));

        LinearLayout.LayoutParams clp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        clp.setMargins(0, 0, 0, CyberUiTheme.dpToPx(context, 14));
        card.setLayoutParams(clp);

        // سطر مشخصات بالای کارت (عکس پروفایل + نام + تگ)
        LinearLayout topRow = new LinearLayout(context);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);

        // فریم گرد عکس پروفایل با برش کامپوزیت مدور
        LinearLayout avatarRing = new LinearLayout(context);
        avatarRing.setGravity(Gravity.CENTER);
        int ringSize = CyberUiTheme.dpToPx(context, 54);
        LinearLayout.LayoutParams alp = new LinearLayout.LayoutParams(ringSize, ringSize);
        alp.setMargins(CyberUiTheme.dpToPx(context, 12), 0, 0, 0);
        avatarRing.setLayoutParams(alp);
        avatarRing.setBackground(CyberUiTheme.createOvalBg("#1E293B", partner.badgeColorHex, 2));

        ImageView avatarIv = new ImageView(context);
        avatarIv.setImageDrawable(CyberUiTheme.createCircularDrawable(context, partner.imageResId));
        int imgSize = CyberUiTheme.dpToPx(context, 50);
        avatarIv.setLayoutParams(new LinearLayout.LayoutParams(imgSize, imgSize));
        avatarIv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        avatarRing.addView(avatarIv);
        topRow.addView(avatarRing);

        // ستون متنی نام و نقش
        LinearLayout nameCol = new LinearLayout(context);
        nameCol.setOrientation(LinearLayout.VERTICAL);
        nameCol.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));

        TextView nameTv = new TextView(context);
        nameTv.setText(partner.nameFa);
        nameTv.setTextColor(Color.WHITE);
        nameTv.setTextSize(14);
        FontHelper.applyBold(nameTv);
        nameCol.addView(nameTv);

        TextView roleTv = new TextView(context);
        roleTv.setText(partner.typeFa);
        roleTv.setTextColor(Color.parseColor(partner.badgeColorHex));
        roleTv.setTextSize(11);
        FontHelper.applyRegular(roleTv);
        roleTv.setPadding(0, CyberUiTheme.dpToPx(context, 2), 0, 0);
        nameCol.addView(roleTv);

        topRow.addView(nameCol);
        card.addView(topRow);

        // متن توضیحات نقش حامی
        TextView descTv = new TextView(context);
        descTv.setText(partner.roleDescriptionFa);
        descTv.setTextColor(Color.parseColor(CyberUiTheme.COLOR_TEXT_MUTED));
        descTv.setTextSize(11);
        FontHelper.applyRegular(descTv);
        descTv.setLineSpacing(CyberUiTheme.dpToPx(context, 3), 1.25f);
        descTv.setPadding(0, CyberUiTheme.dpToPx(context, 10), 0, CyberUiTheme.dpToPx(context, 10));
        card.addView(descTv);

        // آیکون‌های شکیل و تمیز شبکه‌های اجتماعی
        if (!partner.socialLinks.isEmpty()) {
            LinearLayout socialRow = new LinearLayout(context);
            socialRow.setOrientation(LinearLayout.HORIZONTAL);
            socialRow.setGravity(Gravity.CENTER_VERTICAL);
            socialRow.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            socialRow.setPadding(0, CyberUiTheme.dpToPx(context, 4), 0, 0);

            for (MediaPartnerRepository.SocialLink sl : partner.socialLinks) {
                LinearLayout iconBtn = new LinearLayout(context);
                iconBtn.setGravity(Gravity.CENTER);
                int btnSize = CyberUiTheme.dpToPx(context, 40);
                LinearLayout.LayoutParams blp = new LinearLayout.LayoutParams(btnSize, btnSize);
                blp.setMargins(CyberUiTheme.dpToPx(context, 6), 0, 0, 0);
                iconBtn.setLayoutParams(blp);

                GradientDrawable btnBg = new GradientDrawable();
                btnBg.setColor(Color.parseColor("#141E2C"));
                btnBg.setCornerRadius(CyberUiTheme.dpToPx(context, 12));
                btnBg.setStroke(1, Color.parseColor(sl.colorHex));
                iconBtn.setBackground(btnBg);

                ImageView iv = new ImageView(context);
                if (sl.iconResId > 0) {
                    iv.setImageResource(sl.iconResId);
                } else {
                    iv.setImageResource(R.drawable.ic_shield);
                }
                int isz = CyberUiTheme.dpToPx(context, 20);
                iv.setLayoutParams(new LinearLayout.LayoutParams(isz, isz));
                iconBtn.addView(iv);

                iconBtn.setOnClickListener(v -> {
                    if (listener != null) listener.onPartnerClicked(sl.url);
                });
                socialRow.addView(iconBtn);
            }
            card.addView(socialRow);
        }

        return card;
    }
}

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
 * رندر تب آکادمی ویدیوهای آپارات و تالار حامیان رسانه‌ای و اینفلوئنسرها
 * مجهز به تصاویر پروفایل واقعی حامیان، دکمه‌های مستقیم شبکه‌های اجتماعی و تایپوگرافی وزیرمتن
 */
public class AcademyMediaTabView {

    public interface AcademyMediaTabListener {
        void onVideoClicked(String aparatUrl);
        void onPartnerClicked(String partnerUrl);
    }

    public static LinearLayout buildView(Context context, final AcademyMediaTabListener listener) {
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // ۱. تالار حامیان رسانه‌ای و اینفلوئنسرها (در ابتدای صفحه)
        TextView partHeader = CyberUiTheme.createSectionTitle(context, "تالار حامیان رسانه‌ای و چهره‌های فناوری");
        partHeader.setTextColor(Color.parseColor(CyberUiTheme.COLOR_GOLD));
        container.addView(partHeader);

        List<MediaPartnerRepository.PartnerInfo> partners = MediaPartnerRepository.getOfficialPartners();
        for (MediaPartnerRepository.PartnerInfo p : partners) {
            if (p.imageResId > 0) {
                // کارت اختصاصی لوکس با پرتره واقعی
                container.addView(createFeaturedPartnerCard(context, p, listener));
            } else {
                // کارت استاندارد برای خبرگزاری‌ها
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

        // ۲. بخش ویدیوهای آموزشی در آپارات
        TextView vidHeader = CyberUiTheme.createSectionTitle(context, "ویدیوهای آموزشی امنیت و ضد هک در آپارات (اینترنت نیم‌بها)");
        container.addView(vidHeader);

        List<VideoTutorialRepository.VideoTutorial> tutorials = VideoTutorialRepository.getAllTutorials();
        for (VideoTutorialRepository.VideoTutorial v : tutorials) {
            container.addView(CyberUiTheme.createInteractiveCardRow(
                context,
                R.drawable.ic_nav_academy,
                v.titleFa,
                v.descriptionFa + " (" + v.durationFa + ")",
                v.categoryFa,
                "#FF4081",
                view -> {
                    if (listener != null) listener.onVideoClicked(v.aparatUrl);
                }
            ));
        }

        return container;
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

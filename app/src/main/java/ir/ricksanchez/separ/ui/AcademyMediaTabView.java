package ir.ricksanchez.separ.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import ir.ricksanchez.separ.R;
import ir.ricksanchez.separ.data.MediaPartnerRepository;
import ir.ricksanchez.separ.data.VideoTutorialRepository;
import java.util.List;

/**
 * رندر تب آکادمی ویدیوهای آپارات و تالار حامیان رسانه‌ای و اینفلوئنسرها
 * طراحی دارک مود درجه یک بدون استفاده از اموجی، مجهز به وکتورهای تمیز و تایپوگرافی وزیرمتن
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

        // ۱. بخش ویدیوهای آموزشی در آپارات
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

        // ۲. تالار حامیان رسانه‌ای و اینفلوئنسرها
        TextView partHeader = CyberUiTheme.createSectionTitle(context, "تالار حامیان رسانه‌ای، خبرگزاری‌ها و همکاران");
        partHeader.setTextColor(Color.parseColor(CyberUiTheme.COLOR_GOLD));
        container.addView(partHeader);

        List<MediaPartnerRepository.PartnerInfo> partners = MediaPartnerRepository.getOfficialPartners();
        for (MediaPartnerRepository.PartnerInfo p : partners) {
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

        return container;
    }
}

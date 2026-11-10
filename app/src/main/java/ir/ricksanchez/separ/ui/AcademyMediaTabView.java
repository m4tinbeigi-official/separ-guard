package ir.ricksanchez.separ.ui;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import ir.ricksanchez.separ.data.MediaPartnerRepository;
import ir.ricksanchez.separ.data.VideoTutorialRepository;
import java.util.List;

/**
 * رندر تب آکادمی ویدیوهای آپارات و تالار حامیان رسانه‌ای و اینفلوئنسرها
 * مجهز به فونت فارسی وزیرمتن (Vazirmatn)
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
        TextView vidHeader = CyberUiTheme.createSectionTitle(context, "🎬 ویدیوهای آموزشی امنیت و ضد هک در آپارات (اینترنت نیم‌بها):");
        container.addView(vidHeader);

        List<VideoTutorialRepository.VideoTutorial> tutorials = VideoTutorialRepository.getAllTutorials();
        for (VideoTutorialRepository.VideoTutorial v : tutorials) {
            LinearLayout card = new LinearLayout(context);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(18, 14, 18, 14);
            card.setBackground(CyberUiTheme.createCardBg("#151C2C", "#1E2A3F", 16));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 0, 10);
            card.setLayoutParams(lp);

            TextView title = new TextView(context);
            title.setText(v.icon + " " + v.titleFa);
            title.setTextColor(Color.parseColor(CyberUiTheme.COLOR_CYAN));
            title.setTextSize(13);
            FontHelper.applyBold(title);
            card.addView(title);

            TextView desc = new TextView(context);
            desc.setText(v.descriptionFa + " (" + v.durationFa + " • " + v.categoryFa + ")");
            desc.setTextColor(Color.parseColor(CyberUiTheme.COLOR_TEXT_MUTED));
            desc.setTextSize(11);
            FontHelper.applyRegular(desc);
            desc.setPadding(0, 4, 0, 10);
            card.addView(desc);

            Button playBtn = CyberUiTheme.createCyberButton(context, "▶️ پخش ویدیو در آپارات", "#D81B60", "#FFFFFF", 11);
            playBtn.setOnClickListener(view -> {
                if (listener != null) listener.onVideoClicked(v.aparatUrl);
            });
            card.addView(playBtn);

            container.addView(card);
        }

        // ۲. تالار حامیان رسانه‌ای و اینفلوئنسرها
        TextView partHeader = CyberUiTheme.createSectionTitle(context, "🏆 تالار حامیان رسانه‌ای، خبرگزاری‌ها و اینفلوئنسرها:");
        partHeader.setTextColor(Color.parseColor(CyberUiTheme.COLOR_GOLD));
        container.addView(partHeader);

        List<MediaPartnerRepository.PartnerInfo> partners = MediaPartnerRepository.getOfficialPartners();
        for (MediaPartnerRepository.PartnerInfo p : partners) {
            LinearLayout pCard = new LinearLayout(context);
            pCard.setOrientation(LinearLayout.VERTICAL);
            pCard.setPadding(18, 14, 18, 14);
            pCard.setBackground(CyberUiTheme.createCardBg("#131B2A", p.badgeColorHex, 16));
            LinearLayout.LayoutParams plp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            plp.setMargins(0, 0, 0, 10);
            pCard.setLayoutParams(plp);

            TextView pName = new TextView(context);
            pName.setText(p.nameFa + " (" + p.typeFa + ")");
            pName.setTextColor(Color.parseColor(p.badgeColorHex));
            pName.setTextSize(13);
            FontHelper.applyBold(pName);
            pCard.addView(pName);

            TextView pRole = new TextView(context);
            pRole.setText(p.roleDescriptionFa);
            pRole.setTextColor(Color.parseColor(CyberUiTheme.COLOR_TEXT_MUTED));
            pRole.setTextSize(11);
            FontHelper.applyRegular(pRole);
            pRole.setPadding(0, 4, 0, 8);
            pCard.addView(pRole);

            Button visitBtn = CyberUiTheme.createCyberButton(context, "🌐 مشاهده صفحه رسمی / گزارش", "#1A2638", p.badgeColorHex, 11);
            visitBtn.setOnClickListener(view -> {
                if (listener != null) listener.onPartnerClicked(p.url);
            });
            pCard.addView(visitBtn);

            container.addView(pCard);
        }

        return container;
    }
}

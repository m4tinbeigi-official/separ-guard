package ir.ricksanchez.separ.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import ir.ricksanchez.separ.data.PhishingQuizRepository;
import java.util.List;

/**
 * نمایشگر تعاملی آزمون و شبیه‌ساز هوشیاری ضد فیشینگ (Phishing Awareness Quiz Helper)
 */
public class PhishingQuizDialogHelper {

    public static void startQuiz(Context context) {
        showQuestion(context, 0, 0);
    }

    private static void showQuestion(Context context, int index, int score) {
        if (context == null) return;
        List<PhishingQuizRepository.QuizQuestion> questions = PhishingQuizRepository.getQuestions();
        if (index >= questions.size()) {
            showFinalResult(context, score, questions.size());
            return;
        }

        final PhishingQuizRepository.QuizQuestion q = questions.get(index);

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(Gravity.CENTER);
        }

        FrameLayout root = new FrameLayout(context);
        int pad = CyberUiTheme.dpToPx(context, 16);
        root.setPadding(pad, pad, pad, pad);

        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER_HORIZONTAL);
        card.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#0F141C"));
        bg.setCornerRadius(CyberUiTheme.dpToPx(context, 20));
        bg.setStroke(CyberUiTheme.dpToPx(context, 2), Color.parseColor("#00E5FF"));
        card.setBackground(bg);
        card.setPadding(pad, pad, pad, pad);

        // نشانگر شماره سناریو
        TextView tvProgress = new TextView(context);
        tvProgress.setText("سناریوی " + CyberUiTheme.toPersianDigits(String.valueOf(index + 1)) + " از " + CyberUiTheme.toPersianDigits(String.valueOf(questions.size())));
        tvProgress.setTextColor(Color.parseColor("#00E5FF"));
        tvProgress.setTextSize(11);
        FontHelper.applyBold(tvProgress);
        card.addView(tvProgress);

        TextView tvTitle = new TextView(context);
        tvTitle.setText(q.scenarioTitle);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setTextSize(15);
        FontHelper.applyBold(tvTitle);
        tvTitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lpT = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpT.topMargin = CyberUiTheme.dpToPx(context, 8);
        card.addView(tvTitle, lpT);

        // کادر پیام شبیه‌سازی‌شده
        LinearLayout msgBox = new LinearLayout(context);
        msgBox.setOrientation(LinearLayout.VERTICAL);
        msgBox.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        GradientDrawable mbBg = new GradientDrawable();
        mbBg.setColor(Color.parseColor("#151D28"));
        mbBg.setCornerRadius(CyberUiTheme.dpToPx(context, 12));
        mbBg.setStroke(CyberUiTheme.dpToPx(context, 1), Color.parseColor("#243142"));
        msgBox.setBackground(mbBg);

        int p12 = CyberUiTheme.dpToPx(context, 12);
        msgBox.setPadding(p12, p12, p12, p12);
        LinearLayout.LayoutParams lpMb = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpMb.topMargin = CyberUiTheme.dpToPx(context, 12);
        lpMb.bottomMargin = CyberUiTheme.dpToPx(context, 14);
        msgBox.setLayoutParams(lpMb);

        TextView tvSim = new TextView(context);
        tvSim.setText(q.simulatedMessage);
        tvSim.setTextColor(Color.parseColor("#CBD5E1"));
        tvSim.setTextSize(11.5f);
        FontHelper.applyRegular(tvSim);
        tvSim.setLineSpacing(0, 1.25f);
        msgBox.addView(tvSim);

        card.addView(msgBox);

        TextView tvQuestion = new TextView(context);
        tvQuestion.setText("به نظر شما این سناریو معتبر است یا فیشینگ / کلاهبرداری؟");
        tvQuestion.setTextColor(Color.parseColor("#FFD600"));
        tvQuestion.setTextSize(12);
        FontHelper.applyBold(tvQuestion);
        tvQuestion.setGravity(Gravity.CENTER);
        card.addView(tvQuestion);

        // دکمه‌های پاسخ
        LinearLayout btnRow = new LinearLayout(context);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        LinearLayout.LayoutParams lpBr = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lpBr.topMargin = CyberUiTheme.dpToPx(context, 14);
        btnRow.setLayoutParams(lpBr);

        Button btnPhishing = CyberUiTheme.createCyberButton(context, "فیشینگ و خطرناک", "#FF1744", "#FFFFFF", 12);
        btnPhishing.setOnClickListener(v -> {
            dialog.dismiss();
            boolean correct = q.isMalicious;
            showAnswerFeedback(context, q, correct, index, correct ? score + 1 : score);
        });
        LinearLayout.LayoutParams lpBp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        lpBp.leftMargin = CyberUiTheme.dpToPx(context, 6);
        btnRow.addView(btnPhishing, lpBp);

        Button btnLegit = CyberUiTheme.createCyberButton(context, "معتبر و رسمی", "#00E676", "#080B10", 12);
        btnLegit.setOnClickListener(v -> {
            dialog.dismiss();
            boolean correct = !q.isMalicious;
            showAnswerFeedback(context, q, correct, index, correct ? score + 1 : score);
        });
        LinearLayout.LayoutParams lpBl = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        btnRow.addView(btnLegit, lpBl);

        card.addView(btnRow);

        root.addView(card);
        dialog.setContentView(root);
        dialog.setCancelable(false);
        dialog.show();
    }

    private static void showAnswerFeedback(Context context, PhishingQuizRepository.QuizQuestion q, boolean correct, int currentIndex, int newScore) {
        CyberDialogHelper.showCyberAlert(
            context,
            correct ? ir.ricksanchez.separ.R.drawable.ic_shield : ir.ricksanchez.separ.R.drawable.ic_call_warning,
            correct ? "آفرین! تشخیص شما کاملاً درست بود" : "اشتباه بود! مراقب این شگرد باشید",
            q.explanation,
            "رفتن به سناریوی بعدی",
            d -> {
                d.dismiss();
                showQuestion(context, currentIndex + 1, newScore);
            },
            null,
            null
        );
    }

    private static void showFinalResult(Context context, int score, int total) {
        String title = (score == total) ? "شاهکار! نشان طلایی کاربر نفوذناپذیر" : "نتیجه آزمون هوشیاری ضد فیشینگ";
        String message =
            "شما به " + CyberUiTheme.toPersianDigits(String.valueOf(score)) + " سناریو از " +
            CyberUiTheme.toPersianDigits(String.valueOf(total)) + " مورد پاسخ صحیح دادید.\n\n" +
            (score == total ?
                "هوشمندی سایبری شما فوق‌العاده است! هیچ کلاهبرداری نمی‌تواند با ترفندهای مهندسی اجتماعی شما را فریب دهد." :
                "با رعایت ۳ اصل طلایی سپر، همواره در برابر تله‌های پیامکی و درگاه‌های جعلی ایمن خواهید ماند.");

        CyberDialogHelper.showCyberAlert(
            context,
            ir.ricksanchez.separ.R.drawable.ic_shield,
            title,
            message,
            "اتمام آزمون و بازگشت",
            Dialog::dismiss,
            null,
            null
        );
    }
}

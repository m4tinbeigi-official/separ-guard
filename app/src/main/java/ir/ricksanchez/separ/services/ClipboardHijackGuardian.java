package ir.ricksanchez.separ.services;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;
import ir.ricksanchez.separ.SeparApp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * سامانه مانیتورینگ کلیپ‌بورد در برابر بدافزارهای کلیپر (Clipper Trojan)
 * محافظت از شماره کارت‌های بانکی ۱۶ رقمی و آدرس‌های کریپتو و تتر (TRC20/ERC20)
 * در صورتی که بدافزاری شماره کارت یا ولت کپی‌شده را تغییر دهد، فوراً هشدار و مداخله صادر می‌کند.
 */
public class ClipboardHijackGuardian {

    // الگوی کارت‌های ۱۶ رقمی بانکی ایران
    private static final Pattern BANK_CARD_PATTERN = Pattern.compile("\\b([56][0-9]{3}[- ]?[0-9]{4}[- ]?[0-9]{4}[- ]?[0-9]{4})\\b");
    // الگوهای آدرس‌های ارز دیجیتال و تتر
    private static final Pattern USDT_TRC20_PATTERN = Pattern.compile("\\b(T[A-Za-z1-9]{33})\\b");
    private static final Pattern ETH_ERC20_PATTERN = Pattern.compile("\\b(0x[a-fA-F0-9]{40})\\b");
    private static final Pattern BTC_PATTERN = Pattern.compile("\\b([13][a-km-zA-HJ-NP-Z1-9]{25,34}|bc1[a-z0-9]{39,59})\\b");

    public static class ClipboardAnalysisResult {
        public final boolean isSensitiveData;
        public final String dataType; // "BANK_CARD", "USDT_TRC20", "CRYPTO_WALLET"
        public final String sanitizedValue;
        public final boolean isHijackDetected;

        public ClipboardAnalysisResult(boolean isSensitive, String type, String value, boolean isHijack) {
            this.isSensitiveData = isSensitive;
            this.dataType = type;
            this.sanitizedValue = value;
            this.isHijackDetected = isHijack;
        }
    }

    private final Context context;
    private final ClipboardManager clipboardManager;
    private String lastCopiedOriginal = null;
    private long lastCopiedTimestamp = 0;

    public ClipboardHijackGuardian(Context context) {
        this.context = context.getApplicationContext();
        this.clipboardManager = (ClipboardManager) this.context.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    public void startMonitoring() {
        if (clipboardManager == null) return;
        clipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                handleClipboardChanged();
            }
        });
    }

    public ClipboardAnalysisResult analyzeText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ClipboardAnalysisResult(false, null, null, false);
        }

        String clean = text.replace(" ", "").replace("-", "").trim();

        // ۱. بررسی کارت بانکی ۱۶ رقمی
        Matcher cardMatcher = BANK_CARD_PATTERN.matcher(text);
        if (cardMatcher.find()) {
            String rawCard = cardMatcher.group(1).replace(" ", "").replace("-", "");
            if (rawCard.length() == 16 && isValidLuhn(rawCard)) {
                String masked = rawCard.substring(0, 6) + "******" + rawCard.substring(12);
                return new ClipboardAnalysisResult(true, "BANK_CARD", masked, false);
            }
        }

        // ۲. بررسی تتر شبکه ترون (USDT TRC20)
        Matcher trcMatcher = USDT_TRC20_PATTERN.matcher(clean);
        if (trcMatcher.find()) {
            String wallet = trcMatcher.group(1);
            String masked = wallet.substring(0, 6) + "..." + wallet.substring(wallet.length() - 4);
            return new ClipboardAnalysisResult(true, "USDT_TRC20", masked, false);
        }

        // ۳. بررسی اتریوم / تتر ERC20
        Matcher ethMatcher = ETH_ERC20_PATTERN.matcher(clean);
        if (ethMatcher.find()) {
            String wallet = ethMatcher.group(1);
            String masked = wallet.substring(0, 6) + "..." + wallet.substring(wallet.length() - 4);
            return new ClipboardAnalysisResult(true, "CRYPTO_WALLET", masked, false);
        }

        // ۴. بررسی بیت‌کوین
        Matcher btcMatcher = BTC_PATTERN.matcher(clean);
        if (btcMatcher.find()) {
            String wallet = btcMatcher.group(1);
            String masked = wallet.substring(0, 6) + "..." + wallet.substring(wallet.length() - 4);
            return new ClipboardAnalysisResult(true, "CRYPTO_WALLET", masked, false);
        }

        return new ClipboardAnalysisResult(false, null, null, false);
    }

    private void handleClipboardChanged() {
        try {
            if (!clipboardManager.hasPrimaryClip()) return;
            ClipData clip = clipboardManager.getPrimaryClip();
            if (clip == null || clip.getItemCount() == 0) return;

            CharSequence charSeq = clip.getItemAt(0).getText();
            if (charSeq == null) return;
            String text = charSeq.toString().trim();

            ClipboardAnalysisResult analysis = analyzeText(text);
            if (!analysis.isSensitiveData) {
                lastCopiedOriginal = null;
                return;
            }

            long now = System.currentTimeMillis();
            // اگر ظرف کمتر از ۲ ثانیه، داده حساسی با داده حساس دیگری جایگزین شود -> سرقت کلیپر
            if (lastCopiedOriginal != null && !lastCopiedOriginal.equals(text) && (now - lastCopiedTimestamp < 2500)) {
                String dateStr = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());

                String threatTitle = analysis.dataType.equals("BANK_CARD")
                    ? "سرقت و جایگزینی شماره کارت در کلیپ‌بورد (Clipper)"
                    : "سرقت و تعویض آدرس کیف پول تتر/رمزارز";

                String summary = "یک بدافزار در پس‌زمینه تلاش کرد شماره مقصد کپی‌شده (" + analysis.sanitizedValue + ") را به نفع هکر تغییر دهد.";

                SeparApp.getInstance().getRescueLedger().recordRescue(
                    now,
                    dateStr,
                    1,
                    threatTitle,
                    summary,
                    true,
                    "CLIPPER_MALWARE"
                );

                Toast.makeText(context, "🚨 هشدار فوق‌بحرانی: بدافزار کلیپ‌بورد شناسایی شد! شماره کارت یا کیف پول تغییر کرد. هرگز انتقال وجه نزنید!", Toast.LENGTH_LONG).show();
            }

            lastCopiedOriginal = text;
            lastCopiedTimestamp = now;
        } catch (Exception ignored) {}
    }

    private static boolean isValidLuhn(String card) {
        int sum = 0;
        boolean alternate = false;
        for (int i = card.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(card.substring(i, i + 1));
            if (alternate) {
                n *= 2;
                if (n > 9) n = (n % 10) + 1;
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }
}

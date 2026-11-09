package ir.ricksanchez.separ.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class CallForwardingAuditor {

    public static void dialCancelForwarding(Context context) {
        try {
            String encodedHash = Uri.encode("#");
            Uri ussdUri = Uri.parse("tel:" + encodedHash + encodedHash + "002" + encodedHash);
            Intent intent = new Intent(Intent.ACTION_DIAL, ussdUri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception ignored) {}
    }

    public static String getVoiceExplanation() {
        return "اگر تماس گیرنده ناشناسی از شما خواسته کدی شبیه ستاره بیست و یک شماره گیری کنید، پیامک های شما دایورت شده است. این دکمه را لمس کنید تا دایورت خط فوراً لغو شود.";
    }
}

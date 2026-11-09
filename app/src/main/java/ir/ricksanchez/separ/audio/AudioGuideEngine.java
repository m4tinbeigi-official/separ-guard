package ir.ricksanchez.separ.audio;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.speech.tts.TextToSpeech;
import java.util.Locale;

public class AudioGuideEngine implements TextToSpeech.OnInitListener {

    private final Context context;
    private TextToSpeech tts;
    private boolean isTtsReady = false;

    public AudioGuideEngine(Context context) {
        this.context = context.getApplicationContext();
        try {
            this.tts = new TextToSpeech(this.context, this);
        } catch (Exception ignored) {}
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS && tts != null) {
            int result = tts.setLanguage(new Locale("fa", "IR"));
            isTtsReady = (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED);
        }
    }

    public void speak(String text) {
        if (isTtsReady && tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "separ_audio");
        } else {
            playBeep();
        }
    }

    public void playBeep() {
        try {
            ToneGenerator tone = new ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80);
            tone.startTone(ToneGenerator.TONE_PROP_BEEP, 250);
        } catch (Exception ignored) {}
    }

    public void release() {
        if (tts != null) {
            try {
                tts.stop();
                tts.shutdown();
            } catch (Exception ignored) {}
            tts = null;
        }
    }
}

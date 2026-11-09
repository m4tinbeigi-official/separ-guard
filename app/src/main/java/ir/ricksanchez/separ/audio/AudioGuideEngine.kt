package ir.ricksanchez.separ.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.media.AudioManager
import android.speech.tts.TextToSpeech
import java.util.Locale

/**
 * موتور آفلاین پخش راهنماهای صوتی گویا به زبان فارسی برای کاربران کم‌سواد
 */
class AudioGuideEngine(private val context: Context) : TextToSpeech.OnInitListener {

    private var mediaPlayer: MediaPlayer? = null
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (_: Exception) {}
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("fa", "IR"))
            isTtsReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
        }
    }

    /**
     * پخش یک فایل صوتی ذخیره‌شده یا تبدیل متن به گفتار
     */
    fun speak(text: String, rawResId: Int? = null) {
        stop()

        if (rawResId != null) {
            try {
                mediaPlayer = MediaPlayer.create(context, rawResId)?.apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .setUsage(AudioAttributes.USAGE_ASSISTANCE_NAVIGATION_GUIDANCE)
                            .build()
                    )
                    setOnCompletionListener { mp ->
                        mp.release()
                        mediaPlayer = null
                    }
                    start()
                }
                return
            } catch (_: Exception) {}
        }

        // جایگزین TTS یا بیپ صوتی در صورت عدم وجود فایل
        if (isTtsReady && tts != null) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "separ_audio_guide")
        } else {
            playAlertTone()
        }
    }

    private fun playAlertTone() {
        try {
            val tone = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 80)
            tone.startTone(ToneGenerator.TONE_PROP_BEEP, 300)
        } catch (_: Exception) {}
    }

    fun stop() {
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
        tts?.stop()
    }

    fun release() {
        stop()
        tts?.shutdown()
        tts = null
    }
}

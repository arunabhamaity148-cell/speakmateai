package com.example.speech

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.data.model.TtsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TextToSpeechManager(private val context: Context) {

    private val tag = "TextToSpeechManager"
    private var textToSpeech: TextToSpeech? = null
    private var isInitialized = false

    private val _ttsState = MutableStateFlow(TtsState.IDLE)
    val ttsState: StateFlow<TtsState> = _ttsState.asStateFlow()

    private var currentRate = 0.95f
    private var isEnabled = true

    init {
        initializeTts()
    }

    private fun initializeTts() {
        textToSpeech = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.US)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.w(tag, "US English is not supported by device TTS")
                    // Fallback to UK or default
                    textToSpeech?.setLanguage(Locale.UK)
                }
                textToSpeech?.setSpeechRate(currentRate)
                textToSpeech?.setPitch(1.0f)
                setupProgressListener()
                isInitialized = true
                Log.d(tag, "TextToSpeech initialized successfully")
            } else {
                Log.e(tag, "TextToSpeech initialization failed with status $status")
                _ttsState.value = TtsState.ERROR
            }
        }
    }

    private fun setupProgressListener() {
        textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _ttsState.value = TtsState.SPEAKING
            }

            override fun onDone(utteranceId: String?) {
                _ttsState.value = TtsState.IDLE
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _ttsState.value = TtsState.ERROR
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                Log.w(tag, "TTS playback error code $errorCode for utterance $utteranceId")
                _ttsState.value = TtsState.ERROR
            }
        })
    }

    fun setSpeechRate(rate: Float) {
        currentRate = rate.coerceIn(0.5f, 2.0f)
        textToSpeech?.setSpeechRate(currentRate)
    }

    fun setEnabled(enabled: Boolean) {
        isEnabled = enabled
        if (!enabled) {
            stop()
        }
    }

    fun speak(text: String, utteranceId: String = "speakmate_${System.currentTimeMillis()}") {
        if (!isEnabled || text.isBlank()) return

        if (!isInitialized || textToSpeech == null) {
            initializeTts()
            return
        }

        try {
            stop()
            val cleanText = cleanTextForSpeech(text)
            textToSpeech?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        } catch (e: Exception) {
            Log.e(tag, "Error invoking speak", e)
            _ttsState.value = TtsState.ERROR
        }
    }

    fun stop() {
        try {
            textToSpeech?.stop()
        } catch (e: Exception) {
            Log.w(tag, "Error stopping TTS", e)
        } finally {
            _ttsState.value = TtsState.IDLE
        }
    }

    fun shutdown() {
        try {
            textToSpeech?.stop()
            textToSpeech?.shutdown()
        } catch (e: Exception) {
            Log.w(tag, "Error shutting down TTS", e)
        } finally {
            textToSpeech = null
            isInitialized = false
        }
    }

    private fun cleanTextForSpeech(input: String): String {
        // Strip markdown asterisks, emojis or symbols that sound awkward when read aloud
        return input
            .replace(Regex("[*#_`~]"), "")
            .replace(Regex("[\uD83C-\uDBFF\uDC00-\uDFFF]"), "")
            .trim()
    }
}

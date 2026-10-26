package com.example.speech

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import com.example.data.model.SpeechState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class SpeechToTextManager(private val context: Context) {

    private val tag = "SpeechToTextManager"

    private var speechRecognizer: SpeechRecognizer? = null

    private val _speechState = MutableStateFlow(SpeechState.IDLE)
    val speechState: StateFlow<SpeechState> = _speechState.asStateFlow()

    private val _recognizedText = MutableStateFlow("")
    val recognizedText: StateFlow<String> = _recognizedText.asStateFlow()

    private val _partialText = MutableStateFlow("")
    val partialText: StateFlow<String> = _partialText.asStateFlow()

    private val _soundLevel = MutableStateFlow(0f)
    val soundLevel: StateFlow<Float> = _soundLevel.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun isRecognitionAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    fun startListening() {
        if (!isRecognitionAvailable()) {
            _speechState.value = SpeechState.ERROR
            _errorMessage.value = "Speech recognition service is not available on this device. Please use keyboard input."
            return
        }

        try {
            stopListening()
            _errorMessage.value = null
            _recognizedText.value = ""
            _partialText.value = ""
            _speechState.value = SpeechState.LISTENING

            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        Log.d(tag, "onReadyForSpeech")
                        _speechState.value = SpeechState.LISTENING
                    }

                    override fun onBeginningOfSpeech() {
                        Log.d(tag, "onBeginningOfSpeech")
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        // Normalize RMS dB typically in -2dB to 10dB range
                        val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                        _soundLevel.value = normalized
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        Log.d(tag, "onEndOfSpeech")
                        _speechState.value = SpeechState.PROCESSING
                        _soundLevel.value = 0f
                    }

                    override fun onError(error: Int) {
                        val message = when (error) {
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error. Please check your microphone."
                            SpeechRecognizer.ERROR_CLIENT -> "Client error. Please try speaking again."
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required."
                            SpeechRecognizer.ERROR_NETWORK -> "Network connection issue during speech recognition."
                            SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Speech recognition network timeout."
                            SpeechRecognizer.ERROR_NO_MATCH -> "No speech recognized. Please speak clearly into the microphone."
                            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Speech recognition is currently busy."
                            SpeechRecognizer.ERROR_SERVER -> "Speech server error. Please try again."
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "No speech detected. Please try again."
                            else -> "Recognition error ($error). Please try again."
                        }
                        Log.w(tag, "Speech recognition error: $message (code $error)")
                        _soundLevel.value = 0f
                        _errorMessage.value = message
                        _speechState.value = SpeechState.ERROR
                    }

                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()?.trim().orEmpty()
                        Log.d(tag, "onResults: $text")
                        _soundLevel.value = 0f
                        if (text.isNotEmpty()) {
                            _recognizedText.value = text
                            _partialText.value = text
                            _speechState.value = SpeechState.SUCCESS
                        } else {
                            _speechState.value = SpeechState.IDLE
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()?.trim().orEmpty()
                        if (text.isNotEmpty()) {
                            _partialText.value = text
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.US.toLanguageTag())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            }

            speechRecognizer?.startListening(intent)
        } catch (e: Exception) {
            Log.e(tag, "Failed to start speech recognition", e)
            _speechState.value = SpeechState.ERROR
            _errorMessage.value = "Failed to start microphone: ${e.localizedMessage}"
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
        } catch (e: Exception) {
            Log.w(tag, "Error destroying speech recognizer", e)
        } finally {
            speechRecognizer = null
            _soundLevel.value = 0f
            if (_speechState.value == SpeechState.LISTENING) {
                _speechState.value = SpeechState.IDLE
            }
        }
    }

    fun resetState() {
        _speechState.value = SpeechState.IDLE
        _recognizedText.value = ""
        _partialText.value = ""
        _errorMessage.value = null
        _soundLevel.value = 0f
    }

    fun setManualText(text: String) {
        _recognizedText.value = text
        _partialText.value = text
        _speechState.value = SpeechState.SUCCESS
    }
}

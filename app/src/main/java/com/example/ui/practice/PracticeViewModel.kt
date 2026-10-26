package com.example.ui.practice

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.GeminiTutorService
import com.example.data.local.AppDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.GrammarCorrection
import com.example.data.model.ScenarioType
import com.example.data.model.SpeechState
import com.example.data.model.TtsState
import com.example.data.model.UserSettings
import com.example.data.preferences.UserPreferencesManager
import com.example.data.repository.SpeakMateRepository
import com.example.speech.SpeechToTextManager
import com.example.speech.TextToSpeechManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class PracticeUiState(
    val sessionId: Long = 0,
    val sessionTitle: String = "Free Speaking Practice",
    val scenarioContext: String? = null,
    val lessonId: String? = null,
    val messages: List<ChatMessage> = emptyList(),
    val isAiResponding: Boolean = false,
    val currentAiSpokenText: String = "",
    val activeCorrection: GrammarCorrection? = null,
    val showTranscriptReview: Boolean = false,
    val pendingTranscriptText: String = "",
    val durationSeconds: Int = 0,
    val isLiveAi: Boolean = false,
    val aiStatusMessage: String? = null,
    val showPermissionExplanation: Boolean = false,
    val showSaveVocabDialog: Boolean = false,
    val vocabToSaveWord: String = "",
    val vocabToSaveMeaning: String = ""
)

class PracticeViewModel(application: Application) : AndroidViewModel(application) {

    private val tag = "PracticeViewModel"

    private val repository = SpeakMateRepository(
        database = AppDatabase.getInstance(application),
        preferencesManager = UserPreferencesManager(application)
    )

    val speechManager = SpeechToTextManager(application)
    val ttsManager = TextToSpeechManager(application)
    private val tutorService = GeminiTutorService()

    val speechState: StateFlow<SpeechState> = speechManager.speechState
    val recognizedText: StateFlow<String> = speechManager.recognizedText
    val partialText: StateFlow<String> = speechManager.partialText
    val soundLevel: StateFlow<Float> = speechManager.soundLevel
    val speechError: StateFlow<String?> = speechManager.errorMessage

    val ttsState: StateFlow<TtsState> = ttsManager.ttsState

    private val _uiState = MutableStateFlow(PracticeUiState())
    val uiState: StateFlow<PracticeUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var sessionStartTime = System.currentTimeMillis()
    private var mistakeCount = 0

    val userSettings: StateFlow<UserSettings> = repository.userSettings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = UserSettings()
        )

    init {
        viewModelScope.launch {
            userSettings.collect { settings ->
                ttsManager.setSpeechRate(settings.speechRate)
                ttsManager.setEnabled(settings.soundEnabled)
            }
        }
    }

    fun initSession(title: String, type: ScenarioType, scenarioContext: String? = null, lessonId: String? = null, initialMessage: String? = null) {
        if (_uiState.value.sessionId != 0L) return // already initiated

        viewModelScope.launch {
            val sessionId = repository.createSession(title, type)
            sessionStartTime = System.currentTimeMillis()
            startSessionTimer()

            _uiState.value = _uiState.value.copy(
                sessionId = sessionId,
                sessionTitle = title,
                scenarioContext = scenarioContext,
                lessonId = lessonId
            )

            // If there's an opening scenario message, save and speak it
            val opening = initialMessage ?: "Hello! I am SpeakMate AI. Tap the microphone and say anything to begin speaking!"
            repository.saveMessage(sessionId = sessionId, isUser = false, content = opening)
            loadMessages(sessionId)

            if (userSettings.value.soundEnabled) {
                ttsManager.speak(opening)
            }
        }
    }

    private fun startSessionTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _uiState.value = _uiState.value.copy(
                    durationSeconds = _uiState.value.durationSeconds + 1
                )
            }
        }
    }

    private fun loadMessages(sessionId: Long) {
        viewModelScope.launch {
            repository.getMessagesForSession(sessionId).collect { list ->
                _uiState.value = _uiState.value.copy(messages = list)
            }
        }
    }

    fun onMicTapped(hasPermission: Boolean) {
        if (!hasPermission) {
            _uiState.value = _uiState.value.copy(showPermissionExplanation = true)
            return
        }

        when (speechState.value) {
            SpeechState.LISTENING -> {
                speechManager.stopListening()
            }
            SpeechState.PROCESSING -> {
                // Already processing
            }
            else -> {
                ttsManager.stop()
                speechManager.startListening()
            }
        }
    }

    fun onSpeechRecognized(text: String) {
        if (text.isNotBlank()) {
            _uiState.value = _uiState.value.copy(
                showTranscriptReview = true,
                pendingTranscriptText = text
            )
        }
    }

    fun dismissTranscriptReview() {
        _uiState.value = _uiState.value.copy(showTranscriptReview = false)
        speechManager.resetState()
    }

    fun confirmAndSendMessage(finalText: String) {
        val trimmed = finalText.trim()
        if (trimmed.isEmpty() || _uiState.value.isAiResponding) return

        _uiState.value = _uiState.value.copy(
            showTranscriptReview = false,
            isAiResponding = true
        )
        speechManager.resetState()

        viewModelScope.launch {
            val sessionId = _uiState.value.sessionId

            // 1. Save learner message locally
            repository.saveMessage(
                sessionId = sessionId,
                isUser = true,
                content = trimmed,
                originalSpeech = trimmed
            )

            // 2. Build history
            val history = _uiState.value.messages.takeLast(6).map {
                (if (it.isUser) "Learner" else "Tutor") to it.content
            }

            // 3. Request AI Tutor response
            val tutorResult = tutorService.processUserSpeech(
                userText = trimmed,
                history = history,
                scenarioContext = _uiState.value.scenarioContext,
                learnerLevel = userSettings.value.difficultyPreference,
                explanationLanguage = userSettings.value.preferredExplanationLanguage
            )

            if (tutorResult.correction != null) {
                mistakeCount++
            }

            // 4. Save AI message & optional correction to local database
            repository.saveMessage(
                sessionId = sessionId,
                isUser = false,
                content = tutorResult.conversationalReply,
                correction = tutorResult.correction
            )

            _uiState.value = _uiState.value.copy(
                isAiResponding = false,
                currentAiSpokenText = tutorResult.conversationalReply,
                activeCorrection = tutorResult.correction,
                isLiveAi = tutorResult.isLiveAi,
                aiStatusMessage = tutorResult.infoMessage
            )

            // 5. Read aloud response with TextToSpeech
            if (userSettings.value.soundEnabled) {
                ttsManager.speak(tutorResult.conversationalReply)
            }
        }
    }

    fun repeatLastAiSpeech() {
        val lastAiMessage = _uiState.value.messages.lastOrNull { !it.isUser }?.content
        if (!lastAiMessage.isNullOrBlank()) {
            ttsManager.speak(lastAiMessage)
        }
    }

    fun stopSpeaking() {
        ttsManager.stop()
    }

    fun promptToSaveVocabulary(word: String, meaning: String) {
        _uiState.value = _uiState.value.copy(
            showSaveVocabDialog = true,
            vocabToSaveWord = word,
            vocabToSaveMeaning = meaning
        )
    }

    fun dismissSaveVocabDialog() {
        _uiState.value = _uiState.value.copy(showSaveVocabDialog = false)
    }

    fun saveVocabularyItem(word: String, meaning: String, example: String, note: String) {
        viewModelScope.launch {
            repository.addVocabulary(word, meaning, example, note)
            dismissSaveVocabDialog()
        }
    }

    fun dismissPermissionExplanation() {
        _uiState.value = _uiState.value.copy(showPermissionExplanation = false)
    }

    fun closeActiveCorrection() {
        _uiState.value = _uiState.value.copy(activeCorrection = null)
    }

    fun endSession(onFinished: () -> Unit) {
        timerJob?.cancel()
        speechManager.stopListening()
        ttsManager.stop()

        viewModelScope.launch {
            val sessionId = _uiState.value.sessionId
            if (sessionId != 0L) {
                val duration = _uiState.value.durationSeconds
                val msgCount = _uiState.value.messages.size
                repository.updateSessionStats(sessionId, duration, msgCount, mistakeCount)

                // If lesson attached, mark completed
                val lessonId = _uiState.value.lessonId
                if (!lessonId.isNullOrBlank()) {
                    repository.markLessonCompleted(lessonId)
                }
            }
            onFinished()
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        speechManager.stopListening()
        ttsManager.shutdown()
    }
}

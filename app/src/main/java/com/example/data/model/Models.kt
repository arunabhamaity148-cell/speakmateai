package com.example.data.model

enum class SpeechState {
    IDLE,
    LISTENING,
    PROCESSING,
    SUCCESS,
    ERROR
}

enum class TtsState {
    IDLE,
    SPEAKING,
    PAUSED,
    ERROR
}

enum class ScenarioType {
    FREE_TALK,
    ROLE_PLAY,
    DAILY_LESSON
}

data class ChatMessage(
    val id: Long = 0,
    val sessionId: Long,
    val isUser: Boolean,
    val content: String,
    val originalSpeech: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val correction: GrammarCorrection? = null
)

data class GrammarCorrection(
    val id: Long = 0,
    val originalSentence: String,
    val correctedSentence: String,
    val explanationBn: String,
    val naturalAlternative: String = "",
    val errorCategory: String = "Grammar",
    val followUpQuestion: String = ""
)

data class VocabularyItem(
    val id: Long = 0,
    val englishWord: String,
    val bengaliMeaning: String,
    val exampleSentence: String,
    val contextNote: String = "",
    val isLearned: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis()
)

data class RolePlayScenario(
    val id: String,
    val title: String,
    val roleAi: String,
    val roleUser: String,
    val objective: String,
    val initialMessage: String,
    val difficulty: String, // Beginner, Intermediate, Advanced
    val suggestedPhrases: List<String>,
    val iconName: String = "chat"
)

data class LessonStep(
    val title: String,
    val explanation: String,
    val exampleEn: String,
    val exampleBn: String,
    val speakingPrompt: String,
    val expectedKeywords: List<String>
)

data class DailyLesson(
    val id: String,
    val title: String,
    val category: String,
    val durationMinutes: Int,
    val difficulty: String,
    val objective: String,
    val steps: List<LessonStep>,
    val isCompleted: Boolean = false
)

data class PracticeSession(
    val id: Long = 0,
    val title: String,
    val scenarioType: ScenarioType,
    val startedAt: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0,
    val messageCount: Int = 0,
    val mistakeCount: Int = 0
)

data class UserStats(
    val totalSessions: Int = 0,
    val totalPracticeMinutes: Int = 0,
    val lessonsCompleted: Int = 0,
    val vocabularySaved: Int = 0,
    val currentStreak: Int = 0,
    val frequentErrorCategories: List<Pair<String, Int>> = emptyList()
)

data class UserSettings(
    val preferredExplanationLanguage: String = "Bengali", // "Bengali" or "English"
    val speechRate: Float = 0.95f,
    val dailyGoalMinutes: Int = 15,
    val soundEnabled: Boolean = true,
    val difficultyPreference: String = "Beginner", // Beginner, Intermediate, Advanced
    val aiResponseLength: String = "Concise" // Concise, Standard
)

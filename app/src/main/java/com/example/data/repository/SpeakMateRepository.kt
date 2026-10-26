package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.CorrectionEntity
import com.example.data.local.entity.LessonProgressEntity
import com.example.data.local.entity.PracticeSessionEntity
import com.example.data.local.entity.VocabularyEntity
import com.example.data.model.ChatMessage
import com.example.data.model.GrammarCorrection
import com.example.data.model.PracticeSession
import com.example.data.model.ScenarioType
import com.example.data.model.UserSettings
import com.example.data.model.UserStats
import com.example.data.model.VocabularyItem
import com.example.data.preferences.UserPreferencesManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class SpeakMateRepository(
    private val database: AppDatabase,
    private val preferencesManager: UserPreferencesManager
) {
    private val sessionDao = database.sessionDao()
    private val messageDao = database.chatMessageDao()
    private val correctionDao = database.correctionDao()
    private val vocabularyDao = database.vocabularyDao()
    private val lessonDao = database.lessonDao()

    val userSettings: Flow<UserSettings> = preferencesManager.userSettings

    suspend fun updateSettings(settings: UserSettings) {
        preferencesManager.updateSettings(settings)
    }

    // Sessions
    val allSessions: Flow<List<PracticeSession>> = sessionDao.getAllSessions().map { entities ->
        entities.map { it.toDomain() }
    }

    suspend fun createSession(title: String, type: ScenarioType): Long {
        val entity = PracticeSessionEntity(
            title = title,
            scenarioType = type.name
        )
        return sessionDao.insertSession(entity)
    }

    suspend fun updateSessionStats(sessionId: Long, durationSeconds: Int, messageCount: Int, mistakeCount: Int) {
        val session = sessionDao.getSessionById(sessionId) ?: return
        val updated = session.copy(
            durationSeconds = session.durationSeconds + durationSeconds,
            messageCount = session.messageCount + messageCount,
            mistakeCount = session.mistakeCount + mistakeCount
        )
        sessionDao.updateSession(updated)
        preferencesManager.recordPracticeActivity()
    }

    // Messages
    fun getMessagesForSession(sessionId: Long): Flow<List<ChatMessage>> {
        return messageDao.getMessagesForSession(sessionId).map { entities ->
            entities.map { entity ->
                val correction = correctionDao.getCorrectionForMessage(entity.id)?.toDomain()
                ChatMessage(
                    id = entity.id,
                    sessionId = entity.sessionId,
                    isUser = entity.isUser,
                    content = entity.content,
                    originalSpeech = entity.originalSpeech,
                    timestamp = entity.timestamp,
                    correction = correction
                )
            }
        }
    }

    suspend fun saveMessage(
        sessionId: Long,
        isUser: Boolean,
        content: String,
        originalSpeech: String = "",
        correction: GrammarCorrection? = null
    ): Long {
        val messageEntity = ChatMessageEntity(
            sessionId = sessionId,
            isUser = isUser,
            content = content,
            originalSpeech = originalSpeech
        )
        val messageId = messageDao.insertMessage(messageEntity)
        if (correction != null) {
            val correctionEntity = CorrectionEntity(
                messageId = messageId,
                originalSentence = correction.originalSentence,
                correctedSentence = correction.correctedSentence,
                explanationBn = correction.explanationBn,
                naturalAlternative = correction.naturalAlternative,
                errorCategory = correction.errorCategory,
                followUpQuestion = correction.followUpQuestion
            )
            correctionDao.insertCorrection(correctionEntity)
        }
        return messageId
    }

    // Grammar Corrections
    val recentCorrections: Flow<List<GrammarCorrection>> = correctionDao.getRecentCorrections(15).map { list ->
        list.map { it.toDomain() }
    }

    // Vocabulary
    val allVocabulary: Flow<List<VocabularyItem>> = vocabularyDao.getAllVocabulary().map { list ->
        list.map { it.toDomain() }
    }

    suspend fun addVocabulary(
        englishWord: String,
        bengaliMeaning: String,
        exampleSentence: String,
        contextNote: String = ""
    ): Long {
        val entity = VocabularyEntity(
            englishWord = englishWord.trim(),
            bengaliMeaning = bengaliMeaning.trim(),
            exampleSentence = exampleSentence.trim(),
            contextNote = contextNote.trim(),
            isLearned = false
        )
        return vocabularyDao.insertVocabulary(entity)
    }

    suspend fun updateVocabulary(item: VocabularyItem) {
        vocabularyDao.updateVocabulary(item.toEntity())
    }

    suspend fun deleteVocabulary(id: Long) {
        vocabularyDao.deleteVocabularyById(id)
    }

    // Lessons
    val completedLessonIds: Flow<Set<String>> = lessonDao.getAllProgress().map { list ->
        list.filter { it.isCompleted }.map { it.lessonId }.toSet()
    }

    suspend fun markLessonCompleted(lessonId: String, score: Int = 100) {
        lessonDao.setProgress(
            LessonProgressEntity(
                lessonId = lessonId,
                isCompleted = true,
                scorePercentage = score
            )
        )
        preferencesManager.recordPracticeActivity()
    }

    // Combined Stats
    val userStats: Flow<UserStats> = combine(
        sessionDao.getTotalSessionsCount(),
        sessionDao.getTotalDurationSeconds(),
        lessonDao.getCompletedLessonsCount(),
        vocabularyDao.getVocabularyCount(),
        preferencesManager.currentStreak
    ) { sessions, totalDurationSec, lessons, vocab, streak ->
        UserStats(
            totalSessions = sessions,
            totalPracticeMinutes = (totalDurationSec / 60),
            lessonsCompleted = lessons,
            vocabularySaved = vocab,
            currentStreak = streak,
            frequentErrorCategories = emptyList()
        )
    }.combine(correctionDao.getErrorCategoryStats()) { stats, errorStats ->
        stats.copy(frequentErrorCategories = errorStats.map { it.errorCategory to it.count })
    }

    suspend fun clearAllUserData() {
        database.sessionDao().deleteAllSessions()
        database.chatMessageDao().deleteAllMessages()
        database.correctionDao().deleteAllCorrections()
        database.vocabularyDao().deleteAllVocabulary()
        database.lessonDao().deleteAllProgress()
        preferencesManager.clearAllData()
    }

    // Mappers
    private fun PracticeSessionEntity.toDomain() = PracticeSession(
        id = id,
        title = title,
        scenarioType = try {
            ScenarioType.valueOf(scenarioType)
        } catch (_: Exception) {
            ScenarioType.FREE_TALK
        },
        startedAt = startedAt,
        durationSeconds = durationSeconds,
        messageCount = messageCount,
        mistakeCount = mistakeCount
    )

    private fun CorrectionEntity.toDomain() = GrammarCorrection(
        id = id,
        originalSentence = originalSentence,
        correctedSentence = correctedSentence,
        explanationBn = explanationBn,
        naturalAlternative = naturalAlternative,
        errorCategory = errorCategory,
        followUpQuestion = followUpQuestion
    )

    private fun VocabularyEntity.toDomain() = VocabularyItem(
        id = id,
        englishWord = englishWord,
        bengaliMeaning = bengaliMeaning,
        exampleSentence = exampleSentence,
        contextNote = contextNote,
        isLearned = isLearned,
        dateAdded = dateAdded
    )

    private fun VocabularyItem.toEntity() = VocabularyEntity(
        id = id,
        englishWord = englishWord,
        bengaliMeaning = bengaliMeaning,
        exampleSentence = exampleSentence,
        contextNote = contextNote,
        isLearned = isLearned,
        dateAdded = dateAdded
    )
}

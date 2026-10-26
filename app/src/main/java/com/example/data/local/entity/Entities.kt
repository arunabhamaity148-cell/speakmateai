package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "practice_sessions")
data class PracticeSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val scenarioType: String, // FREE_TALK, ROLE_PLAY, DAILY_LESSON
    val startedAt: Long = System.currentTimeMillis(),
    val durationSeconds: Int = 0,
    val messageCount: Int = 0,
    val mistakeCount: Int = 0
)

@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = PracticeSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionId")]
)
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,
    val isUser: Boolean,
    val content: String,
    val originalSpeech: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "grammar_corrections",
    foreignKeys = [
        ForeignKey(
            entity = ChatMessageEntity::class,
            parentColumns = ["id"],
            childColumns = ["messageId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("messageId")]
)
data class CorrectionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val messageId: Long,
    val originalSentence: String,
    val correctedSentence: String,
    val explanationBn: String,
    val naturalAlternative: String,
    val errorCategory: String,
    val followUpQuestion: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "vocabulary_items")
data class VocabularyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val englishWord: String,
    val bengaliMeaning: String,
    val exampleSentence: String,
    val contextNote: String = "",
    val isLearned: Boolean = false,
    val dateAdded: Long = System.currentTimeMillis()
)

@Entity(tableName = "lesson_progress")
data class LessonProgressEntity(
    @PrimaryKey
    val lessonId: String,
    val isCompleted: Boolean,
    val completedAt: Long = System.currentTimeMillis(),
    val scorePercentage: Int = 100
)

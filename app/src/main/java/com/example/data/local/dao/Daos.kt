package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.CorrectionEntity
import com.example.data.local.entity.LessonProgressEntity
import com.example.data.local.entity.PracticeSessionEntity
import com.example.data.local.entity.VocabularyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM practice_sessions ORDER BY startedAt DESC")
    fun getAllSessions(): Flow<List<PracticeSessionEntity>>

    @Query("SELECT * FROM practice_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: Long): PracticeSessionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: PracticeSessionEntity): Long

    @Update
    suspend fun updateSession(session: PracticeSessionEntity)

    @Query("DELETE FROM practice_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: Long)

    @Query("DELETE FROM practice_sessions")
    suspend fun deleteAllSessions()

    @Query("SELECT COUNT(*) FROM practice_sessions")
    fun getTotalSessionsCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(durationSeconds), 0) FROM practice_sessions")
    fun getTotalDurationSeconds(): Flow<Int>
}

@Dao
interface ChatMessageDao {
    @Query("SELECT * FROM chat_messages WHERE sessionId = :sessionId ORDER BY timestamp ASC")
    fun getMessagesForSession(sessionId: Long): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("DELETE FROM chat_messages WHERE sessionId = :sessionId")
    suspend fun deleteMessagesForSession(sessionId: Long)

    @Query("DELETE FROM chat_messages")
    suspend fun deleteAllMessages()
}

@Dao
interface CorrectionDao {
    @Query("SELECT * FROM grammar_corrections WHERE messageId = :messageId LIMIT 1")
    suspend fun getCorrectionForMessage(messageId: Long): CorrectionEntity?

    @Query("SELECT * FROM grammar_corrections ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentCorrections(limit: Int = 10): Flow<List<CorrectionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCorrection(correction: CorrectionEntity): Long

    @Query("SELECT errorCategory, COUNT(*) as count FROM grammar_corrections GROUP BY errorCategory ORDER BY count DESC")
    fun getErrorCategoryStats(): Flow<List<ErrorCategoryStat>>

    @Query("DELETE FROM grammar_corrections")
    suspend fun deleteAllCorrections()
}

data class ErrorCategoryStat(
    val errorCategory: String,
    val count: Int
)

@Dao
interface VocabularyDao {
    @Query("SELECT * FROM vocabulary_items ORDER BY dateAdded DESC")
    fun getAllVocabulary(): Flow<List<VocabularyEntity>>

    @Query("SELECT * FROM vocabulary_items WHERE isLearned = 0 ORDER BY dateAdded DESC")
    fun getUnlearnedVocabulary(): Flow<List<VocabularyEntity>>

    @Query("SELECT COUNT(*) FROM vocabulary_items")
    fun getVocabularyCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVocabulary(item: VocabularyEntity): Long

    @Update
    suspend fun updateVocabulary(item: VocabularyEntity)

    @Delete
    suspend fun deleteVocabulary(item: VocabularyEntity)

    @Query("DELETE FROM vocabulary_items WHERE id = :id")
    suspend fun deleteVocabularyById(id: Long)

    @Query("DELETE FROM vocabulary_items")
    suspend fun deleteAllVocabulary()
}

@Dao
interface LessonDao {
    @Query("SELECT * FROM lesson_progress")
    fun getAllProgress(): Flow<List<LessonProgressEntity>>

    @Query("SELECT * FROM lesson_progress WHERE lessonId = :lessonId LIMIT 1")
    suspend fun getProgressForLesson(lessonId: String): LessonProgressEntity?

    @Query("SELECT COUNT(*) FROM lesson_progress WHERE isCompleted = 1")
    fun getCompletedLessonsCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setProgress(progress: LessonProgressEntity)

    @Query("DELETE FROM lesson_progress")
    suspend fun deleteAllProgress()
}

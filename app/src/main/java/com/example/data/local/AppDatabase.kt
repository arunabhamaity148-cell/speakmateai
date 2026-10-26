package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.ChatMessageDao
import com.example.data.local.dao.CorrectionDao
import com.example.data.local.dao.LessonDao
import com.example.data.local.dao.SessionDao
import com.example.data.local.dao.VocabularyDao
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.CorrectionEntity
import com.example.data.local.entity.LessonProgressEntity
import com.example.data.local.entity.PracticeSessionEntity
import com.example.data.local.entity.VocabularyEntity

@Database(
    entities = [
        PracticeSessionEntity::class,
        ChatMessageEntity::class,
        CorrectionEntity::class,
        VocabularyEntity::class,
        LessonProgressEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sessionDao(): SessionDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun correctionDao(): CorrectionDao
    abstract fun vocabularyDao(): VocabularyDao
    abstract fun lessonDao(): LessonDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "speakmate_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

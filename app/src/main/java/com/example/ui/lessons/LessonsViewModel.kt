package com.example.ui.lessons

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.DailyLesson
import com.example.data.model.LessonsData
import com.example.data.preferences.UserPreferencesManager
import com.example.data.repository.SpeakMateRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LessonsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SpeakMateRepository(
        database = AppDatabase.getInstance(application),
        preferencesManager = UserPreferencesManager(application)
    )

    val completedLessonIds: StateFlow<Set<String>> = repository.completedLessonIds
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    val lessons: StateFlow<List<DailyLesson>> = repository.completedLessonIds.map { completedSet ->
        LessonsData.lessons.map { lesson ->
            lesson.copy(isCompleted = completedSet.contains(lesson.id))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LessonsData.lessons
    )

    fun markCompleted(lessonId: String) {
        viewModelScope.launch {
            repository.markLessonCompleted(lessonId)
        }
    }
}

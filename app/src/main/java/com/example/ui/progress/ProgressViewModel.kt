package com.example.ui.progress

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.PracticeSession
import com.example.data.model.UserStats
import com.example.data.preferences.UserPreferencesManager
import com.example.data.repository.SpeakMateRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ProgressViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SpeakMateRepository(
        database = AppDatabase.getInstance(application),
        preferencesManager = UserPreferencesManager(application)
    )

    val userStats: StateFlow<UserStats> = repository.userStats
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserStats()
        )

    val sessionHistory: StateFlow<List<PracticeSession>> = repository.allSessions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}

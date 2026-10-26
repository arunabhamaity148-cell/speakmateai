package com.example.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.GrammarCorrection
import com.example.data.model.UserStats
import com.example.data.preferences.UserPreferencesManager
import com.example.data.repository.SpeakMateRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val stats: UserStats = UserStats(),
    val recentCorrections: List<GrammarCorrection> = emptyList(),
    val dailyGoalMinutes: Int = 15,
    val greetingName: String = "Learner"
)

class HomeViewModel(application: Application) : AndroidViewModel(application) {

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

    val recentCorrections: StateFlow<List<GrammarCorrection>> = repository.recentCorrections
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val userSettings = repository.userSettings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = com.example.data.model.UserSettings()
        )
}

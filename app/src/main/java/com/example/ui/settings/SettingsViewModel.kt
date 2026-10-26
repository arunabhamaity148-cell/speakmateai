package com.example.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.UserSettings
import com.example.data.preferences.UserPreferencesManager
import com.example.data.repository.SpeakMateRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SpeakMateRepository(
        database = AppDatabase.getInstance(application),
        preferencesManager = UserPreferencesManager(application)
    )

    val settings: StateFlow<UserSettings> = repository.userSettings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettings()
        )

    fun updateSettings(newSettings: UserSettings) {
        viewModelScope.launch {
            repository.updateSettings(newSettings)
        }
    }

    fun clearAllData(onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.clearAllUserData()
            onComplete()
        }
    }
}

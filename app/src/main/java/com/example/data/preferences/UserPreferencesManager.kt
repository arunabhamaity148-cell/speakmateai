package com.example.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "speakmate_prefs")

class UserPreferencesManager(private val context: Context) {

    companion object {
        val KEY_EXPLANATION_LANG = stringPreferencesKey("pref_explanation_lang")
        val KEY_SPEECH_RATE = floatPreferencesKey("pref_speech_rate")
        val KEY_DAILY_GOAL = intPreferencesKey("pref_daily_goal")
        val KEY_SOUND_ENABLED = booleanPreferencesKey("pref_sound_enabled")
        val KEY_DIFFICULTY = stringPreferencesKey("pref_difficulty")
        val KEY_RESPONSE_LENGTH = stringPreferencesKey("pref_response_length")
        val KEY_CURRENT_STREAK = intPreferencesKey("pref_streak")
        val KEY_LAST_PRACTICE_DATE = longPreferencesKey("pref_last_practice_date")
    }

    val userSettings: Flow<UserSettings> = context.dataStore.data.map { prefs ->
        UserSettings(
            preferredExplanationLanguage = prefs[KEY_EXPLANATION_LANG] ?: "Bengali",
            speechRate = prefs[KEY_SPEECH_RATE] ?: 0.95f,
            dailyGoalMinutes = prefs[KEY_DAILY_GOAL] ?: 15,
            soundEnabled = prefs[KEY_SOUND_ENABLED] ?: true,
            difficultyPreference = prefs[KEY_DIFFICULTY] ?: "Beginner",
            aiResponseLength = prefs[KEY_RESPONSE_LENGTH] ?: "Concise"
        )
    }

    val currentStreak: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[KEY_CURRENT_STREAK] ?: 0
    }

    suspend fun updateSettings(settings: UserSettings) {
        context.dataStore.edit { prefs ->
            prefs[KEY_EXPLANATION_LANG] = settings.preferredExplanationLanguage
            prefs[KEY_SPEECH_RATE] = settings.speechRate
            prefs[KEY_DAILY_GOAL] = settings.dailyGoalMinutes
            prefs[KEY_SOUND_ENABLED] = settings.soundEnabled
            prefs[KEY_DIFFICULTY] = settings.difficultyPreference
            prefs[KEY_RESPONSE_LENGTH] = settings.aiResponseLength
        }
    }

    suspend fun recordPracticeActivity() {
        context.dataStore.edit { prefs ->
            val now = System.currentTimeMillis()
            val lastDate = prefs[KEY_LAST_PRACTICE_DATE] ?: 0L
            val currentStreak = prefs[KEY_CURRENT_STREAK] ?: 0

            val calendarNow = Calendar.getInstance().apply { timeInMillis = now }
            val calendarLast = Calendar.getInstance().apply { timeInMillis = lastDate }

            val isSameDay = lastDate > 0 &&
                    calendarNow.get(Calendar.YEAR) == calendarLast.get(Calendar.YEAR) &&
                    calendarNow.get(Calendar.DAY_OF_YEAR) == calendarLast.get(Calendar.DAY_OF_YEAR)

            if (!isSameDay) {
                val calendarYesterday = Calendar.getInstance().apply {
                    timeInMillis = now
                    add(Calendar.DAY_OF_YEAR, -1)
                }
                val isYesterday = lastDate > 0 &&
                        calendarYesterday.get(Calendar.YEAR) == calendarLast.get(Calendar.YEAR) &&
                        calendarYesterday.get(Calendar.DAY_OF_YEAR) == calendarLast.get(Calendar.DAY_OF_YEAR)

                val newStreak = if (isYesterday) currentStreak + 1 else 1
                prefs[KEY_CURRENT_STREAK] = newStreak
                prefs[KEY_LAST_PRACTICE_DATE] = now
            }
        }
    }

    suspend fun clearAllData() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}

package com.example.ui.notebook

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.VocabularyItem
import com.example.data.preferences.UserPreferencesManager
import com.example.data.repository.SpeakMateRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotebookViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SpeakMateRepository(
        database = AppDatabase.getInstance(application),
        preferencesManager = UserPreferencesManager(application)
    )

    val vocabularyList: StateFlow<List<VocabularyItem>> = repository.allVocabulary
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addWord(word: String, meaning: String, example: String, note: String = "") {
        viewModelScope.launch {
            repository.addVocabulary(word, meaning, example, note)
        }
    }

    fun toggleLearned(item: VocabularyItem) {
        viewModelScope.launch {
            repository.updateVocabulary(item.copy(isLearned = !item.isLearned))
        }
    }

    fun deleteWord(id: Long) {
        viewModelScope.launch {
            repository.deleteVocabulary(id)
        }
    }
}

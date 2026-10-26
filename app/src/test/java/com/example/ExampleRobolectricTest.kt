package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ai.GeminiTutorService
import com.example.data.local.AppDatabase
import com.example.data.local.entity.VocabularyEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun readStringFromContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("SpeakMate AI", appName)
    }

    @Test
    fun testRoomDatabaseInsertAndQuery() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getInstance(context)
        val vocabDao = db.vocabularyDao()

        val item = VocabularyEntity(
            englishWord = "Spontaneous",
            bengaliMeaning = "স্বতঃস্ফূর্ত",
            exampleSentence = "She gave a spontaneous answer.",
            contextNote = "Adjective"
        )
        val id = vocabDao.insertVocabulary(item)
        assertTrue(id > 0)

        val list = vocabDao.getAllVocabulary().first()
        assertTrue(list.any { it.englishWord == "Spontaneous" })
    }

    @Test
    fun testOfflineGrammarCorrectionBengali() = runBlocking {
        val tutorService = GeminiTutorService()
        val result = tutorService.processUserSpeech(
            userText = "I am agree with your opinion.",
            history = emptyList()
        )

        assertNotNull(result.correction)
        assertEquals("I agree.", result.correction?.correctedSentence)
        assertTrue(result.correction?.explanationBn?.contains("Agree") == true)
    }
}

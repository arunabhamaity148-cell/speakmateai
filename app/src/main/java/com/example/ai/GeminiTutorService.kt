package com.example.ai

import android.util.Log
import com.example.data.model.GrammarCorrection
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

data class TutorResult(
    val conversationalReply: String,
    val correction: GrammarCorrection?,
    val isLiveAi: Boolean = true,
    val infoMessage: String? = null
)

class GeminiTutorService {

    private val tag = "GeminiTutorService"

    private val _isLiveAiActive = MutableStateFlow(false)
    val isLiveAiActive: StateFlow<Boolean> = _isLiveAiActive.asStateFlow()

    private val _lastAiStatus = MutableStateFlow("Ready")
    val lastAiStatus: StateFlow<String> = _lastAiStatus.asStateFlow()

    private val generativeModel by lazy {
        try {
            val config = generationConfig {
                responseMimeType = "application/json"
                temperature = 0.7f
            }
            Firebase.ai.generativeModel(
                modelName = "gemini-3.8-flash",
                generationConfig = config
            )
        } catch (e: Exception) {
            Log.w(tag, "Failed to initialize Firebase AI generativeModel", e)
            null
        }
    }

    suspend fun processUserSpeech(
        userText: String,
        history: List<Pair<String, String>>,
        scenarioContext: String? = null,
        learnerLevel: String = "Beginner",
        explanationLanguage: String = "Bengali"
    ): TutorResult = withContext(Dispatchers.IO) {
        val trimmed = userText.trim()
        if (trimmed.isEmpty()) {
            return@withContext TutorResult(
                conversationalReply = "I couldn't hear you clearly. Could you please say that again?",
                correction = null,
                isLiveAi = false
            )
        }

        val prompt = TutorPromptTemplates.buildUserTurnPrompt(
            userMessage = trimmed,
            recentHistory = history,
            scenarioContext = scenarioContext,
            learnerLevel = learnerLevel,
            explanationLanguage = explanationLanguage
        )

        try {
            val model = generativeModel
            if (model != null) {
                val response = withTimeout(40_000L) {
                    model.generateContent(prompt)
                }

                val responseText = response.text
                if (!responseText.isNullOrBlank()) {
                    val parsed = parseJsonResponse(responseText)
                    _isLiveAiActive.value = true
                    _lastAiStatus.value = "Connected to Gemini 3.8 Flash"
                    return@withContext parsed.copy(isLiveAi = true)
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(tag, "Live Gemini request failed; switching gracefully to smart offline tutor: ${e.message}")
            _isLiveAiActive.value = false
            _lastAiStatus.value = "Offline Mode: ${e.localizedMessage ?: "Network or backend setup"}"
        }

        // Fallback to offline educational engine with Bengali grammar rules
        return@withContext generateOfflineTutorResponse(
            userInput = trimmed,
            history = history,
            scenarioContext = scenarioContext
        )
    }

    private fun parseJsonResponse(jsonString: String): TutorResult {
        try {
            // Clean markdown wrappers if any
            var clean = jsonString.trim()
            if (clean.startsWith("```json")) {
                clean = clean.substringAfter("```json").substringBeforeLast("```").trim()
            } else if (clean.startsWith("```")) {
                clean = clean.substringAfter("```").substringBeforeLast("```").trim()
            }

            val json = JSONObject(clean)
            val reply = json.optString("conversationalReply", "I understand! Let's keep practicing.")
            val hasCorrection = json.optBoolean("hasCorrection", false)

            val correction = if (hasCorrection) {
                val orig = json.optString("originalSentence", "")
                val corr = json.optString("correctedSentence", "")
                val expl = json.optString("explanationBn", "")
                val alt = json.optString("naturalAlternative", "")
                val cat = json.optString("errorCategory", "Grammar")

                if (orig.isNotBlank() && corr.isNotBlank()) {
                    GrammarCorrection(
                        originalSentence = orig,
                        correctedSentence = corr,
                        explanationBn = expl,
                        naturalAlternative = alt,
                        errorCategory = cat
                    )
                } else null
            } else null

            return TutorResult(
                conversationalReply = reply,
                correction = correction,
                isLiveAi = true
            )
        } catch (e: Exception) {
            Log.e(tag, "Failed to parse JSON response: $jsonString", e)
            return TutorResult(
                conversationalReply = jsonString.take(200),
                correction = null,
                isLiveAi = true
            )
        }
    }

    private fun generateOfflineTutorResponse(
        userInput: String,
        history: List<Pair<String, String>>,
        scenarioContext: String?
    ): TutorResult {
        val lower = userInput.lowercase()

        // 1. Check for prominent grammar errors common among learners
        var detectedCorrection: GrammarCorrection? = null

        when {
            lower.contains("i am agree") || lower.contains("i'm agree") -> {
                detectedCorrection = GrammarCorrection(
                    originalSentence = "I am agree",
                    correctedSentence = "I agree.",
                    explanationBn = "Agree নিজেই একটি verb, তাই 'am' ব্যবহার করতে হয় না। শুধু 'I agree' বলুন।",
                    naturalAlternative = "I completely agree with you.",
                    errorCategory = "Verb Structure"
                )
            }
            Regex("\\b(yesterday|last night|last year|ago)\\b.*\\b(go|see|buy|eat|come)\\b").containsMatchIn(lower) -> {
                val fixed = userInput
                    .replace(Regex("\\bgo\\b", RegexOption.IGNORE_CASE), "went")
                    .replace(Regex("\\bsee\\b", RegexOption.IGNORE_CASE), "saw")
                    .replace(Regex("\\bbuy\\b", RegexOption.IGNORE_CASE), "bought")
                    .replace(Regex("\\beat\\b", RegexOption.IGNORE_CASE), "ate")
                    .replace(Regex("\\bcome\\b", RegexOption.IGNORE_CASE), "came")
                detectedCorrection = GrammarCorrection(
                    originalSentence = userInput,
                    correctedSentence = fixed,
                    explanationBn = "Yesterday বা অতীতের সময় বোঝাতে Past Tense (V2) যেমন went, saw, bought ব্যবহার করতে হয়।",
                    naturalAlternative = "I went there yesterday.",
                    errorCategory = "Tense"
                )
            }
            Regex("\\b(he|she|it|my friend|my brother)\\s+don't\\b").containsMatchIn(lower) -> {
                val fixed = userInput.replace(Regex("\\bdon't\\b", RegexOption.IGNORE_CASE), "doesn't")
                detectedCorrection = GrammarCorrection(
                    originalSentence = userInput,
                    correctedSentence = fixed,
                    explanationBn = "He, She বা Third Person Singular-এর সাথে negative বাক্যে 'doesn't' বসে, 'don't' নয়।",
                    naturalAlternative = fixed,
                    errorCategory = "Subject-Verb Agreement"
                )
            }
            Regex("\\bi\\s+have\\s+\\d{1,2}\\s+years\\s+old\\b").containsMatchIn(lower) -> {
                val age = Regex("\\d{1,2}").find(lower)?.value ?: "20"
                detectedCorrection = GrammarCorrection(
                    originalSentence = userInput,
                    correctedSentence = "I am $age years old.",
                    explanationBn = "ইংরেজি ভাষায় বয়স প্রকাশ করতে 'have' নয়, 'am' ব্যবহার করতে হয়।",
                    naturalAlternative = "I'm $age.",
                    errorCategory = "Verb Form"
                )
            }
            lower.contains("listen me") -> {
                detectedCorrection = GrammarCorrection(
                    originalSentence = "listen me",
                    correctedSentence = "listen to me",
                    explanationBn = "Listen-এর পরে সর্বদা preposition 'to' বসে (যেমন: Listen to me)।",
                    naturalAlternative = "Please listen to what I'm saying.",
                    errorCategory = "Preposition"
                )
            }
            lower.contains("discuss about") -> {
                detectedCorrection = GrammarCorrection(
                    originalSentence = "discuss about",
                    correctedSentence = "discuss",
                    explanationBn = "Discuss শব্দের অর্থই হলো কোনো বিষয়ে আলোচনা করা, তাই এর পর 'about' ব্যবহার করতে হয় না।",
                    naturalAlternative = "Let's discuss this matter.",
                    errorCategory = "Preposition"
                )
            }
            Regex("\\b(an)\\s+[bcdfghjklmnpqrstvwxyz]").containsMatchIn(lower) -> {
                detectedCorrection = GrammarCorrection(
                    originalSentence = userInput,
                    correctedSentence = userInput.replace(Regex("\\ban\\s+", RegexOption.IGNORE_CASE), "a "),
                    explanationBn = "Consonant সাউন্ডের আগে 'an' নয়, 'a' বসে।",
                    naturalAlternative = "a book / a pen",
                    errorCategory = "Article"
                )
            }
        }

        // 2. Formulate conversational reply
        val reply = when {
            lower.contains("hello") || lower.contains("hi") || lower.contains("hey") ->
                "Hello! It's great to practice with you today. What would you like to talk about?"
            lower.contains("my name is") || lower.contains("i am") && lower.contains("from") ->
                "Nice to meet you! It's wonderful to learn English with you. What do you do in your free time?"
            lower.contains("restaurant") || lower.contains("food") || lower.contains("order") ->
                "Welcome to our restaurant! Our special today is grilled chicken pasta. Would you like a table for one or two?"
            lower.contains("direction") || lower.contains("where is") || lower.contains("how to go") ->
                "Go straight for two blocks, then turn left at the traffic signal. Is there anything specific you are looking for?"
            lower.contains("job") || lower.contains("interview") || lower.contains("work") ->
                "Thank you for coming in today. Could you briefly tell me about your background and strongest skills?"
            lower.contains("weather") || lower.contains("rain") || lower.contains("sunny") ->
                "The weather can really change your mood! Do you prefer warm sunny days or cozy rainy weather?"
            userInput.endsWith("?") ->
                "That's a very good question! Personally, I believe daily practice is the fastest key to fluency. What do you think?"
            detectedCorrection != null ->
                "Great effort! I understood what you meant. Let's keep going: What else did you do today?"
            else ->
                "That's very interesting! Could you tell me a little bit more about that in one or two sentences?"
        }

        return TutorResult(
            conversationalReply = reply,
            correction = detectedCorrection,
            isLiveAi = false,
            infoMessage = "Practicing in offline smart mode. Connect to internet for live Gemini feedback."
        )
    }
}

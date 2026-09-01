package com.example.lexicon.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lexicon.data.LearningStatus
import com.example.lexicon.data.SettingsRepository
import com.example.lexicon.data.VocabularyRepository
import com.example.lexicon.data.WordEntity
import com.example.lexicon.domain.VocabularyParser
import com.example.lexicon.data.AppBackup
import com.example.lexicon.data.AppSettingsBackup
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

class LexiconViewModel(
    private val repository: VocabularyRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    val allWords = repository.allWords.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val favorites = repository.favorites.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    val dailyGoal = settingsRepository.dailyGoal.stateIn(viewModelScope, SharingStarted.Lazily, 5)
    val streak = settingsRepository.streak.stateIn(viewModelScope, SharingStarted.Lazily, 0)
    
    val weeklyGoal = settingsRepository.weeklyGoal.stateIn(viewModelScope, SharingStarted.Lazily, 20)
    val currentWeekLearned = settingsRepository.currentWeekLearned.stateIn(viewModelScope, SharingStarted.Lazily, 0)
    
    private val _aiProvider = kotlinx.coroutines.flow.MutableStateFlow("Gemini")
    val aiProvider = _aiProvider.asStateFlow()
    
    private val _geminiApiKey = kotlinx.coroutines.flow.MutableStateFlow("")
    val geminiApiKey = _geminiApiKey.asStateFlow()
    private val _geminiModel = kotlinx.coroutines.flow.MutableStateFlow("gemini-3.5-flash")
    val geminiModel = _geminiModel.asStateFlow()
    
    private val _openaiApiKey = kotlinx.coroutines.flow.MutableStateFlow("")
    val openaiApiKey = _openaiApiKey.asStateFlow()
    private val _openaiModel = kotlinx.coroutines.flow.MutableStateFlow("gpt-4o-mini")
    val openaiModel = _openaiModel.asStateFlow()
    
    private val _openrouterApiKey = kotlinx.coroutines.flow.MutableStateFlow("")
    val openrouterApiKey = _openrouterApiKey.asStateFlow()
    private val _openrouterModel = kotlinx.coroutines.flow.MutableStateFlow("deepseek/deepseek-v4-flash")
    val openrouterModel = _openrouterModel.asStateFlow()
    
    private val _gapgptApiKey = kotlinx.coroutines.flow.MutableStateFlow("")
    val gapgptApiKey = _gapgptApiKey.asStateFlow()
    private val _gapgptModel = kotlinx.coroutines.flow.MutableStateFlow("gpt-4o-mini")
    val gapgptModel = _gapgptModel.asStateFlow()
    
    val homeWordOrder = settingsRepository.homeWordOrder.stateIn(viewModelScope, SharingStarted.Lazily, "Random")

    val customAiPrompt = settingsRepository.customAiPrompt.stateIn(viewModelScope, SharingStarted.Lazily, SettingsRepository.DEFAULT_PROMPT)
    val showPersianPronunciation = settingsRepository.showPersianPronunciation.stateIn(viewModelScope, SharingStarted.Lazily, true)
    val defaultVocabularyInput = settingsRepository.defaultVocabularyInput.stateIn(viewModelScope, SharingStarted.Lazily, "Copy & Paste")
    
    private val randomSeed = kotlin.random.Random.nextInt()
    
    private val sessionAddedWords = kotlinx.coroutines.flow.MutableStateFlow<List<String>>(emptyList())

    val feedWords = combine(allWords, settingsRepository.homeWordOrder, sessionAddedWords) { words, order, sessionWords ->
        val (sessionItems, otherItems) = words.partition { it.word in sessionWords }
        val sortedSessionItems = sessionItems.sortedBy { sessionWords.indexOf(it.word) }
        
        val sortedOtherItems = when (order) {
            "Oldest first" -> otherItems.sortedBy { it.createdAt }
            "Random" -> otherItems.sortedBy { it.id.hashCode() xor randomSeed }
            else -> otherItems.sortedByDescending { it.createdAt } // "Newest first"
        }
        sortedSessionItems + sortedOtherItems
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    
    init {
        viewModelScope.launch { _aiProvider.value = settingsRepository.aiProvider.first() }
        viewModelScope.launch { _geminiApiKey.value = settingsRepository.geminiApiKey.first() }
        viewModelScope.launch { _geminiModel.value = settingsRepository.geminiModel.first() }
        viewModelScope.launch { _openaiApiKey.value = settingsRepository.openaiApiKey.first() }
        viewModelScope.launch { _openaiModel.value = settingsRepository.openaiModel.first() }
        viewModelScope.launch { _openrouterApiKey.value = settingsRepository.openrouterApiKey.first() }
        viewModelScope.launch { _openrouterModel.value = settingsRepository.openrouterModel.first() }
        viewModelScope.launch { _gapgptApiKey.value = settingsRepository.gapgptApiKey.first() }
        viewModelScope.launch { _gapgptModel.value = settingsRepository.gapgptModel.first() }
        // Calendar-based week transition removed.
        // Weeks now progress linearly based on completing the weekly goal.
    }
    
    private suspend fun checkWeekTransition() {
        // No longer tied to calendar weeks.
    }

    suspend fun checkWordExists(word: String): Boolean {
        return repository.getWordByText(word) != null
    }

    fun addWordFromText(text: String, overrideContext: String? = null, overrideSource: String? = null): Boolean {
        var parsedWord = VocabularyParser.parse(text) ?: return false
        if (overrideContext != null) parsedWord = parsedWord.copy(context = overrideContext.ifBlank { "N/A" })
        if (overrideSource != null) parsedWord = parsedWord.copy(source = overrideSource.ifBlank { "N/A" })
        viewModelScope.launch {
            val existing = repository.getWordByText(parsedWord.word)
            if (existing != null) {
                repository.update(parsedWord.copy(id = existing.id, createdAt = existing.createdAt))
            } else {
                repository.insert(parsedWord)
                sessionAddedWords.value = listOf(parsedWord.word) + sessionAddedWords.value
            }
        }
        return true
    }

    fun toggleFavorite(word: WordEntity) {
        viewModelScope.launch {
            repository.update(word.copy(favorite = !word.favorite))
        }
    }
    
    fun toggleBookmark(word: WordEntity) {
        viewModelScope.launch {
            repository.update(word.copy(bookmarked = !word.bookmarked))
        }
    }
    
    fun markAsLearned(word: WordEntity) {
        viewModelScope.launch {
            val newStatus = if (word.learningStatus == LearningStatus.MASTERED) {
                LearningStatus.NEW
            } else {
                LearningStatus.MASTERED
            }
            
            var newCollocations = word.collocations ?: ""
            val currentWeekNum = settingsRepository.currentWeekNumber.first()
            val weekTag = "Week $currentWeekNum"
            
            if (newStatus == LearningStatus.MASTERED && word.learningStatus != LearningStatus.MASTERED) {
                val current = settingsRepository.currentWeekLearned.first()
                val weeklyGoal = settingsRepository.weeklyGoal.first()
                
                if (!newCollocations.contains(weekTag)) {
                    newCollocations = if (newCollocations.isNotBlank()) "$newCollocations\n$weekTag" else weekTag
                }

                if (current + 1 >= weeklyGoal) {
                    settingsRepository.setCurrentWeekLearned(current + 1)
                    repository.update(word.copy(learningStatus = newStatus, collocations = newCollocations.ifBlank { null }))
                    kotlinx.coroutines.delay(800)
                    settingsRepository.setCurrentWeekLearned(0)
                    settingsRepository.setCurrentWeekNumber(currentWeekNum + 1)
                    return@launch
                } else {
                    settingsRepository.setCurrentWeekLearned(current + 1)
                }
            } else if (newStatus != LearningStatus.MASTERED && word.learningStatus == LearningStatus.MASTERED) {
                val regex = Regex("Week \\d+")
                val matches = regex.findAll(newCollocations)
                for (match in matches) {
                    val weekName = match.value
                    newCollocations = newCollocations.replace(weekName, "").trim()
                    
                    if (weekName == weekTag) {
                        val current = settingsRepository.currentWeekLearned.first()
                        if (current > 0) {
                            settingsRepository.setCurrentWeekLearned(current - 1)
                        }
                    }
                }
                newCollocations = newCollocations.split("\n").map { it.trim() }.filter { it.isNotEmpty() }.joinToString("\n")
            }
            
            repository.update(word.copy(learningStatus = newStatus, collocations = newCollocations.ifBlank { null }))
        }
    }
    
    fun deleteCollocation(collocationName: String) {
        viewModelScope.launch {
            val words = repository.allWords.first()
            words.forEach { word ->
                val colls = word.collocations
                if (colls != null && colls.contains(collocationName)) {
                    val newColls = colls.split("\n")
                        .map { it.trim() }
                        .filter { it.isNotEmpty() && it != collocationName }
                        .joinToString("\n")
                    repository.update(word.copy(collocations = newColls.ifBlank { null }))
                }
            }
        }
    }
    
    fun markAsViewed(word: WordEntity) {
        viewModelScope.launch {
            repository.update(word.copy(lastViewedAt = System.currentTimeMillis()))
        }
    }
    
    fun recordPracticeResult(word: WordEntity, isCorrect: Boolean) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val dayMillis = 24L * 60 * 60 * 1000
            
            var newCorrect = word.correctAnswers
            var newIncorrect = word.incorrectAnswers
            var newConsecutiveCorrectAfterStruggling = word.consecutiveCorrectAfterStruggling
            var newReviewCount = word.reviewCount
            var newNextReviewAt = word.nextReviewAt
            var newLearningStatus = word.learningStatus
            
            if (isCorrect) {
                newCorrect++
                newReviewCount++
                if (newIncorrect >= 2) {
                    newConsecutiveCorrectAfterStruggling++
                }
                
                val intervalDays = when (newReviewCount) {
                    1 -> 1L
                    2 -> 3L
                    3 -> 7L
                    4 -> 14L
                    else -> 30L
                }
                newNextReviewAt = now + (intervalDays * dayMillis)
                
            } else {
                newIncorrect++
                newReviewCount = 0
                newConsecutiveCorrectAfterStruggling = 0
                newNextReviewAt = now
            }
            
            repository.update(
                word.copy(
                    correctAnswers = newCorrect,
                    incorrectAnswers = newIncorrect,
                    consecutiveCorrectAfterStruggling = newConsecutiveCorrectAfterStruggling,
                    reviewCount = newReviewCount,
                    lastReviewedAt = now,
                    nextReviewAt = newNextReviewAt,
                    learningStatus = newLearningStatus
                )
            )
        }
    }
    
    fun deleteWord(word: WordEntity) {
        viewModelScope.launch { repository.delete(word.id) }
    }
    
    fun updateDailyGoal(goal: Int) {
        viewModelScope.launch { settingsRepository.setDailyGoal(goal) }
    }
    
    fun updateWeeklyGoal(goal: Int) {
        viewModelScope.launch { settingsRepository.setWeeklyGoal(goal) }
    }
    
    fun updateAiProvider(value: String) {
        _aiProvider.value = value
        viewModelScope.launch { settingsRepository.setAiProvider(value) }
    }
    
    fun updateGeminiApiKey(value: String) {
        _geminiApiKey.value = value
        viewModelScope.launch { settingsRepository.setGeminiApiKey(value) }
    }
    fun updateGeminiModel(value: String) {
        _geminiModel.value = value
        viewModelScope.launch { settingsRepository.setGeminiModel(value) }
    }
    
    fun updateOpenaiApiKey(value: String) {
        _openaiApiKey.value = value
        viewModelScope.launch { settingsRepository.setOpenaiApiKey(value) }
    }
    fun updateOpenaiModel(value: String) {
        _openaiModel.value = value
        viewModelScope.launch { settingsRepository.setOpenaiModel(value) }
    }
    
    fun updateOpenrouterApiKey(value: String) {
        _openrouterApiKey.value = value
        viewModelScope.launch { settingsRepository.setOpenrouterApiKey(value) }
    }
    fun updateOpenrouterModel(value: String) {
        _openrouterModel.value = value
        viewModelScope.launch { settingsRepository.setOpenrouterModel(value) }
    }
    
    fun updateGapgptApiKey(value: String) {
        _gapgptApiKey.value = value
        viewModelScope.launch { settingsRepository.setGapgptApiKey(value) }
    }
    fun updateGapgptModel(value: String) {
        _gapgptModel.value = value
        viewModelScope.launch { settingsRepository.setGapgptModel(value) }
    }

    fun updateHomeWordOrder(order: String) { viewModelScope.launch { settingsRepository.setHomeWordOrder(order) } }
    fun updateShowPersianPronunciation(show: Boolean) { viewModelScope.launch { settingsRepository.setShowPersianPronunciation(show) } }
    fun updateDefaultVocabularyInput(input: String) { viewModelScope.launch { settingsRepository.setDefaultVocabularyInput(input) } }

    fun updateCustomAiPrompt(prompt: String) { viewModelScope.launch { settingsRepository.setCustomAiPrompt(prompt) } }

    fun restoreDefaultPrompt() {
        viewModelScope.launch {
            settingsRepository.setCustomAiPrompt(SettingsRepository.DEFAULT_PROMPT)
        }
    }
    
    private suspend fun testGenericOpenAIEndpoint(url: String, key: String, model: String, providerName: String): String {
        if (key.isBlank()) return "Error: API Key is empty"
        return try {
            val client = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build()
            val jsonMediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val requestBody = JSONObject()
                .put("model", model)
                .put("messages", JSONArray().put(JSONObject().put("role", "user").put("content", "Hello, respond with a short greeting.")))
                .toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $key")
                .post(requestBody)
                .build()
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                "Success: Connected to $providerName API"
            } else {
                "Error: HTTP ${response.code} ${response.message}"
            }
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    suspend fun testAiConnection(): String {
        return withContext(Dispatchers.IO) {
            when (aiProvider.first()) {
                "OpenAI / ChatGPT" -> {
                    testGenericOpenAIEndpoint(
                        url = "https://api.openai.com/v1/chat/completions",
                        key = openaiApiKey.first(),
                        model = openaiModel.first(),
                        providerName = "OpenAI"
                    )
                }
                "OpenRouter" -> {
                    testGenericOpenAIEndpoint(
                        url = "https://openrouter.ai/api/v1/chat/completions",
                        key = openrouterApiKey.first(),
                        model = openrouterModel.first(),
                        providerName = "OpenRouter"
                    )
                }
                "GapGPT" -> {
                    testGapGPTEndpoint(
                        url = "https://api.gapgpt.app/v1/chat/completions",
                        key = gapgptApiKey.first(),
                        model = gapgptModel.first()
                    )
                }
                else -> { // Gemini
                    val key = geminiApiKey.first()
                    val model = geminiModel.first()
                    if (key.isBlank()) return@withContext "Error: API Key is empty"
                    try {
                        val client = OkHttpClient.Builder()
                            .connectTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .build()
                        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$key"
                        val jsonMediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
                        val parts = JSONArray().put(JSONObject().put("text", "Hello, respond with a short greeting."))
                        val contents = JSONArray().put(JSONObject().put("parts", parts))
                        val requestBody = JSONObject().put("contents", contents).toString().toRequestBody(jsonMediaType)
                        val request = Request.Builder().url(url).post(requestBody).build()
                        val response = client.newCall(request).execute()
                        if (response.isSuccessful) {
                            "Success: Connected to Gemini API"
                        } else {
                            "Error: HTTP ${response.code} ${response.message}"
                        }
                    } catch (e: Exception) {
                        "Error: ${e.message}"
                    }
                }
            }
        }
    }
    
    private suspend fun generateGenericOpenAI(url: String, key: String, model: String, finalPrompt: String): Result<String> {
        if (key.isBlank()) return Result.failure(Exception("API Key is missing. Please configure it in Settings."))
        return try {
            val client = OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).build()
            val jsonMediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val requestBody = JSONObject()
                .put("model", model)
                .put("messages", JSONArray().put(JSONObject().put("role", "user").put("content", finalPrompt)))
                .toString().toRequestBody(jsonMediaType)
            
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $key")
                .post(requestBody)
                .build()
            val response = client.newCall(request).execute()
            
            if (response.isSuccessful) {
                val responseData = response.body?.string()
                if (responseData != null) {
                    val jsonObj = JSONObject(responseData)
                    val choices = jsonObj.optJSONArray("choices")
                    if (choices != null && choices.length() > 0) {
                        val message = choices.getJSONObject(0).optJSONObject("message")
                        if (message != null) {
                            val aiText = message.optString("content", "")
                            return Result.success(aiText)
                        }
                    }
                }
                Result.failure(Exception("Invalid response format from AI."))
            } else {
                Result.failure(Exception("HTTP ${response.code}: ${response.message}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun generateVocabulary(word: String, contextStr: String?, sourceStr: String?): Result<String> {
        return withContext(Dispatchers.IO) {
            val provider = aiProvider.first()
            val rawPrompt = customAiPrompt.first()
            
            var finalPrompt = rawPrompt
            
            // Replace first occurrence of {{}} with word
            val idx1 = finalPrompt.indexOf("{{}}")
            if (idx1 != -1) finalPrompt = finalPrompt.replaceFirst("{{}}", word)
            
            // Replace second occurrence with context
            val idx2 = finalPrompt.indexOf("{{}}")
            if (idx2 != -1) finalPrompt = finalPrompt.replaceFirst("{{}}", contextStr ?: "")
            
            // Replace third occurrence with source
            val idx3 = finalPrompt.indexOf("{{}}")
            if (idx3 != -1) finalPrompt = finalPrompt.replaceFirst("{{}}", sourceStr ?: "")
            
            when (provider) {
                "OpenAI / ChatGPT" -> {
                    generateGenericOpenAI(
                        url = "https://api.openai.com/v1/chat/completions",
                        key = openaiApiKey.first(),
                        model = openaiModel.first(),
                        finalPrompt = finalPrompt
                    )
                }
                "OpenRouter" -> {
                    generateGenericOpenAI(
                        url = "https://openrouter.ai/api/v1/chat/completions",
                        key = openrouterApiKey.first(),
                        model = openrouterModel.first(),
                        finalPrompt = finalPrompt
                    )
                }
                "GapGPT" -> {
                    generateGapGPT(
                        url = "https://api.gapgpt.app/v1/chat/completions",
                        key = gapgptApiKey.first(),
                        model = gapgptModel.first(),
                        finalPrompt = finalPrompt
                    )
                }
                else -> { // Gemini
                    val key = geminiApiKey.first()
                    val model = geminiModel.first()
                    if (key.isBlank()) return@withContext Result.failure(Exception("API Key is missing. Please configure it in Settings."))
                    
                    try {
                        val client = OkHttpClient.Builder()
                            .connectTimeout(60, TimeUnit.SECONDS)
                            .readTimeout(60, TimeUnit.SECONDS)
                            .build()
                        val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$key"
                        val jsonMediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
                        val parts = JSONArray().put(JSONObject().put("text", finalPrompt))
                        val contents = JSONArray().put(JSONObject().put("parts", parts))
                        val requestBody = JSONObject().put("contents", contents).toString().toRequestBody(jsonMediaType)
                        
                        val request = Request.Builder().url(url).post(requestBody).build()
                        val response = client.newCall(request).execute()
                        
                        if (response.isSuccessful) {
                            val responseData = response.body?.string()
                            if (responseData != null) {
                                val jsonObj = JSONObject(responseData)
                                val candidates = jsonObj.optJSONArray("candidates")
                                if (candidates != null && candidates.length() > 0) {
                                    val content = candidates.getJSONObject(0).optJSONObject("content")
                                    val resParts = content?.optJSONArray("parts")
                                    if (resParts != null && resParts.length() > 0) {
                                        val aiText = resParts.getJSONObject(0).optString("text", "")
                                        return@withContext Result.success(aiText)
                                    }
                                }
                            }
                            Result.failure(Exception("Invalid response format from AI."))
                        } else {
                            Result.failure(Exception("HTTP ${response.code}: ${response.message}"))
                        }
                    } catch (e: Exception) {
                        Result.failure(e)
                    }
                }
            }
        }
    }

    suspend fun exportData(): String {
        return withContext(Dispatchers.IO) {
            val words = repository.allWords.first()
            val settingsBackup = AppSettingsBackup(
                dailyGoal = settingsRepository.dailyGoal.first(),
                streak = settingsRepository.streak.first(),
                lastLearningDay = settingsRepository.lastLearningDay.first(),
                weeklyGoal = settingsRepository.weeklyGoal.first(),
                currentWeekLearned = settingsRepository.currentWeekLearned.first(),
                currentWeekStart = settingsRepository.currentWeekStart.first(),
                currentWeekNumber = settingsRepository.currentWeekNumber.first(),
                previousWeeksSummary = settingsRepository.previousWeeksSummary.first(),
                homeWordOrder = settingsRepository.homeWordOrder.first(),
                customAiPrompt = settingsRepository.customAiPrompt.first()
            )
            val backup = AppBackup(
                exportedAt = System.currentTimeMillis(),
                words = words,
                settings = settingsBackup
            )
            val moshi = Moshi.Builder().build()
            val adapter = moshi.adapter(AppBackup::class.java)
            adapter.toJson(backup)
        }
    }

    suspend fun importData(json: String, isAddMode: Boolean): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val moshi = Moshi.Builder().build()
                val adapter = moshi.adapter(AppBackup::class.java)
                val backup = adapter.fromJson(json) ?: return@withContext false
                
                if (isAddMode) {
                    val existingWords = repository.allWords.first().associateBy { it.word }
                    val wordsToInsert = backup.words.filter { !existingWords.containsKey(it.word) }
                        .map { it.copy(id = 0) } // Reset ID to let Room auto-generate
                    if (wordsToInsert.isNotEmpty()) {
                        repository.insertAll(wordsToInsert)
                    }
                } else {
                    repository.deleteAll()
                    repository.insertAll(backup.words)
                    
                    settingsRepository.setDailyGoal(backup.settings.dailyGoal)
                    settingsRepository.setStreak(backup.settings.streak)
                    settingsRepository.setLastLearningDay(backup.settings.lastLearningDay)
                    settingsRepository.setWeeklyGoal(backup.settings.weeklyGoal)
                    settingsRepository.setCurrentWeekLearned(backup.settings.currentWeekLearned)
                    settingsRepository.setCurrentWeekStart(backup.settings.currentWeekStart)
                    backup.settings.currentWeekNumber?.let { settingsRepository.setCurrentWeekNumber(it) }
                    settingsRepository.setPreviousWeeksSummary(backup.settings.previousWeeksSummary)
                    backup.settings.homeWordOrder?.let { settingsRepository.setHomeWordOrder(it) }
                    settingsRepository.setCustomAiPrompt(backup.settings.customAiPrompt)
                }
                
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    private suspend fun testGapGPTEndpoint(url: String, key: String, model: String): String {
        if (key.isBlank()) return "Error: API Key is empty"
        return try {
            val client = OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).build()
            val jsonMediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val requestBody = JSONObject()
                .put("model", model)
                .put("messages", JSONArray().put(JSONObject().put("role", "user").put("content", "Hello, respond with a short greeting.")))
                .toString().toRequestBody(jsonMediaType)
            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $key")
                .post(requestBody)
                .build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    "Success: Connected to GapGPT API"
                } else {
                    val errorBody = response.body?.string() ?: ""
                    var errorMsg = "HTTP ${response.code} ${response.message}"
                    try {
                        val errObj = JSONObject(errorBody)
                        if (errObj.has("error")) {
                            val errStr = errObj.optJSONObject("error")?.optString("message") ?: errObj.optString("error")
                            if (errStr.isNotBlank()) errorMsg = "Error: $errStr"
                        }
                    } catch (e: Exception) {}
                    errorMsg
                }
            }
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    private suspend fun generateGapGPT(url: String, key: String, model: String, finalPrompt: String): Result<String> {
        if (key.isBlank()) return Result.failure(Exception("API Key is missing. Please configure it in Settings."))
        return try {
            val client = OkHttpClient.Builder().connectTimeout(60, TimeUnit.SECONDS).readTimeout(60, TimeUnit.SECONDS).build()
            val jsonMediaType = "application/json; charset=utf-8".toMediaTypeOrNull()
            val requestBody = JSONObject()
                .put("model", model)
                .put("messages", JSONArray().put(JSONObject().put("role", "user").put("content", finalPrompt)))
                .toString().toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer $key")
                .post(requestBody)
                .build()
            
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val responseData = response.body?.string()
                    if (responseData != null) {
                        val jsonObj = JSONObject(responseData)
                        val choices = jsonObj.optJSONArray("choices")
                        if (choices != null && choices.length() > 0) {
                            val message = choices.getJSONObject(0).optJSONObject("message")
                            if (message != null) {
                                val aiText = message.optString("content", "")
                                return@use Result.success(aiText)
                            }
                        }
                    }
                    Result.failure(Exception("Invalid response format from AI."))
                } else {
                    val errorBody = response.body?.string() ?: ""
                    var errorMsg = "HTTP ${response.code}: ${response.message}"
                    try {
                        val errObj = JSONObject(errorBody)
                        if (errObj.has("error")) {
                            val errStr = errObj.optJSONObject("error")?.optString("message") ?: errObj.optString("error")
                            if (errStr.isNotBlank()) errorMsg = errStr
                        }
                    } catch (e: Exception) {}
                    Result.failure(Exception(errorMsg))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

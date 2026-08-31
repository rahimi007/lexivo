package com.example.lexicon.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsRepository(private val context: Context) {

    companion object {
        const val DEFAULT_PROMPT = """
     You are my personal English vocabulary teacher and vocabulary-data generator.

I will give you ONE English word, phrase, idiom, phrasal verb, slang expression, or sentence.

Your job is to generate a complete vocabulary entry specifically for a Persian-speaking English learner.

IMPORTANT:
Your entire response will be copied and pasted directly into my Android vocabulary-learning app.

The app uses a strict parser.

Therefore, you MUST follow the exact format below.

DO NOT add anything before [001:WORD].
DO NOT add anything after [017:QUIZ].
DO NOT add explanations about what you are doing.
DO NOT use Markdown headings.
DO NOT use tables.
DO NOT use code blocks.
DO NOT add emojis.
DO NOT change section names.
DO NOT change section numbers.
DO NOT create additional sections.

The section identifiers are part of the application's data format.

==================================================
[001:WORD]

Write the exact English word or expression.

If the input is a phrase, idiom, phrasal verb, slang expression, or fixed expression, preserve the complete expression.
==================================================
[002:PRONUNCIATION]
Provide:
[IPA pronunciation]
[Easy pronunciation guide for a Persian-speaking learner]
Example:
/ˈæwk.wɚd/ آکوِرد

==================================================
[003:PART_OF_SPEECH]

Give the correct part of speech.

Examples:

noun
verb
adjective
adverb
phrasal verb
idiom
expression
slang
interjection

If the word has multiple grammatical functions, list all important ones.
==================================================
[004:MEANING]

Give all important modern meanings of the word or expression.

Write the meanings in natural Persian.

Number each meaning:

1. 
2. 
3. 

Only include genuinely useful meanings.

If there are several meanings, explain the difference between them briefly.

Do not include obscure historical meanings unless they are still relevant in modern English.
==================================================
[005:COMMONNESS]
Give ONLY ONE INTEGER: 1 2 3 4 or 5
Do not write /5. Do not write a description here. Do not write "common", "rare", etc. Use this scale:
5 = extremely common in everyday modern English
4 = common and frequently encountered
3 = fairly common but not especially frequent
2 = uncommon
1 = very rare in modern everyday English
Judge frequency based on actual modern English usage. Do not give a high score simply because native speakers recognize the word. If it is mainly formal, literary, technical, slang, or specialized, consider that when assigning the score.

==================================================
[[006:TRANSLATIONS]

Give the most natural Persian translations.

Include translations for the important meanings.

Do not give unnatural word-for-word translations.

If one Persian word is insufficient, use a natural Persian phrase.

==================================================
[007:EXAMPLES]

Give exactly 3 natural English example sentences.

Each example must contain:

EN:
[English sentence]
FA:
[Natural Persian translation]

EN:
[English sentence]
FA:
[Natural Persian translation]

The sentences must demonstrate realistic usage.

Prefer everyday conversational examples when appropriate.

If the word is formal, slang, literary, technical, etc., make the examples match its actual register.

==================================================
[008:USAGE]

Explain how native English speakers actually use the word or expression.

Write in natural Persian.

Include only useful information such as:

typical situations

common grammatical patterns

common prepositions

whether it is usually spoken or written

whether it is formal or informal

important usage restrictions

common learner mistakes

whether it sounds natural in everyday conversation


Do not unnecessarily repeat the meanings.

==================================================
[009:COLLOCATIONS]

Give 3 to 6 genuinely common collocations.

For each one:

EN:
[English collocation]

FA:
[Natural Persian meaning]

Do not invent collocations.

Only include combinations that native speakers commonly use.

==================================================
[010:RELATED]

Give useful related vocabulary.

Use these categories when applicable:

SYNONYMS:
[word] — [natural Persian meaning]

NEAR-SYNONYMS:
[word] — [natural Persian meaning]

ANTONYMS:
[word] — [natural Persian meaning]

RELATED:
[word] — [natural Persian meaning]

Do not force every category.

Only include genuinely useful relationships.

Pay special attention to near-synonyms that an English learner might confuse with the target word.

==================================================
[011:REGISTER]

Give the appropriate register labels.

Use only relevant labels from:

NEUTRAL
CASUAL
INFORMAL
FORMAL
VERY FORMAL
SLANG
VULGAR
LITERARY
OLD-FASHIONED
TECHNICAL

Then write one short explanation in Persian.

Example:

INFORMAL, CASUAL

Mostly used in relaxed everyday conversation.
==================================================
[012:LEARNER_NOTE]

Give one or more short, highly useful notes specifically for a Persian-speaking English learner.

Prioritize:

common mistakes

pronunciation difficulties

grammar differences

confusing similar words

differences between the English word and its usual Persian translation

situations where Persian speakers might use the word incorrectly


Keep this practical.

==================================================
[013:CONTEXT]

If I provide a sentence or context, explain what the word means specifically in that context.

Also explain any important nuance or tone in that context.

If no context is provided, write:

N/A

==================================================
[014:MEMORY]

Give one short, useful memory association to help remember the word.

It can be based on:

sound

meaning

word origin

similarity to another word

a simple mental association


Do not create a forced or misleading mnemonic.
==================================================
[016:SHORT_DEFINITION]
Write ONE very short and natural English definition. This is the ONLY definition that will appear directly underneath the word on the main screen of my app. It must be: in English short easy to understand approximately 3–10 words natural suitable for a vocabulary-learning app Do not translate it into Persian. Do not include multiple definitions. Example: A feeling of discomfort or embarrassment.

==================================================
[017:QUIZ]

Create ONE multiple-choice question that tests the meaning of the target vocabulary item.

The question MUST directly ask what the target word or expression means.

The user must choose the correct meaning from exactly FOUR English options.

Use exactly this format:

QUESTION:
[Question asking for the meaning of the target word/expression]

OPTION_A:
[English meaning]

OPTION_B:
[English meaning]

OPTION_C:
[English meaning]

OPTION_D:
[English meaning]

CORRECT:
[A, B, C, or D]

IMPORTANT QUIZ RULES:

1. The question must specifically test the meaning of the target word or expression.

2. The question should directly ask something like:
"What does "[WORD]" mean?"
or
"What is the meaning of "[WORD]"?"

3. There must be exactly four answer options.

4. ALL FOUR ANSWER OPTIONS MUST BE IN ENGLISH.

5. The answer options must be short, clear, natural English explanations of possible meanings.

6. There must be exactly ONE correct answer.

7. The correct answer must accurately represent the most common or relevant meaning of the target word.

8. The three incorrect answers must be plausible enough to make the question useful, but they must NOT be meanings of the target word.

9. Do not make the correct answer obviously longer, shorter, or more detailed than the other options.

10. Randomize the position of the correct answer. Do not always put it in the same option.

11. Do not use "All of the above".

12. Do not use "None of the above".

13. Do not make an incorrect option technically correct by accident.

14. Do not use ridiculous or obviously wrong distractors.

15. Keep all four options relatively similar in length and writing style.

16. The question itself MUST be in English.

17. All answer options MUST be in English. Do not use Persian translations in the quiz.

18. If the target is a phrase, idiom, phrasal verb, slang expression, or fixed expression, ask for the actual meaning of that expression.

19. If the target word has multiple meanings, test the most common/useful meaning unless the provided context clearly indicates a different meaning.

20. Do not test pronunciation, spelling, grammar, collocations, synonyms, antonyms, or usage. The quiz must ONLY test the meaning of the target vocabulary item.

21. Do not reveal or hint at the correct answer anywhere except the CORRECT field.

22. CORRECT must contain ONLY one of:
A
B
C
D

Example:

QUESTION:
What does "finesse" mean?

OPTION_A:
To handle something with skill and subtlety

OPTION_B:
To make a serious mistake unexpectedly

OPTION_C:
To measure the weight of something

OPTION_D:
To feel extremely tired

CORRECT:
A

==================================================
ACCURACY RULES
Be accurate. Never invent: meanings translations pronunciations collocations usage patterns frequency synonyms examples Prioritize modern natural English. If the word has several meanings, distinguish them clearly. If the word is uncommon, say so through the 1–5 commonness score. If the word is mostly used in a specific context, make that clear in USAGE and REGISTER. If the input is from a movie or TV show and I provide the sentence, prioritize the meaning intended in that specific context. Persian translations must sound natural to a Persian speaker. English examples must sound like sentences a native English speaker would actually say or write.

==================================================
PARSER RULES
The Android application will parse this response automatically. Therefore: Keep every section identifier exactly as written. Keep every section in the exact order shown. Do not put section identifiers inside other sections. Do not use the same section identifier twice. Do not accidentally write another [xxx:...] pattern inside the content. Preserve line breaks. Do not use Markdown code fences. Do not add commentary outside the sections. The application will use [005:COMMONNESS] internally as an integer from 1–5 and will visually display it ONLY as five filled/empty circles. Therefore, NEVER write the commonness value anywhere else in the response. Do not write: 4/5 4 out of 5 Commonness: 4 Common ★★★★☆ Only write: 4 inside [005:COMMONNESS].

==================================================
INPUT WORD/PHRASE: {{}}
Now generate the complete vocabulary entry."""
    }

    private val DAILY_GOAL = intPreferencesKey("daily_goal")
    private val STREAK = intPreferencesKey("streak")
    private val LAST_LEARNING_DAY = longPreferencesKey("last_learning_day")

    private val WEEKLY_GOAL = intPreferencesKey("weekly_goal")
    private val CURRENT_WEEK_LEARNED = intPreferencesKey("current_week_learned")
    private val CURRENT_WEEK_START = longPreferencesKey("current_week_start")
    private val CURRENT_WEEK_NUMBER = intPreferencesKey("current_week_number")
    private val PREVIOUS_WEEKS_SUMMARY = stringPreferencesKey("previous_weeks_summary")

    private val AI_PROVIDER = stringPreferencesKey("ai_provider")
    private val AI_API_KEY = stringPreferencesKey("ai_api_key") // Legacy
    private val AI_MODEL = stringPreferencesKey("ai_model") // Legacy

    private val GEMINI_API_KEY = stringPreferencesKey("gemini_api_key")
    private val GEMINI_MODEL = stringPreferencesKey("gemini_model")
    private val DEEPSEEK_API_KEY = stringPreferencesKey("deepseek_api_key") // Legacy
    private val DEEPSEEK_MODEL = stringPreferencesKey("deepseek_model") // Legacy
    
    private val OPENAI_API_KEY = stringPreferencesKey("openai_api_key")
    private val OPENAI_MODEL = stringPreferencesKey("openai_model")
    
    private val OPENROUTER_API_KEY = stringPreferencesKey("openrouter_api_key")
    private val OPENROUTER_MODEL = stringPreferencesKey("openrouter_model")
    
    private val GAPGPT_API_KEY = stringPreferencesKey("gapgpt_api_key")
    private val GAPGPT_MODEL = stringPreferencesKey("gapgpt_model")

    private val HOME_WORD_ORDER = stringPreferencesKey("home_word_order")
    private val CUSTOM_AI_PROMPT = stringPreferencesKey("custom_ai_prompt")

    val dailyGoal: Flow<Int> = context.dataStore.data.map { it[DAILY_GOAL] ?: 5 }
    val streak: Flow<Int> = context.dataStore.data.map { it[STREAK] ?: 0 }
    val lastLearningDay: Flow<Long> = context.dataStore.data.map { it[LAST_LEARNING_DAY] ?: 0L }
    
    val weeklyGoal: Flow<Int> = context.dataStore.data.map { it[WEEKLY_GOAL] ?: 20 }
    val currentWeekLearned: Flow<Int> = context.dataStore.data.map { it[CURRENT_WEEK_LEARNED] ?: 0 }
    val currentWeekNumber: Flow<Int> = context.dataStore.data.map { it[CURRENT_WEEK_NUMBER] ?: 1 }
    val currentWeekStart: Flow<Long> = context.dataStore.data.map { it[CURRENT_WEEK_START] ?: 0L }
    val previousWeeksSummary: Flow<String> = context.dataStore.data.map { it[PREVIOUS_WEEKS_SUMMARY] ?: "[]" }
    
    val aiProvider: Flow<String> = context.dataStore.data.map { it[AI_PROVIDER] ?: "Gemini" }
    
    val geminiApiKey: Flow<String> = context.dataStore.data.map { it[GEMINI_API_KEY] ?: "" }
    val geminiModel: Flow<String> = context.dataStore.data.map { it[GEMINI_MODEL] ?: "" }
    
    val openaiApiKey: Flow<String> = context.dataStore.data.map { it[OPENAI_API_KEY] ?: "" }
    val openaiModel: Flow<String> = context.dataStore.data.map { it[OPENAI_MODEL] ?: "" }
    
    val openrouterApiKey: Flow<String> = context.dataStore.data.map { it[OPENROUTER_API_KEY] ?: "" }
    val openrouterModel: Flow<String> = context.dataStore.data.map { it[OPENROUTER_MODEL] ?: "" }
    
    val gapgptApiKey: Flow<String> = context.dataStore.data.map { it[GAPGPT_API_KEY] ?: "" }
    val gapgptModel: Flow<String> = context.dataStore.data.map { it[GAPGPT_MODEL] ?: "" }
    
    val homeWordOrder: Flow<String> = context.dataStore.data.map { it[HOME_WORD_ORDER] ?: "Random" }

    val customAiPrompt: Flow<String> = context.dataStore.data.map { it[CUSTOM_AI_PROMPT] ?: DEFAULT_PROMPT }

    suspend fun setDailyGoal(goal: Int) { context.dataStore.edit { it[DAILY_GOAL] = goal } }
    suspend fun setStreak(streak: Int) { context.dataStore.edit { it[STREAK] = streak } }
    suspend fun setLastLearningDay(timestamp: Long) { context.dataStore.edit { it[LAST_LEARNING_DAY] = timestamp } }
    suspend fun setWeeklyGoal(goal: Int) { context.dataStore.edit { it[WEEKLY_GOAL] = goal } }
    suspend fun setCurrentWeekLearned(count: Int) { context.dataStore.edit { it[CURRENT_WEEK_LEARNED] = count } }
    suspend fun setCurrentWeekNumber(number: Int) { context.dataStore.edit { it[CURRENT_WEEK_NUMBER] = number } }
    suspend fun setCurrentWeekStart(timestamp: Long) { context.dataStore.edit { it[CURRENT_WEEK_START] = timestamp } }
    suspend fun setPreviousWeeksSummary(summaryJson: String) { context.dataStore.edit { it[PREVIOUS_WEEKS_SUMMARY] = summaryJson } }
    
    suspend fun setAiProvider(provider: String) { context.dataStore.edit { it[AI_PROVIDER] = provider } }
    
    suspend fun setGeminiApiKey(key: String) { context.dataStore.edit { it[GEMINI_API_KEY] = key } }
    suspend fun setGeminiModel(model: String) { context.dataStore.edit { it[GEMINI_MODEL] = model } }
    
    suspend fun setOpenaiApiKey(key: String) { context.dataStore.edit { it[OPENAI_API_KEY] = key } }
    suspend fun setOpenaiModel(model: String) { context.dataStore.edit { it[OPENAI_MODEL] = model } }
    
    suspend fun setOpenrouterApiKey(key: String) { context.dataStore.edit { it[OPENROUTER_API_KEY] = key } }
    suspend fun setOpenrouterModel(model: String) { context.dataStore.edit { it[OPENROUTER_MODEL] = model } }
    
    suspend fun setGapgptApiKey(key: String) { context.dataStore.edit { it[GAPGPT_API_KEY] = key } }
    suspend fun setGapgptModel(model: String) { context.dataStore.edit { it[GAPGPT_MODEL] = model } }
    suspend fun setHomeWordOrder(order: String) { context.dataStore.edit { it[HOME_WORD_ORDER] = order } }

    suspend fun setCustomAiPrompt(prompt: String) { context.dataStore.edit { it[CUSTOM_AI_PROMPT] = prompt } }
}

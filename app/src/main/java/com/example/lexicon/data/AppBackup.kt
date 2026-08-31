package com.example.lexicon.data

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AppBackup(
    val backupVersion: Int = 1,
    val exportedAt: Long,
    val words: List<WordEntity>,
    val settings: AppSettingsBackup
)

@JsonClass(generateAdapter = true)
data class AppSettingsBackup(
    val dailyGoal: Int,
    val streak: Int,
    val lastLearningDay: Long,
    val weeklyGoal: Int,
    val currentWeekLearned: Int,
    val currentWeekStart: Long,
    val currentWeekNumber: Int? = 1,
    val previousWeeksSummary: String,
    val homeWordOrder: String? = "Random",
    val customAiPrompt: String
)

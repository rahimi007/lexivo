package com.example.lexicon.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class LearningStatus {
    NEW, LEARNING, FAMILIAR, MASTERED
}

@Entity(tableName = "vocabulary")
@com.squareup.moshi.JsonClass(generateAdapter = true)
data class WordEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val word: String,
    val pronunciation: String? = null,
    val easyPronunciation: String? = null,
    val partOfSpeech: String? = null,
    val shortDefinition: String? = null,
    val meaning: String? = null,
    val commonness: Int = 1, // 1 to 5
    val translations: String? = null,
    val examples: String? = null,
    val usage: String? = null,
    val collocations: String? = null,
    val relatedWords: String? = null,
    val register: String? = null,
    val learnerNote: String? = null,
    val context: String? = null,
    val memory: String? = null,
    val source: String? = null,
    val quizQuestion: String? = null,
    val quizOptionA: String? = null,
    val quizOptionB: String? = null,
    val quizOptionC: String? = null,
    val quizOptionD: String? = null,
    val quizCorrectOption: String? = null, // A, B, C, D
    val favorite: Boolean = false,
    val bookmarked: Boolean = false,
    val learningStatus: LearningStatus = LearningStatus.NEW,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastReviewedAt: Long = 0L,
    val nextReviewAt: Long = 0L,
    val reviewCount: Int = 0,
    val correctAnswers: Int = 0,
    val incorrectAnswers: Int = 0,
    val lastViewedAt: Long = 0L,
    val consecutiveCorrectAfterStruggling: Int = 0
)

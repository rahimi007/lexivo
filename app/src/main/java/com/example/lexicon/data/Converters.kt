package com.example.lexicon.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromLearningStatus(value: LearningStatus): String = value.name

    @TypeConverter
    fun toLearningStatus(value: String): LearningStatus = enumValueOf(value)
}

package com.example.lexicon.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM vocabulary ORDER BY createdAt DESC")
    fun getAllWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM vocabulary WHERE word = :word LIMIT 1")
    suspend fun getWordByText(word: String): WordEntity?
    
    @Query("SELECT * FROM vocabulary WHERE id = :id LIMIT 1")
    suspend fun getWordById(id: Int): WordEntity?

    @Query("SELECT * FROM vocabulary WHERE favorite = 1 ORDER BY createdAt DESC")
    fun getFavorites(): Flow<List<WordEntity>>

    @Query("SELECT * FROM vocabulary WHERE bookmarked = 1 ORDER BY createdAt DESC")
    fun getBookmarks(): Flow<List<WordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordEntity): Long

    @Update
    suspend fun updateWord(word: WordEntity)

    @Query("DELETE FROM vocabulary WHERE id = :id")
    suspend fun deleteWord(id: Int)
    
    @Query("DELETE FROM vocabulary")
    suspend fun deleteAllWords()
}

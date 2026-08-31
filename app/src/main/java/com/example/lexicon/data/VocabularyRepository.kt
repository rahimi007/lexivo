package com.example.lexicon.data

import kotlinx.coroutines.flow.Flow

class VocabularyRepository(private val wordDao: WordDao) {
    val allWords: Flow<List<WordEntity>> = wordDao.getAllWords()
    val favorites: Flow<List<WordEntity>> = wordDao.getFavorites()
    val bookmarks: Flow<List<WordEntity>> = wordDao.getBookmarks()

    suspend fun getWordByText(word: String): WordEntity? = wordDao.getWordByText(word)
    suspend fun getWordById(id: Int): WordEntity? = wordDao.getWordById(id)

    suspend fun insert(word: WordEntity) = wordDao.insertWord(word)
    suspend fun update(word: WordEntity) = wordDao.updateWord(word)
    suspend fun delete(id: Int) = wordDao.deleteWord(id)
    suspend fun deleteAll() = wordDao.deleteAllWords()
    suspend fun insertAll(words: List<WordEntity>) {
        words.forEach { wordDao.insertWord(it) }
    }
}

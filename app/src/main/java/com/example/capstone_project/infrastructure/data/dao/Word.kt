package com.example.capstone_project.infrastructure.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.capstone_project.infrastructure.data.entities.Word

@Dao
interface Word {
    @Query("SELECT * FROM words")
    fun getAll(): List<Word>

    @Query("SELECT * FROM words WHERE isFavorite = 1")
    fun getFavorites(): List<Word>

    @Query("SELECT * FROM words WHERE word LIKE :word LIMIT 1")
    fun findByWord(word: String): Word

    @Query("SELECT COUNT(1) FROM words")
    fun count() : Int

    @Query("SELECT * FROM words WHERE category = :category")
    fun findByCategory(category: String): List<Word>

    @Query("SELECT * FROM words WHERE isFavorite = :isFavorite")
    fun getWordsByFavoriteStatus(isFavorite: Boolean): List<Word>

    @Query("SELECT * FROM words WHERE category = :category")
    fun getWordsByCategory(category: String): List<Word>

    @Insert
    fun insert(word: Word)

    @Insert
    fun insertAll(vararg words: Word)

    @Update
    fun updatePass(word: Word)

    @Update
    fun updateFavoriteStatus(word: Word)

    @Delete
    fun delete(word: Word)
}
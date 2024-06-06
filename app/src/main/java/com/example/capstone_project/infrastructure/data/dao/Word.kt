package com.example.capstone_project.infrastructure.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.capstone_project.infrastructure.data.entities.Word

@Dao
interface Word {
    @Query("SELECT * FROM words")
    fun getAll(): List<Word>

    @Query("SELECT * FROM words WHERE word LIKE :word LIMIT 1")
    fun findByWord(word: String): Word

    @Insert
    fun insertAll(vararg words: Word)

    @Delete
    fun delete(word: Word)
}
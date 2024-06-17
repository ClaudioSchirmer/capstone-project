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

    @Query("SELECT * FROM words WHERE word LIKE :word LIMIT 1")
    fun findByWord(word: String): Word

    @Query("SELECT COUNT(1) FROM words")
    fun count() : Int

    @Insert
    fun insertAll(vararg words: Word)

    @Update
    fun updatePass(word: Word)

    @Delete
    fun delete(word: Word)
}
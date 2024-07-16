package com.example.capstone_project.infrastructure.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.capstone_project.infrastructure.data.entities.Stat

@Dao
interface Stat {
    @Query("SELECT * FROM stats")
    fun getAll(): List<Stat>

    @Query("SELECT COUNT(1) FROM stats")
    fun count() : Int

    @Insert
    fun insert(stat: Stat)

    @Update
    fun updatePass(stat: Stat)

    @Update
    fun updateRememberedStatus(stat: Stat)
}
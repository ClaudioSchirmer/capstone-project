package com.example.capstone_project.infrastructure.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.capstone_project.infrastructure.data.entities.ChartData
import com.example.capstone_project.infrastructure.data.entities.Stat

@Dao
interface Stat {
    @Query("SELECT * FROM stats")
    fun getAll(): List<Stat>

    @Query("SELECT * FROM stats WHERE wordUid = :wordUid AND dateInfo = :dateInfo")
    fun getByWordUidAndDateInfo(wordUid: Int, dateInfo: String): Stat

    @Query("SELECT dateInfo, count(1) as cnt FROM stats WHERE dateInfo >= :dateInfo AND isRemember = :isRemember GROUP BY dateInfo")
    fun getAllByDateInfoAndIsRemember(dateInfo: String, isRemember: Boolean): List<ChartData>

    @Query("SELECT COUNT(1) FROM stats")
    fun count() : Int

    @Insert
    fun insert(stat: Stat)

    @Update
    fun update(stat: Stat)
}
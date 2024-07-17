package com.example.capstone_project.infrastructure.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "stats")
data class Stat(
    @PrimaryKey(autoGenerate = true) val uid: Int,
    @ColumnInfo(name = "dateInfo") var dateInfo: String?,
    @ColumnInfo(name = "wordUid") var wordUid: Int,
    @ColumnInfo(name = "isRemember") var isRemember: Boolean = false,
) {
    @Ignore var used: Boolean = false
}
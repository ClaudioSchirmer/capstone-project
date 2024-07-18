package com.example.capstone_project.infrastructure.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true) val uid: Int? = null,
    @ColumnInfo(name = "word") val word: String,
    @ColumnInfo(name = "definition") val definition: String,
    @ColumnInfo(name = "tip") val tip: String? = null,
    @ColumnInfo(name = "pass") var pass: Int = 0,
    @ColumnInfo(name = "fail") var fail: Int = 0,
    @ColumnInfo(name = "category") var category: String? = null,
    @ColumnInfo(name = "isFavorite") var isFavorite: Boolean = false,
) {
    @Ignore var used: Boolean = false
}
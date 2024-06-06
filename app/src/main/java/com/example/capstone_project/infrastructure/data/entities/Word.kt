package com.example.capstone_project.infrastructure.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "words")
data class Word(
    @PrimaryKey(autoGenerate = true) val uid: Int? = null,
    @ColumnInfo(name = "word") val word: String,
    @ColumnInfo(name = "definition") val definition: String,
    @ColumnInfo(name = "tip") val tip: String? = null
)
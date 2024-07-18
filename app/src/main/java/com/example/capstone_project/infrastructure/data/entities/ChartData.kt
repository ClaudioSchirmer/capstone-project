package com.example.capstone_project.infrastructure.data.entities

import androidx.room.ColumnInfo

data class ChartData(
    @ColumnInfo(name = "dateInfo") var dateInfo: String,
    @ColumnInfo(name = "cnt") var cnt: Int,
)
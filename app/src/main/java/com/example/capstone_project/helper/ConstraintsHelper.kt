package com.example.capstone_project.helper

import java.text.DateFormat
import java.text.SimpleDateFormat

class ConstraintsHelper {
    companion object {
        val dfDateInfo: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val dfXAxis: DateFormat = SimpleDateFormat("M/d")
        var minDay: Int = 1
        var maxDay: Int = 7
    }
}
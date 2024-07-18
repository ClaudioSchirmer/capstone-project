package com.example.capstone_project.application.word

data class AddStatCommand(
    val dateInfo: String?,
    val wordUid: Int?,
    val isRemember: Boolean?
)
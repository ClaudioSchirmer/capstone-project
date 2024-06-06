package com.example.capstone_project.application.word

data class AddWordCommand(
    val word: String?,
    val tip: String?,
    val definition: String?
)
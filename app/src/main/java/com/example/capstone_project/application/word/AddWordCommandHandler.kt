package com.example.capstone_project.application.word

import android.content.Context
import com.example.capstone_project.application.ApplicationNotification
import com.example.capstone_project.application.Notification
import com.example.capstone_project.infrastructure.data.AppDatabase
import com.example.capstone_project.infrastructure.data.entities.Word

class AddWordCommandHandler(
    private val context: Context
) {
    private val notifications = mutableListOf<Notification>()
    suspend fun invoke(command: AddWordCommand) {
        notifications.clear()

        command.word.validate("word")
        command.definition.validate("definition")

        if (notifications.isNotEmpty())
            throw ApplicationNotification(notifications)

        AppDatabase(context = context).wordDAO().insertAll(
            Word(
                uid = null,
                word = command.word!!,
                tip = command.tip,
                definition = command.definition!!,
                category = command.category
            )
        )
    }

    private fun String?.validate(fieldName: String) {
        val addNotification = { message: String ->
            notifications.add(
                Notification(
                    fieldName = fieldName,
                    message = message
                )
            )
        }
        if (this.isNullOrBlank()) {
            addNotification("${fieldName.replaceFirstChar { it.titlecase() }} is required!")
        }
        if (this?.any { !it.isLetter() && !it.isWhitespace() && it != '.' && it != ',' } == true) {
            addNotification("You must use only letters!")
        }
        if ((this?.trim()?.replace("[,.]".toRegex(), "")?.length ?: 0) <= 1) {
            addNotification("${fieldName.replaceFirstChar { it.titlecase() }} should have at least 2 letters!")
        }
    }
}
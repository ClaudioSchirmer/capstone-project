package com.example.capstone_project.application.word

import android.content.Context
import com.example.capstone_project.infrastructure.data.AppDatabase
import com.example.capstone_project.infrastructure.data.entities.Stat

class AddStatCommandHandler(
    private val context: Context
) {
    suspend fun insertOrUpdate(command: AddStatCommand) {

        val statDao = AppDatabase(context = context).statDao()
        var stat:Stat = statDao.getByWordUidAndDateInfo(command.wordUid!!, command.dateInfo!!)

        //if (null == stat) {
            stat = Stat(
                uid = null,
                dateInfo = command.dateInfo,
                wordUid = command.wordUid,
                isRemember = command.isRemember!!
            )
            statDao.insert(stat)
       // } else {
           // stat.isRemember = command.isRemember!!
           // statDao.update(stat)
       // }
    }
}
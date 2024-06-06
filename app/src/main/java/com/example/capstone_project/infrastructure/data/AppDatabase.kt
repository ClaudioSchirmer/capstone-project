package com.example.capstone_project.infrastructure.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.capstone_project.infrastructure.data.entities.Word as WordEntity
import com.example.capstone_project.infrastructure.data.dao.Word as WordDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Database(entities = [WordEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun WordDAO(): WordDAO

    companion object {
        suspend operator fun invoke(context: Context) = withContext(Dispatchers.IO) {
            Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                name = "SnapLearn.db"
            ).build()
        }
    }
}
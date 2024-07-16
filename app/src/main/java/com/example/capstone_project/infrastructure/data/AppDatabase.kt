package com.example.capstone_project.infrastructure.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.capstone_project.infrastructure.data.dao.Word as WordDAO
import com.example.capstone_project.infrastructure.data.dao.Stat as StatDAO
import com.example.capstone_project.infrastructure.data.entities.Word as WordEntity
import com.example.capstone_project.infrastructure.data.entities.Stat as StatEntity

@Database(entities = [WordEntity::class, StatEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun wordDAO(): WordDAO

    abstract fun statDao(): StatDAO

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "SnapLearn.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
        suspend operator fun invoke(context: Context) = withContext(Dispatchers.IO) {
            Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                name = "SnapLearn.db"
            ).build()
        }
    }
}
package com.example.capstone_project.infrastructure.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.capstone_project.infrastructure.data.dao.Word as WordDAO
import com.example.capstone_project.infrastructure.data.entities.Word as WordEntity

@Database(entities = [WordEntity::class], version = 3)
abstract class AppDatabase : RoomDatabase() {

    abstract fun wordDAO(): WordDAO

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE words ADD COLUMN example_new_column INTEGER DEFAULT 0")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "SnapLearn.db"
                )
                    .addMigrations(MIGRATION_2_3)
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
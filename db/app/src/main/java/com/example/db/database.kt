package com.example.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.db.dao.HabitDAO
import com.example.db.dao.UserDao
import com.example.db.entities.HabitEntity
import com.example.db.entities.UserEntity

@Database(entities = [HabitEntity::class, UserEntity::class], version = 1)
abstract class HabitDatabase : RoomDatabase() {
    abstract fun habitDAO(): HabitDAO
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: HabitDatabase? = null

        fun getDatabase(context: Context): HabitDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HabitDatabase::class.java,
                    "habit_database"
                )
                    .fallbackToDestructiveMigration()  // Esta línea elimina la base de datos existente al cambiar el esquema
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
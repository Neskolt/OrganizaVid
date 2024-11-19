package com.example.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.db.entities.HabitEntity
import com.example.db.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDAO {
    @Insert
    suspend fun insert(habit: HabitEntity)

    @Insert
    suspend fun insertAll(habits: List<HabitEntity>)

    @Update
    suspend fun update(habit: HabitEntity)

    @Delete
    suspend fun delete(habit: HabitEntity)

    @Query("SELECT * FROM habits WHERE ageGroup = :ageGroup")
    suspend fun getHabitsByAgeGroup(ageGroup: String): List<HabitEntity>
}

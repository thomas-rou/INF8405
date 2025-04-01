package com.example.polyhike.db

import androidx.room.*
import com.example.polyhike.model.Statistic

@Dao
interface StatisticDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(statistic: Statistic)

    @Delete
    fun delete(statistic: Statistic)

    @Update
    fun update(statistic: Statistic)

    @Query("SELECT * FROM statistics WHERE id = :id")
    fun getById(id: Int): Statistic
}
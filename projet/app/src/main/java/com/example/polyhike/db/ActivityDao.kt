package com.example.polyhike.db

import androidx.room.*
import com.example.polyhike.model.Activity

@Dao
interface ActivityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(activity: Activity)

    @Delete
    fun delete(activity: Activity)

    @Update
    fun update(activity: Activity)

    @Query("SELECT * FROM activities WHERE id = :id")
    fun getById(id: Int): Activity
}
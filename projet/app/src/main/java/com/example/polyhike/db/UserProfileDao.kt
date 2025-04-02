package com.example.polyhike.db

import androidx.room.*
import com.example.polyhike.model.UserProfile

@Dao
interface UserProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(userProfile: UserProfile)

    @Delete
    fun delete(userProfile: UserProfile)

    @Update
    fun update(userProfile: UserProfile)

    @Query("SELECT * FROM user_profiles WHERE id = :id")
    fun getById(id: Int): UserProfile?

    @Query("SELECT name FROM user_profiles WHERE id = :id")
    fun getUserNameById(id: Int): String

    @Query("SELECT * FROM user_profiles WHERE name = :name AND password = :password")
    fun getUserByNameAndPassword(name: String, password: String): UserProfile?
}
package com.example.polyhike.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.util.Date

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val password: String,
    val dateOfBirth: Date,
    val photo: String, // TODO: change type ?
    val friends: Int, // TODO: change ti list
)
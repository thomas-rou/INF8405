package com.example.polyhike.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "statistics")
data class Statistic(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val userId: Int, // Foreign Key from UserProfile
    val date: String,
    val value: Double
)
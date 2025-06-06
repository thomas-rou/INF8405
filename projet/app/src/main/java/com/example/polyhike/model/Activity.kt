package com.example.polyhike.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time
import java.util.Date

@Entity(tableName = "activities")
data class Activity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val userId: Int, // Foreign Key from UserProfile
    val description: String,
    val itinerary: String,
    val date: String, // start date and time
    val duration: Int,
    val totalDistance: Int,
    val totalSteps: Int,
    val meanSpeed: Int,
    val meanAcceleration: Int,
    val temperature: Int,
    val likedBy: Int,
)
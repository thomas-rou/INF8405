package com.example.polyhike.model

import androidx.room.*
import com.google.android.gms.maps.model.LatLng
import java.util.Date

@Entity(tableName = "hikeInfo")
data class HikeInfo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val currentUserId: Int,
    val startDate: Date,
    val endDate: Date,
    val currentSpeed: Double,
    val averageSpeed: Double,
    val totalSteps: Int,
    val totalDistance: Double,
    val currentTemperature: Double,
    val recordedPath: MutableList<LatLng>,
    val pausePath: MutableList<LatLng>
)

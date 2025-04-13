package com.example.polyhike.ui.sensor

data class SensorUiState(
    val azimuth: Float = 0f,
    val stepCount: Int = 0,
    val accelerationMagnitude: Float = 0f,
    val temperature: Float? = null
)
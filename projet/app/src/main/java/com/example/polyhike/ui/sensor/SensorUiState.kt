package com.example.polyhike.ui.sensor

data class SensorUiState(
    val azimuth: Float = 0f,
    val stepCount: Int = 0,
    val speed: Float = 0f,
    val temperature: Float? = null
)
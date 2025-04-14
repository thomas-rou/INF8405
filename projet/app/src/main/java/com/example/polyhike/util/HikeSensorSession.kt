package com.example.polyhike.util

import android.content.Context

object HikeSensorSession {
    var totalSteps: Int = -1
    var currentTemperature: Double = -1.0
    private var lastWeatherFetchTime: Long = 0
    private const val WEATHER_FETCH_INTERVAL = 10 * 60 * 1000 // 10 min

    private var isInitialized = false
    private lateinit var stepManager: StepCounterManager
    private lateinit var tempManager: AmbientTemperatureManager

    suspend fun fetchTemperatureFallback(lat: Double, lon: Double) {
        val now = System.currentTimeMillis()
        if (currentTemperature == -1.0 || now - lastWeatherFetchTime > WEATHER_FETCH_INTERVAL) {
            WeatherApiClient.getTemperature(lat, lon)?.let {
                currentTemperature = it
                lastWeatherFetchTime = now
            }
        }
    }

    fun start(context: Context) {
        if (isInitialized) return
        isInitialized = true

        stepManager = StepCounterManager(context) { stepsSinceStart ->
            totalSteps = stepsSinceStart
        }

        tempManager = AmbientTemperatureManager(context) { currentTemperature = it?.toDouble() ?: -1.0 }

        stepManager.start()
        tempManager.start()
    }

    fun stop() {
        if (!isInitialized) return
        stepManager.stop()
        tempManager.stop()
        isInitialized = false
    }

    fun pauseStepCounter() {
        stepManager.pause()
    }

    fun resumeStepCounter() {
        stepManager.resume()
    }

    fun resetStepCounterManager(){
        stepManager.reset()
    }
}
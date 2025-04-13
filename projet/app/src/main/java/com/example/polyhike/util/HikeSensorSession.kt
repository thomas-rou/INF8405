package com.example.polyhike.util

import android.content.Context

object HikeSensorSession {
    var totalSteps: Int = -1
    var currentTemperature: Double = -1.0

    private var isInitialized = false
    private lateinit var stepManager: StepCounterManager
    private lateinit var tempManager: AmbientTemperatureManager

    fun start(context: Context) {
        if (isInitialized) return
        isInitialized = true

        stepManager = StepCounterManager(context) { totalSteps = it }
        tempManager = AmbientTemperatureManager(context) {
            currentTemperature = it?.toDouble() ?: -1.0
        }

        stepManager.start()
        tempManager.start()
    }

    fun stop() {
        if (!isInitialized) return
        stepManager.stop()
        tempManager.stop()
        isInitialized = false
    }
}
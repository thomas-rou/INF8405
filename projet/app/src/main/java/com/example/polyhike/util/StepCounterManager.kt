package com.example.polyhike.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class StepCounterManager(
    context: Context,
    private val onStepCountUpdated: (Int) -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

    private var stepOffset: Float? = null
    private var stepsDuringPause: Float = 0f
    private var stepsAtPauseStart: Float? = null
    private var totalPauseSteps = 0f
    private var isPaused = false

    fun start() {
        if (stepSensor != null) {
            sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            onStepCountUpdated(-1) // Indique qu'aucun capteur n'est disponible
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    fun pause() {
        isPaused = true
        stepsAtPauseStart = null
    }

    fun resume() {
        isPaused = false
        if (stepsAtPauseStart != null) {
            totalPauseSteps += stepsDuringPause
        }
        stepsDuringPause = 0f
        stepsAtPauseStart = null
    }


    fun reset() {
        stepOffset = null
        stepsDuringPause = 0f
        stepsAtPauseStart = null
        totalPauseSteps = 0f
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val rawSteps = event?.values?.firstOrNull() ?: return

        if (stepOffset == null) {
            stepOffset = rawSteps
        }

        val currentTotal = rawSteps - stepOffset!!

        if (isPaused) {
            if (stepsAtPauseStart == null) {
                stepsAtPauseStart = currentTotal
            } else {
                stepsDuringPause = currentTotal - stepsAtPauseStart!!
            }
            return // don’t update steps while paused
        }

        val stepsToReport = (currentTotal - totalPauseSteps).toInt()
        onStepCountUpdated(stepsToReport)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

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

    fun start() {
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.values?.firstOrNull()?.let { totalSteps ->
            if (stepOffset == null) {
                stepOffset = totalSteps // point de référence
            }

            val stepsSinceStart = (totalSteps - stepOffset!!).toInt()
            onStepCountUpdated(stepsSinceStart)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

package com.example.polyhike.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class StepCounterManager(
    context: Context,
    private val onStepUpdate: (Int) -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var initialStepCount = -1

    fun start() {
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val steps = event.values[0].toInt()
            if (initialStepCount == -1) initialStepCount = steps
            val stepDelta = steps - initialStepCount
            onStepUpdate(stepDelta)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
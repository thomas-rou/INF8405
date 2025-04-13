package com.example.polyhike.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class LinearAccelerationManager(
    context: Context,
    private val onAcceleration: (x: Float, y: Float, z: Float, magnitude: Float) -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val linearAccelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

    // Threshold, considéré comme du bruit, surtout pour la magnitude de l'accélération
    private val NOISE_THRESHOLD = 0.5f

    fun start() {
        if (linearAccelerationSensor != null) {
            sensorManager.registerListener(this, linearAccelerationSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            onAcceleration(0f, 0f, 0f, -1f)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.values?.let { values ->
            val x = values[0]
            val y = values[1]
            val z = values[2]

            // Norme combinée de l'accélération
            val magnitude = kotlin.math.sqrt(x * x + y * y + z * z)

            val filteredMagnitude = if (magnitude < NOISE_THRESHOLD) 0f else magnitude

            onAcceleration(x, y, z, filteredMagnitude)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
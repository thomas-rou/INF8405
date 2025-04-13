package com.example.polyhike.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log

class AmbientTemperatureManager(
    context: Context,
    private val onTemperatureChanged: (Float?) -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val temperatureSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)

    fun start() {
        if (temperatureSensor != null) {
            sensorManager.registerListener(this, temperatureSensor, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            Log.w("AmbientTempManager", "Ambient temperature sensor not available.")
            onTemperatureChanged(null)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            onTemperatureChanged(event.values[0])
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

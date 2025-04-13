package com.example.polyhike.util

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import kotlin.math.pow

class AmbientPressureManager(
    context: Context,
    private val onPressureAndAltitudeChanged: (pressure: Float?, altitude: Float?) -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val pressureSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)

    fun start() {
        if (pressureSensor != null) {
            sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL)
        } else {
            Log.w("AmbientPressureManager", "Capteur de pression non disponible.")
            onPressureAndAltitudeChanged(null, null)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_PRESSURE) {
            val pressure = event.values[0]  // en hPa (hectopascals/millibar) --> bar
            val altitude = calculateAltitude(pressure)
            onPressureAndAltitudeChanged(pressure, altitude)
        }
    }

    private fun calculateAltitude(pressure: Float): Float {
        val seaLevelPressure = 1013.25f // pression standard en hPa (millibar)
        return 44330f * (1f - (pressure / seaLevelPressure).toDouble().pow(1.0 / 5.255)).toFloat()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
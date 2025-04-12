package com.example.polyhike.util


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.abs

class CompassManager(
    context: Context,
    private val onAzimuthChanged: (smoothedAzimuth: Float, rawAzimuth: Float) -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var gravity = FloatArray(3)
    private var geomagnetic = FloatArray(3)

    private var smoothedAzimuth = 0f
    private val alpha = 0.1f

    fun start() {
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_UI
        )
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            SensorManager.SENSOR_DELAY_UI
        )
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> gravity = event.values.clone()
            Sensor.TYPE_MAGNETIC_FIELD -> geomagnetic = event.values.clone()
        }

        val R = FloatArray(9)
        val I = FloatArray(9)
        if (SensorManager.getRotationMatrix(R, I, gravity, geomagnetic)) {
            val orientation = FloatArray(3)
            SensorManager.getOrientation(R, orientation)
            val rawAzimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
            // éviter les valeurs négatives
            val fixedAzimuth = (rawAzimuth + 360) % 360

            // filtre passe-bas pour smooth + ignorer les changements minimes
            smoothedAzimuth = smoothedAzimuth * (1 - alpha) + fixedAzimuth * alpha

            if (abs(fixedAzimuth - smoothedAzimuth) > 0.5f) {
                onAzimuthChanged(smoothedAzimuth, fixedAzimuth)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
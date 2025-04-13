package com.example.polyhike.util


import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class CompassManager(
    context: Context,
    private val onAzimuthChanged: (Float, Float) -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    private var smoothedAzimuth = 0f
    private val alpha = 0.1f

    fun start() {
        rotationVectorSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_ROTATION_VECTOR) return

        val rotationMatrix = FloatArray(9)
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

        val adjustedRotationMatrix = FloatArray(9)
        SensorManager.remapCoordinateSystem(
            rotationMatrix,
            SensorManager.AXIS_X, SensorManager.AXIS_Z, // X vers la droite, Z vers le haut
            adjustedRotationMatrix
        )

        val orientation = FloatArray(3)
        SensorManager.getOrientation(adjustedRotationMatrix, orientation)

        val azimuthRad = orientation[0]
        val rawAzimuth = Math.toDegrees(azimuthRad.toDouble()).toFloat()
        val fixedAzimuth = (rawAzimuth + 360f) % 360f

        smoothedAzimuth = smoothedAzimuth * (1 - alpha) + fixedAzimuth * alpha

        onAzimuthChanged(fixedAzimuth, rawAzimuth)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
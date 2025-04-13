package com.example.polyhike.util


import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.view.Surface
import java.lang.Math.toDegrees

private const val AZIMUTH = 0

class CompassManager(
    private val context: Context,
    private val onAzimuthChanged: (Float) -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val rotationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    private val rotationMatrix = FloatArray(9)
    private val remappedMatrix = FloatArray(9)
    private val orientation = FloatArray(3)


    fun start() {
        if (rotationVectorSensor != null){
            sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            onAzimuthChanged(-1f)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type != Sensor.TYPE_ROTATION_VECTOR) return

        // Calculer la matrice de rotation
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

        // Adapter à la rotation de l’écran
        val displayRotation = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display?.rotation ?: Surface.ROTATION_0
        } else {
            @Suppress("DEPRECATION")
            (context as? Activity)?.windowManager?.defaultDisplay?.rotation ?: Surface.ROTATION_0
        }

        when (displayRotation) {
            Surface.ROTATION_90 -> SensorManager.remapCoordinateSystem(
                rotationMatrix,
                SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, remappedMatrix
            )
            Surface.ROTATION_180 -> SensorManager.remapCoordinateSystem(
                rotationMatrix,
                SensorManager.AXIS_MINUS_X, SensorManager.AXIS_MINUS_Y, remappedMatrix
            )
            Surface.ROTATION_270 -> SensorManager.remapCoordinateSystem(
                rotationMatrix,
                SensorManager.AXIS_MINUS_Y, SensorManager.AXIS_X, remappedMatrix
            )
            else -> SensorManager.remapCoordinateSystem(
                rotationMatrix,
                SensorManager.AXIS_X, SensorManager.AXIS_Y, remappedMatrix
            )
        }

        SensorManager.getOrientation(remappedMatrix, orientation)

        val azimuthRad = orientation[AZIMUTH]
        val azimuthDeg = (toDegrees(azimuthRad.toDouble()) + 360) % 360

        onAzimuthChanged(azimuthDeg.toFloat())
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
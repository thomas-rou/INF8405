package com.example.polyhike.ui.sensor

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import com.example.polyhike.R
import com.example.polyhike.util.Azimuth

class CompassView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val rotatingGroup: View
    private val needle: ImageView
    private var lastAzimuth = 0f

    init {
        LayoutInflater.from(context).inflate(R.layout.view_compass, this, true)
        rotatingGroup = findViewById(R.id.compassRotatingGroup)
        needle = findViewById(R.id.compass_nav_arrow)
    }

    fun updateAzimuth(degrees: Float) {
        val azimuth = Azimuth(degrees)
        rotateCompass(-azimuth.degrees) // rotation inverse
    }

    private fun rotateCompass(toDegrees: Float) {
        var diff = toDegrees - lastAzimuth

        // Normaliser en [-180, 180]
        diff = (diff + 540) % 360 - 180

        val finalAzimuth = lastAzimuth + diff

        rotatingGroup.animate()
            .rotation(finalAzimuth)
            .setDuration(300)
            .setInterpolator(LinearInterpolator())
            .start()

        lastAzimuth = finalAzimuth
    }
}
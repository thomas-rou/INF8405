package com.example.polyhike.ui.sensor

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.polyhike.R
import com.example.polyhike.util.Azimuth

class CompassView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val background: ImageView
    private val needle: ImageView
    private val directionLabel: TextView
    private var lastAzimuth = 0f

    init {
        LayoutInflater.from(context).inflate(R.layout.view_compass, this, true)
        background = findViewById(R.id.compassBackground)
        needle = findViewById(R.id.compass_nav_arrow)
        directionLabel = findViewById(R.id.textCardinal)
    }

    fun updateAzimuth(degrees: Float) {
        val azimuth = Azimuth(degrees)
        rotateBackground(-azimuth.degrees) // rotation inverse
        directionLabel.text = azimuth.cardinalDirection
    }

    private fun rotateBackground(toDegrees: Float) {
        val fromDegrees = lastAzimuth
        val diff = ((toDegrees - fromDegrees + 540) % 360) - 180
        val correctedTo = fromDegrees + diff

        val animation = RotateAnimation(
            fromDegrees,
            correctedTo,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        animation.duration = 300
        animation.fillAfter = true

        background.startAnimation(animation)
        lastAzimuth = correctedTo
    }
}
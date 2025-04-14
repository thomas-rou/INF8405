package com.example.polyhike.ui.sensor

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.example.polyhike.R

class CompassView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private val rotatingGroup: View
    private val needle: ImageView

    init {
        LayoutInflater.from(context).inflate(R.layout.view_compass, this, true)
        rotatingGroup = findViewById(R.id.compassRotatingGroup)
        needle = findViewById(R.id.compass_nav_arrow)
    }

    fun updateAzimuth(degrees: Float) {
        val rotation = -degrees  // Inverse car on tourne le fond, pas l'aiguille
        rotateCompass(rotation)
    }

    private fun rotateCompass(toRotation: Float) {
        rotatingGroup.rotation = toRotation
    }
}
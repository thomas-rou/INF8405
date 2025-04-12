package com.example.polyhike.util

import kotlin.math.roundToInt

private data class SemiClosedFloatRange(val fromInclusive: Float, val toExclusive: Float)

private operator fun SemiClosedFloatRange.contains(value: Float): Boolean =
    fromInclusive <= value && value < toExclusive

private infix fun Float.until(to: Float) = SemiClosedFloatRange(this, to)

class Azimuth(rawDegrees: Float) {
    val degrees = normalizeAngle(rawDegrees)

    val cardinalDirection: String = when (degrees) {
        in 22.5f until 67.5f -> "NE"
        in 67.5f until 112.5f -> "E"
        in 112.5f until 157.5f -> "SE"
        in 157.5f until 202.5f -> "S"
        in 202.5f until 247.5f -> "SW"
        in 247.5f until 292.5f -> "W"
        in 292.5f until 337.5f -> "NW"
        else -> "N"
    }

    private fun normalizeAngle(deg: Float): Float = (deg + 360f) % 360f
}
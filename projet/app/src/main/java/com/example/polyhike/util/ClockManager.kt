package com.example.polyhike.util

import android.os.Handler
import android.os.Looper
import java.text.SimpleDateFormat
import java.util.*

class ClockManager(
    private val onTimeUpdate: (String) -> Unit
) {
    private val handler = Handler(Looper.getMainLooper())
    private val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    private val clockRunnable = object : Runnable {
        override fun run() {
            val currentTime = formatter.format(Date())
            onTimeUpdate(currentTime) // ← plus de TextView directe ici
            handler.postDelayed(this, 1000)
        }
    }

    fun start() = handler.post(clockRunnable)
    fun stop() = handler.removeCallbacks(clockRunnable)
}
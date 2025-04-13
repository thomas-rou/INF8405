package com.example.polyhike.util

import android.os.Handler
import android.os.Looper
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class ClockManager(private val textView: TextView) {

    private val handler = Handler(Looper.getMainLooper())
    private val formatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    private val clockRunnable = object : Runnable {
        override fun run() {
            val currentTime = formatter.format(Date())
            textView.text = "Heure: $currentTime"
            handler.postDelayed(this, 1000)
        }
    }

    fun start() {
        handler.post(clockRunnable)
    }

    fun stop() {
        handler.removeCallbacks(clockRunnable)
    }
}
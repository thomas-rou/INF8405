package com.example.polybluetoothmap

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    companion object {
        const val OPENNING_TIME: Long = 3000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash)
    }

    /**
     * Called when the activity comes to the foreground (after being paused or stopped).
     * It starts the timer to load the main page after the specified duration.
     */
    override fun onResume() {
        super.onResume()
        loadMainPageAfterTimerExpires()
    }

    /**
     * Initiates a delayed transition to the MapsActivity after the specified splash screen duration.
     */
    private fun loadMainPageAfterTimerExpires() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
            finish()
        }, OPENNING_TIME)


    }

}
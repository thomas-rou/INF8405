package com.example.polyhike

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.polyhike.MainActivity
import com.example.polyhike.R

class SplashActivity : AppCompatActivity() {
    companion object {
        const val OPENNING_TIME: Long = 3000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_splash)
    }

    override fun onResume() {
        super.onResume()
        loadMainPageAfterTimerExpires()
    }

    private fun loadMainPageAfterTimerExpires() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, OPENNING_TIME)
    }

}
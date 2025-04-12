package com.example.polyhike

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.polyhike.databinding.ActivityNavManagerBinding
import com.example.polyhike.ui.home.HomeFragmentCallback
import com.google.android.material.bottomnavigation.BottomNavigationView


class NavManagerActivity: AppCompatActivity(), HomeFragmentCallback {
    private lateinit var binding: ActivityNavManagerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNavManagerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_container)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_record, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onActionTriggered() {
        loadBatteryInfo()
    }

    private fun loadBatteryInfo() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED)
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(batteryInfoReceiver, intentFilter)
    }

    private val batteryInfoReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateBatteryData(intent)
        }
    }

    private fun updateBatteryData(intent: Intent) {
        var present: Boolean = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false)
        if (present) {
            val textView: TextView = findViewById<TextView>(R.id.text_home)
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            if (level != -1 && scale != -1)
                textView.text = "Battery Pct : ${((level / scale.toFloat()) * 100f).toInt()} %"
        } else {
            Toast.makeText(this, "No Battery present", Toast.LENGTH_SHORT).show()
        }
    }
}

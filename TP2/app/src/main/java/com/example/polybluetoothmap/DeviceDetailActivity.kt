package com.example.polybluetoothmap

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.polybluetoothmap.model.trackedDevice.TrackedDevice

class DeviceDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_device_details)

//        val device = intent.getParcelableExtra<TrackedDevice>("trackedDevice")
        val device = TrackedDevice(1, 45.5, -73.6, "Device A", "00:11:22:33:44:55", 1, 0, false,"Alias A", 0, null)
        val tvDetails = findViewById<TextView>(R.id.tvDeviceDetails)

        device?.let {
            val details = """
                Name: ${it.name ?: "Unknown"}
                Alias: ${it.alias ?: "None"}
                MAC Address: ${it.address}
                Latitude: ${it.latitude}
                Longitude: ${it.longitude}
                Type: ${it.type}
                Bond State: ${it.bondState}
                Bluetooth Class: ${it.bluetoothClass ?: "N/A"}
                UUIDs: ${it.uuids?.joinToString() ?: "N/A"}
            """.trimIndent()

            tvDetails.text = details
        }
    }
}

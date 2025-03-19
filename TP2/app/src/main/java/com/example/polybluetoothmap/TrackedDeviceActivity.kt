package com.example.polybluetoothmap

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.polybluetoothmap.model.trackedDevice.TrackedDevice
import com.example.polybluetoothmap.ui.TrackedDeviceAdapter
import com.example.polybluetoothmap.viewmodel.TrackedDeviceViewModel

class TrackedDeviceActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TrackedDeviceAdapter
    private lateinit var deviceList: MutableList<TrackedDevice>
    private val trackedDeviceViewModel: TrackedDeviceViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracked_devices)

        recyclerView = findViewById(R.id.recyclerViewTrackedDevices)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Exemple de données (remplacer par Room)
        deviceList = mutableListOf(
            TrackedDevice(1, 45.5, -73.6, "Device A", "00:11:22:33:44:55", 1, 0, false,"Alias A", 0, null),
            TrackedDevice(2, 45.6, -73.7, "Device B", "AA:BB:CC:DD:EE:FF", 2, 1, true,"Alias B", 0, listOf("UUID1", "UUID2"))
        )

//        trackedDeviceViewModel.getAll { devices ->
//            deviceList.clear()
//            deviceList.addAll(devices)
//            adapter.notifyDataSetChanged()
//        }

//        adapter = TrackedDeviceAdapter(deviceList,
//            onItemClick = { device ->
//                val intent = Intent(this, DeviceDetailActivity::class.java)
//                intent.putExtra("trackedDevice", device)
//                startActivity(intent)
//            },
//            onFavoriteClick = { device ->
//                updateDeviceInDatabase(device)
//            }
//        )

        recyclerView.adapter = adapter
    }

    private fun updateDeviceInDatabase(device: TrackedDevice) {
        trackedDeviceViewModel.updateFavoriteStatus(device.address, !device.isFavorite)
    }
}

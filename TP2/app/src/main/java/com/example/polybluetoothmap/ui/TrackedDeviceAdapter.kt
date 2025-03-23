package com.example.polybluetoothmap.ui


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.polybluetoothmap.MapsActivity
import com.example.polybluetoothmap.R
import com.example.polybluetoothmap.model.trackedDevice.TrackedDevice
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine

class TrackedDeviceAdapter(
    private val deviceList: MutableList<TrackedDevice>,
    private val onItemClick: (TrackedDevice) -> Unit,
    private val onFavoriteClick: (TrackedDevice) -> Unit
) : RecyclerView.Adapter<TrackedDeviceAdapter.TrackedDeviceViewHolder>() {

    class TrackedDeviceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDeviceName: TextView = view.findViewById(R.id.tvDeviceName)
        val tvDeviceAddress: TextView = view.findViewById(R.id.tvDeviceAddress)
        val favoriteIcon: ImageView = view.findViewById(R.id.favoriteIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackedDeviceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tracked_device, parent, false)
        return TrackedDeviceViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackedDeviceViewHolder, position: Int) {
        val device = deviceList[position]
        holder.tvDeviceName.text = device.name ?: "Unknown Device"
        holder.tvDeviceAddress.text = device.address

        holder.favoriteIcon.setImageResource(
            if (device.isFavorite) R.drawable.fav_icon else R.drawable.non_fav
        )

        holder.itemView.setOnClickListener { onItemClick(device) }

        holder.favoriteIcon.setOnClickListener {
            deviceList[position] = device.copy(isFavorite = !device.isFavorite)
            notifyItemChanged(position)
            onFavoriteClick(deviceList[position]) // Met à jour la base de données
        }
    }

    override fun getItemCount(): Int = deviceList.size
}

fun MapsActivity.trackedItemSetupView(){
    recyclerView = findViewById(R.id.recyclerViewTrackedDevices)
    recyclerView.layoutManager = LinearLayoutManager(this)


    adapter = TrackedDeviceAdapter(deviceList,
        onItemClick = { device ->
            selectedDeviceAddress = device.address
            showDeviceDetailsDialog(this, device)
        },
        onFavoriteClick = { device ->
            trackedDeviceViewModel.updateFavoriteStatus(device.address, device.isFavorite)
//            trackedDeviceViewModel.update(device)
        }
    )
    recyclerView.adapter = adapter

    updateDeviceListView()

}

fun MapsActivity.updateDeviceListView() {
    lifecycleScope.launch(Dispatchers.Main) {
        val devices = fetchAllTrackedDevices()
        deviceList.clear()
        deviceList.addAll(devices ?: emptyList())
        adapter.notifyDataSetChanged()
        for (device in deviceList) {
            displayFindDeviceOnMap(device)
        }
    }
}

suspend fun MapsActivity.fetchAllTrackedDevices(): List<TrackedDevice> {
    return suspendCancellableCoroutine { continuation ->
        val devices = trackedDeviceViewModel.getAll()
        lateinit var observer: Observer<List<TrackedDevice>>
        observer = Observer { trackedDevices ->
            devices.removeObserver(observer)
            if (continuation.isActive) {
                continuation.resume(trackedDevices, onCancellation = null)
            }
        }
        devices.observeForever(observer)
        continuation.invokeOnCancellation {
            devices.removeObserver(observer)
        }
    }
}


fun MapsActivity.showDeviceDetailsDialog(context: Context, device: TrackedDevice) {
    val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_device_details, null)
    val tvDetails = dialogView.findViewById<TextView>(R.id.tvDeviceDetails)
    val btnClose = dialogView.findViewById<Button>(R.id.btnClose)
    val directionBtn = dialogView.findViewById<Button>(R.id.directionBtn)
    val shareBtn = dialogView.findViewById<Button>(R.id.btnShare)

    val details = """
        Name: ${device.name ?: "Unknown"}
        Alias: ${device.alias ?: "None"}
        MAC Address: ${device.address}
        Latitude: ${device.latitude}
        Longitude: ${device.longitude}
        Type: ${device.type}
        Bond State: ${device.bondState}
        Bluetooth Class: ${device.bluetoothClass ?: "N/A"}
        UUIDs: ${device.uuids?.joinToString() ?: "N/A"}
    """.trimIndent()

    tvDetails.text = details

    val dialog = AlertDialog.Builder(context)
        .setView(dialogView)
        .setCancelable(true)
        .create()

    btnClose.setOnClickListener {
        dialog.dismiss()
    }

    directionBtn.setOnClickListener {
        getDirectionToSelectedDevice()
        dialog.dismiss()
    }

    shareBtn.setOnClickListener {
        shareInformation(dialogView)
        dialog.dismiss()
    }

    dialog.show()
}

fun MapsActivity.displayFindDeviceOnMap(device: TrackedDevice){
    val deviceLocation = LatLng(device.latitude, device.longitude)
    mMap.addMarker(MarkerOptions().position(deviceLocation).title(device.address))

}

fun MapsActivity.shareInformation(view: View){
    val currentDevice: TrackedDevice? = deviceList.find { device -> device.address == selectedDeviceAddress }
    if (currentDevice != null) {
        val tvDetails = view.findViewById<TextView>(R.id.tvDeviceDetails)
        val infoToShare = tvDetails.text.toString()

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, infoToShare)
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share Device Info"))
    }
    else {
        Toast.makeText(this, "No device selected!", Toast.LENGTH_SHORT).show()
    }
}


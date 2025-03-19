package com.example.polybluetoothmap.ui


import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.polybluetoothmap.MapsActivity
import com.example.polybluetoothmap.R
import com.example.polybluetoothmap.model.trackedDevice.TrackedDevice

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

    // Exemple de données (remplacer par Room)
    deviceList = mutableListOf(
        TrackedDevice(1, 45.5, -73.6, "Device A", "00:11:22:33:44:55", 1, 0, false,"Alias A", 0, null),
        TrackedDevice(2, 45.6, -73.7, "Device B", "AA:BB:CC:DD:EE:FF", 2, 1, true,"Alias B", 0, listOf("UUID1", "UUID2"))
    )

    adapter = TrackedDeviceAdapter(deviceList,
        onItemClick = { device ->
            showDeviceDetailsDialog(this, device)
        },
        onFavoriteClick = { device ->
            trackedDeviceViewModel.updateFavoriteStatus(device.address, !device.isFavorite)
        }
    )
    recyclerView.adapter = adapter
        val devices = trackedDeviceViewModel.getAll().value
        deviceList.clear()
        deviceList.addAll(devices ?: emptyList())
        adapter.notifyDataSetChanged()


}


fun MapsActivity.showDeviceDetailsDialog(context: Context, device: TrackedDevice) {
    val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_device_details, null)
    val tvDetails = dialogView.findViewById<TextView>(R.id.tvDeviceDetails)
    val btnClose = dialogView.findViewById<Button>(R.id.btnClose)

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

    dialog.show()
}

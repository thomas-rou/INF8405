package com.example.polybluetoothmap.ui


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
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

        // Met à jour l'icône en fonction de l'état du favori
        holder.favoriteIcon.setImageResource(
            if (device.isFavorite) R.drawable.fav_icon else R.drawable.non_fav
        )

        // Clic sur l'élément pour voir les détails
        holder.itemView.setOnClickListener { onItemClick(device) }

        // Clic sur l'icône de favori
        holder.favoriteIcon.setOnClickListener {
            deviceList[position] = device.copy(isFavorite = !device.isFavorite)
            notifyItemChanged(position)
            onFavoriteClick(deviceList[position]) // Met à jour la base de données
        }
    }

    override fun getItemCount(): Int = deviceList.size
}

package com.example.polybluetoothmap.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.polybluetoothmap.MapsActivity
import com.example.polybluetoothmap.model.trackedDevice.TrackedDevice
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng

@SuppressLint("MissingPermission")
fun MapsActivity.getDirectionToSelectedDevice() {
    val currentDevice: TrackedDevice? = deviceList.find { device -> device.address == selectedDeviceAddress }
    if (currentDevice != null && isLocationPermissionGranted()) {
        locationProvider.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val location = task.result
                    val destLatLng = LatLng(currentDevice.latitude, currentDevice.longitude)

                    val gmmIntentUri = Uri.parse("google.navigation:q=${destLatLng.latitude},${destLatLng.longitude}&mode=d")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")

                    if (mapIntent.resolveActivity(packageManager) != null) {
                        startActivity(mapIntent)
                    } else {
                        Toast.makeText(this, "Google Maps is not installed", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Failed to get current location", Toast.LENGTH_SHORT).show()
                }
            }
    } else {
        Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
    }
}
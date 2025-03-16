package com.example.polybluetoothmap

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.polybluetoothmap.databinding.ActivityMapsBinding
import com.google.android.gms.location.LocationServices
import android.content.Intent
import android.net.Uri
import android.provider.Settings

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    enum class ThemeMode {
        LIGHT,
        DARK
    }

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    public val currentThemeMode = ThemeMode.LIGHT
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val BLUETOOTH_PERMISSION_REQUEST_CODE = 2
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun isLocationPermissionGranted(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                )
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
    }

    private fun requestLocationPermission(){
        if (!isLocationPermissionGranted()) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
        }
        else{
            setLocationToCurrentPosition()
        }
    }

    @SuppressLint("MissingPermission")
    private fun setLocationToCurrentPosition(){
        if (isLocationPermissionGranted()) {
            mMap.isMyLocationEnabled = true
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    mMap.addMarker(MarkerOptions().position(currentLatLng).title("current location"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                } else {
                    Toast.makeText(this, "Unable to fetch current location", Toast.LENGTH_SHORT)
                        .show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to fetch location: ${it.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        else{
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
            finish()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        requestLocationPermission()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            setLocationToCurrentPosition()
        } else if (requestCode == BLUETOOTH_PERMISSION_REQUEST_CODE) {

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

}
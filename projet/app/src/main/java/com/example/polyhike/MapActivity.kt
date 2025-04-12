package com.example.polyhike

import android.content.pm.PackageManager
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity: AppCompatActivity(), OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback  {
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
    private lateinit var mMap: GoogleMap
    private lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var recordedPath: MutableList<LatLng>
    private var isRecording = false
    private lateinit var locationCallback: com.google.android.gms.location.LocationCallback
    private lateinit var locationRequest: com.google.android.gms.location.LocationRequest

private fun configureButton(){
    val buttonStopHike = findViewById<ImageButton>(R.id.button_stop_hike)
    val buttonPauseHike = findViewById<ImageButton>(R.id.button_pause_hike)
    val buttonStartHike = findViewById<ImageButton>(R.id.button_start_hike)
    buttonStopHike.setOnClickListener {
        stopRecording()
    }
    buttonPauseHike.setOnClickListener {
        pauseRecording()
        }
    buttonStartHike.setOnClickListener {
        startRecording()
    }
    if (isRecording) {
        buttonStopHike.setEnabled(true)
        buttonPauseHike.setEnabled(true)
        buttonStartHike.setEnabled(false)
    } else {
        buttonStopHike.setEnabled(false)
        buttonPauseHike.setEnabled(false)
        buttonStartHike.setEnabled(true)
    }
}

    private fun isLocationPermissionGranted(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestLocationPermission() {
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
    }

    private fun pauseRecording() {
        if (::locationCallback.isInitialized) {
            locationProvider.removeLocationUpdates(locationCallback)
        }
        isRecording = false
    }

    private fun stopRecording() {
        if (::locationCallback.isInitialized) {
            locationProvider.removeLocationUpdates(locationCallback)
        }
        isRecording = false
    }

    @SuppressLint("MissingPermission")
    fun startRecording() {
        if (!isLocationPermissionGranted()) {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show()
            return
        }

        if (isRecording) return // Already recording

        isRecording = true
        recordedPath = mutableListOf()

        locationRequest = com.google.android.gms.location.LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 5000L // every 5 seconds
        ).setMinUpdateIntervalMillis(2000L)
            .setMaxUpdateDelayMillis(10000L)
            .build()

        locationCallback = object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                for (location in locationResult.locations) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    recordedPath.add(latLng)
                    // Optional: draw path
                    drawPolyline()
                }
            }
        }

        locationProvider.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
    }

    private fun drawPolyline() {
        if (recordedPath.size < 2) return

        val polylineOptions = com.google.android.gms.maps.model.PolylineOptions()
            .addAll(recordedPath)
            .width(8f)
            .color(android.graphics.Color.BLUE)

        mMap.clear() // Clear old lines/markers if needed
        mMap.addPolyline(polylineOptions)


    }

    @SuppressLint("MissingPermission")
    private fun setLocationToCurrentPosition() {
        if (isLocationPermissionGranted()) {
            mMap.isMyLocationEnabled = true
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        val currentLatLng = LatLng(location.latitude, location.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                    } else {
                        Toast.makeText(
                            this,
                            "Unable to fetch current location",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(
                        this,
                        "Failed to fetch location: ${it.message}",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
        } else {
            Toast.makeText(this, "Location permission is required", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (isLocationPermissionGranted()) {
            setLocationToCurrentPosition()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        requestLocationPermission()
        setContentView(R.layout.map_activity)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        locationProvider = LocationServices.getFusedLocationProviderClient(this)
        configureButton()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if(isLocationPermissionGranted())
                setLocationToCurrentPosition()
            else {
                Toast.makeText(this, "Bluetooth permissions is required", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


}
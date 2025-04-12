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
import java.util.Date

class MapActivity: AppCompatActivity(), OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback  {
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val POSITION_UPDATE_TIME_LOWER_BOUND = 1000L
        private const val POSITION_UPDATE_TIME_UPPER_BOUND = 10000L
        private const val POSITION_UPDATE_TIME = 5000L

    }
    private lateinit var mMap: GoogleMap
    private lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var recordedPath: MutableList<LatLng>
    private var pausePath: MutableList<LatLng> = mutableListOf()
    private var isRecording = false
    private var isHistoryShown = false
    private lateinit var startDate: Date
    private lateinit var endDate: Date
    private lateinit var locationCallback: com.google.android.gms.location.LocationCallback
    private lateinit var locationRequest: com.google.android.gms.location.LocationRequest

@SuppressLint("UseCompatLoadingForDrawables")
private fun configureButton(){
    val buttonStopHike = findViewById<ImageButton>(R.id.button_stop_hike)
    val buttonPauseHike = findViewById<ImageButton>(R.id.button_pause_hike)
    val buttonStartHike = findViewById<ImageButton>(R.id.button_start_hike)
    if (!isHistoryShown){
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
            buttonStopHike.isEnabled = true
            buttonStopHike.background = getDrawable(R.mipmap.stop_hike)
            buttonPauseHike.isEnabled = true
            buttonPauseHike.background = getDrawable(R.mipmap.pause_hike)
            buttonStartHike.isEnabled = false
            buttonStartHike.background = getDrawable(R.mipmap.start_hike_disable)
        } else {
            buttonStopHike.isEnabled = false
            buttonStopHike.background = getDrawable(R.mipmap.stop_hike_disable)
            buttonPauseHike.isEnabled = false
            buttonPauseHike.background = getDrawable(R.mipmap.pause_hike_disable)
            buttonStartHike.isEnabled = true
            buttonStartHike.background = getDrawable(R.mipmap.start_hike)
        }
    }
    else{
        buttonStopHike.setEnabled(false)
        buttonStopHike.visibility = View.GONE
        buttonPauseHike.setEnabled(false)
        buttonPauseHike.visibility = View.GONE
        buttonStartHike.setEnabled(false)
        buttonStartHike.visibility = View.GONE
    }
}

    private fun addMarker(latLng: LatLng, marker: Float, title: String) {
        val markerOptions = MarkerOptions()
            .position(latLng)
            .icon(BitmapDescriptorFactory.defaultMarker(marker))
            .title(title)
        mMap.addMarker(markerOptions)

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
        configureButton()
        pausePath.add(recordedPath.last())
        drawDirection(android.graphics.Color.YELLOW)
    }

    private fun stopRecording() {
        if (::locationCallback.isInitialized) {
            locationProvider.removeLocationUpdates(locationCallback)
        }
        isRecording = false
        endDate = Date()
        configureButton()
        drawDirection(android.graphics.Color.RED)
        addMarker(recordedPath.last(), BitmapDescriptorFactory.HUE_RED, "End")
    }

    @SuppressLint("MissingPermission")
    fun startRecording() {
        if (!isLocationPermissionGranted()) {
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_SHORT).show()
            return
        }

        if (isRecording) return // Already recording

        isRecording = true
        startDate = Date()
        configureButton()
        recordedPath = mutableListOf()

        locationRequest = com.google.android.gms.location.LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, POSITION_UPDATE_TIME
        ).setMinUpdateIntervalMillis(POSITION_UPDATE_TIME_LOWER_BOUND)
            .setMaxUpdateDelayMillis(POSITION_UPDATE_TIME_UPPER_BOUND)
            .build()

        locationCallback = object : com.google.android.gms.location.LocationCallback() {
            override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                super.onLocationResult(locationResult)
                for (location in locationResult.locations) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    recordedPath.add(latLng)
                    drawDirection(android.graphics.Color.GREEN)
                }
            }
        }

        locationProvider.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
    }

    private fun drawDirection(color: Int) {
        if (recordedPath.size < 2) return

        val polylineOptions = com.google.android.gms.maps.model.PolylineOptions()
            .addAll(recordedPath)
            .width(8f)
            .color(color)

        mMap.clear()
        addMarker(recordedPath.first(), BitmapDescriptorFactory.HUE_GREEN, "Start")
        if(pausePath.isNotEmpty()){
            for (latLng in pausePath) {
                addMarker(latLng, BitmapDescriptorFactory.HUE_YELLOW, "Pause")
            }
        }
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
        if(isHistoryShown)
        {
            // fetch recorded path from database
            drawDirection(android.graphics.Color.RED)
            addMarker(recordedPath.first(), BitmapDescriptorFactory.HUE_GREEN, "Start")
            addMarker(recordedPath.last(), BitmapDescriptorFactory.HUE_RED, "End")
            for (latLng in pausePath) {
                addMarker(latLng, BitmapDescriptorFactory.HUE_YELLOW, "Pause")
            }
        }

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
package com.example.polyhike

import android.content.pm.PackageManager
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.polyhike.model.HikeInfo
import com.example.polyhike.ui.record.HikeInfoViewModel
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class MapActivity: AppCompatActivity(), OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback  {
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val POSITION_UPDATE_TIME_LOWER_BOUND = 1000L
        private const val POSITION_UPDATE_TIME_UPPER_BOUND = 10000L
        private const val POSITION_UPDATE_TIME = 5000L

    }

    private lateinit var hikeInfoViewModel: HikeInfoViewModel
    private var totalDistance = 0.0
    private var lastLocation: LatLng? = null
    private var totalSteps = 0
    private var currentSpeed = 0.0
    private var currentBearing = 0.0
    private var currentTemperature = 0.0
    private var averageSpeed = 0.0
    private var recordedPath: MutableList<LatLng> = mutableListOf()
    private lateinit var mMap: GoogleMap
    private lateinit var locationProvider: FusedLocationProviderClient
    private var pausePath: MutableList<LatLng> = mutableListOf()
    private var isRecording = false
    private var isHistoryShown = false
    private var startDate: Date? = null
    private var endDate: Date? = null
    private lateinit var locationCallback: com.google.android.gms.location.LocationCallback
    private lateinit var locationRequest: com.google.android.gms.location.LocationRequest

@SuppressLint("UseCompatLoadingForDrawables")
private fun configureButton(){
    val buttonStopHike = findViewById<ImageButton>(R.id.button_stop_hike)
    val buttonPauseHike = findViewById<ImageButton>(R.id.button_pause_hike)
    val buttonStartHike = findViewById<ImageButton>(R.id.button_start_hike)
    val buttonToggleList = findViewById<ImageButton>(R.id.BtnToggleList)
    val buttonBackToHistory = findViewById<ImageButton>(R.id.button_back_to_history)

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

    buttonToggleList.setOnClickListener {
        showHikeInfo()
    }

    buttonBackToHistory.setOnClickListener {
        val intent = Intent(this, NavManagerActivity::class.java)
        startActivity(intent)
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
        if (recordedPath.size >= 2)
            addMarker(recordedPath.last(), BitmapDescriptorFactory.HUE_RED, "End")

        saveToDatabase()
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

                    lastLocation?.let {
                        val results = FloatArray(1)
                        android.location.Location.distanceBetween(
                            it.latitude, it.longitude,
                            latLng.latitude, latLng.longitude,
                            results
                        )
                        totalDistance += results[0]
                    }
                    lastLocation = latLng
                    // Speed (m/s to km/h)
                    currentSpeed = location.speed * 3.6
                    currentBearing = location.bearing.toDouble()
                    // TODO: use sensor
                    currentTemperature = 15 + (Math.random() * 10)
                    // TODO: use sensor
                    totalSteps += 1

                    recordedPath.add(latLng)
                    drawDirection(android.graphics.Color.GREEN)
                }
            }
        }

        locationProvider.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
    }

    @SuppressLint("SetTextI18n", "DefaultLocale")
    private fun showHikeInfo(){
        val infoView = LayoutInflater.from(this).inflate(R.layout.hike_menu, null)
        val startDateTextView = infoView.findViewById<TextView>(R.id.startDate)
        val endDateTextView = infoView.findViewById<TextView>(R.id.endDate)
        val speedTextView = infoView.findViewById<TextView>(R.id.speed)
        val stepsTextView = infoView.findViewById<TextView>(R.id.steps)
        val temperatureTextView = infoView.findViewById<TextView>(R.id.temperature)
        val distanceTextView = infoView.findViewById<TextView>(R.id.distance)
        val averageSpeedTextView = infoView.findViewById<TextView>(R.id.average_speed)

        if(isHistoryShown)
            speedTextView.visibility = View.GONE
        if (startDate != null ) {
            if(isHistoryShown)
                averageSpeed = totalDistance / ((endDate!!.time - startDate!!.time) / 1000.0) * 3.6
            else if (isRecording)
                averageSpeed = totalDistance / ((Date().time - startDate!!.time) / 1000.0) * 3.6
        }

        startDateTextView.text = "Date de début : ${startDate?.toString()?: "Non démarré"}"
        endDateTextView.text = "Date de fin : ${endDate?.toString()?: "Non terminé"}"
        speedTextView.text = "Vitesse : ${String.format("%.2f", currentSpeed)} km/h"
        stepsTextView.text = "Nombre de pas : $totalSteps"
        temperatureTextView.text = "Température : ${String.format("%.1f", currentTemperature)}°C"
        distanceTextView.text = "Distance parcourue : ${String.format("%.2f", totalDistance / 1000)} km"
        averageSpeedTextView.text = "Vitesse moyenne : ${String.format("%.2f", averageSpeed)} km/h"

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(infoView)
            .setCancelable(true)
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
        dialog.show()
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
        hikeInfoViewModel = ViewModelProvider(this)[HikeInfoViewModel::class.java]
        supportActionBar?.hide()
        requestLocationPermission()
        setContentView(R.layout.map_activity)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        locationProvider = LocationServices.getFusedLocationProviderClient(this)
        configureButton()

        isHistoryShown = intent.getBooleanExtra("HISTORY_MODE", false)
        val hikeId = intent.getIntExtra("HIKE_ID", -1)

        if(isHistoryShown)
        {
            displayPreviousHikeMap(hikeId)
        }

    }

    private fun displayPreviousHikeMap(hikeId: Int){
        lifecycleScope.launch(Dispatchers.Main) {
            hikeInfoViewModel.getHikeById(hikeId).observe(this@MapActivity) { hike ->
                hikeInfoViewModel.currentHike = hike

            startDate = hikeInfoViewModel.currentHike.startDate
            endDate = hikeInfoViewModel.currentHike.endDate
            totalDistance = hikeInfoViewModel.currentHike.totalDistance
            recordedPath = hikeInfoViewModel.currentHike.recordedPath
            pausePath = hikeInfoViewModel.currentHike.pausePath
            totalSteps = hikeInfoViewModel.currentHike.totalSteps
            currentSpeed = hikeInfoViewModel.currentHike.currentSpeed
            currentTemperature = hikeInfoViewModel.currentHike.currentTemperature
            averageSpeed = hikeInfoViewModel.currentHike.averageSpeed
            configureButton()
            drawDirection(android.graphics.Color.RED)
            addMarker(recordedPath.first(), BitmapDescriptorFactory.HUE_GREEN, "Start")
            addMarker(recordedPath.last(), BitmapDescriptorFactory.HUE_RED, "End")
            for (latLng in pausePath) {
                addMarker(latLng, BitmapDescriptorFactory.HUE_YELLOW, "Pause")
            }
            }
        }
    }

    private fun saveToDatabase() {
        val hikeInfo = HikeInfo(
            currentUserId = hikeInfoViewModel.currentUserId,
            startDate = startDate?: Date(),
            endDate = endDate?: Date(),
            currentSpeed = currentSpeed,
            averageSpeed = averageSpeed,
            totalSteps = totalSteps,
            totalDistance = totalDistance,
            currentTemperature = currentTemperature,
            recordedPath = recordedPath,
            pausePath = pausePath
        )

        hikeInfoViewModel.saveHike(hikeInfo)
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
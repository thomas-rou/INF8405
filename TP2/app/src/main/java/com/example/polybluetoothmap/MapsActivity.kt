package com.example.polybluetoothmap

import androidx.activity.viewModels
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.polybluetoothmap.databinding.ActivityMapsBinding
import com.google.android.gms.location.LocationServices
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.polybluetoothmap.model.trackedDevice.TrackedDevice
import com.example.polybluetoothmap.ui.TrackedDeviceAdapter
import com.example.polybluetoothmap.ui.displayFindDeviceOnMap
import com.example.polybluetoothmap.ui.showDeviceDetailsDialog
import com.example.polybluetoothmap.ui.trackedItemSetupView
import com.example.polybluetoothmap.ui.updateDeviceListView
import com.example.polybluetoothmap.ui.SideMenuHelper
import com.example.polybluetoothmap.ui.ThemeManager
import com.example.polybluetoothmap.viewmodel.TrackedDeviceViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MapStyleOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    public lateinit var recyclerView: RecyclerView
    public lateinit var selectedDeviceAddress: String
    public lateinit var adapter: TrackedDeviceAdapter
    public var deviceList: MutableList<TrackedDevice> = mutableListOf()
    lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    internal lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var bluetoothAdapter: BluetoothAdapter
    private lateinit var themeManager: ThemeManager
    private lateinit var sideMenuHelper: SideMenuHelper
    val trackedDeviceViewModel: TrackedDeviceViewModel by viewModels()
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val BLUETOOTH_PERMISSION_REQUEST_CODE = 2
    }

    private val bluetoothScanner = object : BroadcastReceiver() {
        @SuppressLint("MissingPermission", "NewApi")
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {

                BluetoothDevice.ACTION_FOUND -> {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    if (device != null){
                        locationProvider.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val location = task.result
                                if (location != null) {
                                    val trackedDevice = TrackedDevice.fromBluetoothDevice(device, location.latitude, location.longitude)
                                    trackedDeviceViewModel.insert(trackedDevice)
                                    updateDeviceListView()
                                    return@addOnCompleteListener
                                }
                            }
                        }
                    }

                }

                BluetoothAdapter.ACTION_DISCOVERY_FINISHED ->{
                    bluetoothAdapter.startDiscovery()
                    updateCurrentPositionMarker()
                }

                BluetoothAdapter.ACTION_STATE_CHANGED ->{
                    when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)) {
                        BluetoothAdapter.STATE_ON -> {
                            Toast.makeText(context, "Bluetooth is on", Toast.LENGTH_SHORT).show()
                            bluetoothAdapter.startDiscovery()
                        }
                        BluetoothAdapter.STATE_OFF -> {
                            Toast.makeText(context, "Bluetooth is off", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

            }

        }
    }


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        requestBluetoothPermission()
        val bluetoothManager: BluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager.adapter
        registerReceiver(bluetoothScanner, IntentFilter(BluetoothDevice.ACTION_FOUND))
        registerReceiver(bluetoothScanner, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED))
        registerReceiver(bluetoothScanner, IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED))
        registerReceiver(bluetoothScanner, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        locationProvider = LocationServices.getFusedLocationProviderClient(this)

        themeManager = ThemeManager(this)


        bluetoothAdapter.startDiscovery()
        trackedItemSetupView()

        val btnToggleList = findViewById<ImageButton>(R.id.BtnToggleList)
        val drawerLayout = findViewById<DrawerLayout>(R.id.drawerLayout)
        val menuDrawer = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.menuDrawer)

        sideMenuHelper = SideMenuHelper(drawerLayout, menuDrawer, btnToggleList)
    }

    fun isLocationPermissionGranted(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
                )
    }

    private fun isBluetoothPermissionGranted():Boolean{
        return (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH)
                == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT)
                ==PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN)
                ==PackageManager.PERMISSION_GRANTED
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
        // else{
        //     setLocationToCurrentPosition()
        // }
    }

    private fun requestBluetoothPermission(){
        if (!isBluetoothPermissionGranted()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.BLUETOOTH,
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.BLUETOOTH_SCAN),
                BLUETOOTH_PERMISSION_REQUEST_CODE)
    }
    }

    @SuppressLint("MissingPermission")
    private fun updateCurrentPositionMarker(){
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.addMarker(MarkerOptions().position(currentLatLng).title("current location")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)))
            }
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
                    mMap.addMarker(MarkerOptions().position(currentLatLng).title("current location")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)))
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

    fun toggleTheme(view: View) {
        themeManager.toggleTheme()
        themeManager.applyMapStyle(mMap)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMarkerClickListener { clickedMarker ->
            val device = deviceList.find { it.address == clickedMarker.title }
            if (device != null) {
                selectedDeviceAddress = device.address
                showDeviceDetailsDialog(this, device)
            }
            false
            }
        requestLocationPermission()

        themeManager.applyMapStyle(mMap)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            //setLocationToCurrentPosition()
        } else if (requestCode == BLUETOOTH_PERMISSION_REQUEST_CODE) {
            if(!isBluetoothPermissionGranted()) {
                Toast.makeText(this, "Bluetooth permissions is required", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}
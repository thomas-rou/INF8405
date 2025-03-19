package com.example.polybluetoothmap.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.polybluetoothmap.model.AppDatabase
import com.example.polybluetoothmap.model.trackedDevice.TrackedDevice
import kotlinx.coroutines.launch

class TrackedDeviceViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val trackedDeviceDao = db.trackedDeviceDao()

    fun insert(trackedDevice: TrackedDevice) {
        viewModelScope.launch {
            trackedDeviceDao.insert(trackedDevice)
        }
    }

    fun updateFavoriteStatus(address: String, isFavorite: Boolean) {
        viewModelScope.launch {
            trackedDeviceDao.updateFavoriteStatus(address, isFavorite)
        }
    }

    fun getAll(callback: (List<TrackedDevice>) -> Unit) {
        viewModelScope.launch {
            callback(trackedDeviceDao.getAll())
        }
    }

    fun getByAddress(address: String, callback: (TrackedDevice?) -> Unit) {
        viewModelScope.launch {
            callback(trackedDeviceDao.getByAddress(address))
        }
    }

    fun delete(trackedDevice: TrackedDevice) {
        viewModelScope.launch {
            trackedDeviceDao.delete(trackedDevice)
        }
    }
}

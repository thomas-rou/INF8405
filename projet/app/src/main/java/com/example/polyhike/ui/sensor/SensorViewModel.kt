package com.example.polyhike.ui.sensor

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SensorViewModel(application: Application) : AndroidViewModel(application) {

    private val _sensorUiState = MutableLiveData(SensorUiState())
    val sensorUiState: LiveData<SensorUiState> = _sensorUiState

    fun updateAzimuth(newAzimuth: Float) {
        _sensorUiState.value = _sensorUiState.value?.copy(azimuth = newAzimuth)
    }

    fun updateStepCount(count: Int) {
        _sensorUiState.value = _sensorUiState.value?.copy(stepCount = count)
    }

    fun updateAcceleration(accelerationMagnitude: Float) {
        _sensorUiState.value = _sensorUiState.value?.copy(
            accelerationMagnitude = accelerationMagnitude
        )
    }

    fun updateTemperature(temp: Float?) {
        _sensorUiState.value = _sensorUiState.value?.copy(temperature = temp)
    }
}
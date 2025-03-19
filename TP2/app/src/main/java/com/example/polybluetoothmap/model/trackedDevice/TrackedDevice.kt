package com.example.polybluetoothmap.model.trackedDevice

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import com.google.gson.Gson
import androidx.room.*
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromListString(value: List<String>?): String {
        return gson.toJson(value) // Convertit la liste en JSON
    }

    @TypeConverter
    fun toListString(value: String): List<String>? {
        val type = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, type) // Convertit le JSON en liste
    }
}

@Entity(tableName = "tracked_devices")
data class TrackedDevice(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val latitude: Double,
    val longitude: Double,
    val name: String?,
    val address: String, // Adresse MAC unique
    val type: Int,
    val bondState: Int,
    val isFavorite: Boolean = false,
    val alias: String?,
    val bluetoothClass: Int?,
    @TypeConverters(Converters::class) val uuids: List<String>?
) {
    companion object {
        @SuppressLint("MissingPermission", "NewApi")
        fun fromBluetoothDevice(device: BluetoothDevice, latitude: Double, longitude: Double): TrackedDevice {
            return TrackedDevice(
                latitude = latitude,
                longitude = longitude,
                name = device.name?:"",
                address = device.address,
                type = device.type,
                bondState = device.bondState,
                alias = device.alias?:"",
                bluetoothClass = device.bluetoothClass?.deviceClass,
                uuids = device.uuids?.map { it.toString() }
            )
        }
    }
}

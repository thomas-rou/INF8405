package com.example.polybluetoothmap.model.trackedDevice

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TrackedDeviceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(trackedDevice: TrackedDevice)

    @Query("SELECT * FROM tracked_devices")
    fun getAll(): LiveData<List<TrackedDevice>>

    @Query("SELECT * FROM tracked_devices WHERE address = :address")
    fun getByAddress(address: String): LiveData<TrackedDevice?>

    @Update
    suspend fun update(trackedDevice: TrackedDevice)

    @Query("UPDATE tracked_devices SET isFavorite = :isFavorite WHERE address = :address")
    suspend fun updateFavoriteStatus(address: String, isFavorite: Boolean)

    @Delete
    suspend fun delete(trackedDevice: TrackedDevice)
}

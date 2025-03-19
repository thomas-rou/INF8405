package com.example.polybluetoothmap.model.trackedDevice

import androidx.room.*

@Dao
interface TrackedDeviceDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(trackedDevice: TrackedDevice)

    @Query("SELECT * FROM tracked_devices")
    suspend fun getAll(): List<TrackedDevice>

    @Query("SELECT * FROM tracked_devices WHERE address = :address")
    suspend fun getByAddress(address: String): TrackedDevice?

    @Update
    suspend fun update(trackedDevice: TrackedDevice)

    @Query("UPDATE tracked_devices SET isFavorite = :isFavorite WHERE address = :address")
    suspend fun updateFavoriteStatus(address: String, isFavorite: Boolean)

    @Delete
    suspend fun delete(trackedDevice: TrackedDevice)
}

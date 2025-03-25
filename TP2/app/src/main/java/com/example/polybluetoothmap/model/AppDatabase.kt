package com.example.polybluetoothmap.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.polybluetoothmap.model.trackedDevice.Converters
import com.example.polybluetoothmap.model.trackedDevice.TrackedDevice
import com.example.polybluetoothmap.model.trackedDevice.TrackedDeviceDao

@Database(entities = [TrackedDevice::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun trackedDeviceDao(): TrackedDeviceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tracked_devices_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

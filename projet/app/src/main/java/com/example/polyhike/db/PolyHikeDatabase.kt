package com.example.polyhike.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.polyhike.util.DateConverter
import com.example.polyhike.model.UserProfile
import com.example.polyhike.model.Activity
import com.example.polyhike.model.HikeInfo
import com.example.polyhike.model.Statistic
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromLatLngList(list: MutableList<LatLng>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun toLatLngList(json: String): MutableList<LatLng> {
        val type = object : TypeToken<MutableList<LatLng>>() {}.type
        return gson.fromJson(json, type)
    }
}

@Database(entities = [UserProfile::class, Statistic::class, Activity::class, HikeInfo::class], version = 2)
@TypeConverters(DateConverter::class, Converters::class)
abstract class PolyHikeDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun statisticDao(): StatisticDao
    abstract fun activityDao(): ActivityDao
    abstract fun hikeInfoDao(): HikeInfoDao

    companion object {
        @Volatile
        private var INSTANCE: PolyHikeDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): PolyHikeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PolyHikeDatabase::class.java,
                    "polyhike_database"
                )
                    .addCallback(AppDatabaseCallback(CoroutineScope(Dispatchers.IO)))
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    populateDB(database)
                }
            }
        }

        fun populateDB(database: PolyHikeDatabase) {
            database.userProfileDao().insert(UserProfile(id = 1, "UserName", password = "pass", dateOfBirth = "01/01/2001", photoURI = "", friends = 2))
        }
    }
}
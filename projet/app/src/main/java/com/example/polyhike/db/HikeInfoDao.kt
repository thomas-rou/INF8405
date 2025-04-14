package com.example.polyhike.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.polyhike.model.HikeInfo

@Dao
interface HikeInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHike(hikeInfo: HikeInfo)

    @Query("SELECT * FROM hikeInfo WHERE currentUserId = :userId")
    fun getHikesForUser(userId: Int): LiveData<List<HikeInfo>>
    @Query("SELECT * FROM hikeInfo")
    fun getAllHikes(): LiveData<List<HikeInfo>>
    @Query("SELECT * FROM hikeInfo WHERE id = :hikeId")
    fun getHikeById(hikeId: Int): LiveData<HikeInfo>

    @Query("SELECT SUM(totalSteps) FROM hikeInfo WHERE currentUserId = :userId")
    fun getTotalStepsBUserId(userId: Int): Int?

    @Query("SELECT SUM(totalDistance) FROM hikeInfo WHERE currentUserId = :userId")
    fun getTotalDistancesBUserId(userId: Int): Int?
}
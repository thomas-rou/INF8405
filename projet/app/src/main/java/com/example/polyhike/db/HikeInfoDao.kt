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
}
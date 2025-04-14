package com.example.polyhike.ui.record

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.polyhike.db.PolyHikeDatabase
import com.example.polyhike.model.HikeInfo
import com.example.polyhike.model.UserProfile
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.util.Date

class HikeInfoViewModel(application: Application) : AndroidViewModel(application) {
    var currentUserId: Int = -1
    lateinit var currentHike:HikeInfo
    private val hikeDao = PolyHikeDatabase.getDatabase(application, viewModelScope).hikeInfoDao()

    fun saveHike(hikeInfo: HikeInfo) = viewModelScope.launch {
        hikeDao.insertHike(hikeInfo)
    }

    fun addHikeToFirestore(hike: HikeInfo?) {
        val db = Firebase.firestore
        val hikeInfo = hashMapOf(
            "id" to hike?.id,
            "userId" to hike?.currentUserId,
            "startDate" to hike?.startDate,
            "endDate" to hike?.endDate,
            "currentSpeed" to hike?.currentSpeed,
            "averageSpeed" to hike?.averageSpeed,
            "totalSteps" to hike?.totalSteps,
            "totalDistance" to hike?.totalDistance,
            "currentTemperature" to hike?.currentTemperature,
            "recordedPath" to hike?.recordedPath,
            "pausePath" to hike?.pausePath,
        )
        db.collection("hikesInfos")
            .add(hikeInfo)
            .addOnSuccessListener { documentReference ->
                println("DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                println("Error adding document: $e")
            }
    }

    fun getUserHikes(userId: Int = currentUserId): LiveData<List<HikeInfo>> {
        return hikeDao.getHikesForUser(userId)
    }

    fun getAllHikes(): LiveData<List<HikeInfo>> {
        return hikeDao.getAllHikes()
    }

    fun getHikeById(hikeId: Int): LiveData<HikeInfo> {
        return hikeDao.getHikeById(hikeId)
    }

}

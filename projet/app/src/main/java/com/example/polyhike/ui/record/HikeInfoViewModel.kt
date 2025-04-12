package com.example.polyhike.ui.record

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.polyhike.db.PolyHikeDatabase
import com.example.polyhike.model.HikeInfo
import kotlinx.coroutines.launch

class HikeInfoViewModel(application: Application) : AndroidViewModel(application) {
    var isHistoryMode = false
    var currentUserId: Int = -1
    lateinit var currentHike:HikeInfo
    private val hikeDao = PolyHikeDatabase.getDatabase(application, viewModelScope).hikeInfoDao()

    fun saveHike(hikeInfo: HikeInfo) = viewModelScope.launch {
        hikeDao.insertHike(hikeInfo)
    }

    fun getUserHikes(): LiveData<List<HikeInfo>> {
        return hikeDao.getHikesForUser(currentUserId)
    }
}

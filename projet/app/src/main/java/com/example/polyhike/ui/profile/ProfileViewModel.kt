package com.example.polyhike.ui.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.polyhike.db.PolyHikeDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val userProfileDao = PolyHikeDatabase.getDatabase(application, viewModelScope).userProfileDao()

    private val _text = MutableLiveData<String>()
    val text: LiveData<String> = _text

    fun getUserProfile(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) { // Switch to a background thread
            try {
                val userProfile = userProfileDao.getById(userId)
                // Switch back to the Main thread to update LiveData
                withContext(Dispatchers.Main) {
                    _text.value = userProfile?.name
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error getting user profile: ${e.message}")
                withContext(Dispatchers.Main) {
                    _text.value = "Error loading user profile"
                }
            }
        }
    }
}
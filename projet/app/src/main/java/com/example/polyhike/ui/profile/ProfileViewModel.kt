package com.example.polyhike.ui.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.polyhike.db.PolyHikeDatabase
import com.example.polyhike.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val userProfileDao = PolyHikeDatabase.getDatabase(application, viewModelScope).userProfileDao()

    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: LiveData<UserProfile?> = _userProfile

    fun getUserProfile(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userProfile = userProfileDao.getById(userId)
                withContext(Dispatchers.Main) {
                    _userProfile.value = userProfile
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error getting user profile: ${e.message}")
            }
        }
    }

    fun getAgeFromDateOfBirth(dateOfBirth: String?): Int {
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val dateOfBirthFormated = sdf.parse(dateOfBirth) ?: return 0

        val calDateOfBirth = Calendar.getInstance()
        calDateOfBirth.time = dateOfBirthFormated

        val calToday = Calendar.getInstance()
        var age = calToday.get(Calendar.YEAR) - calDateOfBirth.get(Calendar.YEAR)
        if (calToday.get(Calendar.DAY_OF_YEAR) < calDateOfBirth.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        return age
    }
}
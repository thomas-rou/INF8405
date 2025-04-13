package com.example.polyhike.ui.profile

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.polyhike.db.PolyHikeDatabase
import com.example.polyhike.model.UserProfile
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.graphics.Color
import androidx.core.graphics.toColorInt

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val userProfileDao = PolyHikeDatabase.getDatabase(application, viewModelScope).userProfileDao()
    private val hikeInfoDao = PolyHikeDatabase.getDatabase(application, viewModelScope).hikeInfoDao()

    private val _userProfile = MutableLiveData<UserProfile?>()
    val userProfile: LiveData<UserProfile?> = _userProfile
    private val _totalSteps = MutableLiveData<Int?>()
    val totalSteps: LiveData<Int?> = _totalSteps
    private val _totalDistance = MutableLiveData<Int?>()
    val totalDistance: LiveData<Int?> = _totalDistance

    private val _barData = MutableLiveData<BarData>()
    val barData: LiveData<BarData> = _barData

    init {
        setupChartData()
    }

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

    fun getTotalStepsByUserId(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val stepsTotal = hikeInfoDao.getTotalStepsBUserId(userId)
                withContext(Dispatchers.Main) {
                    _totalSteps.value = stepsTotal
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error getting user total steps: ${e.message}")
            }
        }
    }

    fun getTotalDistanceByUserId(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val distanceTotal = hikeInfoDao.getTotalDistancesBUserId(userId)
                withContext(Dispatchers.Main) {
                    _totalDistance.value = distanceTotal
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error getting user total distance: ${e.message}")
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

    fun setupChartData() {
        // TODO: replace
        val entries = arrayListOf<BarEntry>()
        entries.add(BarEntry(0f, 1000f))
        entries.add(BarEntry(1f, 2000f))
        entries.add(BarEntry(2f, 3000f))
        entries.add(BarEntry(3f, 4000f))
        entries.add(BarEntry(4f, 5000f))
        entries.add(BarEntry(5f, 6000f))
        entries.add(BarEntry(6f, 7000f))

        val dataSet = BarDataSet(entries, "Nombre de pas par jour")
        dataSet.color = "#E5E5E5".toColorInt()
        val barData = BarData(dataSet)
        barData.barWidth = 0.5f

        _barData.value = barData
    }
}
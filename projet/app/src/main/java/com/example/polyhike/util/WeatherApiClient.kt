package com.example.polyhike.util

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import com.example.polyhike.BuildConfig
import java.net.URL

object WeatherApiClient {
    private const val API_KEY = BuildConfig.WEATHER_API_KEY
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/weather"

    suspend fun getTemperature(lat: Double, lon: Double): Double? = withContext(Dispatchers.IO) {
        try {
            val url = "$BASE_URL?lat=$lat&lon=$lon&units=metric&appid=$API_KEY"
            val response = URL(url).readText()
            val json = JSONObject(response)
            return@withContext json.getJSONObject("main").getDouble("temp")
        } catch (e: Exception) {
            Log.e("WeatherApiClient", "Erreur API météo : ${e.message}")
            return@withContext null
        }
    }
}
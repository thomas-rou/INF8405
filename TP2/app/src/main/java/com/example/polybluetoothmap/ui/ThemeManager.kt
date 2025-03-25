package com.example.polybluetoothmap.ui
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MapStyleOptions
import com.example.polybluetoothmap.R

class ThemeManager(private val context: Context) {

    enum class ThemeMode {
        LIGHT,
        DARK
    }

    private var currentThemeMode: ThemeMode = ThemeMode.LIGHT

    init {
        val prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("isDarkMode", false)
        currentThemeMode = if (isDarkMode) ThemeMode.DARK else ThemeMode.LIGHT
        applyTheme()
    }

    fun toggleTheme() {
        currentThemeMode = if (currentThemeMode == ThemeMode.LIGHT) {
            ThemeMode.DARK
        } else {
            ThemeMode.LIGHT
        }
        saveThemePreference(currentThemeMode == ThemeMode.DARK)
        applyTheme()
    }

    private fun applyTheme() {
        AppCompatDelegate.setDefaultNightMode(
            if (currentThemeMode == ThemeMode.DARK) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    fun applyMapStyle(mMap: GoogleMap) {
        val styleResId = if (currentThemeMode == ThemeMode.DARK) {
            R.raw.dark_map_style
        } else {
            R.raw.default_map_style
        }
        try {
            val success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, styleResId))
            if (!success) {
                Log.e("ThemeManager", "Failed to apply map style: $styleResId")
                Toast.makeText(context, "Failed to load map style. Using default.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("ThemeManager", "Error applying map style", e)
            Toast.makeText(context, "Error applying map style. Please try again.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveThemePreference(isDarkMode: Boolean) {
        val prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putBoolean("isDarkMode", isDarkMode)
            apply()
        }
    }
}

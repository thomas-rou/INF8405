package com.example.polybluetoothmap.ui
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MapStyleOptions
import com.example.polybluetoothmap.R


/**
 * ThemeManager class handles the application of themes (light and dark mode) and manages map styling
 * based on the selected theme.
 *
 * @param context The context of the application, used to access shared preferences and apply themes.
 */
class ThemeManager(private val context: Context) {

    enum class ThemeMode {
        LIGHT,
        DARK
    }

    private var currentThemeMode: ThemeMode = ThemeMode.LIGHT

    /**
     * Initializes the ThemeManager by loading the saved theme preference from shared preferences
     * and applying the corresponding theme.
     */
    init {
        val prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val isDarkMode = prefs.getBoolean("isDarkMode", false)
        currentThemeMode = if (isDarkMode) ThemeMode.DARK else ThemeMode.LIGHT
        applyTheme()
    }

    /**
     * Toggles the theme between LIGHT and DARK modes.
     * Saves the new preference and applies the selected theme.
     */
    fun toggleTheme() {
        currentThemeMode = if (currentThemeMode == ThemeMode.LIGHT) {
            ThemeMode.DARK
        } else {
            ThemeMode.LIGHT
        }
        saveThemePreference(currentThemeMode == ThemeMode.DARK)
        applyTheme()
    }

    /**
     * Applies the selected theme (either LIGHT or DARK) by updating the app's night mode.
     */
    private fun applyTheme() {
        AppCompatDelegate.setDefaultNightMode(
            if (currentThemeMode == ThemeMode.DARK) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

    /**
     * Applies a map style based on the selected theme mode.
     * If the theme is DARK, a dark map style is applied; otherwise, a default map style is applied.
     *
     * @param mMap The GoogleMap instance on which the style will be applied.
     */
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

    /**
     * Saves the theme preference (LIGHT or DARK) in shared preferences.
     *
     * @param isDarkMode Boolean indicating whether the dark mode is enabled or not.
     */
    private fun saveThemePreference(isDarkMode: Boolean) {
        val prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        with(prefs.edit()) {
            putBoolean("isDarkMode", isDarkMode)
            apply()
        }
    }
}

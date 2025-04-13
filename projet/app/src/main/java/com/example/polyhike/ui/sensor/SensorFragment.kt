package com.example.polyhike.ui.sensor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.polyhike.R
import com.example.polyhike.util.Azimuth
import com.example.polyhike.util.CompassManager
import com.example.polyhike.util.StepCounterManager
import kotlin.math.roundToInt

// TODO: add conditionnal activation of sensors => e.g. N/A if sensors not available.

class SensorFragment : Fragment() {

    private val viewModel: SensorViewModel by viewModels()
    private lateinit var compassManager: CompassManager
    private lateinit var stepCounterManager: StepCounterManager

    private lateinit var textAzimuth: TextView
    private lateinit var textSteps: TextView
    private lateinit var textSpeed: TextView
    private lateinit var textTemp: TextView
    private lateinit var compassView: CompassView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_sensor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        textAzimuth = view.findViewById(R.id.textAzimuth)
        textSteps = view.findViewById(R.id.textSteps)
        textSpeed = view.findViewById(R.id.textSpeed)
        textTemp = view.findViewById(R.id.textTemperature)
        compassView = view.findViewById(R.id.compassView)


        // Compass
        compassManager = CompassManager(requireContext()) { smoothedAzimuth, rawAzimuth ->
            compassView.updateAzimuth(smoothedAzimuth)
            viewModel.updateAzimuth(rawAzimuth)
        }

        stepCounterManager = StepCounterManager(requireContext()) { steps ->
            viewModel.updateStepCount(steps)
        }

        viewModel.sensorUiState.observe(viewLifecycleOwner) { state ->
            val az = Azimuth(state.azimuth)
            textAzimuth.text = "Direction: ${az.cardinalDirection} (${state.azimuth.roundToInt()}°)"
            textSteps.text = "Pas: ${state.stepCount}"
            textSpeed.text = "Vitesse: ${"%.1f".format(state.speed)} km/h"
            textTemp.text = "Température: ${state.temperature?.toString() ?: "--"} °C"
        }
    }

    override fun onResume() {
        super.onResume()
        compassManager.start()
        stepCounterManager.start()
    }

    override fun onPause() {
        super.onPause()
        compassManager.stop()
        stepCounterManager.stop()
    }
}
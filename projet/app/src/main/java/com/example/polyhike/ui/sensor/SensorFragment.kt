package com.example.polyhike.ui.sensor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.polyhike.R
import com.example.polyhike.util.AmbientTemperatureManager
import com.example.polyhike.util.Azimuth
import com.example.polyhike.util.ClockManager
import com.example.polyhike.util.CompassManager
import com.example.polyhike.util.LinearAccelerationManager
import com.example.polyhike.util.StepCounterManager
import kotlin.math.roundToInt

// TODO: add conditionnal activation of sensors => e.g. N/A if sensors not available.

class SensorFragment : Fragment() {

    private val viewModel: SensorViewModel by viewModels()
    private lateinit var compassManager: CompassManager
    private lateinit var stepCounterManager: StepCounterManager
    private lateinit var linearAccelerationManager: LinearAccelerationManager
    private lateinit var ambientTemperatureManager: AmbientTemperatureManager
    private lateinit var clockManager: ClockManager

    private lateinit var textAzimuth: TextView
    private lateinit var textSteps: TextView
    private lateinit var textAcceleration: TextView
    private lateinit var textTemp: TextView
    private lateinit var compassView: CompassView
    private lateinit var textClock: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_sensor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        textAzimuth = view.findViewById(R.id.textAzimuth)
        textSteps = view.findViewById(R.id.textSteps)
        textAcceleration= view.findViewById(R.id.textAcceleration)
        textTemp = view.findViewById(R.id.textTemperature)
        compassView = view.findViewById(R.id.compassView)
        textClock = view.findViewById(R.id.textClock)


        // Compass
        compassManager = CompassManager(requireContext()) { smoothedAzimuth, rawAzimuth ->
            compassView.updateAzimuth(smoothedAzimuth)
            viewModel.updateAzimuth(rawAzimuth)
        }

        stepCounterManager = StepCounterManager(requireContext()) { steps ->
            viewModel.updateStepCount(steps)
        }

        linearAccelerationManager = LinearAccelerationManager(requireContext()) { x, y, z, magnitude ->
            viewModel.updateAcceleration(magnitude)
        }

        ambientTemperatureManager = AmbientTemperatureManager(requireContext()) { temperature ->
            viewModel.updateTemperature(temperature)
        }

        clockManager = ClockManager(textClock)
        stepCounterManager.start()
        clockManager.start()

        viewModel.sensorUiState.observe(viewLifecycleOwner) { state ->
            val az = Azimuth(state.azimuth)
            val deg = (state.azimuth + 360f) % 360f
            textAzimuth.text = "Direction: ${az.cardinalDirection} (${deg.roundToInt()}°)"
            textSteps.text = "Pas: ${state.stepCount}"
            textAcceleration.text = "Accélération: ${"%.2f".format(state.accelerationMagnitude)} m/s²"
            textTemp.text = "Température: ${state.temperature?.toString() ?: "--"} °C"
        }
    }

    override fun onResume() {
        super.onResume()
        compassManager.start()
        linearAccelerationManager.start()
        ambientTemperatureManager.start()
    }

    override fun onPause() {
        super.onPause()
        compassManager.stop()
        stepCounterManager.stop()
        linearAccelerationManager.stop()
        ambientTemperatureManager.stop()
        clockManager.stop()
    }
}
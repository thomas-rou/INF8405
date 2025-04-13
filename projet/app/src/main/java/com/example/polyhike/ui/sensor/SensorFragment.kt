package com.example.polyhike.ui.sensor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.polyhike.R
import com.example.polyhike.util.AmbientPressureManager
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
    private lateinit var ambientPressureManager: AmbientPressureManager

    private lateinit var textAzimuth: TextView
    private lateinit var textSteps: TextView
    private lateinit var textAcceleration: TextView
    private lateinit var textTemp: TextView
    private lateinit var compassView: CompassView
    private lateinit var textClock: TextView
    private lateinit var textPressure: TextView
    private lateinit var textAltitude: TextView

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
        textPressure = view.findViewById(R.id.textPressure)
        textAltitude = view.findViewById(R.id.textAltitude)

        setupSensors()

        viewModel.sensorUiState.observe(viewLifecycleOwner) { state ->
            val az = Azimuth(state.azimuth)
            compassView.updateAzimuth(state.azimuth)
            textAzimuth.text = "Direction: ${az.cardinalDirection} (${state.azimuth.roundToInt()}°)"
            textSteps.text = "Pas: ${state.stepCount}"
            textAcceleration.text = "Accélération: ${"%.2f".format(state.accelerationMagnitude)} m/s²"
            textTemp.text = "Température ambiante: ${state.temperature?.toString() ?: "--"} °C"
            textClock.text = "Heure: ${state.clock}"
            textPressure.text = "Pression: ${state.ambientPressure?.let { "%.2f hPa".format(it) } ?: "--"}"
            textAltitude.text = "Altitude: ${state.estimatedAltitude?.let { "%.1f m".format(it) } ?: "--"}"
        }
    }

    private fun setupSensors() {
        compassManager = CompassManager(requireContext()) { azimuth ->
            compassView.updateAzimuth(azimuth)
            viewModel.updateAzimuth(azimuth)
        }

        stepCounterManager = StepCounterManager(requireContext()) {
            viewModel.updateStepCount(it)
        }

        linearAccelerationManager = LinearAccelerationManager(requireContext()) { _, _, _, magnitude ->
            viewModel.updateAcceleration(magnitude)
        }

        ambientTemperatureManager = AmbientTemperatureManager(requireContext()) {
            viewModel.updateTemperature(it)
        }

        ambientPressureManager = AmbientPressureManager(requireContext()) { pressure, altitude ->
            viewModel.updateAmbientPressure(pressure)
            viewModel.updateEstimatedAltitude(altitude)
        }

        clockManager = ClockManager {
            viewModel.updateClock(it)
        }
    }

    override fun onResume() {
        super.onResume()
        compassManager.start()
        linearAccelerationManager.start()
        ambientTemperatureManager.start()
        ambientPressureManager.start()
        stepCounterManager.start()
        clockManager.start()
    }

    override fun onPause() {
        super.onPause()
        compassManager.stop()
        stepCounterManager.stop()
        linearAccelerationManager.stop()
        ambientTemperatureManager.stop()
        clockManager.stop()
        ambientPressureManager.stop()
    }
}
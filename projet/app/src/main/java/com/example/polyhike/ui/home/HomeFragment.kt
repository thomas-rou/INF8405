package com.example.polyhike.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.BATTERY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import com.example.polyhike.R
import com.example.polyhike.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var batteryPercentage: MutableLiveData<String> = MutableLiveData()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val homeViewModel =
            ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        batteryPercentage.observe(viewLifecycleOwner) {
            textView.text = it
        }

        loadBatteryInfo()
        return root
    }

    private fun loadBatteryInfo() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_POWER_CONNECTED)
        intentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED)
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED)
        requireActivity().registerReceiver(batteryInfoReceiver, intentFilter)
    }

    private val batteryInfoReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            updateBatteryData(intent)
        }
    }

    private fun updateBatteryData(intent: Intent) {
        var present: Boolean = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false)
        if (present) {
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            if (level != -1 && scale != -1) {
                batteryPercentage.apply {
                    value = "Battery Percentage : ${((level / scale.toFloat()) * 100f).toInt()} %"
                }
            }

            val batteryManager = requireActivity().getSystemService(BATTERY_SERVICE) as BatteryManager?
            val energy: Long? = batteryManager?.getLongProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER)
//            if (energy != null)
//                batteryPercentage = "Battery Energy : ${(energy)} Wh"

        } else {
            Toast.makeText(requireActivity(), "No Battery present", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
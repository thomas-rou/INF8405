package com.example.polyhike.ui.home

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.BATTERY_SERVICE
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.IntentFilter
import android.net.TrafficStats
import android.os.BatteryManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import com.example.polyhike.databinding.FragmentHomeBinding
import android.os.Handler

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val mHandler: Handler = Handler()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        mHandler.postDelayed(mRunnable, 1000)
        loadBatteryInfo()
        return root
    }

    override fun onResume() {
        super.onResume()
        mHandler.post(mRunnable)
        loadBatteryInfo()
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
            getBatteryData(intent)
        }
    }

    private fun getBatteryData(intent: Intent) {
        var present: Boolean = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false)
        if (present) {
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val batteryPct = ((level / scale.toFloat()) * 100f).toInt()
            val voltage: Int = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0) //mV
            val capacity: Long = getBatteryCapacity() //micro Ah
            val batteryEnergy: Int = (voltage * capacity / 1000000).toInt() //mWh

            setNewBatteryUsage(batteryPct, batteryEnergy)
        } else {
            Toast.makeText(requireActivity(), "No Battery present", Toast.LENGTH_SHORT).show()
        }
    }

    fun getBatteryCapacity(): Long {
        val mBatteryManager = requireContext().getSystemService(BATTERY_SERVICE) as BatteryManager
        val chargeCounter = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CHARGE_COUNTER)
        val capacity = mBatteryManager.getLongProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val value = ((chargeCounter.toFloat() / capacity.toFloat()) * 100f).toLong()
        return value
    }

    private fun setNewBatteryUsage(batteryPercentage: Int, batteryEnergy: Int) {
        val batteryPercentageUsed: TextView = binding.batteryPct
        val batteryEnergyUsed: TextView = binding.batteryEnergy
        val sharedPref = requireActivity().getSharedPreferences("session", MODE_PRIVATE)
        val batteryInitialPct = sharedPref.getInt("batteryInitialPct", -1)
        val batteryInitialEnergy = sharedPref.getInt("batteryInitialEnergy", -1)
        if (batteryInitialPct == -1) {
            sharedPref.edit() { putInt("batteryInitialPct", batteryPercentage) }
            sharedPref.edit() { putInt("batteryInitialEnergy", batteryEnergy) }
            batteryPercentageUsed.text = "Pourcentage utilisé : 0%"
            batteryEnergyUsed.text = "Energie : 0 mWh"
        } else {
            batteryPercentageUsed.text = "Pourcentage utilisé : ${batteryInitialPct - batteryPercentage}%"

            batteryEnergyUsed.text = "Energie : ${batteryEnergy} mWh"
        }
    }

    private val mRunnable: Runnable = object : Runnable {
        override fun run() {
            val downlink: TextView = binding.downlink
            val uplink: TextView = binding.uplink
            val rxBytes: Long = TrafficStats.getTotalRxBytes()
            if (rxBytes >= 0) downlink.text = "Downlink : ${rxBytes} o/s"
            val txBytes: Long = TrafficStats.getTotalTxBytes()
            if (txBytes >= 0) uplink.text = "Uplink : ${txBytes} o/s"
            mHandler.postDelayed(mRunnable, 1000)
        }
    }

    override fun onPause() {
        super.onPause()
        mHandler.removeCallbacks(mRunnable)
        requireContext().unregisterReceiver(batteryInfoReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
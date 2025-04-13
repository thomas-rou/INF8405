package com.example.polyhike.ui.profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.polyhike.databinding.FragmentProfileBinding
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var profileViewModel: ProfileViewModel

    private lateinit var barChart: BarChart

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val textViewName: TextView = binding.tvNameAge
        val textViewAddress: TextView = binding.tvAddress
        val imageView: ImageView = binding.userImage
        val textViewSteps: TextView = binding.steps
        val textViewDistance: TextView = binding.distance

        profileViewModel.userProfile.observe(viewLifecycleOwner) {
            textViewName.text = buildString {
                append(it?.name)
                append(", ")
                append(profileViewModel.getAgeFromDateOfBirth(it?.dateOfBirth))
                append(" ans")
            }
            textViewAddress.text = "Montreal, Canada"  // TODO: set address dynamically
            if (it?.photoURI?.isNotEmpty() == true) {
                Glide.with(requireContext())
                    .load(it.photoURI.toUri())
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageView)
            }
        }
        profileViewModel.totalSteps.observe(viewLifecycleOwner) {
            if (it != null) textViewSteps.text = it.toString()
            else textViewSteps.text = "0"
        }
        profileViewModel.totalDistance.observe(viewLifecycleOwner) {
            if (it != null) textViewDistance.text = (it / 1000).toString()
            else textViewDistance.text = "0"
        }
        val sharedPref = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("userId", -1)
        profileViewModel.getUserProfile(userId)
        profileViewModel.getTotalStepsByUserId(userId)
        profileViewModel.getTotalDistanceByUserId(userId)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val days = arrayOf("Lu", "Ma", "Me", "Je", "Ve", "Sa", "Di")
        barChart = binding.barChart
        barChart.setFitBars(true)
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisRight.isEnabled = false
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.setDrawGridLines(false)
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(days)

        profileViewModel.barData.observe(viewLifecycleOwner, Observer { barData ->
            barChart.data = barData
            barChart.invalidate()
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
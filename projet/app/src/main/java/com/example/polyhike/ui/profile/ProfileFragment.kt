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
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.polyhike.NavManagerActivity

class ProfileFragment : Fragment() {
    private var userId = -1
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var profileViewModel: ProfileViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        userId  = (activity as? NavManagerActivity)?.userId?:-1
        val root: View = binding.root
        val textViewName: TextView = binding.tvName
        val textViewAge: TextView = binding.tvAge
        val textViewAddress: TextView = binding.tvAddress
        val imageView: ImageView = binding.userImage
        val textViewSteps: TextView = binding.steps
        val textViewDistance: TextView = binding.distance

        profileViewModel.userProfile.observe(viewLifecycleOwner) {
            textViewName.text = it?.name
            textViewAge.text = "${profileViewModel.getAgeFromDateOfBirth(it?.dateOfBirth)} ans"
            textViewAddress.text = "Montreal, Canada"
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
        profileViewModel.getUserProfile(userId)
        profileViewModel.getTotalStepsByUserId(userId)
        profileViewModel.getTotalDistanceByUserId(userId)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
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

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this)[ProfileViewModel::class.java]

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textViewName: TextView = binding.tvName
        val textViewAddress: TextView = binding.tvAddress
        val textViewAge: TextView = binding.tvAge
        val imageView: ImageView = binding.userImage

        profileViewModel.userProfile.observe(viewLifecycleOwner) {
            textViewName.text = it?.name
            textViewAddress.text = "Montreal, Canada"  // TODO: set address dynamically
            textViewAge.text = "${profileViewModel.getAgeFromDateOfBirth(it?.dateOfBirth)} ans"
            val uri = it?.photoURI?.toUri()
            if (uri != null) {
                Glide.with(requireContext())
                    .load(uri)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageView)
            }
        }

        val sharedPref = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("userId", -1)

        profileViewModel.getUserProfile(userId)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
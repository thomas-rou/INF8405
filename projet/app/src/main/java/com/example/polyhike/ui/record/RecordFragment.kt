package com.example.polyhike.ui.record

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.polyhike.MapActivity
import com.example.polyhike.databinding.FragmentRecordBinding
import com.example.polyhike.model.HikeInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine



class RecordFragment : Fragment() {

    private var _binding: FragmentRecordBinding? = null
    private lateinit var hikeInfoViewModel: HikeInfoViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private fun recordHike() {
        val intent = Intent(requireContext(), MapActivity::class.java)
        startActivity(intent)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val recordViewModel =
            ViewModelProvider(this)[RecordViewModel::class.java]

        hikeInfoViewModel = ViewModelProvider(this)[HikeInfoViewModel::class.java]

        _binding = FragmentRecordBinding.inflate(inflater, container, false)
        binding.hikeRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val root: View = binding.root
        binding.recordHikeButton.setOnClickListener {
            recordHike()
        }

        lifecycleScope.launch(Dispatchers.Main) {
            val hikes = getAllHikeInfo()
            val adapter = HikeAdapter(hikes,
                onItemClick = { hikeItem ->
                    val intent = Intent(requireContext(), MapActivity::class.java)
                    intent.putExtra("HIKE_ID", hikeItem.id)
                    intent.putExtra("HISTORY_MODE", true)
                    startActivity(intent)
                })
            binding.hikeRecyclerView.adapter = adapter
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun getAllHikeInfo(): List<HikeInfo> {
        return suspendCancellableCoroutine { continuation ->
            val allHikeInfo = hikeInfoViewModel.getUserHikes()
            lateinit var observer: Observer<List<HikeInfo>>
            observer = Observer { gotHikes ->
                allHikeInfo.removeObserver(observer)
                if (continuation.isActive) {
                    continuation.resume(gotHikes, onCancellation = null)
                }
            }
            allHikeInfo.observeForever(observer)
            continuation.invokeOnCancellation {
                allHikeInfo.removeObserver(observer)
            }
        }
    }

}
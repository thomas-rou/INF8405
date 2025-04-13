package com.example.polyhike.ui.record

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.polyhike.MainActivity
import com.example.polyhike.MapActivity
import com.example.polyhike.NavManagerActivity
import com.example.polyhike.R
import com.example.polyhike.model.HikeInfo

class HikeAdapter(private val hikes: List<HikeInfo>, private val onItemClick: (HikeInfo) -> Unit,) : RecyclerView.Adapter<HikeAdapter.HikeViewHolder>() {

    private lateinit var hikeInfoViewModel: HikeInfoViewModel

    inner class HikeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hikeDate: TextView = itemView.findViewById(R.id.hike_item_date)
        val goto_icon: View = itemView.findViewById(R.id.goto_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HikeViewHolder {
        hikeInfoViewModel = ViewModelProvider(parent.context as NavManagerActivity)[HikeInfoViewModel::class.java]
        val view = LayoutInflater.from(parent.context).inflate(R.layout.hike_item, parent, false)
        return HikeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HikeViewHolder, position: Int) {
        val hike = hikes[position]
        holder.hikeDate.text = "Hike du : ${ hike.startDate.toString() }"
        holder.goto_icon.setOnClickListener {
            onItemClick(hike)
        }
    }

    override fun getItemCount(): Int = hikes.size
}

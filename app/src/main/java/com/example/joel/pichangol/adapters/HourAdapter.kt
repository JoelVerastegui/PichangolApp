package com.example.joel.pichangol.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.joel.pichangol.R
import com.example.joel.pichangol.models.Hour
import com.example.joel.pichangol.viewholders.HourViewHolder

class HourAdapter (var hours : List<Hour>) : RecyclerView.Adapter<HourViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hour, parent, false)
        return HourViewHolder(view)
    }

    override fun getItemCount(): Int {
        return hours.size
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        holder.loadHour(hours[position])
    }

}
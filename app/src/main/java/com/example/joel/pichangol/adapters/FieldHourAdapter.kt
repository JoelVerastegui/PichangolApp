package com.example.joel.pichangol.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.joel.pichangol.R
import com.example.joel.pichangol.viewholders.FieldHourViewHolder

class FieldHourAdapter (var hours : List<String>) : RecyclerView.Adapter<FieldHourViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldHourViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_field_hours, parent, false)
        return FieldHourViewHolder(view)
    }

    override fun getItemCount(): Int {
        return hours.size
    }

    override fun onBindViewHolder(holder: FieldHourViewHolder, position: Int) {
        holder.loadFieldHour(hours[position])
    }

}
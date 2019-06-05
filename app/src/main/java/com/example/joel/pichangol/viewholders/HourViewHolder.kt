package com.example.joel.pichangol.viewholders

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.joel.pichangol.models.Hour
import kotlinx.android.synthetic.main.item_hour.view.*

class HourViewHolder(view : View) : RecyclerView.ViewHolder (view) {
    fun loadHour(hour : Hour){
        itemView.lblHour.text = hour.hour

        when(hour.availability){
            0 -> itemView.lblAvailability.setBackgroundColor(Color.parseColor("#F56F6F"))
            1 -> itemView.lblAvailability.setBackgroundColor(Color.parseColor("#FFFFFF"))
            2 -> itemView.lblAvailability.setBackgroundColor(Color.parseColor("#90D65E"))
        }
    }
}
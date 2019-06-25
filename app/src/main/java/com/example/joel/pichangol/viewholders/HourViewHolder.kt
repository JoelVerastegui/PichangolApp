package com.example.joel.pichangol.viewholders

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.joel.pichangol.models.Hour
import kotlinx.android.synthetic.main.item_hours.view.*

class HourViewHolder(view : View) : RecyclerView.ViewHolder (view) {
    fun loadHour(hour : Hour){
        itemView.lblHour.text = hour.hour

        when(hour.availability){
            0 -> itemView.lblHour.setBackgroundColor(Color.parseColor("#F56F6F"))
            1 -> itemView.lblHour.setBackgroundColor(Color.parseColor("#DBD6D6"))
            2 -> itemView.lblHour.setBackgroundColor(Color.parseColor("#90D65E"))
        }
    }
}
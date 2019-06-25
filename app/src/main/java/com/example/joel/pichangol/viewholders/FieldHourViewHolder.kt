package com.example.joel.pichangol.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.synthetic.main.item_field_hours.view.*

class FieldHourViewHolder(view : View) : RecyclerView.ViewHolder (view) {
    fun loadFieldHour(hour : String){
        itemView.lblHour.text = hour
    }
}
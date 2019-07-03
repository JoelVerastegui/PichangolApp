package com.example.joel.pichangol.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.joel.pichangol.models.Date
import kotlinx.android.synthetic.main.item_day.view.*

class DateViewHolder(view : View) : RecyclerView.ViewHolder (view) {
    fun loadDate(date : Date){
        itemView.lblDay.text = date.day
        itemView.txtBirthDate.text = date.date
    }
}
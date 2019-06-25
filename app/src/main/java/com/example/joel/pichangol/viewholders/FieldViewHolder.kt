package com.example.joel.pichangol.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.joel.pichangol.models.Field
import kotlinx.android.synthetic.main.item_fields.view.*

class FieldViewHolder(view : View) : RecyclerView.ViewHolder (view) {
    var recyclerView = itemView.rvHours

    fun loadField(field : Field){
        itemView.lblDescription.text = field.description
    }
}
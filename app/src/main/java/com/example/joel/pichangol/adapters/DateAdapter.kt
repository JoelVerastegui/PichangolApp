package com.example.joel.pichangol.adapters

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.joel.pichangol.R
import com.example.joel.pichangol.activities.SoccerFieldActivity
import com.example.joel.pichangol.models.Date
import com.example.joel.pichangol.viewholders.DateViewHolder
import kotlinx.android.synthetic.main.activity_soccer_field.*
import kotlinx.android.synthetic.main.item_day.view.*

class DateAdapter (var dates : List<Date>, var soccerFieldActivity: SoccerFieldActivity) : RecyclerView.Adapter<DateViewHolder>() {

    var rowPosition = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_day, parent, false)
        return DateViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dates.size
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.loadDate(dates[position])

        val itemView = holder.itemView

        itemView.setOnClickListener {
            rowPosition = position

            Log.d("TEST","$rowPosition")

            soccerFieldActivity.imgLoading2.visibility = View.VISIBLE
            soccerFieldActivity.soccerFieldGetRequest(soccerFieldActivity.displayedDates[rowPosition])

            // Refresh RecyclerView
            notifyDataSetChanged()
        }

        if(rowPosition == position){
            itemView.lblDay.setTextColor(Color.parseColor("#FFFF8800"))
            itemView.txtBirthDate.setTextColor(Color.parseColor("#FFFF8800"))
            itemView.lblLine.visibility = View.VISIBLE
        } else{
            itemView.lblDay.setTextColor(Color.parseColor("#FF747474"))
            itemView.txtBirthDate.setTextColor(Color.parseColor("#FF747474"))
            itemView.lblLine.visibility = View.GONE
        }
    }

}
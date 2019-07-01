package com.example.joel.pichangol.adapters

import android.content.Intent
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.joel.pichangol.R
import com.example.joel.pichangol.Server
import com.example.joel.pichangol.activities.HourActivity
import com.example.joel.pichangol.models.Field
import com.example.joel.pichangol.viewholders.FieldViewHolder

class FieldAdapter (var fields : List<Field>) : RecyclerView.Adapter<FieldViewHolder> () {

    val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_fields, parent, false)
        return FieldViewHolder(view)
    }

    override fun getItemCount(): Int {
        return fields.size
    }

    override fun onBindViewHolder(holder: FieldViewHolder, position: Int) {
        holder.loadField(fields[position])

        var hoursGrid = GridLayoutManager(holder.recyclerView.context,5)
        var hours = fields[position].hours

        // Show FieldHoursGridView
        holder.recyclerView.apply {
            layoutManager = hoursGrid
            adapter = FieldHourAdapter(hours, position)
            recycledViewPool = viewPool
        }

        holder.itemView.setOnClickListener {
            val context = it.context
            var hourActivity = Intent(context, HourActivity::class.java)
            hourActivity.putExtra("soccerFieldId",Server.instance.localS!!.soccerFields[position].id)
            context.startActivity(hourActivity)
        }
    }

}
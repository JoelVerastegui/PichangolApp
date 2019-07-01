package com.example.joel.pichangol.adapters

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.joel.pichangol.R
import com.example.joel.pichangol.activities.HourActivity
import com.example.joel.pichangol.models.Hour
import com.example.joel.pichangol.viewholders.HourViewHolder
import kotlinx.android.synthetic.main.activity_hour.*
import kotlinx.android.synthetic.main.activity_hour.view.*
import kotlinx.android.synthetic.main.item_hours.view.*

class HourAdapter (var hours : List<Hour>, var hourActivity: HourActivity) : RecyclerView.Adapter<HourViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hours, parent, false)
        return HourViewHolder(view)
    }

    override fun getItemCount(): Int {
        return hours.size
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        holder.loadHour(hours[position])

        holder.itemView.setOnClickListener {
            when(hours[position].availability){
                0 -> Toast.makeText(it.context,"Ocupada",Toast.LENGTH_SHORT).show()
                1 -> {
                    if(hours.find { x -> x.availability == 2 } != null){
                        if(position == 0){
                            if(hours[position+1].availability == 2){
                                holder.itemView.lblHour.setBackgroundColor(Color.parseColor("#90D65E"))
                                hours[position].availability = 2
                            } else{
                                Toast.makeText(it.context,"Debe seleccionar horas seguidas por reserva",Toast.LENGTH_SHORT).show()
                            }
                        } else if(position == hours.size-1){
                            if(hours[position-1].availability == 2){
                                holder.itemView.lblHour.setBackgroundColor(Color.parseColor("#90D65E"))
                                hours[position].availability = 2
                            } else{
                                Toast.makeText(it.context,"Debe seleccionar horas seguidas por reserva",Toast.LENGTH_SHORT).show()
                            }
                        } else if(hours[position-1].availability == 2 || hours[position+1].availability == 2){
                            holder.itemView.lblHour.setBackgroundColor(Color.parseColor("#90D65E"))
                            hours[position].availability = 2
                        } else{
                            Toast.makeText(it.context,"Debe seleccionar horas seguidas por reserva",Toast.LENGTH_SHORT).show()
                        }
                    } else{
                        holder.itemView.lblHour.setBackgroundColor(Color.parseColor("#90D65E"))
                        hours[position].availability = 2
                    }
                }
                2 -> {
                    if(position != 0 && position != hours.size-1){
                        if(hours[position-1].availability == 2 && hours[position+1].availability == 2){
                            Toast.makeText(it.context,"Borre las horas seleccionadas al inicio o fin del rango seleccionado",Toast.LENGTH_LONG).show()
                        } else{
                            holder.itemView.lblHour.setBackgroundColor(Color.parseColor("#DBD6D6"))
                            hours[position].availability = 1
                        }
                    } else{
                        holder.itemView.lblHour.setBackgroundColor(Color.parseColor("#DBD6D6"))
                        hours[position].availability = 1
                    }
                }
            }

            if(hours.find { x -> x.availability == 2 } != null){
                var selected = hours.filter { x -> x.availability == 2 }
                if(selected.size == 1){
                    var hourInt = selected[0].hour.toInt()
                    if(hourInt < 9){
                        hourActivity.lblSelectedHours.text = "${selected[0].hour}:00 - 0${hourInt+1}:00"
                    } else if(hourInt == 9){
                        hourActivity.lblSelectedHours.text = "${selected[0].hour}:00 - 10:00"
                    } else{
                        hourActivity.lblSelectedHours.text = "${selected[0].hour}:00 - ${hourInt+1}:00"
                    }
                } else{
                    if(selected[selected.size-1].hour.toInt() == 9){
                        hourActivity.lblSelectedHours.text = "${selected[0].hour}:00 - 10:00"
                    } else{
                        hourActivity.lblSelectedHours.text = "${selected[0].hour}:00 - ${selected[selected.size-1].hour.toInt()+1}:00"
                    }

                }
                hourActivity.lblNonSelected.visibility = View.GONE
                ObjectAnimator.ofFloat(hourActivity.btnNext,"translationY", 0f).apply {
                    duration = 200
                    start()
                }
                ObjectAnimator.ofFloat(hourActivity.btnNextIcon,"translationY", 0f).apply {
                    duration = 200
                    start()
                }
            } else{
                hourActivity.lblNonSelected.visibility = View.VISIBLE
                ObjectAnimator.ofFloat(hourActivity.btnNext,"translationY", 160f).apply {
                    duration = 200
                    start()
                }
                ObjectAnimator.ofFloat(hourActivity.btnNextIcon,"translationY", 160f).apply {
                    duration = 200
                    start()
                }
            }
        }

    }

    public fun getArrayList() : List<Hour>{
        return hours
    }

}
package com.example.joel.pichangol.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.joel.pichangol.models.Reservation
import kotlinx.android.synthetic.main.item_reservation.view.*

class ReservationViewHolder(view : View) : RecyclerView.ViewHolder (view) {

    fun loadReservation(reservation: Reservation){

        itemView.lblLocal.text = reservation.local
        itemView.lblSoccerField.text = reservation.soccerField
        itemView.lblDate.text = reservation.date
        itemView.lblTotal.text = "S/. ${reservation.total}"

        val startText = "0${reservation.start}"
        val start = startText.substring(startText.length-2)
        val endText = "0${reservation.end}"
        val end = endText.substring(endText.length-2)

        val hours = reservation.end-reservation.start

        itemView.lblHours.text = "$start:00 - $end:00"
    }
}
package com.example.joel.pichangol.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.joel.pichangol.R
import com.example.joel.pichangol.models.Reservation
import com.example.joel.pichangol.viewholders.ReservationViewHolder

class ReservationAdapter (var reservations : List<Reservation>) : RecyclerView.Adapter<ReservationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reservation, parent, false)
        return ReservationViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reservations.size
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        holder.loadReservation(reservations[position])
    }

}
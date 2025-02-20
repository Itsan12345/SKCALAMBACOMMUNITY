package com.example.skcamotes.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skcamotes.R
import com.example.skcamotes.ReservationDataClass

class CompletedReservationsAdapter(private val reservationsList: List<ReservationDataClass>) :
    RecyclerView.Adapter<CompletedReservationsAdapter.ReservationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.reservation_item, parent, false)
        return ReservationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val reservation = reservationsList[position]
        holder.userName.text = reservation.user_name
        holder.userEmail.text = reservation.user_email
        holder.placeReserved.text = reservation.place_reserved
        holder.reservationDate.text = reservation.date
        holder.numberOfPersons.text = "Persons: ${reservation.number_of_persons}"
        holder.paymentMethod.text = "Payment: ${reservation.payment_method}"
        holder.totalPrice.text = "Total: ${reservation.total_price}"
    }

    override fun getItemCount(): Int {
        return reservationsList.size
    }

    class ReservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.user_name)
        val userEmail: TextView = itemView.findViewById(R.id.user_email)
        val placeReserved: TextView = itemView.findViewById(R.id.place_reserved)
        val reservationDate: TextView = itemView.findViewById(R.id.reservation_date)
        val numberOfPersons: TextView = itemView.findViewById(R.id.number_of_persons)
        val paymentMethod: TextView = itemView.findViewById(R.id.payment_method)
        val totalPrice: TextView = itemView.findViewById(R.id.total_price)
    }
}

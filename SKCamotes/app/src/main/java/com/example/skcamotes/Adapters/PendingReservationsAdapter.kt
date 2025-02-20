package com.example.skcamotes.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.skcamotes.R
import com.example.skcamotes.ReservationDataClass

class PendingReservationsAdapter(private val reservationsList: List<ReservationDataClass>) :
    RecyclerView.Adapter<PendingReservationsAdapter.ReservationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.reservation_item, parent, false)
        return ReservationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val reservation = reservationsList[position]

        // Display only if user name is not empty
        if (reservation.user_name.isNotEmpty()) {
            holder.userName.text = reservation.user_name
            holder.userName.visibility = View.VISIBLE
        } else {
            holder.userName.visibility = View.GONE
        }

        // Display only if user email is not empty
        if (reservation.user_email.isNotEmpty()) {
            holder.userEmail.text = reservation.user_email
            holder.userEmail.visibility = View.VISIBLE
        } else {
            holder.userEmail.visibility = View.GONE
        }

        // Display only if place reserved is not empty
        if (reservation.place_reserved.isNotEmpty()) {
            holder.placeReserved.text = reservation.place_reserved
            holder.placeReserved.visibility = View.VISIBLE
        } else {
            holder.placeReserved.visibility = View.GONE
        }

        // Display only if reservation date is not empty
        if (reservation.date.isNotEmpty()) {
            holder.reservationDate.text = reservation.date
            holder.reservationDate.visibility = View.VISIBLE
        } else {
            holder.reservationDate.visibility = View.GONE
        }

        // Display number of persons only if greater than 0
        if (reservation.number_of_persons > 0) {
            holder.numberOfPersons.text = "Persons: ${reservation.number_of_persons}"
            holder.numberOfPersons.visibility = View.VISIBLE
        } else {
            holder.numberOfPersons.visibility = View.GONE
        }

        // Display only if payment method is not empty
        if (reservation.payment_method.isNotEmpty()) {
            holder.paymentMethod.text = "Payment: ${reservation.payment_method}"
            holder.paymentMethod.visibility = View.VISIBLE
        } else {
            holder.paymentMethod.visibility = View.GONE
        }

        // Display only if total price is not empty
        if (reservation.total_price.isNotEmpty()) {
            holder.totalPrice.text = "Total: ${reservation.total_price}"
            holder.totalPrice.visibility = View.VISIBLE
        } else {
            holder.totalPrice.visibility = View.GONE
        }
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


package com.example.skcamotes.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.skcamotes.R
import com.example.skcamotes.ReservationDataClass
import com.google.firebase.database.FirebaseDatabase

class UpcomingReservationsAdapter(private val reservationsList: List<ReservationDataClass>) :
    RecyclerView.Adapter<UpcomingReservationsAdapter.ReservationViewHolder>() {

    private var expandedPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.upcoming_reservation_item, parent, false)
        return ReservationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val reservation = reservationsList[position]

        holder.userName.text = reservation.user_name
        holder.placeReserved.text = reservation.place_reserved

        if (expandedPosition == position) {
            holder.buttonContainer.visibility = View.VISIBLE
        } else {
            holder.buttonContainer.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            expandedPosition = if (expandedPosition == position) -1 else position
            notifyDataSetChanged()
        }

        // Handle the "Paid" button click
        holder.btnPaid.setOnClickListener {
            val databasePaid = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("paidreservations")

            val reservationKey = reservation.key
            val databaseReceipt = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("reservationreceipt")

            if (reservationKey != null) {
                // Push the current reservation to the "paidreservations" path
                databasePaid.push().setValue(reservation)
                    .addOnSuccessListener {
                        // Remove the reservation from "reservationreceipt"
                        databaseReceipt.child(reservationKey).removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(holder.itemView.context, "Reservation moved to Paid successfully!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { error ->
                                Toast.makeText(holder.itemView.context, "Failed to remove reservation: ${error.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(holder.itemView.context, "Failed to move reservation to Paid: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(holder.itemView.context, "Reservation key is missing!", Toast.LENGTH_SHORT).show()
            }
        }


        // Populate fields (e.g., userName, userEmail, etc.) as in the original code
        holder.userName.text = reservation.user_name
        holder.placeReserved.text = reservation.place_reserved

        // Manage button visibility
        if (expandedPosition == position) {
            holder.buttonContainer.visibility = View.VISIBLE
        } else {
            holder.buttonContainer.visibility = View.GONE
        }

        // Handle item click to show/hide buttons
        holder.itemView.setOnClickListener {
            expandedPosition = if (expandedPosition == position) -1 else position
            notifyDataSetChanged()
        }


        holder.btnCancel.setOnClickListener {
            val databasePaid = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("cancelledreservations")

            val reservationKey = reservation.key
            val databaseReceipt = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("reservationreceipt")

            if (reservationKey != null) {
                // Push the current reservation to the "paidreservations" path
                databasePaid.push().setValue(reservation)
                    .addOnSuccessListener {
                        // Remove the reservation from "reservationreceipt"
                        databaseReceipt.child(reservationKey).removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(holder.itemView.context, "Reservation moved to Cancelled successfully!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { error ->
                                Toast.makeText(holder.itemView.context, "Failed to remove reservation: ${error.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(holder.itemView.context, "Failed to move reservation to Paid: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(holder.itemView.context, "Reservation key is missing!", Toast.LENGTH_SHORT).show()
            }
        }

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
        val buttonContainer: View = itemView.findViewById(R.id.buttonContainer)
        val btnPaid: Button = itemView.findViewById(R.id.btnPaid)
        val btnCancel: Button = itemView.findViewById(R.id.btnCancel)
    }
}


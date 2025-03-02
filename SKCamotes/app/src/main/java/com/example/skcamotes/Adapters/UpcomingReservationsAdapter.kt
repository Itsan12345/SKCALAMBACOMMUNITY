package com.example.skcamotes.Adapters

import android.content.Context
import android.content.Intent
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
        holder.userEmail.text = reservation.user_email
        holder.placeReserved.text = reservation.place_reserved

        holder.buttonContainer.visibility = if (expandedPosition == position) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            expandedPosition = if (expandedPosition == position) -1 else position
            notifyDataSetChanged()
        }

        // Handle "Paid" button click
        holder.btnPaid.setOnClickListener {
            val databasePaid = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("paidreservations")

            val reservationKey = reservation.key
            val databaseReceipt = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("reservationreceipt")

            if (reservationKey != null) {
                databasePaid.push().setValue(reservation)
                    .addOnSuccessListener {
                        databaseReceipt.child(reservationKey).removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(holder.itemView.context, "Reservation moved to Paid successfully!", Toast.LENGTH_SHORT).show()
                                sendEmailNotification(holder.itemView.context, reservation.user_email, reservation.user_name, reservation.place_reserved)
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

        // Handle "Cancel" button click
        holder.btnCancel.setOnClickListener {
            val databaseCancelled = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("cancelledreservations")

            val reservationKey = reservation.key
            val databaseReceipt = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app")
                .getReference("reservationreceipt")

            if (reservationKey != null) {
                databaseCancelled.push().setValue(reservation)
                    .addOnSuccessListener {
                        databaseReceipt.child(reservationKey).removeValue()
                            .addOnSuccessListener {
                                Toast.makeText(holder.itemView.context, "Reservation moved to Cancelled successfully!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { error ->
                                Toast.makeText(holder.itemView.context, "Failed to remove reservation: ${error.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(holder.itemView.context, "Failed to move reservation to Cancelled: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(holder.itemView.context, "Reservation key is missing!", Toast.LENGTH_SHORT).show()
            }
        }

        // Conditional UI Visibility
        holder.userName.apply {
            text = reservation.user_name
            visibility = if (reservation.user_name.isNotEmpty()) View.VISIBLE else View.GONE
        }

        holder.userEmail.apply {
            text = reservation.user_email
            visibility = if (reservation.user_email.isNotEmpty()) View.VISIBLE else View.GONE
        }

        holder.placeReserved.apply {
            text = reservation.place_reserved
            visibility = if (reservation.place_reserved.isNotEmpty()) View.VISIBLE else View.GONE
        }

        holder.reservationDate.apply {
            text = reservation.date
            visibility = if (reservation.date.isNotEmpty()) View.VISIBLE else View.GONE
        }

        holder.numberOfPersons.apply {
            text = "Persons: ${reservation.number_of_persons}"
            visibility = if (reservation.number_of_persons > 0) View.VISIBLE else View.GONE
        }

        holder.paymentMethod.apply {
            text = "Payment: ${reservation.payment_method}"
            visibility = if (reservation.payment_method.isNotEmpty()) View.VISIBLE else View.GONE
        }

        holder.totalPrice.apply {
            text = "Total: ${reservation.total_price}"
            visibility = if (reservation.total_price.isNotEmpty()) View.VISIBLE else View.GONE
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

    private fun sendEmailNotification(context: Context, userEmail: String, userName: String, placeReserved: String) {
        val subject = "Your Reservation Has Been Paid"
        val message = "Hello $userName,\n\nYour reservation for '$placeReserved' has been successfully paid. Thank you for using our service!\n\nBest Regards."

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(userEmail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }

        try {
            context.startActivity(Intent.createChooser(intent, "Send Email"))
        } catch (e: Exception) {
            Toast.makeText(context, "No email app found. Please install an email client.", Toast.LENGTH_SHORT).show()
        }
    }
}

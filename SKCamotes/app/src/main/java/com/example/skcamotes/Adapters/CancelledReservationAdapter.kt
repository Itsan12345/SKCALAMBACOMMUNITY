package com.example.skcamotes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CancelledReservationAdapter(private val reservationList: List<CancelledReservationDataClass>) :
    RecyclerView.Adapter<CancelledReservationAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val placeReserved: TextView = itemView.findViewById(R.id.txtPlaceReserved)
        val date: TextView = itemView.findViewById(R.id.txtDate)
        val persons: TextView = itemView.findViewById(R.id.txtPersons)
        val paymentMethod: TextView = itemView.findViewById(R.id.txtPaymentMethod)
        val totalPrice: TextView = itemView.findViewById(R.id.txtTotalPrice)
        val imgReserved: ImageView = itemView.findViewById(R.id.imgReserved)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.myreservation_cancelled_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reservation = reservationList[position]
        holder.placeReserved.text = reservation.place_reserved
        holder.date.text = "Date: ${reservation.date}"
        holder.persons.text = "Persons: ${reservation.number_of_persons}"
        holder.paymentMethod.text = "Payment: ${reservation.payment_method}"
        holder.totalPrice.text = "Total: ${reservation.total_price}"

        // Load Image Based on Place Reserved
        val imageUrl = when (reservation.place_reserved) {
            "Barangay Calamba Gym" -> "https://white-badger-937570.hostingersite.com/Assets/img_gymcourt.png"
            "Third Floor Facility" -> "https://white-badger-937570.hostingersite.com/Assets/img_thirdfloor.png"
            else -> null
        }

        // Use Glide to Load Image
        imageUrl?.let {
            Glide.with(holder.itemView.context)
                .load(it)
                .placeholder(R.drawable.default_image) // Optional: Add a placeholder image
                .error(R.drawable.default_image) // Optional: Add an error fallback image
                .into(holder.imgReserved)
        }
    }

    override fun getItemCount(): Int = reservationList.size
}

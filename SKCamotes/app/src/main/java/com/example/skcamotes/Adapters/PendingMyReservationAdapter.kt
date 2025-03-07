package com.example.skcamotes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class PendingMyReservationAdapter(
    private val reservationList: MutableList<PendingMyReservationDataClass>,
    private val onItemClick: (PendingMyReservationDataClass) -> Unit
) : RecyclerView.Adapter<PendingMyReservationAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val placeReserved: TextView = itemView.findViewById(R.id.txtPlaceReserved)
        val date: TextView = itemView.findViewById(R.id.txtDate)
        val persons: TextView = itemView.findViewById(R.id.txtPersons)
        val paymentMethod: TextView = itemView.findViewById(R.id.txtPaymentMethod)
        val totalPrice: TextView = itemView.findViewById(R.id.txtTotalPrice)
        val imgReserved: ImageView = itemView.findViewById(R.id.imgReserved)
        val btnCancel: Button = itemView.findViewById(R.id.btn_cancel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.myreservation_pending_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val reservation = reservationList[position]
        holder.placeReserved.text = reservation.place_reserved
        holder.date.text = "Date: ${reservation.date}"
        holder.persons.text = "Persons: ${reservation.number_of_persons}"
        holder.paymentMethod.text = "Payment: ${reservation.payment_method}"
        holder.totalPrice.text = "Total: ${reservation.total_price}"

        // Initially hide the cancel button
        holder.btnCancel.visibility = View.GONE

        // Toggle visibility on item click
        holder.itemView.setOnClickListener {
            if (holder.btnCancel.visibility == View.GONE) {
                holder.btnCancel.visibility = View.VISIBLE
            } else {
                holder.btnCancel.visibility = View.GONE
            }
        }

        // Handle cancel button click
        holder.btnCancel.setOnClickListener {
            val bottomSheet = CancelBottomSheetDialog {
                cancelReservation(reservation, position, holder.itemView)
            }

            val activity = holder.itemView.context as? AppCompatActivity
            activity?.supportFragmentManager?.let {
                bottomSheet.show(it, "CancelBottomSheetDialog")
            }
        }


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

    private fun cancelReservation(reservation: PendingMyReservationDataClass, position: Int, view: View) {
        val database = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val reservationRef = database.getReference("reservationreceipt")
        val cancelledRef = database.getReference("cancelledreservations")

        val reservationKey = reservationRef.orderByChild("user_email").equalTo(reservation.user_email)

        reservationKey.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (reservationSnapshot in snapshot.children) {
                    val fetchedReservation = reservationSnapshot.getValue(PendingMyReservationDataClass::class.java)
                    if (fetchedReservation?.place_reserved == reservation.place_reserved &&
                        fetchedReservation.date == reservation.date &&
                        fetchedReservation.number_of_persons == reservation.number_of_persons &&
                        fetchedReservation.total_price == reservation.total_price &&
                        fetchedReservation.payment_method == reservation.payment_method) {

                        // Move data to cancelled reservations
                        cancelledRef.child(reservationSnapshot.key!!).setValue(fetchedReservation)
                            .addOnSuccessListener {
                                // Remove from current list
                                reservationSnapshot.ref.removeValue()
                                reservationList.removeAt(position)
                                notifyItemRemoved(position)
                                Toast.makeText(view.context, "Reservation cancelled successfully", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(view.context, "Failed to cancel reservation", Toast.LENGTH_SHORT).show()
                            }
                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(view.context, "Error fetching reservation", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

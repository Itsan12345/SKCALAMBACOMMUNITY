package com.example.skcamotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.skcamotes.Adapters.PendingReservationsAdapter
import com.example.skcamotes.Adapters.UpcomingReservationsAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UpcomingReservationsFragment : Fragment() {

    private lateinit var reservationsRecyclerView: RecyclerView
    private lateinit var upcomingReservationsAdapter: UpcomingReservationsAdapter
    private val reservationsList = mutableListOf<ReservationDataClass>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_upcoming_reservations, container, false)
        reservationsRecyclerView = view.findViewById(R.id.upcomingreservationsRecyclerView)
        reservationsRecyclerView.layoutManager = LinearLayoutManager(context)
        upcomingReservationsAdapter = UpcomingReservationsAdapter(reservationsList)
        reservationsRecyclerView.adapter = upcomingReservationsAdapter

        fetchReservationsFromFirebase()
        return view
    }

    private fun fetchReservationsFromFirebase() {
        val database = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("reservationreceipt")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reservationsList.clear()
                for (reservationSnapshot in snapshot.children) {
                    val reservation = reservationSnapshot.getValue(ReservationDataClass::class.java)
                    val key = reservationSnapshot.key // Get the key from Firebase

                    if (reservation != null && key != null && hasValidData(reservation)) {
                        reservation.key = key // Set the key in the reservation object
                        reservationsList.add(reservation)
                    }
                }
                upcomingReservationsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println("Failed to fetch reservations: ${error.message}")
            }
        })
    }


    private fun hasValidData(reservation: ReservationDataClass): Boolean {
        return reservation.user_name.isNotEmpty() ||
                reservation.user_email.isNotEmpty() ||
                reservation.place_reserved.isNotEmpty() ||
                reservation.date.isNotEmpty() ||
                reservation.number_of_persons > 0 ||
                reservation.payment_method.isNotEmpty() ||
                reservation.total_price.isNotEmpty()
    }
}

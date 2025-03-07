package com.example.skcamotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Reservations_PaidFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var reservationAdapter: PaidMyReservationAdapter
    private lateinit var reservationList: MutableList<PaidMyReservationDataClass>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var userEmail: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reservations__paid, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewPaidReservations)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        reservationList = mutableListOf()
        reservationAdapter = PaidMyReservationAdapter(reservationList)
        recyclerView.adapter = reservationAdapter

        auth = FirebaseAuth.getInstance()
        userEmail = auth.currentUser?.email  // Get the logged-in user's email

        fetchReservations()

        return view
    }

    private fun fetchReservations() {
        databaseReference = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("paidreservations")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reservationList.clear()

                for (reservationSnapshot in snapshot.children) {
                    val reservation = reservationSnapshot.getValue(PaidMyReservationDataClass::class.java)

                    // Ensure fetched reservation has valid data and belongs to the logged-in user
                    if (reservation != null &&
                        reservation.place_reserved.isNotBlank() &&
                        reservation.date.isNotBlank() &&
                        reservation.number_of_persons > 0 &&
                        reservation.payment_method.isNotBlank() &&
                        reservation.total_price.isNotBlank() &&
                        reservation.user_email == userEmail// Only show reservations of the logged-in user
                    ) {
                        reservationList.add(reservation)
                    }
                }
                reservationAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

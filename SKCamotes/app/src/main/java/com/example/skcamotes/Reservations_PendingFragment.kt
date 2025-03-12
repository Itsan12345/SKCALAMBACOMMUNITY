package com.example.skcamotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Reservations_PendingFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var reservationAdapter: PendingMyReservationAdapter
    private lateinit var reservationList: MutableList<PendingMyReservationDataClass>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private var userEmail: String? = null
    private lateinit var emptyStateLayout: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reservations__pending, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewReservations)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        reservationList = mutableListOf()
        reservationAdapter = PendingMyReservationAdapter(reservationList) {}
        recyclerView.adapter = reservationAdapter

        emptyStateLayout = view.findViewById(R.id.emptyStateLayout)

        auth = FirebaseAuth.getInstance()
        userEmail = auth.currentUser?.email  // Get the logged-in user's email

        fetchReservations()

        return view
    }

    private fun fetchReservations() {
        databaseReference = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .getReference("reservationreceipt")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                reservationList.clear()

                for (reservationSnapshot in snapshot.children) {
                    val reservation = reservationSnapshot.getValue(PendingMyReservationDataClass::class.java)

                    // Ensure fetched reservation has valid data and belongs to the logged-in user
                    if (reservation != null &&
                        reservation.place_reserved.isNotBlank() &&
                        reservation.date.isNotBlank() &&
                        reservation.number_of_persons > 0 &&
                        reservation.payment_method.isNotBlank() &&
                        reservation.total_price.isNotBlank() &&
                        reservation.user_email == userEmail &&  // Only show reservations of the logged-in user
                        reservation.payment_method == "Cash on Delivery (COD)" // Show only COD payments
                    ) {
                        reservationList.add(reservation)
                    }
                }

                // Show or hide empty state layout based on data availability
                if (reservationList.isEmpty()) {
                    emptyStateLayout.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                } else {
                    emptyStateLayout.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }

                reservationAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to fetch data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

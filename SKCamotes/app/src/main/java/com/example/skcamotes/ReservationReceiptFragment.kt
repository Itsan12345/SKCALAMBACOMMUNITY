package com.example.skcamotes

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ReservationReceiptFragment : Fragment() {
    private lateinit var placenameValue: TextView
    private lateinit var dateValue: TextView
    private lateinit var personValueText: TextView
    private lateinit var totalPriceValue: TextView
    private lateinit var homeButton: Button

    private lateinit var database: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reservation_receipt, container, false)

        // Initialize UI components
        placenameValue = view.findViewById(R.id.placename_value)
        dateValue = view.findViewById(R.id.date_value)
        personValueText = view.findViewById(R.id.person_value)
        totalPriceValue = view.findViewById(R.id.totalprice_value)
        homeButton = view.findViewById(R.id.home_button)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("reservationreceipt")

        // Retrieve the passed data from Bundle arguments
        val gymTitle = arguments?.getString("GYM_TITLE") ?: "Default Subtitle"
        val dateRange = arguments?.getString("DATE_RANGE") ?: "No Date Selected"
        val guestsCount = arguments?.getInt("GUESTS_COUNT") ?: 0
        val totalPrice = arguments?.getString("TOTAL_PRICE", "₱0")

        // Set data to UI components
        placenameValue.text = gymTitle
        dateValue.text = dateRange
        personValueText.text = "$guestsCount Guest(s)"
        totalPriceValue.text = totalPrice

        // Save reservation data to Firebase
        saveToFirebase(gymTitle, dateRange, guestsCount, totalPrice ?: "₱0")

        // Home button click listener to navigate back to MainActivity
        homeButton.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        return view
    }

    private fun saveToFirebase(place: String, date: String, persons: Int, total: String) {
        val reservationData = mapOf(
            "place_reserved" to place,
            "date" to date,
            "number_of_persons" to persons,
            "total_price" to total
        )

        // Push the data to Firebase with a unique key
        database.push().setValue(reservationData)
            .addOnSuccessListener {
                println("Reservation details saved successfully.")
            }
            .addOnFailureListener {
                println("Failed to save reservation details: ${it.message}")
            }
    }
}

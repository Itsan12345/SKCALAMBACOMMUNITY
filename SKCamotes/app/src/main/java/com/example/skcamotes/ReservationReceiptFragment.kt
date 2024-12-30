package com.example.skcamotes

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

class ReservationReceiptFragment : Fragment() {
    private lateinit var placenameValue: TextView
    private lateinit var dateValue: TextView
    private lateinit var personValueText: TextView
    private lateinit var totalPriceValue: TextView
    private lateinit var homeButton: Button // Declare the home button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_reservation_receipt, container, false)

        placenameValue = view.findViewById(R.id.placename_value)
        dateValue = view.findViewById(R.id.date_value)
        personValueText = view.findViewById(R.id.person_value)
        totalPriceValue = view.findViewById(R.id.totalprice_value)
        homeButton = view.findViewById(R.id.home_button) // Initialize the home button

        // Retrieve the passed total price from the arguments
        val totalPrice = arguments?.getString("TOTAL_PRICE", "₱0")
        totalPriceValue.text = totalPrice

        val guestsCount = arguments?.getInt("GUESTS_COUNT") ?: 0
        personValueText.text = "$guestsCount Guest(s)"

        val dateRange = arguments?.getString("DATE_RANGE")
        if (!dateRange.isNullOrEmpty()) {
            dateValue.text = dateRange
        }

        val gymTitle = arguments?.getString("GYM_TITLE") ?: "Default Subtitle"
        placenameValue.text = gymTitle

        // Set click listener for the home button
        homeButton.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent) // Launch MainActivity
            activity?.finish() // Optionally close the current activity hosting the fragment
        }

        return view
    }
}

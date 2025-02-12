package com.example.skcamotes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ReservationReceiptActivity : AppCompatActivity() {

    private lateinit var placenameValue: TextView
    private lateinit var dateValue: TextView
    private lateinit var personValueText: TextView
    private lateinit var totalPriceValue: TextView
    private lateinit var homeButton: Button

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_reservation_receipt) // Keeping the same layout

        // Initialize UI components
        placenameValue = findViewById(R.id.placename_value)
        dateValue = findViewById(R.id.date_value)
        personValueText = findViewById(R.id.person_value)
        totalPriceValue = findViewById(R.id.totalprice_value)
        homeButton = findViewById(R.id.home_button)
        val paymentMethodValue = findViewById<TextView>(R.id.payment_method_value)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("reservationreceipt")

        // Retrieve the passed data from Intent extras
        val gymTitle = intent.getStringExtra("GYM_TITLE") ?: "Default Subtitle"
        val dateRange = intent.getStringExtra("DATE_RANGE") ?: "No Date Selected"
        val guestsCount = intent.getIntExtra("GUESTS_COUNT", 0)
        val totalPrice = intent.getStringExtra("TOTAL_PRICE") ?: "₱0"
        val paymentMethod = intent.getStringExtra("PAYMENT_METHOD") ?: "No Payment Method Selected"

        // Set data to UI components
        placenameValue.text = gymTitle
        dateValue.text = dateRange
        personValueText.text = "$guestsCount Guest(s)"
        totalPriceValue.text = totalPrice
        paymentMethodValue.text = paymentMethod // Set selected payment method

        // Save reservation data to Firebase
        saveToFirebase(gymTitle, dateRange, guestsCount, totalPrice, paymentMethod)

        // Home button click listener to navigate back to MainActivity
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun saveToFirebase(place: String, date: String, persons: Int, total: String, paymentMethod: String) {
        val reservationData = mapOf(
            "place_reserved" to place,
            "date" to date,
            "number_of_persons" to persons,
            "total_price" to total,
            "payment_method" to paymentMethod // Include payment method
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
package com.example.skcamotes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ReservationReceiptActivity : AppCompatActivity() {

    private lateinit var placenameValue: TextView
    private lateinit var dateValue: TextView
    private lateinit var personValueText: TextView
    private lateinit var totalPriceValue: TextView
    private lateinit var homeButton: Button

    private lateinit var database: DatabaseReference
    private lateinit var userDatabase: DatabaseReference

    private lateinit var auth: FirebaseAuth
    private var userName: String = ""
    private var userEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_reservation_receipt)

        // Initialize Firebase Auth and Database references
        auth = FirebaseAuth.getInstance()
        val userId = auth.currentUser?.uid

        database = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("reservationreceipt")

        userDatabase = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Users")

        // Initialize UI components
        placenameValue = findViewById(R.id.placename_value)
        dateValue = findViewById(R.id.date_value)
        personValueText = findViewById(R.id.person_value)
        totalPriceValue = findViewById(R.id.totalprice_value)
        homeButton = findViewById(R.id.home_button)
        val paymentMethodValue = findViewById<TextView>(R.id.payment_method_value)

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
        paymentMethodValue.text = paymentMethod

        // Fetch user details from Firebase
        if (userId != null) {
            fetchUserDetails(userId) {
                // Save reservation data to Firebase after fetching user details
                saveToFirebase(gymTitle, dateRange, guestsCount, totalPrice, paymentMethod)
            }
        }

        // Home button click listener to navigate back to MainActivity
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun fetchUserDetails(userId: String, onComplete: () -> Unit) {
        userDatabase.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userName = snapshot.child("name").getValue(String::class.java) ?: "Unknown User"
                userEmail = snapshot.child("email").getValue(String::class.java) ?: "Unknown Email"
                onComplete()
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error fetching user details: ${error.message}")
            }
        })
    }

    private fun saveToFirebase(place: String, date: String, persons: Int, total: String, paymentMethod: String) {
        val reservationData = mapOf(
            "place_reserved" to place,
            "date" to date,
            "number_of_persons" to persons,
            "total_price" to total,
            "payment_method" to paymentMethod,
            "user_name" to userName,
            "user_email" to userEmail
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

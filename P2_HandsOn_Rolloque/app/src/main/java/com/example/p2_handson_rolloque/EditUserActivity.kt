package com.example.p2_handson_rolloque

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.p2_handson_rolloque.models.User
import com.google.firebase.database.FirebaseDatabase

class EditUserActivity : AppCompatActivity() {

    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etMiddleName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etAge: EditText
    private lateinit var etDOB: EditText
    private lateinit var btnUpdate: Button
    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        // Initialize UI elements
        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        etMiddleName = findViewById(R.id.etMiddleName)
        etEmail = findViewById(R.id.etEmail)
        etAge = findViewById(R.id.etAge)
        etDOB = findViewById(R.id.etDOB)
        btnUpdate = findViewById(R.id.btnUpdate)
        tvStatus = findViewById(R.id.tvStatus)

        // Set up the button click listener
        btnUpdate.setOnClickListener {
            updateUserData()
        }
    }

    private fun updateUserData() {
        // Collect user input from EditTexts
        val firstName = etFirstName.text.toString()
        val lastName = etLastName.text.toString()
        val middleName = etMiddleName.text.toString()
        val email = etEmail.text.toString()
        val age = etAge.text.toString()
        val dob = etDOB.text.toString()

        // Validate inputs before proceeding with the update
        if (firstName.isNotEmpty() && lastName.isNotEmpty() && email.isNotEmpty() && age.isNotEmpty() && dob.isNotEmpty()) {
            // Assuming you have a method to handle the database update (e.g., updateUserInDatabase())
            val isUpdated = updateUserInDatabase(firstName, lastName, middleName, email, age, dob)

            if (isUpdated) {
                tvStatus.text = "User Data Successfully Updated!"
                tvStatus.visibility = View.VISIBLE
            } else {
                tvStatus.text = "Update Failed!"
                tvStatus.visibility = View.VISIBLE
            }
        } else {
            tvStatus.text = "Please fill in all fields"
            tvStatus.visibility = View.VISIBLE
        }
    }

    private fun updateUserInDatabase(
        firstName: String,
        lastName: String,
        middleName: String,
        email: String,
        age: String,
        dob: String
    ): Boolean {
        // Assuming you're using SQLite for database updates (replace this with your actual DB logic)
        val db = DatabaseHelper(this)  // Assuming you have a DatabaseHelper class
        val contentValues = ContentValues().apply {
            put("first_name", firstName)
            put("last_name", lastName)
            put("middle_name", middleName)
            put("email", email)
            put("age", age)
            put("dob", dob)
        }

        // Log to verify database method is being called
        Log.d("EditUserActivity", "Updating user data for $email")

        try {
            val result = db.updateUser(contentValues)  // Assuming you have a method to update the user
            Log.d("EditUserActivity", "Update result: $result")
            return result > 0  // If rows affected is greater than 0, it was successful
        } catch (e: Exception) {
            Log.e("EditUserActivity", "Error updating user", e)
            return false
        }
    }
}

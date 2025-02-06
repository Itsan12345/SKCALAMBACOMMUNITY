package com.example.p2_handson_rolloque

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.p2_handson_rolloque.models.User
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var etFirstName: EditText
    private lateinit var etLastName: EditText
    private lateinit var etMiddleName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etAge: EditText
    private lateinit var etDOB: EditText
    private lateinit var btnSubmit: Button
    private lateinit var btnViewUsers: Button
    private lateinit var tvStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etFirstName = findViewById(R.id.etFirstName)
        etLastName = findViewById(R.id.etLastName)
        etMiddleName = findViewById(R.id.etMiddleName)
        etEmail = findViewById(R.id.etEmail)
        etAge = findViewById(R.id.etAge)
        etDOB = findViewById(R.id.etDOB)
        btnSubmit = findViewById(R.id.btnSubmit)
        btnViewUsers = findViewById(R.id.btnViewUsers)
        tvStatus = findViewById(R.id.tvStatus)

        btnSubmit.setOnClickListener {
            saveUserData()
        }

        btnViewUsers.setOnClickListener {
            startActivity(Intent(this, UsersListActivity::class.java))
        }
    }

    private fun saveUserData() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            // Redirect to login if user is not authenticated
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("students")

        val user = User(
            etFirstName.text.toString(),
            etLastName.text.toString(),
            etMiddleName.text.toString(),
            etEmail.text.toString(),
            etAge.text.toString(),  // Ensure age is passed as String
            etDOB.text.toString()
        )

        val userId = reference.push().key
        if (userId != null) {
            reference.child(userId).setValue(user).addOnCompleteListener {
                tvStatus.text = "User Data Successfully Saved!"
            }.addOnFailureListener {
                tvStatus.text = "Failed to Save Data!"
            }
        }
    }
}

package com.example.skcamotes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUpPage : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val ADMIN_EMAIL = "calambacommunity@gmail.com"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()

        val loginText: TextView = findViewById(R.id.tv_login)
        loginText.setOnClickListener {
            startActivity(Intent(this, LoginPage::class.java))
        }

        val signupButton: Button = findViewById(R.id.btn_signup)
        signupButton.setOnClickListener {
            val name = findViewById<EditText>(R.id.et_name).text.toString().trim()
            val phone = findViewById<EditText>(R.id.et_phone_number).text.toString().trim()
            val email = findViewById<EditText>(R.id.et_email).text.toString().trim()
            val password = findViewById<EditText>(R.id.et_password).text.toString().trim()

            if (validateInputs(name, phone, email, password)) {
                createAccount(name, phone, email, password)
            }
        }
    }

    private fun validateInputs(name: String, phone: String, email: String, password: String): Boolean {
        if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || password.isEmpty() || password.length < 6) {
            Toast.makeText(this, "Please provide valid inputs", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun createAccount(name: String, phone: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    sendEmailVerification() // Send email verification after account creation
                    saveUserToDatabase(name, phone, email)
                    Toast.makeText(this, "Signup successful! Please check your email for verification.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginPage::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Signup failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun sendEmailVerification() {
        val user = auth.currentUser
        user?.sendEmailVerification()
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Verification email sent!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to send verification email: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun saveUserToDatabase(name: String, phone: String, email: String) {
        val database = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("Users")
        val userRole = if (email == ADMIN_EMAIL) "admin" else "user"

        val userData = mapOf(
            "name" to name,
            "phone" to phone,
            "email" to email,
            "role" to userRole
        )

        database.child(auth.currentUser!!.uid).setValue(userData)
            .addOnSuccessListener {
                Toast.makeText(this, "User saved successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save user: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

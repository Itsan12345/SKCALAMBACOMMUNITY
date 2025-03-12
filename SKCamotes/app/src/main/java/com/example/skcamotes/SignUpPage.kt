package com.example.skcamotes

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class SignUpPage : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private val ADMIN_EMAIL = "calambacommunity@gmail.com"
    private val databaseRef = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("Users")

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
                    val userId = auth.currentUser!!.uid
                    sendEmailVerification()
                    saveUserToDatabase(userId, name, phone, email)
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

    private fun saveUserToDatabase(userId: String, name: String, phone: String, email: String) {
        val userRole = if (email == ADMIN_EMAIL) "admin" else "user"

        val userData = mapOf(
            "name" to name,
            "phone" to phone,
            "email" to email,
            "role" to userRole,
            "accepted_terms" to false // Default to false
        )

        databaseRef.child(userId).setValue(userData)
            .addOnSuccessListener {
                checkTermsAndConditions(userId)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save user: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkTermsAndConditions(userId: String) {
        val userRef = databaseRef.child(userId).child("accepted_terms")

        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val hasAcceptedTerms = snapshot.getValue(Boolean::class.java) ?: false
                if (!hasAcceptedTerms) {
                    showTermsAndConditionsDialog(userId)
                } else {
                    redirectToHome()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SignUpPage, "Error checking terms & conditions", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showTermsAndConditionsDialog(userId: String) {
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Terms & Conditions")
        dialogBuilder.setMessage(
            "By using our services, you agree that if any equipment you requested is lost, " +
                    "you are obliged to pay for its replacement.\n\n" +
                    "Do you accept these terms?"
        )
        dialogBuilder.setPositiveButton("Accept") { _, _ ->
            databaseRef.child(userId).child("accepted_terms").setValue(true)
                .addOnSuccessListener {
                    redirectToHome()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to accept terms. Try again.", Toast.LENGTH_SHORT).show()
                }
        }
        dialogBuilder.setNegativeButton("Decline") { _, _ ->
            auth.signOut()
            Toast.makeText(this, "You must accept the terms to use this app.", Toast.LENGTH_SHORT).show()
        }

        val dialog = dialogBuilder.create()
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun redirectToHome() {
        Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, LoginPage::class.java))
        finish()
    }
}

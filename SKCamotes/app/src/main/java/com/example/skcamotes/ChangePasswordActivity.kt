package com.example.skcamotes

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        auth = FirebaseAuth.getInstance()

        val newPasswordInput = findViewById<EditText>(R.id.et_new_password)
        val confirmPasswordInput = findViewById<EditText>(R.id.et_confirm_password)
        val changePasswordButton = findViewById<Button>(R.id.btn_change_password)
        val backButton = findViewById<ImageView>(R.id.btn_back)

        // Handle back button click
        backButton.setOnClickListener {
            finish() // Close the current activity and go back to the previous screen
        }

        changePasswordButton.setOnClickListener {
            val newPassword = newPasswordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            if (newPassword.isEmpty() || newPassword.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            changePassword(newPassword)
        }
    }

    private fun changePassword(newPassword: String) {
        val user = auth.currentUser
        user?.updatePassword(newPassword)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
                    finish()  // Close the Change Password screen
                } else {
                    Toast.makeText(this, "Failed to change password: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

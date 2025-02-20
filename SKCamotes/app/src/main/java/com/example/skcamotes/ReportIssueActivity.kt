package com.example.skcamotes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ReportIssueActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_issue)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("reportsandfeedback")

        val issueEditText = findViewById<EditText>(R.id.edit_issue)
        val submitButton = findViewById<Button>(R.id.submit_issue_button)
        val backButton = findViewById<ImageView>(R.id.btn_back)

        // Handle submit button click
        submitButton.setOnClickListener {
            val issueText = issueEditText.text.toString().trim()

            if (issueText.isNotEmpty()) {
                // Save the feedback to Firebase
                saveFeedbackToFirebase(issueText)
                // Optionally, send an email to admin
                sendEmail(issueText)
            } else {
                Toast.makeText(this, "Please describe the issue before submitting.", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle back button click
        backButton.setOnClickListener {
            finish() // Close the current activity and go back to the previous screen
        }
    }

    private fun saveFeedbackToFirebase(issueText: String) {
        // Create a new unique ID for each report
        val feedbackId = database.push().key ?: return

        val feedback = HashMap<String, String>()
        feedback["feedback"] = issueText
        feedback["timestamp"] = System.currentTimeMillis().toString() // Adding a timestamp for when the feedback was submitted

        // Save the feedback to the "reportsandfeedback" node in Firebase Realtime Database
        database.child(feedbackId).setValue(feedback).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Thank you for your feedback!", Toast.LENGTH_SHORT).show()
                finish()  // Close the activity after submission
            } else {
                Toast.makeText(this, "Failed to submit feedback. Please try again later.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun sendEmail(issueText: String) {
        val adminEmail = "calambacommunity@gmail.com"
        val subject = "Feedback/Report from User"

        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(adminEmail))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, issueText)
        }

        try {
            startActivity(Intent.createChooser(emailIntent, "Send email using"))
            Toast.makeText(this, "Thank you for your feedback!", Toast.LENGTH_SHORT).show()
            finish()  // Close the activity after submission
        } catch (e: android.content.ActivityNotFoundException) {
            Toast.makeText(this, "No email client found", Toast.LENGTH_SHORT).show()
        }
    }
}

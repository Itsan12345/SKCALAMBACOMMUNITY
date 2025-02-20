package com.example.skcamotes

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var profileImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var signOutButton: LinearLayout
    private lateinit var backButton: ImageButton

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var databaseReference: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)


        findViewById<LinearLayout>(R.id.notifications_container).setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.changepass_container).setOnClickListener {
            startActivity(Intent(this, ChangePasswordActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.report_issue_container).setOnClickListener {
            startActivity(Intent(this, ReportIssueActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.sign_out).setOnClickListener {
            signOut()
        }


        // Initialize Firebase Auth and Google Sign-In Client
        auth = FirebaseAuth.getInstance()
        googleSignInClient = GoogleSignIn.getClient(
            this,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        )

        // Initialize Views
        profileImageView = findViewById(R.id.profile_image)
        nameTextView = findViewById(R.id.name_text)
        emailTextView = findViewById(R.id.email_text)
        signOutButton = findViewById(R.id.sign_out)
        backButton = findViewById(R.id.back_button)


        // Set the back button click listener
        backButton.setOnClickListener {
            onBackPressed()  // Go to the previous page
        }

        // Initialize Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance(
            "https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("Users")


        // Fetch user info
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userEmail = currentUser.email
            if (userEmail != null) {
                fetchUserData(userEmail)
            }
        }

        // Fetch user info
        val account: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            val userEmail = account.email
            if (userEmail != null) {
                fetchUserData(userEmail)
            }
        }

    }

    private fun fetchUserData(email: String) {
        databaseReference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val name = userSnapshot.child("name").value.toString()
                        val email = userSnapshot.child("email").value.toString()
                        nameTextView.text = name
                        emailTextView.text = email
                    }
                } else {
                    nameTextView.text = "Name not found"
                    emailTextView.text = "Email not found"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                nameTextView.text = "Error fetching data"
                emailTextView.text = "Error fetching data"
            }
        })
    }

    private fun signOut() {
        // Sign out from Firebase
        auth.signOut()

        // Sign out from Google Sign-In
        googleSignInClient.signOut().addOnCompleteListener {
            // After signing out, navigate to the LoginPage activity
            val intent = Intent(this, LoginPage::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish() // Close the ProfileActivity
        }
    }
}

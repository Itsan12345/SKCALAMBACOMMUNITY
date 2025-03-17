package com.example.skcamotes

import android.content.Intent
import android.os.Bundle
import android.view.View
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
    private lateinit var notificationBadge: TextView

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var databaseReference: DatabaseReference
    private lateinit var notificationsRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val notificationsContainer = findViewById<LinearLayout>(R.id.notifications_container)
        notificationBadge = findViewById(R.id.notification_badge)

        notificationsContainer.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }

        findViewById<LinearLayout>(R.id.pushnotifications_container).setOnClickListener {
            startActivity(Intent(this, PushNotificationActivty::class.java))
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
            onBackPressed()
        }

        // Initialize Firebase Database References
        databaseReference = FirebaseDatabase.getInstance(
            "https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("Users")

        notificationsRef = FirebaseDatabase.getInstance(
            "https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app/"
        ).getReference("notifications")

        // Fetch user info
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val userEmail = currentUser.email
            if (userEmail != null) {
                fetchUserData(userEmail)
            }
        }

        val account: GoogleSignInAccount? = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            val userEmail = account.email
            if (userEmail != null) {
                fetchUserData(userEmail)
            }
        }

        // Fetch notification count
        fetchNotificationCount()
    }

    private fun fetchUserData(email: String) {
        databaseReference.orderByChild("email").equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
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

    private fun fetchNotificationCount() {
        notificationsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var count = 0
                for (data in snapshot.children) {
                    val read = data.child("read").getValue(Boolean::class.java) ?: false
                    if (!read) count++
                }

                if (count > 0) {
                    notificationBadge.text = count.toString()
                    notificationBadge.visibility = View.VISIBLE
                } else {
                    notificationBadge.visibility = View.GONE
                }

                // Save unread count in SharedPreferences
                val sharedPref = getSharedPreferences("NotificationPrefs", MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.putInt("unread_count", count)
                editor.apply()
            }

            override fun onCancelled(error: DatabaseError) {
                notificationBadge.visibility = View.GONE
            }
        })
    }

    private fun signOut() {
        auth.signOut()
        googleSignInClient.signOut().addOnCompleteListener {
            val intent = Intent(this, LoginPage::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}
package com.example.skcamotes

import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging

class NotificationActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var databaseReference: DatabaseReference
    private val notificationList = mutableListOf<Notification>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)


        recyclerView = findViewById(R.id.recyclerViewNotifications)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference.child("notifications")

        fetchNotifications()

        val backButton = findViewById<ImageView>(R.id.btn_back)


        backButton.setOnClickListener {
            finish()
        }

    }

    private fun fetchNotifications() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                notificationList.clear()
                for (data in snapshot.children) {
                    val message = data.child("message").getValue(String::class.java) ?: ""
                    val timestamp = data.child("timestamp").getValue(Long::class.java) ?: 0L
                    val read = data.child("read").getValue(Boolean::class.java) ?: false
                    val id = data.key ?: ""

                    if (message.isNotEmpty()) {
                        notificationList.add(Notification(message, timestamp, read, id))
                    }
                }

                // Sort the notifications by timestamp (most recent first)
                notificationList.sortByDescending { it.timestamp }

                // Initialize adapter after sorting
                notificationAdapter = NotificationAdapter(notificationList, databaseReference)
                recyclerView.adapter = notificationAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "Failed to fetch data: ${error.message}")
            }
        })
    }
}
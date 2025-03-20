package com.example.skcamotes

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.skcamotes.AdminSide.Announcement
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONObject

class AnnouncementsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AnnouncementsAdapter
    private lateinit var databaseReference: DatabaseReference
    private lateinit var fabAddAnnouncement: FloatingActionButton
    private lateinit var notificationsRef: DatabaseReference

    private val CHANNEL_ID = "announcement_channel"
    private val NOTIFICATION_ID = 1

    companion object {
        private const val NOTIFICATION_PERMISSION_REQUEST_CODE = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_announcements, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewAnnouncements)
        fabAddAnnouncement = view.findViewById(R.id.fabAddAnnouncement)

        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = AnnouncementsAdapter()
        recyclerView.adapter = adapter

        // Initialize Firebase Database Reference
        databaseReference = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference.child("announcements")

        // Load announcements from Firebase
        loadAnnouncements()

        // Floating Action Button Click
        fabAddAnnouncement.setOnClickListener { showAddAnnouncementDialog() }

        // Subscribe to Notifications
        FirebaseMessaging.getInstance().subscribeToTopic("announcements")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "Subscribed to notifications", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Subscription failed", Toast.LENGTH_SHORT).show()
                }
            }

        createNotificationChannel()

        return view
    }

    private fun loadAnnouncements() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val announcementList = mutableListOf<Announcement>()
                for (data in snapshot.children) {
                    val announcement = data.getValue(Announcement::class.java)
                    if (announcement != null) {
                        announcementList.add(announcement)
                    }
                }
                adapter.submitList(announcementList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load announcements", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun postAnnouncement(title: String, content: String) {
        val announcementData = mapOf(
            "title" to title,
            "content" to content,
            "timestamp" to System.currentTimeMillis()
        )

        val newAnnouncementRef = databaseReference.push()
        newAnnouncementRef.setValue(announcementData)
            .addOnSuccessListener {
                Toast.makeText(context, "Announcement posted successfully", Toast.LENGTH_SHORT).show()
                showLocalNotification("The Admin has posted a new Announcement!!")
                sendPushNotification("The Admin has posted a new Announcement!!")
                pushNotificationToFirebase() // Push to the notifications node
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to post announcement", Toast.LENGTH_SHORT).show()
            }
    }

    private fun pushNotificationToFirebase() {
        notificationsRef = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app/")
            .reference.child("notifications")
        val notificationData = mapOf(
            "message" to "The Admin has posted a new Announcement!!",
            "timestamp" to System.currentTimeMillis()
        )

        notificationsRef.push().setValue(notificationData)
    }

    private fun sendPushNotification(message: String) {
        val url = "https://calambacommunity.scarlet2.io/send_notification.php"

        val requestBody = JSONObject().apply {
            put("title", "New Announcement")
            put("message", message)
            put("topic", "announcements")
        }

        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                Toast.makeText(context, "Notification Sent to All Users", Toast.LENGTH_SHORT).show()
            },
            Response.ErrorListener { error ->
                Toast.makeText(context, "Failed to send notification: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getBody(): ByteArray {
                return requestBody.toString().toByteArray(Charsets.UTF_8)
            }

            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }
        }

        Volley.newRequestQueue(context).add(request)
    }

    private fun showAddAnnouncementDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_announcement, null)
        val editTextDialogTitle: EditText = dialogView.findViewById(R.id.editTextDialogTitle)
        val editTextDialogContent: EditText = dialogView.findViewById(R.id.editTextDialogContent)

        AlertDialog.Builder(requireContext())
            .setTitle("Add Announcement")
            .setView(dialogView)
            .setPositiveButton("Submit") { _, _ ->
                val title = editTextDialogTitle.text.toString().trim()
                val content = editTextDialogContent.text.toString().trim()
                if (title.isNotEmpty() && content.isNotEmpty()) {
                    postAnnouncement(title, content)
                } else {
                    Toast.makeText(context, "Please enter both title and content", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showLocalNotification(content: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), NOTIFICATION_PERMISSION_REQUEST_CODE)
                return
            }
        }

        val notificationBuilder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("New Announcement")
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(requireContext())) {
            notify(NOTIFICATION_ID, notificationBuilder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Announcements",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for new announcements"
            }

            val notificationManager: NotificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
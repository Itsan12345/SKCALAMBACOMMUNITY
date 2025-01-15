package com.example.skcamotes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AnnouncementsFragment : Fragment() {

    private lateinit var editTextTitle: EditText
    private lateinit var editTextContent: EditText
    private lateinit var buttonPostAnnouncement: Button
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_announcements, container, false)

        // Initialize UI elements
        editTextTitle = view.findViewById(R.id.editTextTitle)
        editTextContent = view.findViewById(R.id.editTextContent)
        buttonPostAnnouncement = view.findViewById(R.id.buttonPostAnnouncement)

        // Initialize Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance("https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app/").reference.child("announcements")

        // Set click listener for posting the announcement
        buttonPostAnnouncement.setOnClickListener {
            val title = editTextTitle.text.toString().trim()
            val content = editTextContent.text.toString().trim()

            if (title.isNotEmpty() && content.isNotEmpty()) {
                postAnnouncement(title, content)
            } else {
                Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun postAnnouncement(title: String, content: String) {
        // Create a map with the announcement data
        val announcementData = hashMapOf(
            "title" to title,
            "content" to content,
            "timestamp" to System.currentTimeMillis() // Save timestamp in milliseconds
        )

        // Push data to Firebase Database
        val newAnnouncementRef = databaseReference.push() // Generate a unique ID
        newAnnouncementRef.setValue(announcementData)
            .addOnSuccessListener {
                Toast.makeText(context, "Announcement posted successfully", Toast.LENGTH_SHORT).show()
                editTextTitle.text.clear()
                editTextContent.text.clear()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to post announcement", Toast.LENGTH_SHORT).show()
            }
    }
}

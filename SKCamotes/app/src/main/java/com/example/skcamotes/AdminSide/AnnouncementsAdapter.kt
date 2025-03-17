package com.example.skcamotes

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.skcamotes.AdminSide.Announcement
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AnnouncementsAdapter :
    ListAdapter<Announcement, AnnouncementsAdapter.AnnouncementViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Announcement>() {
            override fun areItemsTheSame(oldItem: Announcement, newItem: Announcement): Boolean {
                return oldItem.timestamp == newItem.timestamp
            }

            override fun areContentsTheSame(oldItem: Announcement, newItem: Announcement): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_announcement, parent, false)
        return AnnouncementViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
        val announcement = getItem(position)
        holder.bind(announcement)
    }

    inner class AnnouncementViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.announcement_title)
        private val contentTextView: TextView = itemView.findViewById(R.id.announcement_content)
        private val dateTextView: TextView = itemView.findViewById(R.id.announcement_date)

        fun bind(announcement: Announcement) {
            titleTextView.text = announcement.title
            contentTextView.text = announcement.content
            dateTextView.text = formatRelativeTime(announcement.timestamp)

            // Long-press listener for delete functionality
            itemView.setOnLongClickListener {
                showDeleteConfirmationDialog(announcement)
                true
            }
        }

        private fun formatRelativeTime(timestamp: Long): String {
            val now = System.currentTimeMillis()
            val diff = now - timestamp

            return when {
                diff < TimeUnit.MINUTES.toMillis(1) -> "Just now"
                diff < TimeUnit.HOURS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toMinutes(diff)} minutes ago"
                diff < TimeUnit.DAYS.toMillis(1) -> "${TimeUnit.MILLISECONDS.toHours(diff)} hours ago"
                diff < TimeUnit.DAYS.toMillis(2) -> "Yesterday"
                else -> SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(timestamp))
            }
        }

        private fun showDeleteConfirmationDialog(announcement: Announcement) {
            AlertDialog.Builder(itemView.context)
                .setTitle("Delete Announcement")
                .setMessage("Are you sure you want to delete this announcement?")
                .setPositiveButton("Delete") { _, _ ->
                    deleteAnnouncement(announcement)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        private fun deleteAnnouncement(announcement: Announcement) {
            val databaseReference: DatabaseReference = FirebaseDatabase.getInstance(
                "https://calambacommunity-default-rtdb.asia-southeast1.firebasedatabase.app/"
            ).reference.child("announcements")

            // Query to find the announcement by timestamp
            databaseReference.orderByChild("timestamp").equalTo(announcement.timestamp.toDouble())
                .addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
                    override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                        for (dataSnapshot in snapshot.children) {
                            dataSnapshot.ref.removeValue() // Remove the matching announcement
                        }
                    }

                    override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                        // Handle cancellation or errors here
                    }
                })
        }
    }
}
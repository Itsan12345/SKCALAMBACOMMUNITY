package com.example.skcamotes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import java.text.SimpleDateFormat
import java.util.*

class NotificationAdapter(
    private val notificationList: List<Notification>,
    private val databaseReference: DatabaseReference
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.notification_title)
        val content: TextView = itemView.findViewById(R.id.notification_content)
        val timestamp: TextView = itemView.findViewById(R.id.notification_timestamp)
        val container: View = itemView // For background color change
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notificationList[position]
        holder.content.text = notification.message
        holder.timestamp.text = formatTimestamp(notification.timestamp)

        // Set background and text color based on read status
        if (notification.read) {
            setGrayBackground(holder)
        } else {
            setWhiteBackground(holder)
        }

        // On click, mark as read and change the background and text color
        holder.container.setOnClickListener {
            if (!notification.read) {
                setGrayBackground(holder)
                databaseReference.child(notification.id).child("read").setValue(true)
                decreaseNotificationCount(holder.itemView.context)
            }
        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    private fun setGrayBackground(holder: NotificationViewHolder) {
        holder.container.setBackgroundResource(R.drawable.notification_read)
        holder.title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.gray))
        holder.content.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.gray))
    }

    private fun setWhiteBackground(holder: NotificationViewHolder) {
        holder.container.setBackgroundResource(R.drawable.notification_unread)
        holder.title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.red))
        holder.content.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.black))
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    private fun decreaseNotificationCount(context: Context) {
        val sharedPref = context.getSharedPreferences("NotificationPrefs", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        val currentCount = sharedPref.getInt("unread_count", 0)
        if (currentCount > 0) {
            editor.putInt("unread_count", currentCount - 1)
            editor.apply()
        }
    }
}

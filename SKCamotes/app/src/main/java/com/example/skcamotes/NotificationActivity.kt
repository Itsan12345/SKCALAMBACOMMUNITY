package com.example.skcamotes

import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

class NotificationActivity : AppCompatActivity() {

    private lateinit var notificationSwitch: Switch
    private val TAG = "NotificationActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)

        notificationSwitch = findViewById(R.id.switch_notifications)
        val backButton = findViewById<ImageView>(R.id.btn_back)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d(TAG, "FCM Token: $token")
            Toast.makeText(this, "Your FCM Token: $token", Toast.LENGTH_LONG).show()
        }

        backButton.setOnClickListener {
            finish()
        }

        val sharedPrefs = getSharedPreferences("NotificationPrefs", MODE_PRIVATE)
        val isNotificationsEnabled = sharedPrefs.getBoolean("notifications_enabled", false)
        notificationSwitch.isChecked = isNotificationsEnabled

        notificationSwitch.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            val editor = sharedPrefs.edit()
            editor.putBoolean("notifications_enabled", isChecked)
            editor.apply()

            if (isChecked) {
                FirebaseMessaging.getInstance().subscribeToTopic("announcements")
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Subscribed to notifications",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this,
                                "Subscription failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("announcements")
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Unsubscribed from notifications",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this,
                                "Unsubscription failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
    }
}

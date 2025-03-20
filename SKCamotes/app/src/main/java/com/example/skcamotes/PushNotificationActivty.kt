package com.example.skcamotes

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging

class PushNotificationActivty : AppCompatActivity() {

    private lateinit var notificationSwitch: Switch
    private val TAG = "NotificationActivity"
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_push_notification)

        notificationSwitch = findViewById(R.id.switch_notifications)
        val backButton = findViewById<ImageView>(R.id.btn_back)
        sharedPrefs = getSharedPreferences("NotificationPrefs", MODE_PRIVATE)

        // Generate a new FCM Token on login
        generateNewFCMToken()

        backButton.setOnClickListener {
            finish()
        }

        val isNotificationsEnabled = sharedPrefs.getBoolean("notifications_enabled", false)
        notificationSwitch.isChecked = isNotificationsEnabled

        notificationSwitch.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            val editor = sharedPrefs.edit()
            editor.putBoolean("notifications_enabled", isChecked)
            editor.apply()

            if (isChecked) {
                FirebaseMessaging.getInstance().subscribeToTopic("announcements")
                    .addOnCompleteListener { task ->
                        Toast.makeText(
                            this,
                            if (task.isSuccessful) "Subscribed to notifications" else "Subscription failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("announcements")
                    .addOnCompleteListener { task ->
                        Toast.makeText(
                            this,
                            if (task.isSuccessful) "Unsubscribed from notifications" else "Unsubscription failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        }
    }

    private fun generateNewFCMToken() {
        FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }
                val newToken = task.result
                Log.d(TAG, "New FCM Token: $newToken")
                Toast.makeText(this, "New FCM Token: $newToken", Toast.LENGTH_LONG).show()

                // Store new token in SharedPreferences
                sharedPrefs.edit().putString("fcm_token", newToken).apply()
            }
        }
    }
}
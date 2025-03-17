package com.example.skcamotes

data class Notification(
    val message: String = "",
    val timestamp: Long = 0L,
    var read: Boolean = false,  // New property to track read status
    val id: String = ""         // Store notification ID
)
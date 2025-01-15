package com.example.skcamotes.AdminSide

data class User(
    val id: String? = null,
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val role: String? = null,
    var timestamp: Long = 0L // Store the timestamp
)

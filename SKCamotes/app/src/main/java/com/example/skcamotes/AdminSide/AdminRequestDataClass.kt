package com.example.skcamotes.AdminSide

data class AdminRequestDataClass(
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    var hasNewRequest: Boolean = false // New property for notification badge
)

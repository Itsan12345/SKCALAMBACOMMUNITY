package com.example.skcamotes.RequestFeature

data class UserRequestsDataClass(
    val userEmail: String = "",
    val itemName: String = "",
    val quantity: String = "", // Updated to String
    val selectedDate: String = "",
    val selectedTime: String = ""
)
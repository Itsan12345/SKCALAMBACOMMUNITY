package com.example.skcamotes.AdminSide

data class AdminReturnedRequestDataClass(
    val userEmail: String = "",
    val itemName: String = "",
    val quantity: String = "", // Updated to String
    val selectedDate: String = "",
    val selectedTime: String = ""
)

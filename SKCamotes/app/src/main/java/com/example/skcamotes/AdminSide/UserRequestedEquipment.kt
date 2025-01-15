package com.example.skcamotes.AdminSide

data class UserRequestedEquipment(
    val description: String = "",
    val userEmail: String = "",
    val fullName: String = "",
    val itemName: String = "",
    val phoneNumber: String = "",
    val quantity: String = "",
    val selectedDate: String = "",
    val selectedTime: String = ""
)

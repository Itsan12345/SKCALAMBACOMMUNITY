package com.example.skcamotes

data class CancelledReservationDataClass(
    val place_reserved: String = "",
    val date: String = "",
    val number_of_persons: Int = 0,
    val total_price: String = "",
    val payment_method: String = "",
    val user_email: String = "" // Add this field to filter by user
)

package com.example.skcamotes

data class ReservationDataClass(
    val place_reserved: String = "",
    val date: String = "",
    val number_of_persons: Int = 0,
    val total_price: String = "",
    val payment_method: String = "",
    val user_email: String = "",
    val user_name: String = "",
    var key: String? = null // New field for Firebase key
)

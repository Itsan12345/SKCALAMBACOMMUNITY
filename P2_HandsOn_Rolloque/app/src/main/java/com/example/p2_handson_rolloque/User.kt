package com.example.p2_handson_rolloque.models

data class User(
    val firstName: String = "",
    val lastName: String = "",
    val middleName: String = "",
    val email: String = "",
    val age: String = "",  // changed to String for consistency
    val dateOfBirth: String = ""
)
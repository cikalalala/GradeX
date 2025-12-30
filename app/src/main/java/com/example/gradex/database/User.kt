package com.example.gradex.database

data class User(
    val user_id: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val password: String? = null,
    val profileImage: String? = null
)
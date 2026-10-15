package com.example.booknest.data.model

import com.squareup.moshi.JsonClass

/** Body sent to `POST /sydney/auth`. */
@JsonClass(generateAdapter = true)
data class LoginRequest(
    val username: String,
    val password: String
)

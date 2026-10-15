package com.example.booknest.data.model

import com.squareup.moshi.JsonClass

/** Successful body of `POST /sydney/auth`. The keypass selects the dashboard topic. */
@JsonClass(generateAdapter = true)
data class LoginResponse(
    val keypass: String
)

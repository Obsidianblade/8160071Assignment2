package com.example.booknest.data.model

import com.squareup.moshi.JsonClass

/** Body of `GET /dashboard/{keypass}`. */
@JsonClass(generateAdapter = true)
data class DashboardResponse(
    val entities: List<Book>,
    val entityTotal: Int
)

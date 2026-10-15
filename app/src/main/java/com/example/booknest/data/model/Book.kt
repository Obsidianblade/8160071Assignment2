package com.example.booknest.data.model

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * One book returned by the dashboard endpoint.
 *
 * - [JsonClass] lets Moshi generate a JSON adapter at compile time (via KSP).
 *   The property names match the JSON keys exactly, so no `@Json(name = ...)` is needed.
 * - [Parcelize] generates the Parcelable code so a Book can be passed to the
 *   Details screen as a Navigation argument without another network request.
 */
@Parcelize
@JsonClass(generateAdapter = true)
data class Book(
    val title: String,
    val author: String,
    val genre: String,
    val publicationYear: Int,
    val description: String
) : Parcelable

package com.example.booknest.ui

import androidx.annotation.StringRes
import com.example.booknest.R
import com.example.booknest.util.AppError

/** Turns an [AppError] into the friendly message shown to the user (from strings.xml). */
@StringRes
fun AppError.toMessageRes(): Int = when (this) {
    AppError.INVALID_CREDENTIALS -> R.string.error_invalid_credentials
    AppError.INVALID_KEYPASS -> R.string.error_invalid_keypass
    AppError.NETWORK -> R.string.error_network
    AppError.TIMEOUT -> R.string.error_timeout
    AppError.SERVER -> R.string.error_server
    AppError.UNEXPECTED_RESPONSE -> R.string.error_unexpected_response
    AppError.UNKNOWN -> R.string.error_unknown
}

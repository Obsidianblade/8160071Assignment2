package com.example.booknest.util

/**
 * The state of a piece of data that is loaded asynchronously.
 *
 * A `sealed` type means these are the only possible states, so a `when` over a Resource
 * is checked by the compiler and the UI can never forget to handle one of them.
 */
sealed interface Resource<out T> {
    data object Loading : Resource<Nothing>
    data class Success<out T>(val data: T) : Resource<T>
    data class Error(val error: AppError) : Resource<Nothing>
}

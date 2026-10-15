package com.example.booknest.data.repository

import com.example.booknest.data.model.Book
import com.example.booknest.util.Resource

/**
 * The single source of BookNest data for the ViewModels.
 *
 * ViewModels depend on this interface rather than on Retrofit or the implementation
 * (Dependency Inversion Principle), which is what lets the unit tests replace it with a mock.
 */
interface BookRepository {

    /** Authenticates the user and returns the keypass on success. */
    suspend fun login(username: String, password: String): Resource<String>

    /** Loads every book available for [keypass]. */
    suspend fun getBooks(keypass: String): Resource<List<Book>>
}

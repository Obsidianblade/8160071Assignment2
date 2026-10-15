package com.example.booknest.ui.dashboard

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booknest.data.model.Book
import com.example.booknest.data.repository.BookRepository
import com.example.booknest.util.AppError
import com.example.booknest.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Loads the books for the keypass received from the Login screen and exposes them as
 * [booksState] (Loading -> Success or Error).
 *
 * Hilt injects the [BookRepository] and a [SavedStateHandle]. The Navigation Component
 * copies the screen's arguments into the SavedStateHandle, so the keypass is available
 * here without the Fragment passing it in.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: BookRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val keypass: String? = savedStateHandle[KEYPASS_ARG]

    private val _booksState = MutableStateFlow<Resource<List<Book>>>(Resource.Loading)
    val booksState: StateFlow<Resource<List<Book>>> = _booksState.asStateFlow()

    private var loadJob: Job? = null

    init {
        // Load once when the ViewModel is created. Rotating the screen or returning from the
        // Details screen reuses this ViewModel, so no extra network request is made.
        loadBooks()
    }

    /** Fetches the books. Also used by the "Try again" button after an error. */
    fun loadBooks() {
        if (loadJob?.isActive == true) return

        val currentKeypass = keypass
        if (currentKeypass.isNullOrBlank()) {
            _booksState.value = Resource.Error(AppError.INVALID_KEYPASS)
            return
        }

        _booksState.value = Resource.Loading
        loadJob = viewModelScope.launch {
            _booksState.value = repository.getBooks(currentKeypass)
        }
    }

    companion object {
        /** Must match the argument name declared for dashboardFragment in nav_graph.xml. */
        const val KEYPASS_ARG = "keypass"
    }
}

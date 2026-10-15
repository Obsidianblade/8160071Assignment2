package com.example.booknest.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booknest.data.repository.BookRepository
import com.example.booknest.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Validates the login form, calls the repository and exposes the result as [uiState].
 *
 * @HiltViewModel + @Inject constructor: Hilt supplies the [BookRepository]; this class
 * never creates Retrofit or the repository itself, so tests can pass in a mock.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: BookRepository
) : ViewModel() {

    // Private mutable state, public read-only StateFlow (only the ViewModel can change it).
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(username: String, password: String) {
        // A request is already running: ignore extra taps so only one login is sent.
        if (_uiState.value.isLoading) return

        // Student IDs never contain spaces, so trim accidental ones. The password is sent
        // exactly as typed because it is case- and character-sensitive.
        val trimmedUsername = username.trim()
        val usernameMissing = trimmedUsername.isEmpty()
        val passwordMissing = password.isBlank()
        if (usernameMissing || passwordMissing) {
            _uiState.update {
                it.copy(
                    isUsernameMissing = usernameMissing,
                    isPasswordMissing = passwordMissing,
                    error = null
                )
            }
            return
        }

        _uiState.value = LoginUiState(isLoading = true)
        // viewModelScope cancels this coroutine automatically if the ViewModel is cleared.
        viewModelScope.launch {
            when (val result = repository.login(trimmedUsername, password)) {
                is Resource.Success -> _uiState.value = LoginUiState(keypass = result.data)
                is Resource.Error -> _uiState.value = LoginUiState(error = result.error)
                Resource.Loading -> Unit // the repository only returns finished results
            }
        }
    }

    /** Clears the "username required" message as soon as the user starts typing. */
    fun onUsernameChanged() {
        _uiState.update { it.copy(isUsernameMissing = false) }
    }

    /** Clears the "password required" message as soon as the user starts typing. */
    fun onPasswordChanged() {
        _uiState.update { it.copy(isPasswordMissing = false) }
    }

    /** Called by the Fragment after navigating, so the navigation isn't repeated. */
    fun onNavigatedToDashboard() {
        _uiState.update { it.copy(keypass = null) }
    }
}

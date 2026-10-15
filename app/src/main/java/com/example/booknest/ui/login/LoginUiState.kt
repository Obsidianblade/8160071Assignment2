package com.example.booknest.ui.login

import com.example.booknest.util.AppError

/**
 * Everything the Login screen needs to draw itself. The Fragment renders this state and
 * never decides anything on its own.
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val isUsernameMissing: Boolean = false,
    val isPasswordMissing: Boolean = false,
    /** Why the last login attempt failed, or null. */
    val error: AppError? = null,
    /**
     * Set when login succeeds. The Fragment navigates to the Dashboard with it and then
     * calls [LoginViewModel.onNavigatedToDashboard] so it is only handled once.
     */
    val keypass: String? = null
)

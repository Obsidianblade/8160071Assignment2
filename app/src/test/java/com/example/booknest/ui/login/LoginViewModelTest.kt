package com.example.booknest.ui.login

import com.example.booknest.MainDispatcherRule
import com.example.booknest.TestData.KEYPASS
import com.example.booknest.TestData.PASSWORD
import com.example.booknest.TestData.USERNAME
import com.example.booknest.data.repository.BookRepository
import com.example.booknest.util.AppError
import com.example.booknest.util.Resource
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: BookRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        // A MockK mock replaces the real repository, so no network request is ever made.
        repository = mockk()
        viewModel = LoginViewModel(repository)
    }

    @Test
    fun `successful login exposes the keypass`() = runTest {
        // Given
        coEvery { repository.login(USERNAME, PASSWORD) } returns Resource.Success(KEYPASS)

        // When
        viewModel.login(USERNAME, PASSWORD)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(KEYPASS, state.keypass)
        assertFalse(state.isLoading)
        assertNull(state.error)
    }

    @Test
    fun `keypass is used exactly as the server returns it`() = runTest {
        coEvery { repository.login(any(), any()) } returns Resource.Success("history")

        viewModel.login(USERNAME, PASSWORD)
        advanceUntilIdle()

        assertEquals("history", viewModel.uiState.value.keypass)
    }

    @Test
    fun `invalid credentials expose an INVALID_CREDENTIALS error`() = runTest {
        coEvery { repository.login(any(), any()) } returns Resource.Error(AppError.INVALID_CREDENTIALS)

        viewModel.login(USERNAME, "wrong")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(AppError.INVALID_CREDENTIALS, state.error)
        assertNull(state.keypass)
        assertFalse(state.isLoading)
    }

    @Test
    fun `empty username shows a validation error and skips the API`() = runTest {
        viewModel.login("   ", PASSWORD)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isUsernameMissing)
        assertFalse(state.isPasswordMissing)
        coVerify(exactly = 0) { repository.login(any(), any()) }
    }

    @Test
    fun `empty password shows a validation error and skips the API`() = runTest {
        viewModel.login(USERNAME, "")
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isPasswordMissing)
        assertFalse(state.isUsernameMissing)
        coVerify(exactly = 0) { repository.login(any(), any()) }
    }

    @Test
    fun `both fields empty flags both fields`() = runTest {
        viewModel.login("", "")

        val state = viewModel.uiState.value
        assertTrue(state.isUsernameMissing)
        assertTrue(state.isPasswordMissing)
    }

    @Test
    fun `network failure exposes a NETWORK error`() = runTest {
        coEvery { repository.login(any(), any()) } returns Resource.Error(AppError.NETWORK)

        viewModel.login(USERNAME, PASSWORD)
        advanceUntilIdle()

        assertEquals(AppError.NETWORK, viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `loading is shown while the request runs and cleared afterwards`() = runTest {
        coEvery { repository.login(any(), any()) } coAnswers {
            delay(1_000) // virtual time: the test does not actually wait
            Resource.Success(KEYPASS)
        }

        viewModel.login(USERNAME, PASSWORD)
        assertTrue(viewModel.uiState.value.isLoading)

        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `a second tap while loading does not send a duplicate request`() = runTest {
        coEvery { repository.login(any(), any()) } coAnswers {
            delay(1_000)
            Resource.Success(KEYPASS)
        }

        viewModel.login(USERNAME, PASSWORD)
        viewModel.login(USERNAME, PASSWORD)
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.login(any(), any()) }
    }

    @Test
    fun `username is trimmed but the password is sent exactly as typed`() = runTest {
        coEvery { repository.login(any(), any()) } returns Resource.Success(KEYPASS)

        viewModel.login("  $USERNAME ", " $PASSWORD")
        advanceUntilIdle()

        coVerify { repository.login(USERNAME, " $PASSWORD") }
    }

    @Test
    fun `typing clears the field validation errors`() {
        viewModel.login("", "")

        viewModel.onUsernameChanged()
        viewModel.onPasswordChanged()

        assertFalse(viewModel.uiState.value.isUsernameMissing)
        assertFalse(viewModel.uiState.value.isPasswordMissing)
    }

    @Test
    fun `keypass is cleared after navigation so it is handled only once`() = runTest {
        coEvery { repository.login(any(), any()) } returns Resource.Success(KEYPASS)
        viewModel.login(USERNAME, PASSWORD)
        advanceUntilIdle()

        viewModel.onNavigatedToDashboard()

        assertNull(viewModel.uiState.value.keypass)
    }
}

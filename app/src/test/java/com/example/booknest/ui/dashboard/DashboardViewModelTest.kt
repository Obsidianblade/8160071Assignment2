package com.example.booknest.ui.dashboard

import androidx.lifecycle.SavedStateHandle
import com.example.booknest.MainDispatcherRule
import com.example.booknest.TestData
import com.example.booknest.TestData.KEYPASS
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DashboardViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: BookRepository

    @Before
    fun setUp() {
        repository = mockk()
    }

    /** Builds the ViewModel the same way Navigation does: the keypass sits in the SavedStateHandle. */
    private fun createViewModel(keypass: String? = KEYPASS): DashboardViewModel {
        val arguments = if (keypass == null) emptyMap() else mapOf(DashboardViewModel.KEYPASS_ARG to keypass)
        return DashboardViewModel(repository, SavedStateHandle(arguments))
    }

    @Test
    fun `starts Loading, then exposes the books on success`() = runTest {
        coEvery { repository.getBooks(KEYPASS) } returns Resource.Success(TestData.books)

        val viewModel = createViewModel()
        assertEquals(Resource.Loading, viewModel.booksState.value)

        advanceUntilIdle()
        assertEquals(Resource.Success(TestData.books), viewModel.booksState.value)
    }

    @Test
    fun `all seven books are kept, in the order the API sent them`() = runTest {
        coEvery { repository.getBooks(any()) } returns Resource.Success(TestData.books)

        val viewModel = createViewModel()
        advanceUntilIdle()

        val books = (viewModel.booksState.value as Resource.Success).data
        assertEquals(7, books.size)
        assertEquals(TestData.books.map { it.title }, books.map { it.title })
    }

    @Test
    fun `requests books with the keypass from the navigation arguments`() = runTest {
        coEvery { repository.getBooks(any()) } returns Resource.Success(emptyList())

        createViewModel(keypass = "history")
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.getBooks("history") }
    }

    @Test
    fun `empty response is a Success with an empty list`() = runTest {
        coEvery { repository.getBooks(any()) } returns Resource.Success(emptyList())

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.booksState.value
        assertTrue(state is Resource.Success && state.data.isEmpty())
    }

    @Test
    fun `server failure exposes a SERVER error`() = runTest {
        coEvery { repository.getBooks(any()) } returns Resource.Error(AppError.SERVER)

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(Resource.Error(AppError.SERVER), viewModel.booksState.value)
    }

    @Test
    fun `missing keypass reports INVALID_KEYPASS without calling the API`() = runTest {
        val viewModel = createViewModel(keypass = null)
        advanceUntilIdle()

        assertEquals(Resource.Error(AppError.INVALID_KEYPASS), viewModel.booksState.value)
        coVerify(exactly = 0) { repository.getBooks(any()) }
    }

    @Test
    fun `retry after an error goes back to Loading and then succeeds`() = runTest {
        coEvery { repository.getBooks(any()) } returnsMany listOf(
            Resource.Error(AppError.NETWORK),
            Resource.Success(TestData.books)
        )
        val viewModel = createViewModel()
        advanceUntilIdle()
        assertEquals(Resource.Error(AppError.NETWORK), viewModel.booksState.value)

        viewModel.loadBooks()
        assertEquals(Resource.Loading, viewModel.booksState.value)

        advanceUntilIdle()
        assertEquals(Resource.Success(TestData.books), viewModel.booksState.value)
        coVerify(exactly = 2) { repository.getBooks(KEYPASS) }
    }

    @Test
    fun `retry while a request is running does not start a second one`() = runTest {
        coEvery { repository.getBooks(any()) } coAnswers {
            delay(1_000)
            Resource.Success(TestData.books)
        }

        val viewModel = createViewModel()
        viewModel.loadBooks()
        advanceUntilIdle()

        coVerify(exactly = 1) { repository.getBooks(any()) }
    }
}

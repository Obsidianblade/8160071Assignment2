package com.example.booknest.data.repository

import com.example.booknest.TestData
import com.example.booknest.TestData.KEYPASS
import com.example.booknest.TestData.PASSWORD
import com.example.booknest.TestData.USERNAME
import com.example.booknest.data.model.DashboardResponse
import com.example.booknest.data.model.LoginRequest
import com.example.booknest.data.model.LoginResponse
import com.example.booknest.data.remote.BookNestApiService
import com.example.booknest.util.AppError
import com.example.booknest.util.Resource
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import kotlin.coroutines.cancellation.CancellationException

/**
 * Tests the repository against a mocked Retrofit service: every server reply and failure the
 * app can meet is simulated, and no real network request is made.
 */
class BookRepositoryImplTest {

    private lateinit var apiService: BookNestApiService
    private lateinit var repository: BookRepositoryImpl

    @Before
    fun setUp() {
        apiService = mockk()
        repository = BookRepositoryImpl(apiService)
    }

    private fun <T> httpError(code: Int): Response<T> = Response.error(code, "".toResponseBody(null))

    // ---------- login ----------

    @Test
    fun `login sends the credentials and returns the keypass`() = runTest {
        val request = LoginRequest(USERNAME, PASSWORD)
        coEvery { apiService.login(request) } returns Response.success(LoginResponse(KEYPASS))

        val result = repository.login(USERNAME, PASSWORD)

        assertEquals(Resource.Success(KEYPASS), result)
        coVerify(exactly = 1) { apiService.login(request) }
    }

    @Test
    fun `login with HTTP 400 (wrong password) returns INVALID_CREDENTIALS`() = runTest {
        coEvery { apiService.login(any()) } returns httpError(400)

        assertEquals(Resource.Error(AppError.INVALID_CREDENTIALS), repository.login(USERNAME, "wrong"))
    }

    @Test
    fun `login with HTTP 404 (unknown user) returns INVALID_CREDENTIALS`() = runTest {
        coEvery { apiService.login(any()) } returns httpError(404)

        assertEquals(Resource.Error(AppError.INVALID_CREDENTIALS), repository.login("00000000", PASSWORD))
    }

    @Test
    fun `login with HTTP 500 returns SERVER`() = runTest {
        coEvery { apiService.login(any()) } returns httpError(500)

        assertEquals(Resource.Error(AppError.SERVER), repository.login(USERNAME, PASSWORD))
    }

    @Test
    fun `login with an empty body returns UNEXPECTED_RESPONSE`() = runTest {
        coEvery { apiService.login(any()) } returns Response.success<LoginResponse>(null)

        assertEquals(Resource.Error(AppError.UNEXPECTED_RESPONSE), repository.login(USERNAME, PASSWORD))
    }

    @Test
    fun `login with a blank keypass returns UNEXPECTED_RESPONSE`() = runTest {
        coEvery { apiService.login(any()) } returns Response.success(LoginResponse("  "))

        assertEquals(Resource.Error(AppError.UNEXPECTED_RESPONSE), repository.login(USERNAME, PASSWORD))
    }

    @Test
    fun `no connection returns NETWORK`() = runTest {
        coEvery { apiService.login(any()) } throws IOException("Unable to resolve host")

        assertEquals(Resource.Error(AppError.NETWORK), repository.login(USERNAME, PASSWORD))
    }

    @Test
    fun `timeout returns TIMEOUT`() = runTest {
        coEvery { apiService.login(any()) } throws SocketTimeoutException("timeout")

        assertEquals(Resource.Error(AppError.TIMEOUT), repository.login(USERNAME, PASSWORD))
    }

    @Test
    fun `malformed JSON returns UNEXPECTED_RESPONSE`() = runTest {
        coEvery { apiService.login(any()) } throws JsonEncodingException("Use JsonReader.setLenient(true)")

        assertEquals(Resource.Error(AppError.UNEXPECTED_RESPONSE), repository.login(USERNAME, PASSWORD))
    }

    @Test
    fun `any other exception returns UNKNOWN`() = runTest {
        coEvery { apiService.login(any()) } throws IllegalStateException("boom")

        assertEquals(Resource.Error(AppError.UNKNOWN), repository.login(USERNAME, PASSWORD))
    }

    @Test
    fun `coroutine cancellation is rethrown, not reported as an error`() = runTest {
        coEvery { apiService.login(any()) } throws CancellationException("screen closed")

        val thrown = runCatching { repository.login(USERNAME, PASSWORD) }.exceptionOrNull()

        assertTrue(thrown is CancellationException)
    }

    // ---------- getBooks ----------

    @Test
    fun `getBooks calls the API with the keypass and returns every book`() = runTest {
        coEvery { apiService.getDashboard(KEYPASS) } returns
            Response.success(DashboardResponse(entities = TestData.books, entityTotal = 7))

        val result = repository.getBooks(KEYPASS)

        assertEquals(Resource.Success(TestData.books), result)
        coVerify(exactly = 1) { apiService.getDashboard(KEYPASS) }
    }

    @Test
    fun `getBooks with no entities returns an empty Success`() = runTest {
        coEvery { apiService.getDashboard(any()) } returns
            Response.success(DashboardResponse(entities = emptyList(), entityTotal = 0))

        assertEquals(Resource.Success(emptyList<Any>()), repository.getBooks(KEYPASS))
    }

    @Test
    fun `getBooks with HTTP 404 (unknown keypass) returns INVALID_KEYPASS`() = runTest {
        coEvery { apiService.getDashboard(any()) } returns httpError(404)

        assertEquals(Resource.Error(AppError.INVALID_KEYPASS), repository.getBooks("unknown"))
    }

    @Test
    fun `getBooks with HTTP 503 returns SERVER`() = runTest {
        coEvery { apiService.getDashboard(any()) } returns httpError(503)

        assertEquals(Resource.Error(AppError.SERVER), repository.getBooks(KEYPASS))
    }

    @Test
    fun `getBooks with JSON missing a field returns UNEXPECTED_RESPONSE`() = runTest {
        coEvery { apiService.getDashboard(any()) } throws JsonDataException("Required value 'title' missing")

        assertEquals(Resource.Error(AppError.UNEXPECTED_RESPONSE), repository.getBooks(KEYPASS))
    }
}

package com.example.booknest.data.repository

import com.example.booknest.data.model.Book
import com.example.booknest.data.model.LoginRequest
import com.example.booknest.data.remote.BookNestApiService
import com.example.booknest.util.AppError
import com.example.booknest.util.Resource
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

/**
 * Retrofit-backed [BookRepository].
 *
 * @Inject constructor: Hilt creates this class itself and passes in the
 * [BookNestApiService] provided by NetworkModule (constructor injection).
 */
class BookRepositoryImpl @Inject constructor(
    private val apiService: BookNestApiService
) : BookRepository {

    override suspend fun login(username: String, password: String): Resource<String> =
        safeApiCall(
            clientError = AppError.INVALID_CREDENTIALS,
            call = { apiService.login(LoginRequest(username, password)) },
            onSuccess = { body ->
                // Use whatever keypass the server returns; never assume a fixed value.
                if (body.keypass.isBlank()) {
                    Resource.Error(AppError.UNEXPECTED_RESPONSE)
                } else {
                    Resource.Success(body.keypass)
                }
            }
        )

    override suspend fun getBooks(keypass: String): Resource<List<Book>> =
        safeApiCall(
            clientError = AppError.INVALID_KEYPASS,
            call = { apiService.getDashboard(keypass) },
            onSuccess = { body -> Resource.Success(body.entities) }
        )

    /**
     * Runs [call] and converts every possible outcome into a [Resource], so callers never
     * see an exception.
     *
     * @param clientError the error to report for an HTTP 4xx response, which means
     * something different for each endpoint.
     */
    private suspend fun <T : Any, R> safeApiCall(
        clientError: AppError,
        call: suspend () -> Response<T>,
        onSuccess: (T) -> Resource<R>
    ): Resource<R> {
        return try {
            val response = call()
            val body = response.body()
            when {
                response.isSuccessful && body != null -> onSuccess(body)
                response.isSuccessful -> Resource.Error(AppError.UNEXPECTED_RESPONSE)
                response.code() in 400..499 -> Resource.Error(clientError)
                else -> Resource.Error(AppError.SERVER)
            }
        } catch (e: CancellationException) {
            // The coroutine was cancelled (e.g. the screen closed). Rethrow so cancellation
            // keeps working instead of being reported as an error.
            throw e
        } catch (e: SocketTimeoutException) {
            Resource.Error(AppError.TIMEOUT)
        } catch (e: JsonDataException) {
            // JSON was valid but did not match our data classes (e.g. a missing field).
            Resource.Error(AppError.UNEXPECTED_RESPONSE)
        } catch (e: JsonEncodingException) {
            // Body was not valid JSON. Must be caught before IOException, its superclass.
            Resource.Error(AppError.UNEXPECTED_RESPONSE)
        } catch (e: IOException) {
            Resource.Error(AppError.NETWORK)
        } catch (e: Exception) {
            Resource.Error(AppError.UNKNOWN)
        }
    }
}

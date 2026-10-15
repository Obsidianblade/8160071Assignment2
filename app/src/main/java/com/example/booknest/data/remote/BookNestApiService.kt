package com.example.booknest.data.remote

import com.example.booknest.data.model.DashboardResponse
import com.example.booknest.data.model.LoginRequest
import com.example.booknest.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit description of the NIT3213 API. Retrofit generates the implementation.
 *
 * The functions are `suspend`, so they run asynchronously inside a coroutine and
 * never block the main thread. They return [Response] (rather than the body directly)
 * so the repository can inspect the HTTP status code and handle an empty body.
 */
interface BookNestApiService {

    @POST("sydney/auth")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("dashboard/{keypass}")
    suspend fun getDashboard(@Path("keypass") keypass: String): Response<DashboardResponse>
}

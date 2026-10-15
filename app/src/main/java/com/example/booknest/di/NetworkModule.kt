package com.example.booknest.di

import com.example.booknest.BuildConfig
import com.example.booknest.data.remote.BookNestApiService
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Tells Hilt how to build the networking objects. These are third-party classes
 * (we can't add @Inject to their constructors) or interfaces, so they need @Provides.
 *
 * Installed in SingletonComponent and marked @Singleton, so the whole app shares one
 * OkHttpClient/Retrofit instance (one connection pool, one configuration).
 *
 * Dependency chain: Moshi + OkHttpClient -> Retrofit -> BookNestApiService.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // The API is hosted on a free tier that sleeps when idle; the first request can take
    // close to a minute while it wakes up, so the read timeout is generous.
    private const val CONNECT_TIMEOUT_SECONDS = 30L
    private const val READ_TIMEOUT_SECONDS = 60L

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder().build()

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            // BASIC logs the method, URL, status code and timing. BODY is deliberately not
            // used because it would print the login password to Logcat.
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    @Provides
    @Singleton
    fun provideBookNestApiService(retrofit: Retrofit): BookNestApiService =
        retrofit.create(BookNestApiService::class.java)
}

package com.example.booknest.di

import com.example.booknest.data.repository.BookRepository
import com.example.booknest.data.repository.BookRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * ViewModels ask for the [BookRepository] interface. Hilt cannot construct an interface,
 * so this module tells it which implementation to supply.
 *
 * Hilt builds [BookRepositoryImpl] itself through its @Inject constructor (receiving the
 * API service from NetworkModule) and passes it in here. @Singleton means every ViewModel
 * shares the same repository instance.
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideBookRepository(impl: BookRepositoryImpl): BookRepository = impl
}

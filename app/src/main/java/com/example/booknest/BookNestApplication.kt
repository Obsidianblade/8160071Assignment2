package com.example.booknest

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class, registered in AndroidManifest.xml.
 *
 * @HiltAndroidApp triggers Hilt's code generation and creates the application-level
 * dependency container (SingletonComponent) that every other injection point uses.
 */
@HiltAndroidApp
class BookNestApplication : Application()

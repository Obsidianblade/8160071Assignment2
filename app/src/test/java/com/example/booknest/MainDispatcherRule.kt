package com.example.booknest

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * Replaces Dispatchers.Main with a test dispatcher for each test and restores it afterwards.
 *
 * ViewModels launch work in viewModelScope, which runs on Dispatchers.Main. That dispatcher
 * needs Android's main thread, which doesn't exist in a JVM unit test. This rule packages the
 * Dispatchers.setMain / Dispatchers.resetMain steps from Lecture 8 so every test class can
 * reuse them with a single @get:Rule line.
 *
 * StandardTestDispatcher queues coroutines instead of running them immediately, so tests can
 * observe the Loading state and then call advanceUntilIdle() to let the work finish.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

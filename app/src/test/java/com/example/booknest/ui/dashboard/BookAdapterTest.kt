package com.example.booknest.ui.dashboard

import com.example.booknest.TestData
import io.mockk.every
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

/**
 * Follows the adapter test from Lecture 8: the adapter is wrapped in a MockK spy so the real
 * logic runs, while notifyDataSetChanged() (which needs a real RecyclerView) is stubbed out.
 */
class BookAdapterTest {

    private lateinit var adapter: BookAdapter

    @Before
    fun setUp() {
        adapter = spyk(BookAdapter(onBookClick = {}))
        every { adapter.notifyDataSetChanged() } returns Unit
    }

    @Test
    fun `adapter is empty before any data is set`() {
        assertEquals(0, adapter.itemCount)
    }

    @Test
    fun `setData shows every book and refreshes the list`() {
        adapter.setData(TestData.books)

        assertEquals(7, adapter.itemCount)
        verify { adapter.notifyDataSetChanged() }
    }

    @Test
    fun `setData replaces the previous list`() {
        adapter.setData(TestData.books)
        adapter.setData(TestData.books.take(2))

        assertEquals(2, adapter.itemCount)
    }
}

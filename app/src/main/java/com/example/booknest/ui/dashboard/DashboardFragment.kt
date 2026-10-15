package com.example.booknest.ui.dashboard

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.booknest.R
import com.example.booknest.data.model.Book
import com.example.booknest.ui.toMessageRes
import com.example.booknest.util.Resource
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Library dashboard: a header with the number of books, then every book from the API in a
 * RecyclerView. Shows a loading, error (with retry) or empty state when appropriate.
 */
@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private val viewModel: DashboardViewModel by viewModels()

    private lateinit var bookAdapter: BookAdapter
    private lateinit var bookCountText: TextView
    private lateinit var booksRecycler: RecyclerView
    private lateinit var loadingGroup: View
    private lateinit var errorGroup: View
    private lateinit var errorMessageText: TextView
    private lateinit var emptyText: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)

        // The LayoutManager is set in XML (a grid with 1 column on phones, 2 on tablets).
        bookAdapter = BookAdapter(onBookClick = ::openBookDetails)
        booksRecycler.adapter = bookAdapter

        view.findViewById<MaterialButton>(R.id.retry_button).setOnClickListener {
            viewModel.loadBooks()
        }
        observeBooks()
    }

    private fun bindViews(view: View) {
        bookCountText = view.findViewById(R.id.book_count)
        booksRecycler = view.findViewById(R.id.books_recycler)
        loadingGroup = view.findViewById(R.id.loading_group)
        errorGroup = view.findViewById(R.id.error_group)
        errorMessageText = view.findViewById(R.id.error_message)
        emptyText = view.findViewById(R.id.empty_text)
    }

    private fun observeBooks() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.booksState.collect { state -> render(state) }
            }
        }
    }

    private fun render(state: Resource<List<Book>>) {
        val books = (state as? Resource.Success)?.data.orEmpty()

        loadingGroup.isVisible = state is Resource.Loading
        errorGroup.isVisible = state is Resource.Error
        emptyText.isVisible = state is Resource.Success && books.isEmpty()
        booksRecycler.isVisible = books.isNotEmpty()
        bookCountText.isVisible = state is Resource.Success

        when (state) {
            is Resource.Success -> {
                bookAdapter.setData(books)
                bookCountText.text =
                    resources.getQuantityString(R.plurals.dashboard_book_count, books.size, books.size)
            }
            is Resource.Error -> errorMessageText.setText(state.error.toMessageRes())
            Resource.Loading -> Unit
        }
    }

    private fun openBookDetails(book: Book) {
        // The whole Book is passed as a Safe Args argument, so Details needs no network call.
        findNavController().navigate(
            DashboardFragmentDirections.actionDashboardFragmentToDetailsFragment(book)
        )
    }
}

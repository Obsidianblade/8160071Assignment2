package com.example.booknest.ui.dashboard

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.booknest.R
import com.example.booknest.data.model.Book

/**
 * Bridges the list of books and the RecyclerView: creates ViewHolders, binds a book to
 * each one and reports how many items there are.
 *
 * @param onBookClick called with the tapped book. Passing a lambda keeps navigation out
 * of the adapter; the Fragment decides what a click does.
 */
class BookAdapter(
    private val onBookClick: (Book) -> Unit
) : RecyclerView.Adapter<BookViewHolder>() {

    private var books: List<Book> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        holder.bind(books[position], onBookClick)
    }

    override fun getItemCount(): Int = books.size

    // The API returns the whole list in one response, so the list is replaced in full
    // and a full refresh is the right notification.
    @SuppressLint("NotifyDataSetChanged")
    fun setData(newBooks: List<Book>) {
        books = newBooks
        notifyDataSetChanged()
    }
}

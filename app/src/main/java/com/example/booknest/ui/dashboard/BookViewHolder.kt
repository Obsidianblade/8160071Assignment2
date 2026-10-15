package com.example.booknest.ui.dashboard

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.booknest.R
import com.example.booknest.data.model.Book

/**
 * Holds the views of one book card (item_book.xml) so they are looked up once and reused
 * as the list scrolls. Shows a summary only: the description is left for the Details screen.
 */
class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val titleText: TextView = itemView.findViewById(R.id.book_title)
    private val authorText: TextView = itemView.findViewById(R.id.book_author)
    private val genreYearText: TextView = itemView.findViewById(R.id.book_genre_year)

    fun bind(book: Book, onBookClick: (Book) -> Unit) {
        val context = itemView.context
        titleText.text = book.title
        authorText.text = book.author
        genreYearText.text = context.getString(R.string.book_genre_year, book.genre, book.publicationYear)

        // One clear sentence for screen readers instead of four separate fragments.
        itemView.contentDescription = context.getString(
            R.string.book_card_description,
            book.title,
            book.author,
            book.genre,
            book.publicationYear
        )
        itemView.setOnClickListener { onBookClick(book) }
    }
}

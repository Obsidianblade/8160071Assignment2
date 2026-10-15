package com.example.booknest

import com.example.booknest.data.model.Book

/**
 * Fixed sample data for tests, copied from the dashboard API response so the tests are
 * realistic but never touch the network.
 *
 * The credentials are deliberately fake: real login details never appear in the tests.
 */
object TestData {

    const val USERNAME = "12345678"
    const val PASSWORD = "Alice"
    const val KEYPASS = "books"

    val books = listOf(
        Book(
            title = "To Kill a Mockingbird",
            author = "Harper Lee",
            genre = "Fiction",
            publicationYear = 1960,
            description = "A novel about racial injustice and the loss of innocence in the American South during the Great Depression."
        ),
        Book(
            title = "1984",
            author = "George Orwell",
            genre = "Dystopian Fiction",
            publicationYear = 1949,
            description = "A dystopian novel set in a totalitarian society, exploring themes of mass surveillance and the consequences of totalitarianism."
        ),
        Book(
            title = "Pride and Prejudice",
            author = "Jane Austen",
            genre = "Romance",
            publicationYear = 1813,
            description = "A romantic novel of manners set in Georgian England, focusing on the relationship between Elizabeth Bennet and Mr. Darcy."
        ),
        Book(
            title = "The Great Gatsby",
            author = "F. Scott Fitzgerald",
            genre = "Fiction",
            publicationYear = 1925,
            description = "A novel set in the Jazz Age, exploring themes of decadence, idealism, and the American Dream."
        ),
        Book(
            title = "One Hundred Years of Solitude",
            author = "Gabriel García Márquez",
            genre = "Magical Realism",
            publicationYear = 1967,
            description = "A multi-generational saga that tells the story of the Buendía family in the fictional town of Macondo."
        ),
        Book(
            title = "The Catcher in the Rye",
            author = "J.D. Salinger",
            genre = "Coming-of-age Fiction",
            publicationYear = 1951,
            description = "A novel that follows the experiences of a teenage protagonist in New York City, exploring themes of alienation and identity."
        ),
        Book(
            title = "The Hobbit",
            author = "J.R.R. Tolkien",
            genre = "Fantasy",
            publicationYear = 1937,
            description = "A children's fantasy novel about the adventures of hobbit Bilbo Baggins, serving as a prelude to The Lord of the Rings."
        )
    )
}

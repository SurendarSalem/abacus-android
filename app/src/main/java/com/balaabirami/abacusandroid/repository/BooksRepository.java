package com.balaabirami.abacusandroid.repository;

import com.balaabirami.abacusandroid.model.Book;
import com.balaabirami.abacusandroid.model.Level;

import java.util.ArrayList;
import java.util.List;

public class BooksRepository {
    private List<Book> books = new ArrayList<>();
    private static BooksRepository booksRepository;

    public static BooksRepository newInstance() {
        if (booksRepository == null) {
            booksRepository = new BooksRepository();
        }
        return booksRepository;
    }

    public List<Book> getBooks() {
        if (books.isEmpty()) {
            books = createBooksList();
        }
        return books;
    }


    private List<Book> createBooksList() {
        books.add(new Book("Select a Book"));
        books.add(new Book("Level MA Assessment Paper"));
        books.add(new Book("CB MA Assessment Paper"));
        books.add(new Book("PB MA Assessment Paper"));
        return books;
    }

    public void setBooks(List<Book> levels) {
        this.books = levels;
    }

    public void clear() {
        setBooks(null);
        booksRepository = null;
    }
}

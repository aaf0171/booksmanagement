package com.books.repository;

import com.books.model.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BooksRepositoryFileTest {

    @Autowired
    private BooksRepositoryFile booksRepositoryFile;

    @Test
    void shouldReturnAllBooks() {
        List<Book> books = booksRepositoryFile.findAll();

        assertNotNull(books);
        assertFalse(books.isEmpty());

        Book firstBook = books.get(0);
        assertNotNull(firstBook.getTitle());
        assertNotNull(firstBook.getIsbn());
    }
}

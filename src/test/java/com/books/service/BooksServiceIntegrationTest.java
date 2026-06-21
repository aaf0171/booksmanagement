package com.books.service;

import com.books.dto.BookDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BooksServiceIntegrationTest {

    @Autowired
    private BooksService booksService;

    @Test
    void shouldReturnListOfBookDTOWithoutIsbn() {
        List<BookDTO> books = booksService.findAll();

        assertNotNull(books);
        assertFalse(books.isEmpty());

        BookDTO firstBook = books.get(0);
        assertNotNull(firstBook.getTitre());
        assertNull(firstBook.getId());
    }
}

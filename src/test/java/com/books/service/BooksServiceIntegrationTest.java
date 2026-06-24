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
    void shouldReturnListOfBookDTOFromDatabase() {
        List<BookDTO> books = booksService.findAll();

        assertNotNull(books);
        assertTrue(books.size() >= 1);

        BookDTO firstBook = books.get(0);
        assertNotNull(firstBook.getTitle());
        assertNotNull(firstBook.getId());
    }
}

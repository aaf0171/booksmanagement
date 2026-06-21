package com.books;

import com.books.model.Book;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    void shouldCreateBookWithTitleAndIsbn() {
        Book book = new Book();
        book.setTitre("Spring Boot in Action");
        book.setIsbn("9781617292545");

        assertEquals("Spring Boot in Action", book.getTitre());
        assertEquals("9781617292545", book.getIsbn());
        assertNull(book.getId());
    }
}

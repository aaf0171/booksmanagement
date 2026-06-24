package com.books.repository;

import com.books.model.Book;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext
class BooksRepositoryDatabaseTest {

    @Autowired
    private BooksRepositoryDatabase booksRepositoryDatabase;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldReturnEmptyListWhenNoBooks() {
        entityManager.createQuery("DELETE FROM Book").executeUpdate();
        entityManager.flush();

        List<Book> books = booksRepositoryDatabase.findAll();

        assertNotNull(books);
        assertTrue(books.isEmpty());
    }

    @Test
    void shouldReturnAllBooks() {
        entityManager.createQuery("DELETE FROM Book").executeUpdate();
        entityManager.flush();

        Book book1 = new Book("Spring Boot in Action", "9781617292545");
        Book book2 = new Book("Java 21 Handbook", "9781098148026");
        entityManager.persist(book1);
        entityManager.persist(book2);
        entityManager.flush();

        List<Book> books = booksRepositoryDatabase.findAll();

        assertNotNull(books);
        assertEquals(2, books.size());
    }

    @Test
    void shouldPersistAndReturnBookWithGeneratedId() {
        entityManager.createQuery("DELETE FROM Book").executeUpdate();
        entityManager.flush();

        Book book = new Book("Test Book", "978-3-16-148410-0");
        entityManager.persist(book);
        entityManager.flush();

        List<Book> books = booksRepositoryDatabase.findAll();

        assertEquals(1, books.size());
        assertNotNull(books.get(0).getId());
        assertEquals("Test Book", books.get(0).getTitle());
        assertEquals("978-3-16-148410-0", books.get(0).getIsbn());
    }
}

package com.books.repository;

import com.books.model.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Primary
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BooksRepositoryDatabase implements BookRepository {

    private final EntityManager entityManager;

    @Override
    public List<Book> findAll() {
        return entityManager.createQuery("SELECT b FROM Book b", Book.class).getResultList();
    }
}

package com.books.repository;

import com.books.model.Borrower;
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
public class BorrowersRepositoryDatabase implements BorrowerRepository {

    private final EntityManager entityManager;

    @Override
    public List<Borrower> findAll() {
        return entityManager.createQuery("SELECT b FROM Borrower b", Borrower.class).getResultList();
    }
}

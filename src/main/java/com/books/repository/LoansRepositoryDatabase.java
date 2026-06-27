package com.books.repository;

import com.books.model.Loans;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Primary
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoansRepositoryDatabase implements LoansRepository {

    private final EntityManager entityManager;

    @Override
    public List<Loans> findAllActiveLoans() {
        return entityManager.createQuery("SELECT l FROM Loans l WHERE l.returnDate IS NULL", Loans.class).getResultList();
    }
}

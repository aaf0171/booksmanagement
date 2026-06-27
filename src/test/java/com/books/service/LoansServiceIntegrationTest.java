package com.books.service;

import com.books.model.Loans;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext
class LoansServiceIntegrationTest {

    @Autowired
    private LoansService loansService;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldReturnEmptyListWhenNoActiveLoans() {
        entityManager.createQuery("DELETE FROM Loans").executeUpdate();
        entityManager.flush();

        List<Loans> activeLoans = loansService.findAllActiveLoans();

        assertNotNull(activeLoans);
        assertTrue(activeLoans.isEmpty());
    }

    @Test
    void shouldReturnOnlyActiveLoans() {
        entityManager.createQuery("DELETE FROM Loans").executeUpdate();
        entityManager.flush();

        Loans activeLoan = new Loans(1L, 1L, LocalDate.of(2025, 1, 1), 14, LocalDate.of(2025, 1, 15), null, "active");
        Loans returnedLoan = new Loans(2L, 2L, LocalDate.of(2025, 1, 1), 14, LocalDate.of(2025, 1, 15), LocalDate.of(2025, 1, 10), "returned");
        entityManager.persist(activeLoan);
        entityManager.persist(returnedLoan);
        entityManager.flush();

        List<Loans> activeLoans = loansService.findAllActiveLoans();

        assertNotNull(activeLoans);
        assertEquals(1, activeLoans.size());
        assertEquals(1L, activeLoans.get(0).getBookId());
        assertNull(activeLoans.get(0).getReturnDate());
    }

    @Test
    void shouldReturnAllActiveLoansWhenMultiple() {
        entityManager.createQuery("DELETE FROM Loans").executeUpdate();
        entityManager.flush();

        Loans loan1 = new Loans(1L, 1L, LocalDate.of(2025, 1, 1), 14, LocalDate.of(2025, 1, 15), null, "active");
        Loans loan2 = new Loans(2L, 2L, LocalDate.of(2025, 2, 1), 14, LocalDate.of(2025, 2, 15), null, "active");
        Loans loan3 = new Loans(3L, 3L, LocalDate.of(2025, 1, 1), 14, LocalDate.of(2025, 1, 15), LocalDate.of(2025, 1, 10), "returned");
        entityManager.persist(loan1);
        entityManager.persist(loan2);
        entityManager.persist(loan3);
        entityManager.flush();

        List<Loans> activeLoans = loansService.findAllActiveLoans();

        assertNotNull(activeLoans);
        assertEquals(2, activeLoans.size());
    }
}

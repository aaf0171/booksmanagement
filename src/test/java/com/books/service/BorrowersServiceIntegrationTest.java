package com.books.service;

import com.books.model.Borrower;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext
class BorrowersServiceIntegrationTest {

    @Autowired
    private BorrowersService borrowersService;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void shouldReturnAllBorrowers() {
        entityManager.flush();
        entityManager.createNativeQuery("DELETE FROM borrowers").executeUpdate();

        Borrower borrower1 = new Borrower("Jean", "Dupont");
        Borrower borrower2 = new Borrower("Marie", "Martin");
        entityManager.persist(borrower1);
        entityManager.persist(borrower2);
        entityManager.flush();

        List<Borrower> borrowers = borrowersService.findAll();

        assertNotNull(borrowers);
        assertEquals(2, borrowers.size());

        Borrower first = borrowers.stream()
                .filter(b -> "Jean".equals(b.getName()))
                .findFirst()
                .orElseThrow();
        assertEquals("Dupont", first.getSurname());

        Borrower second = borrowers.stream()
                .filter(b -> "Marie".equals(b.getName()))
                .findFirst()
                .orElseThrow();
        assertEquals("Martin", second.getSurname());
    }
}

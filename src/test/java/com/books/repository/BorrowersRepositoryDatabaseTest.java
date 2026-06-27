package com.books.repository;

import com.books.model.Borrower;
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
class BorrowersRepositoryDatabaseTest {

    @Autowired
    private BorrowersRepositoryDatabase borrowersRepositoryDatabase;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldReturnEmptyListWhenNoBorrowers() {
        entityManager.createQuery("DELETE FROM Borrower").executeUpdate();
        entityManager.flush();

        List<Borrower> borrowers = borrowersRepositoryDatabase.findAll();

        assertNotNull(borrowers);
        assertTrue(borrowers.isEmpty());
    }

    @Test
    void shouldReturnAllBorrowers() {
        entityManager.createQuery("DELETE FROM Borrower").executeUpdate();
        entityManager.flush();

        Borrower borrower1 = new Borrower("Jean", "Dupont");
        Borrower borrower2 = new Borrower("Marie", "Martin");
        entityManager.persist(borrower1);
        entityManager.persist(borrower2);
        entityManager.flush();

        List<Borrower> borrowers = borrowersRepositoryDatabase.findAll();

        assertNotNull(borrowers);
        assertEquals(2, borrowers.size());
    }

    @Test
    void shouldPersistAndReturnBorrowerWithGeneratedId() {
        entityManager.createQuery("DELETE FROM Borrower").executeUpdate();
        entityManager.flush();

        Borrower borrower = new Borrower("Pierre", "Bernard");
        entityManager.persist(borrower);
        entityManager.flush();

        List<Borrower> borrowers = borrowersRepositoryDatabase.findAll();

        assertEquals(1, borrowers.size());
        assertNotNull(borrowers.get(0).getId());
        assertEquals("Pierre", borrowers.get(0).getName());
        assertEquals("Bernard", borrowers.get(0).getSurname());
    }
}

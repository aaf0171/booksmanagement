package com.books.repository;

import com.books.dto.ActiveLoanDTO;
import com.books.model.Book;
import com.books.model.Borrower;
import com.books.model.Loan;
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
class LoansRepositoryDatabaseTest {

    @Autowired
    private LoansRepositoryDatabase loansRepositoryDatabase;

    @Autowired
    private EntityManager entityManager;

    @Test
    void shouldReturnEmptyListWhenNoActiveLoans() {
        entityManager.createQuery("DELETE FROM Loan").executeUpdate();
        entityManager.flush();

        List<ActiveLoanDTO> activeLoans = loansRepositoryDatabase.findActiveLoans();

        assertNotNull(activeLoans);
        assertTrue(activeLoans.isEmpty());
    }

    @Test
    void shouldReturnOnlyActiveLoans() {
        entityManager.createQuery("DELETE FROM Loan").executeUpdate();
        entityManager.createQuery("DELETE FROM Book").executeUpdate();
        entityManager.createQuery("DELETE FROM Borrower").executeUpdate();
        entityManager.flush();

        Book book1 = new Book("Book One", "ISBN-001");
        Book book2 = new Book("Book Two", "ISBN-002");
        entityManager.persist(book1);
        entityManager.persist(book2);
        entityManager.flush();

        Borrower borrower1 = new Borrower("John", "Doe");
        Borrower borrower2 = new Borrower("Jane", "Doe");
        entityManager.persist(borrower1);
        entityManager.persist(borrower2);
        entityManager.flush();

        Long book1Id = book1.getId();
        Long book2Id = book2.getId();
        Long borrower1Id = borrower1.getId();
        Long borrower2Id = borrower2.getId();

        Loan activeLoan = new Loan(
            book1Id, 
            borrower1Id, 
            LocalDate.of(2026, 6, 14), 
            14, 
            null, 
            null, 
            "active");
        Loan returnedLoan = new Loan(
            book2Id, 
            borrower2Id, 
            LocalDate.of(2025, 1, 1), 
            14, 
            LocalDate.of(2025, 1, 15), 
            LocalDate.of(2025, 1, 10), 
            "returned");
        entityManager.persist(activeLoan);
        entityManager.persist(returnedLoan);
        entityManager.flush();

        List<ActiveLoanDTO> activeLoans = loansRepositoryDatabase.findActiveLoans();

        System.out.println("###################################");
        System.out.println(activeLoans.toString());
        System.out.println("################FIN#################");

        assertNotNull(activeLoans);
        assertEquals(1, activeLoans.size());
        assertEquals("Book One", activeLoans.get(0).getBookTitle());
        assertNull(activeLoans.get(0).getDueDate());
    }

    @Test
    void shouldReturnAllActiveLoansWhenMultiple() {
        entityManager.createQuery("DELETE FROM Loan").executeUpdate();
        entityManager.createQuery("DELETE FROM Book").executeUpdate();
        entityManager.createQuery("DELETE FROM Borrower").executeUpdate();
        entityManager.flush();

        Book book1 = new Book("Book One", "ISBN-001");
        Book book2 = new Book("Book Two", "ISBN-002");
        Book book3 = new Book("Book Three", "ISBN-003");
        entityManager.persist(book1);
        entityManager.persist(book2);
        entityManager.persist(book3);
        entityManager.flush();

        Long book1Id = book1.getId();
        Long book2Id = book2.getId();
        Long book3Id = book3.getId();

        Borrower borrower1 = new Borrower("John", "Doe");
        Borrower borrower2 = new Borrower("Jane", "Doe");
        Borrower borrower3 = new Borrower("Malcolm", "Doe");
        entityManager.persist(borrower1);
        entityManager.persist(borrower2);
        entityManager.persist(borrower3);
        entityManager.flush();

        Long borrower1Id = borrower1.getId();
        Long borrower2Id = borrower2.getId();
        Long borrower3Id = borrower3.getId();

        Loan loan1 = new Loan(book1Id, borrower1Id, LocalDate.of(2025, 1, 1), 
            14, LocalDate.of(2025, 1, 15), null, "active");
        Loan loan2 = new Loan(book2Id, borrower2Id, LocalDate.of(2025, 2, 1), 
            14, LocalDate.of(2025, 2, 15), null, "active");
        Loan loan3 = new Loan(book3Id, borrower3Id, LocalDate.of(2025, 1, 1), 
            14, LocalDate.of(2025, 1, 15), 
            LocalDate.of(2025, 1, 10), "returned");
        entityManager.persist(loan1);
        entityManager.persist(loan2);
        entityManager.persist(loan3);
        entityManager.flush();

        List<ActiveLoanDTO> activeLoans = loansRepositoryDatabase.findActiveLoans();

        assertNotNull(activeLoans);
        assertEquals(2, activeLoans.size());
    }
}

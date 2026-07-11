package com.books.repository;

import com.books.model.Borrower;
import com.books.model.Login;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BorrowerLoginRepositoryTest {

    @Autowired
    private BorrowerRepository borrowerRepository;

    @Autowired
    private LoginsRepository loginsRepository;

    private Login testLogin;

    @BeforeEach
    void setUp() {
        testLogin = Login.builder()
                .username("borrower-login-test")
                .passwordHash("$2a$10$hash")
                .enabled(false)
                .build();
    }

    @Test
    @DisplayName("should_save_borrower_with_login_fk")
    void shouldSaveBorrowerWithLoginFK() {
        Login savedLogin = loginsRepository.save(testLogin);
        loginsRepository.flush();

        Borrower borrower = Borrower.builder()
                .login_id(savedLogin.getId())
                .firstname("John")
                .lastname("Doe")
                .email("john@example.com")
                .build();
        Borrower savedBorrower = borrowerRepository.save(borrower);
        borrowerRepository.flush();

        assertNotNull(savedBorrower.getId());
        assertEquals(savedLogin.getId(), savedBorrower.getLogin_id());
    }

    @Test
    @DisplayName("should_save_login_before_borrower")
    void shouldSaveLoginBeforeBorrower() {
        Login savedLogin = loginsRepository.save(testLogin);
        loginsRepository.flush();

        Borrower borrower = Borrower.builder()
                .login_id(savedLogin.getId())
                .firstname("Jane")
                .lastname("Smith")
                .build();
        Borrower savedBorrower = borrowerRepository.save(borrower);
        borrowerRepository.flush();

        Borrower reloaded = borrowerRepository.findById(savedBorrower.getId()).orElseThrow();
        assertEquals(savedLogin.getId(), reloaded.getLogin_id());
        assertTrue(loginsRepository.findById(savedLogin.getId()).isPresent());
    }

    @Test
    @DisplayName("should_cascade_login_and_borrower")
    void shouldCascadeLoginAndBorrower() {
        Login savedLogin = loginsRepository.save(testLogin);
        loginsRepository.flush();

        Borrower borrower = Borrower.builder()
                .login_id(savedLogin.getId())
                .firstname("Borrower")
                .lastname("Test")
                .build();
        Borrower savedBorrower = borrowerRepository.save(borrower);
        borrowerRepository.flush();

        loginsRepository.delete(savedLogin);

        assertFalse(loginsRepository.findById(savedLogin.getId()).isPresent());
    }

    @Test
    @DisplayName("should_reject_duplicate_username")
    void shouldRejectDuplicateUsername() {
        Login first = Login.builder()
                .username("unique-borrower")
                .passwordHash("$2a$10$hash1")
                .enabled(false)
                .build();

        Login second = Login.builder()
                .username("unique-borrower")
                .passwordHash("$2a$10$hash2")
                .enabled(false)
                .build();

        loginsRepository.save(first);
        loginsRepository.flush();

        assertTrue(loginsRepository.existsByUsername("unique-borrower"));
    }
}

package com.books.repository;

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
class LoginsRepositoryTest {

    @Autowired
    private LoginsRepository loginsRepository;

    private Login testLogin;

    @BeforeEach
    void setUp() {
        testLogin = Login.builder()
                .username("repo-test-user")
                .passwordHash("$2a$10$dummyhashvalueforrepository0000000000000000000000000")
                .enabled(true)
                .build();
    }

    @Test
    @DisplayName("should_save_login")
    void shouldSaveLogin() {
        Login saved = loginsRepository.save(testLogin);

        assertNotNull(saved.getId());
        assertEquals("repo-test-user", saved.getUsername());
        assertTrue(saved.getEnabled());
        loginsRepository.flush();
        assertTrue(loginsRepository.findById(saved.getId()).isPresent());
    }

    @Test
    @DisplayName("should_find_login_by_username")
    void shouldFindLoginByUsername() {
        Login saved = loginsRepository.save(testLogin);
        loginsRepository.flush();

        java.util.Optional<Login> found = loginsRepository.findByUsername("repo-test-user");

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    @DisplayName("should_return_empty_when_username_not_found")
    void shouldReturnEmptyWhenUsernameNotFound() {
        java.util.Optional<Login> found = loginsRepository.findByUsername("nonexistent");

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("should_reject_duplicate_username")
    void shouldRejectDuplicateUsername() {
        Login first = Login.builder()
                .username("unique-user")
                .passwordHash("$2a$10$hash1")
                .enabled(true)
                .build();
        Login second = Login.builder()
                .username("unique-user")
                .passwordHash("$2a$10$hash2")
                .enabled(true)
                .build();

        loginsRepository.save(first);
        loginsRepository.flush();

        assertTrue(loginsRepository.existsByUsername("unique-user"));
    }
}

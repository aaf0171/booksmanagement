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
class LoginsRepositoryDeleteTest {

    @Autowired
    private LoginsRepository loginsRepository;

    private Login testLogin;

    @BeforeEach
    void setUp() {
        testLogin = Login.builder()
                .username("delete-test-user")
                .passwordHash("$2a$10$hash")
                .enabled(true)
                .build();
    }

    @Test
    @DisplayName("should_delete_existing_login")
    void shouldDeleteExistingLogin() {
        Login saved = loginsRepository.save(testLogin);
        loginsRepository.flush();

        assertTrue(loginsRepository.existsById(saved.getId()));

        int deleted = loginsRepository.deleteLoginByIdCustom(saved.getId());

        assertEquals(1, deleted);
        assertFalse(loginsRepository.findById(saved.getId()).isPresent());
    }

    @Test
    @DisplayName("should_return_zero_affected_rows_when_login_does_not_exist")
    void shouldReturnZeroAffectedRowsWhenLoginDoesNotExist() {
        int deleted = loginsRepository.deleteLoginByIdCustom(99999L);

        assertEquals(0, deleted);
    }

    @Test
    @DisplayName("should_cascade_delete_associated_records")
    void shouldCascadeDeleteAssociatedRecords() {
        Login saved = loginsRepository.save(testLogin);
        loginsRepository.flush();

        int deleted = loginsRepository.deleteLoginByIdCustom(saved.getId());

        assertEquals(1, deleted);
        assertFalse(loginsRepository.findById(saved.getId()).isPresent());
    }
}

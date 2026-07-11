package com.books.repository;

import com.books.model.Login;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LoginsRepositoryDisableTest {

    @Autowired
    private LoginsRepository loginsRepository;

    private Login testLogin;

    @BeforeEach
    void setUp() {
        testLogin = Login.builder()
                .username("disable-test-user")
                .passwordHash("$2a$10$hash")
                .enabled(true)
                .build();
    }

    @Test
    @DisplayName("should_update_enabled_to_false")
    void shouldUpdateEnabledToFalse() {
        Login saved = loginsRepository.save(testLogin);
        loginsRepository.flush();

        saved.setEnabled(false);
        Login updated = loginsRepository.save(saved);

        assertFalse(updated.getEnabled());

        Login reloaded = loginsRepository.findById(saved.getId()).orElseThrow();
        assertFalse(reloaded.getEnabled());
    }

    @Test
    @DisplayName("should_return_zero_affected_rows_when_login_does_not_exist")
    void shouldReturnZeroAffectedRowsWhenLoginDoesNotExist() {
        Optional<Login> result = loginsRepository.findById(99999L);

        assertFalse(result.isPresent());
    }
}

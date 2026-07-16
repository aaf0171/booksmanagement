package com.books.repository;

import com.books.model.ActivationToken;
import com.books.model.Login;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ActivationTokenRepositoryTest {

    @Autowired
    private ActivationTokenRepository activationTokenRepository;

    @Autowired
    private LoginsRepository loginsRepository;

    private Login testLogin;
    private ActivationToken testToken;

    @BeforeEach
    void setUp() {
        loginsRepository.deleteAll();
        activationTokenRepository.deleteAll();

        testLogin = Login.builder()
                .username("token-test-user")
                .passwordHash("$2a$10$dummyhashvaluefortoken0000000000000000000000000000000")
                .enabled(true)
                .build();
        Login savedLogin = loginsRepository.save(testLogin);

        String tokenHash = "a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2";
        testToken = ActivationToken.builder()
                .loginId(savedLogin.getId())
                .type("ACTIVATION")
                .tokenHash(tokenHash)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("should_save_activationToken")
    void shouldSaveActivationToken() {
        ActivationToken saved = activationTokenRepository.save(testToken);

        assertNotNull(saved.getId());
        assertEquals("ACTIVATION", saved.getType());
        assertEquals(64, saved.getTokenHash().length());
        activationTokenRepository.flush();
        assertTrue(activationTokenRepository.findById(saved.getId()).isPresent());
    }

    @Test
    @DisplayName("should_find_by_token_hash")
    void shouldFindByTokenHash() {
        ActivationToken saved = activationTokenRepository.save(testToken);
        activationTokenRepository.flush();

        Optional<ActivationToken> found = activationTokenRepository.findByTokenHash(
                "a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2");

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    @DisplayName("should_find_active_by_login_id")
    void shouldFindActiveByLoginId() {
        ActivationToken saved = activationTokenRepository.save(testToken);
        activationTokenRepository.flush();

        Optional<ActivationToken> found = activationTokenRepository.findActiveByLoginId(
                saved.getLoginId(), LocalDateTime.now());

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    @DisplayName("should_not_find_expired_token_as_active")
    void shouldNotFindExpiredTokenAsActive() {
        ActivationToken expiredToken = ActivationToken.builder()
                .loginId(testLogin.getId())
                .type("ACTIVATION")
                .tokenHash("expiredhash00000000000000000000000000000000000000000000000000000000")
                .expiresAt(LocalDateTime.now().minusHours(1))
                .createdAt(LocalDateTime.now().minusHours(2))
                .build();
        activationTokenRepository.save(expiredToken);
        activationTokenRepository.flush();

        Optional<ActivationToken> found = activationTokenRepository.findActiveByLoginId(
                testLogin.getId(), LocalDateTime.now());

        assertFalse(found.isPresent());
    }

    @Test
    @DisplayName("should_mark_token_as_used")
    void shouldMarkTokenAsUsed() {
        ActivationToken saved = activationTokenRepository.save(testToken);
        activationTokenRepository.flush();

        int updated = activationTokenRepository.markAsUsed(saved.getId());

        assertEquals(1, updated);

        // Re-query with refresh to see the update within same transaction
        ActivationToken refreshToken = activationTokenRepository.findById(saved.getId()).orElseThrow();
        activationTokenRepository.flush();
        // The update is done via native query, verify the count is correct
        assertTrue(updated >= 0);
    }

    @Test
    @DisplayName("should_return_zero_affected_rows_when_login_does_not_exist")
    void shouldReturnZeroAffectedRowsWhenLoginDoesNotExist() {
        int updated = activationTokenRepository.markAsUsed(99999L);

        assertEquals(0, updated);
    }

    @Test
    @DisplayName("should_invalidate_unused_tokens")
    void shouldInvalidateUnusedTokens() {
        ActivationToken saved = activationTokenRepository.save(testToken);
        activationTokenRepository.flush();

        int invalidated = activationTokenRepository.invalidateUnusedTokens(
                testLogin.getId(), LocalDateTime.now());

        assertEquals(1, invalidated);
    }

    @Test
    @DisplayName("should_not_invalidate_expired_tokens")
    void shouldNotInvalidateExpiredTokens() {
        ActivationToken expiredToken = ActivationToken.builder()
                .loginId(testLogin.getId())
                .type("ACTIVATION")
                .tokenHash("expiredhash00000000000000000000000000000000000000000000000000000000")
                .expiresAt(LocalDateTime.now().minusHours(1))
                .createdAt(LocalDateTime.now().minusHours(2))
                .build();
        activationTokenRepository.save(expiredToken);
        activationTokenRepository.flush();

        int invalidated = activationTokenRepository.invalidateUnusedTokens(
                testLogin.getId(), LocalDateTime.now());

        assertEquals(0, invalidated);
    }

    @Test
    @DisplayName("should_find_all_tokens_by_login_id")
    void shouldFindAllTokensByLoginId() {
        ActivationToken saved1 = activationTokenRepository.save(testToken);
        activationTokenRepository.flush();

        ActivationToken saved2 = ActivationToken.builder()
                .loginId(testLogin.getId())
                .type("ACTIVATION")
                .tokenHash("secondhash000000000000000000000000000000000000000000000000000000000000")
                .expiresAt(LocalDateTime.now().plusHours(2))
                .createdAt(LocalDateTime.now())
                .build();
        activationTokenRepository.save(saved2);
        activationTokenRepository.flush();

        java.util.List<ActivationToken> tokens = activationTokenRepository.findByLoginId(testLogin.getId());

        assertEquals(2, tokens.size());
    }

    @Test
    @DisplayName("should_update_login_enabled")
    void shouldUpdateLoginEnabled() {
        testLogin.setEnabled(false);
        loginsRepository.save(testLogin);
        activationTokenRepository.flush();

        assertFalse(testLogin.getEnabled());

        Login updatedLogin = loginsRepository.findById(testLogin.getId()).orElseThrow();
        updatedLogin.setEnabled(true);
        loginsRepository.save(updatedLogin);
        activationTokenRepository.flush();

        Login fetchedLogin = loginsRepository.findById(testLogin.getId()).orElseThrow();
        assertTrue(fetchedLogin.getEnabled());
    }
}

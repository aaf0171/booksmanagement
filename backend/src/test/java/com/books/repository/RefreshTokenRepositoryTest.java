package com.books.repository;

import com.books.model.RefreshToken;
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
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private RefreshToken testToken;

    @BeforeEach
    void setUp() {
        testToken = RefreshToken.builder()
                .loginId(1L)
                .tokenHash("abc123hash456def789")
                .expiresAt(LocalDateTime.now().plusDays(30))
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("shouldSaveRefreshToken")
    void shouldSaveRefreshToken() {
        RefreshToken saved = refreshTokenRepository.save(testToken);

        assertNotNull(saved.getId());
        assertEquals(1L, saved.getLoginId());
        assertEquals("abc123hash456def789", saved.getTokenHash());
        assertFalse(saved.getRevoked());
        assertTrue(saved.getId() > 0);
    }

    @Test
    @DisplayName("shouldFindRefreshTokenByHash")
    void shouldFindRefreshTokenByHash() {
        RefreshToken saved = refreshTokenRepository.save(testToken);
        refreshTokenRepository.flush();

        Optional<RefreshToken> found = refreshTokenRepository.findByTokenHash("abc123hash456def789");

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
    }

    @Test
    @DisplayName("shouldMarkRefreshTokenAsRevoked")
    void shouldMarkRefreshTokenAsRevoked() {
        RefreshToken saved = refreshTokenRepository.save(testToken);
        refreshTokenRepository.flush();

        int rows = refreshTokenRepository.revokeByTokenHash("abc123hash456def789");

        assertEquals(1, rows);

        Optional<RefreshToken> found = refreshTokenRepository.findByTokenHash("abc123hash456def789");
        assertTrue(found.isPresent());
        assertTrue(found.get().getRevoked());
    }

    @Test
    @DisplayName("shouldReturnZeroRowsWhenTokenNotExists")
    void shouldReturnZeroRowsWhenTokenNotExists() {
        int rows = refreshTokenRepository.revokeByTokenHash("nonexistenthash");

        assertEquals(0, rows);
    }

    @Test
    @DisplayName("shouldNotRevokeAlreadyRevokedToken")
    void shouldNotRevokeAlreadyRevokedToken() {
        RefreshToken saved = refreshTokenRepository.save(testToken);
        refreshTokenRepository.flush();

        refreshTokenRepository.revokeByTokenHash("abc123hash456def789");

        int secondRevoke = refreshTokenRepository.revokeByTokenHash("abc123hash456def789");

        assertEquals(0, secondRevoke);
    }

    @Test
    @DisplayName("shouldReturnZeroWhenFindExpiredToken")
    void shouldReturnZeroWhenFindExpiredToken() {
        RefreshToken expiredToken = RefreshToken.builder()
                .loginId(1L)
                .tokenHash("expiredhash")
                .expiresAt(LocalDateTime.now().minusDays(1))
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .build();

        refreshTokenRepository.save(expiredToken);
        refreshTokenRepository.flush();

        Optional<RefreshToken> found = refreshTokenRepository.findByTokenHash("expiredhash");

        assertTrue(found.isPresent());
    }
}

package com.books.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenTest {

    @Test
    @DisplayName("shouldBuildRefreshTokenWithAllFields")
    void shouldBuildRefreshTokenWithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiry = now.plusDays(30);

        RefreshToken token = RefreshToken.builder()
                .loginId(1L)
                .tokenHash("abc123hash")
                .expiresAt(expiry)
                .revoked(false)
                .createdAt(now)
                .build();

        assertEquals(1L, token.getLoginId());
        assertEquals("abc123hash", token.getTokenHash());
        assertEquals(expiry, token.getExpiresAt());
        assertFalse(token.getRevoked());
        assertEquals(now, token.getCreatedAt());
        assertNull(token.getId());
    }

    @Test
    @DisplayName("shouldSetIdWhenPersisted")
    void shouldSetIdWhenPersisted() {
        RefreshToken token = RefreshToken.builder()
                .id(42L)
                .loginId(1L)
                .tokenHash("hash123")
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build();

        assertEquals(42L, token.getId());
    }

    @Test
    @DisplayName("shouldDefaultRevokedToFalse")
    void shouldDefaultRevokedToFalse() {
        RefreshToken token = RefreshToken.builder()
                .loginId(1L)
                .tokenHash("hash")
                .expiresAt(LocalDateTime.now().plusDays(30))
                .build();

        assertFalse(token.getRevoked());
    }

    @Test
    @DisplayName("canMarkTokenAsRevoked")
    void canMarkTokenAsRevoked() {
        RefreshToken token = RefreshToken.builder()
                .loginId(1L)
                .tokenHash("hash")
                .expiresAt(LocalDateTime.now().plusDays(30))
                .revoked(false)
                .build();

        token.setRevoked(true);
        assertTrue(token.getRevoked());
    }
}

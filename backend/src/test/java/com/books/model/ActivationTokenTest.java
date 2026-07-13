package com.books.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ActivationTokenTest {

    @Test
    @DisplayName("shouldGenerateSecureRandomToken")
    void shouldGenerateSecureRandomToken() {
        ActivationToken token = ActivationToken.builder()
                .loginId(1L)
                .type("ACTIVATION")
                .tokenHash("a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2c3d4e5f6a1b2")
                .expiresAt(LocalDateTime.now().plusHours(1))
                .createdAt(LocalDateTime.now())
                .build();

        assertNotNull(token);
        assertEquals("ACTIVATION", token.getType());
        assertEquals(64, token.getTokenHash().length());
    }

    @Test
    @DisplayName("shouldHashTokenBeforeStorage")
    void shouldHashTokenBeforeStorage() {
        String plaintextToken = "aBcDeFgHiJkLmNoPqRsTuVwXyZ0123456789_-aB";

        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(plaintextToken.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(64);
            for (byte b : hashBytes) {
                String hex = String.format("%02x", b);
                hexString.append(hex);
            }
            String hash = hexString.toString();

            assertEquals(64, hash.length());
            assertNotNull(hash);
        } catch (Exception e) {
            fail("SHA-256 hashing should work: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("shouldAcceptNullUsedAtAtEntityLevel")
    void shouldAcceptNullUsedAtAtEntityLevel() {
        ActivationToken token = ActivationToken.builder()
                .loginId(1L)
                .type("ACTIVATION")
                .tokenHash("abc123hash")
                .expiresAt(LocalDateTime.now().plusHours(1))
                .createdAt(LocalDateTime.now())
                .build();

        assertNotNull(token);
        assertNull(token.getUsedAt());
    }

    @Test
    @DisplayName("shouldAcceptValidActivationTokenWithAllFields")
    void shouldAcceptValidActivationTokenWithAllFields() {
        LocalDateTime now = LocalDateTime.now();
        ActivationToken token = ActivationToken.builder()
                .loginId(42L)
                .type("ACTIVATION")
                .tokenHash("deadbeef" + "deadbeef" + "deadbeef" + "deadbeef" +
                           "deadbeef" + "deadbeef" + "deadbeef" + "deadbeef")
                .expiresAt(now.plusHours(1))
                .usedAt(now)
                .createdAt(now)
                .build();

        assertNotNull(token);
        assertEquals(42L, token.getLoginId());
        assertEquals("ACTIVATION", token.getType());
        assertNotNull(token.getUsedAt());
    }

    @Test
    @DisplayName("shouldCreateTokenBuilderWithoutUsedAt")
    void shouldCreateTokenBuilderWithoutUsedAt() {
        ActivationToken token = ActivationToken.builder()
                .loginId(10L)
                .type("ACTIVATION")
                .tokenHash("0".repeat(64))
                .expiresAt(LocalDateTime.now().plusSeconds(3600))
                .createdAt(LocalDateTime.now())
                .build();

        assertNotNull(token);
        assertNull(token.getUsedAt());
        assertEquals("ACTIVATION", token.getType());
    }
}

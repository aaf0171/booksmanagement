package com.books.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenGeneratorTest {

    private JwtTokenGenerator jwtTokenGenerator;

    @BeforeEach
    void setUp() {
        String secret = "aVeryLongSecretKeyForJwtSigningPurposesMustBeAtLeast32Chars";
        jwtTokenGenerator = new JwtTokenGenerator(secret, 900);
    }

    @Test
    @DisplayName("shouldGenerateJwtTokenWithCorrectClaims")
    void shouldGenerateJwtTokenWithCorrectClaims() {
        String token = jwtTokenGenerator.generate(12345L, "jc.dusse", java.util.List.of("BORROWER"));

        assertNotNull(token);
        assertTrue(token.startsWith("eyJ"));

        var claims = jwtTokenGenerator.parseClaims(token);

        assertEquals("12345", claims.getSubject());
        assertEquals("jc.dusse", claims.get("login", String.class));
        @SuppressWarnings("unchecked")
        java.util.List<String> roles = claims.get("roles", java.util.List.class);
        assertEquals(java.util.List.of("BORROWER"), roles);
    }

    @Test
    @DisplayName("shouldValidateJwtSignature")
    void shouldValidateJwtSignature() {
        String token = jwtTokenGenerator.generate(1L, "testuser", java.util.List.of("ADMIN"));

        assertDoesNotThrow(() -> jwtTokenGenerator.parseClaims(token));
    }

    @Test
    @DisplayName("shouldRejectJwtWithWrongSignature")
    void shouldRejectJwtWithWrongSignature() {
        JwtTokenGenerator otherGenerator = new JwtTokenGenerator("DifferentSecretKey12345678901234567890", 900);
        String validToken = otherGenerator.generate(1L, "testuser", java.util.List.of("ADMIN"));

        assertThrows(Exception.class, () -> jwtTokenGenerator.parseClaims(validToken));
    }

    @Test
    @DisplayName("shouldRejectExpiredJwt")
    void shouldRejectExpiredJwt() {
        JwtTokenGenerator expiredGenerator = new JwtTokenGenerator(
                "aVeryLongSecretKeyForJwtSigningPurposesMustBeAtLeast32Chars", 1);

        String token = expiredGenerator.generate(1L, "testuser", java.util.List.of("BORROWER"));

        try {
            Thread.sleep(1100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        assertTrue(expiredGenerator.isTokenExpired(token));
    }

    @Test
    @DisplayName("shouldGenerateUniqueTokens")
    void shouldGenerateUniqueTokens() {
        String token1 = jwtTokenGenerator.generate(1L, "user1", java.util.List.of("BORROWER"));
        String token2 = jwtTokenGenerator.generate(2L, "user2", java.util.List.of("ADMIN"));

        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("shouldIncludeRolesInToken")
    void shouldIncludeRolesInToken() {
        String token = jwtTokenGenerator.generate(1L, "user", java.util.List.of("ADMIN", "BORROWER"));

        var claims = jwtTokenGenerator.parseClaims(token);
        @SuppressWarnings("unchecked")
        java.util.List<String> roles = claims.get("roles", java.util.List.class);

        assertEquals(2, roles.size());
        assertTrue(roles.contains("ADMIN"));
        assertTrue(roles.contains("BORROWER"));
    }
}

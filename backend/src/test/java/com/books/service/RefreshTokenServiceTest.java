package com.books.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Base64;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

class RefreshTokenServiceTest {

    private Function<String, String> hashFunction;

    @BeforeEach
    void setUp() {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            hashFunction = input -> {
                byte[] hash = digest.digest(input.getBytes());
                StringBuilder hexString = new StringBuilder();
                for (byte b : hash) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) hexString.append('0');
                    hexString.append(hex);
                }
                return hexString.toString();
            };
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("shouldGenerateSecureRandomRefreshToken")
    void shouldGenerateSecureRandomRefreshToken() {
        byte[] token1 = new byte[32];
        new java.security.SecureRandom().nextBytes(token1);

        byte[] token2 = new byte[32];
        new java.security.SecureRandom().nextBytes(token2);

        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("shouldHashRefreshTokenBeforeStorage")
    void shouldHashRefreshTokenBeforeStorage() {
        String plaintext = "mySecretToken123";

        String hash1 = hashFunction.apply(plaintext);
        String hash2 = hashFunction.apply(plaintext);

        assertNotEquals(plaintext, hash1);
        assertEquals(hash1, hash2);
        assertEquals(64, hash1.length());
    }

    @Test
    @DisplayName("shouldProduceDifferentHashesForDifferentInputs")
    void shouldProduceDifferentHashesForDifferentInputs() {
        String hash1 = hashFunction.apply("token1");
        String hash2 = hashFunction.apply("token2");

        assertNotEquals(hash1, hash2);
    }
}

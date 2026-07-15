package com.books.service;

import com.books.repository.RefreshTokenRepository;
import com.books.model.RefreshToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.function.Function;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final Function<String, String> hashFunction;
    private final long expiryDays;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            @Value("${auth.refresh-token.expiry-days:30}") long expiryDays) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.expiryDays = expiryDays;
        this.hashFunction = createSha256Hash();
    }

    private Function<String, String> createSha256Hash() {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            return input -> {
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
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    public String create(Long loginId) {
        byte[] randomBytes = new byte[32];
        new SecureRandom().nextBytes(randomBytes);
        String plaintextToken = Base64.getEncoder().encodeToString(randomBytes);

        String hashedToken = hashFunction.apply(plaintextToken);

        RefreshToken token = RefreshToken.builder()
                .loginId(loginId)
                .tokenHash(hashedToken)
                .expiresAt(LocalDateTime.now().plusDays(expiryDays))
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .build();

        refreshTokenRepository.save(token);

        return plaintextToken;
    }

    public Optional<RefreshToken> findValidToken(String plaintextToken) {
        String hashedToken = hashFunction.apply(plaintextToken);
        return refreshTokenRepository.findByTokenHash(hashedToken)
                .filter(t -> !t.getRevoked())
                .filter(t -> t.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    public boolean revoke(String plaintextToken) {
        String hashedToken = hashFunction.apply(plaintextToken);
        int rows = refreshTokenRepository.revokeByTokenHash(hashedToken);
        return rows > 0;
    }

    public boolean isValid(String plaintextToken) {
        return findValidToken(plaintextToken).isPresent();
    }
}

package com.books.service;

import com.books.dto.ActivationTokenDTO;
import com.books.exception.LoginNotFoundException;
import com.books.exception.LoginValidationException;
import com.books.model.ActivationToken;
import com.books.model.Login;
import com.books.repository.ActivationTokenRepository;
import com.books.repository.LoginsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivationTokenService {

    private static final String TOKEN_TYPE = "ACTIVATION";
    private static final int TOKEN_BYTE_SIZE = 32;

    private final LoginsRepository loginsRepository;
    private final ActivationTokenRepository activationTokenRepository;

    @Value("${activation.token.expiration-seconds:3600}")
    private long expirationSeconds;

    public ActivationTokenDTO generateToken(Long loginId) {
        Login login = loginsRepository.findById(loginId)
                .orElseThrow(() -> new LoginNotFoundException(loginId));

        if (!login.getEnabled()) {
            throw new LoginValidationException("Login is disabled: " + loginId);
        }

        invalidateExistingUnusedTokens(loginId);

        String plaintextToken = generateSecureToken();
        String tokenHash = hashToken(plaintextToken);

        LocalDateTime now = LocalDateTime.now();
        ActivationToken token = ActivationToken.builder()
                .loginId(loginId)
                .type(TOKEN_TYPE)
                .tokenHash(tokenHash)
                .expiresAt(now.plusSeconds(expirationSeconds))
                .createdAt(now)
                .build();

        ActivationToken saved = activationTokenRepository.save(token);

        return ActivationTokenDTO.builder()
                .id(saved.getId())
                .loginId(saved.getLoginId())
                .type(saved.getType())
                .token(plaintextToken)
                .expiresAt(saved.getExpiresAt())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    private void invalidateExistingUnusedTokens(Long loginId) {
        activationTokenRepository.invalidateUnusedTokens(loginId, LocalDateTime.now());
    }

    private String generateSecureToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] tokenBytes = new byte[TOKEN_BYTE_SIZE];
        secureRandom.nextBytes(tokenBytes);
        String base64Token = Base64.getEncoder().encodeToString(tokenBytes);
        return base64Token;
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hashBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash token", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(64);
        for (byte b : bytes) {
            String hex = String.format("%02x", b);
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Transactional
    public boolean validateToken(Long loginId, String token) {
        String tokenHash = hashToken(token);

        Optional<ActivationToken> tokenOpt = activationTokenRepository.findByTokenHash(tokenHash);

        if (tokenOpt.isEmpty()) {
            return false;
        }

        ActivationToken storedToken = tokenOpt.get();

        if (storedToken.getUsedAt() != null) {
            log.warn("Token already used for loginId: {}", loginId);
            return false;
        }

        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            log.warn("Token expired for loginId: {}", loginId);
            return false;
        }

        if (!storedToken.getLoginId().equals(loginId)) {
            log.warn("Token loginId mismatch: expected {}, got {}", loginId, storedToken.getLoginId());
            return false;
        }

        return true;
    }

    @Transactional
    public void markTokenAsUsed(Long tokenId) {
        activationTokenRepository.markAsUsed(tokenId);
    }
}

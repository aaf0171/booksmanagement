package com.books.service;

import com.books.dto.ActivationTokenDTO;
import com.books.dto.ActivationResponseDTO;
import com.books.enums.ActivationStatus;
import com.books.exception.LoginNotFoundException;
import com.books.exception.LoginValidationException;
import com.books.model.ActivationToken;
import com.books.model.Login;
import com.books.repository.ActivationTokenRepository;
import com.books.repository.LoginsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActivationTokenServiceTest {

    @Mock
    private LoginsRepository loginsRepository;

    @Mock
    private ActivationTokenRepository activationTokenRepository;

    @InjectMocks
    private ActivationTokenService activationTokenService;

    private Login activeLogin;
    private Login disabledLogin;
    private ActivationToken existingToken;

    @BeforeEach
    void setUp() {
        activeLogin = Login.builder()
                .id(1L)
                .username("testuser")
                .passwordHash("$2a$10$hash")
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        disabledLogin = Login.builder()
                .id(2L)
                .username("disableduser")
                .passwordHash("$2a$10$hash")
                .enabled(false)
                .createdAt(LocalDateTime.now())
                .build();

        existingToken = ActivationToken.builder()
                .id(1L)
                .loginId(1L)
                .type("ACTIVATION")
                .tokenHash("existinghash")
                .expiresAt(LocalDateTime.now().plusHours(1))
                .usedAt(null)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // === Domain unit tests for findTokenResult ===

    @Test
    @DisplayName("shouldReturnSuccessStatusForValidToken")
    void shouldReturnSuccessStatusForValidToken() {
        String plaintextToken = "validToken123";
        String tokenHash = hashToken(plaintextToken);

        ActivationToken validToken = ActivationToken.builder()
                .id(1L)
                .loginId(1L)
                .type("ACTIVATION")
                .tokenHash(tokenHash)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .usedAt(null)
                .createdAt(LocalDateTime.now())
                .build();

        when(activationTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(validToken));

        ActivationResponseDTO result = activationTokenService.findTokenResult(plaintextToken);

        assertEquals(ActivationStatus.SUCCESS, result.status());
        assertNull(result.email());
    }

    @Test
    @DisplayName("shouldReturnExpiredStatusForExpiredToken")
    void shouldReturnExpiredStatusForExpiredToken() {
        String plaintextToken = "expiredToken123";
        String tokenHash = hashToken(plaintextToken);

        ActivationToken expiredToken = ActivationToken.builder()
                .id(1L)
                .loginId(1L)
                .type("ACTIVATION")
                .tokenHash(tokenHash)
                .expiresAt(LocalDateTime.now().minusHours(1))
                .usedAt(null)
                .createdAt(LocalDateTime.now().minusHours(2))
                .build();

        when(activationTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(expiredToken));
        when(loginsRepository.findById(1L)).thenReturn(Optional.of(activeLogin));

        ActivationResponseDTO result = activationTokenService.findTokenResult(plaintextToken);

        assertEquals(ActivationStatus.TOKEN_EXPIRED, result.status());
    }

    @Test
    @DisplayName("shouldReturnInvalidStatusForUnknownToken")
    void shouldReturnInvalidStatusForUnknownToken() {
        String plaintextToken = "unknownToken123";
        String tokenHash = hashToken(plaintextToken);

        when(activationTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.empty());

        ActivationResponseDTO result = activationTokenService.findTokenResult(plaintextToken);

        assertEquals(ActivationStatus.TOKEN_INVALID, result.status());
        assertNull(result.email());
    }

    @Test
    @DisplayName("shouldReturnAlreadyActivatedStatusForUsedTokenWithEnabledLogin")
    void shouldReturnAlreadyActivatedStatusForUsedTokenWithEnabledLogin() {
        String plaintextToken = "usedToken123";
        String tokenHash = hashToken(plaintextToken);

        ActivationToken usedToken = ActivationToken.builder()
                .id(1L)
                .loginId(1L)
                .type("ACTIVATION")
                .tokenHash(tokenHash)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .usedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now().minusHours(1))
                .build();

        when(activationTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(usedToken));
        when(loginsRepository.findById(1L)).thenReturn(Optional.of(activeLogin));

        ActivationResponseDTO result = activationTokenService.findTokenResult(plaintextToken);

        assertEquals(ActivationStatus.ALREADY_ACTIVATED, result.status());
        assertNull(result.email());
    }

    @Test
    @DisplayName("shouldReturnInvalidStatusForUsedTokenWithDisabledLogin")
    void shouldReturnInvalidStatusForUsedTokenWithDisabledLogin() {
        String plaintextToken = "usedToken123";
        String tokenHash = hashToken(plaintextToken);

        ActivationToken usedToken = ActivationToken.builder()
                .id(1L)
                .loginId(2L)
                .type("ACTIVATION")
                .tokenHash(tokenHash)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .usedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now().minusHours(1))
                .build();

        when(activationTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(usedToken));
        when(loginsRepository.findById(2L)).thenReturn(Optional.of(disabledLogin));

        ActivationResponseDTO result = activationTokenService.findTokenResult(plaintextToken);

        assertEquals(ActivationStatus.TOKEN_INVALID, result.status());
        assertNull(result.email());
    }

    @Test
    @DisplayName("shouldNotExposeEmailForInvalidToken")
    void shouldNotExposeEmailForInvalidToken() {
        String plaintextToken = "unknownToken123";
        String tokenHash = hashToken(plaintextToken);

        when(activationTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.empty());

        ActivationResponseDTO result = activationTokenService.findTokenResult(plaintextToken);

        assertEquals(ActivationStatus.TOKEN_INVALID, result.status());
        assertNull(result.email());
    }

    @Test
    @DisplayName("shouldExposeEmailForExpiredTokenWithKnownUser")
    void shouldExposeEmailForExpiredTokenWithKnownUser() {
        String plaintextToken = "expiredToken123";
        String tokenHash = hashToken(plaintextToken);

        Login loginWithEmail = Login.builder()
                .id(1L)
                .username("utilisateur@example.com")
                .passwordHash("$2a$10$hash")
                .enabled(false)
                .createdAt(LocalDateTime.now())
                .build();

        ActivationToken expiredToken = ActivationToken.builder()
                .id(1L)
                .loginId(1L)
                .type("ACTIVATION")
                .tokenHash(tokenHash)
                .expiresAt(LocalDateTime.now().minusHours(1))
                .usedAt(null)
                .createdAt(LocalDateTime.now().minusHours(2))
                .build();

        when(activationTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(expiredToken));
        when(loginsRepository.findById(1L)).thenReturn(Optional.of(loginWithEmail));

        ActivationResponseDTO result = activationTokenService.findTokenResult(plaintextToken);

        assertEquals(ActivationStatus.TOKEN_EXPIRED, result.status());
        assertEquals("utilisateur@example.com", result.email());
    }

    @Test
    @DisplayName("shouldNotExposeEmailForExpiredTokenWithoutUser")
    void shouldNotExposeEmailForExpiredTokenWithoutUser() {
        String plaintextToken = "expiredToken123";
        String tokenHash = hashToken(plaintextToken);

        ActivationToken expiredToken = ActivationToken.builder()
                .id(1L)
                .loginId(1L)
                .type("ACTIVATION")
                .tokenHash(tokenHash)
                .expiresAt(LocalDateTime.now().minusHours(1))
                .usedAt(null)
                .createdAt(LocalDateTime.now().minusHours(2))
                .build();

        when(activationTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(expiredToken));
        when(loginsRepository.findById(1L)).thenReturn(Optional.empty());

        ActivationResponseDTO result = activationTokenService.findTokenResult(plaintextToken);

        assertEquals(ActivationStatus.TOKEN_INVALID, result.status());
        assertNull(result.email());
    }

    // === Legacy tests (preserve existing functionality) ===

    @Test
    @DisplayName("shouldGenerateToken")
    void shouldGenerateToken() throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        when(loginsRepository.findById(1L)).thenReturn(Optional.of(activeLogin));
        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            LocalDateTime now = invocation.getArgument(1);
            return 1;
        }).when(activationTokenRepository).invalidateUnusedTokens(anyLong(), any(LocalDateTime.class));

        when(activationTokenRepository.save(any(ActivationToken.class))).thenAnswer(invocation -> {
            ActivationToken saved = invocation.getArgument(0);
            saved.setId(10L);
            return saved;
        });

        ActivationTokenDTO result = activationTokenService.generateToken(1L);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(1L, result.getLoginId());
        assertEquals("ACTIVATION", result.getType());
        assertNotNull(result.getToken());
        assertEquals(44, result.getToken().length());
        assertNotNull(result.getExpiresAt());
        assertNotNull(result.getCreatedAt());
        verify(activationTokenRepository).save(any(ActivationToken.class));
    }

    @Test
    @DisplayName("shouldReturnNotFoundWhenLoginDoesNotExist")
    void shouldReturnNotFoundWhenLoginDoesNotExist() {
        when(loginsRepository.findById(999L)).thenReturn(Optional.empty());

        LoginNotFoundException ex = assertThrows(LoginNotFoundException.class,
                () -> activationTokenService.generateToken(999L));

        assertEquals("Login not found with id: 999", ex.getMessage());
        verify(activationTokenRepository, never()).save(any());
        verify(activationTokenRepository, never()).invalidateUnusedTokens(anyLong(), any());
    }

    @Test
    @DisplayName("shouldInvalidatePreviousUnusedToken")
    void shouldInvalidatePreviousUnusedToken() throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        when(loginsRepository.findById(1L)).thenReturn(Optional.of(activeLogin));
        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            LocalDateTime now = invocation.getArgument(1);
            return 1;
        }).when(activationTokenRepository).invalidateUnusedTokens(anyLong(), any(LocalDateTime.class));

        when(activationTokenRepository.save(any(ActivationToken.class))).thenAnswer(invocation -> {
            ActivationToken saved = invocation.getArgument(0);
            saved.setId(11L);
            return saved;
        });

        ActivationTokenDTO result = activationTokenService.generateToken(1L);

        assertNotNull(result);
        verify(activationTokenRepository).invalidateUnusedTokens(eq(1L), any(LocalDateTime.class));
        verify(activationTokenRepository).save(any(ActivationToken.class));
    }

    @Test
    @DisplayName("shouldReturnPlaintextTokenOnce")
    void shouldReturnPlaintextTokenOnce() throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        when(loginsRepository.findById(1L)).thenReturn(Optional.of(activeLogin));
        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            LocalDateTime now = invocation.getArgument(1);
            return 1;
        }).when(activationTokenRepository).invalidateUnusedTokens(anyLong(), any(LocalDateTime.class));
        when(activationTokenRepository.save(any(ActivationToken.class))).thenAnswer(invocation -> {
            ActivationToken saved = invocation.getArgument(0);
            saved.setId(12L);
            return saved;
        });

        ActivationTokenDTO result = activationTokenService.generateToken(1L);

        assertNotNull(result.getToken());
        assertFalse(result.getToken().isEmpty());
        assertEquals(44, result.getToken().length());
    }

    @Test
    @DisplayName("shouldValidateToken_success")
    void shouldValidateToken_success() throws NoSuchAlgorithmException {
        String plaintextToken = "aBcDeFgHiJkLmNoPqRsTuVwXyZ0123456789_-aB==";

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(plaintextToken.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(64);
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }
        String tokenHash = hexString.toString();

        ActivationToken storedToken = ActivationToken.builder()
                .id(1L)
                .loginId(1L)
                .type("ACTIVATION")
                .tokenHash(tokenHash)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .usedAt(null)
                .createdAt(LocalDateTime.now())
                .build();

        when(activationTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(storedToken));

        boolean isValid = activationTokenService.validateToken(1L, plaintextToken);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("shouldRejectExpiredToken")
    void shouldRejectExpiredToken() throws NoSuchAlgorithmException {
        String plaintextToken = "aBcDeFgHiJkLmNoPqRsTuVwXyZ0123456789_-aB==";

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(plaintextToken.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(64);
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }
        String tokenHash = hexString.toString();

        ActivationToken expiredToken = ActivationToken.builder()
                .id(1L)
                .loginId(1L)
                .type("ACTIVATION")
                .tokenHash(tokenHash)
                .expiresAt(LocalDateTime.now().minusHours(1))
                .usedAt(null)
                .createdAt(LocalDateTime.now().minusHours(2))
                .build();

        when(activationTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(expiredToken));

        boolean isValid = activationTokenService.validateToken(1L, plaintextToken);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("shouldRejectUsedToken")
    void shouldRejectUsedToken() throws NoSuchAlgorithmException {
        String plaintextToken = "aBcDeFgHiJkLmNoPqRsTuVwXyZ0123456789_-aB==";

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(plaintextToken.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(64);
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }
        String tokenHash = hexString.toString();

        ActivationToken usedToken = ActivationToken.builder()
                .id(1L)
                .loginId(1L)
                .type("ACTIVATION")
                .tokenHash(tokenHash)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .usedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now().minusHours(1))
                .build();

        when(activationTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(usedToken));

        boolean isValid = activationTokenService.validateToken(1L, plaintextToken);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("shouldRejectNonExistentToken")
    void shouldRejectNonExistentToken() throws NoSuchAlgorithmException {
        String plaintextToken = "aBcDeFgHiJkLmNoPqRsTuVwXyZ0123456789_-aB==";

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(plaintextToken.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(64);
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }
        String tokenHash = hexString.toString();

        when(activationTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.empty());

        boolean isValid = activationTokenService.validateToken(1L, plaintextToken);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("shouldMarkTokenAsUsed")
    void shouldMarkTokenAsUsed() {
        activationTokenService.markTokenAsUsed(1L);

        verify(activationTokenRepository).markAsUsed(1L);
    }

    @Test
    @DisplayName("shouldHashTokenCorrectly")
    void shouldHashTokenCorrectly() throws NoSuchAlgorithmException {
        String plaintextToken = "testToken123";

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(plaintextToken.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(64);
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }
        String expectedHash = hexString.toString();

        when(activationTokenRepository.findByTokenHash(expectedHash)).thenReturn(Optional.of(existingToken));

        java.util.Optional<ActivationToken> result = activationTokenService.findValidToken(plaintextToken);

        assertEquals(64, expectedHash.length());
        verify(activationTokenRepository).findByTokenHash(expectedHash);
    }

    @Test
    @DisplayName("shouldFindValidTokenByHash")
    void shouldFindValidTokenByHash() throws NoSuchAlgorithmException {
        String plaintextToken = "validToken123";

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(plaintextToken.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(64);
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }
        String tokenHash = hexString.toString();

        ActivationToken validToken = ActivationToken.builder()
                .id(1L)
                .loginId(1L)
                .type("ACTIVATION")
                .tokenHash(tokenHash)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .usedAt(null)
                .createdAt(LocalDateTime.now())
                .build();

        when(activationTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(validToken));

        java.util.Optional<ActivationToken> result = activationTokenService.findValidToken(plaintextToken);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals(1L, result.get().getLoginId());
    }

    @Test
    @DisplayName("shouldReturnEmptyForInvalidToken")
    void shouldReturnEmptyForInvalidToken() throws NoSuchAlgorithmException {
        String plaintextToken = "nonExistentToken";

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(plaintextToken.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(64);
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }
        String tokenHash = hexString.toString();

        when(activationTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.empty());

        java.util.Optional<ActivationToken> result = activationTokenService.findValidToken(plaintextToken);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("shouldReturnEmptyForExpiredToken")
    void shouldReturnEmptyForExpiredToken() throws NoSuchAlgorithmException {
        String plaintextToken = "expiredToken123";

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(plaintextToken.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(64);
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }
        String tokenHash = hexString.toString();

        ActivationToken expiredToken = ActivationToken.builder()
                .id(2L)
                .loginId(1L)
                .type("ACTIVATION")
                .tokenHash(tokenHash)
                .expiresAt(LocalDateTime.now().minusHours(1))
                .usedAt(null)
                .createdAt(LocalDateTime.now().minusHours(2))
                .build();

        when(activationTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(expiredToken));

        java.util.Optional<ActivationToken> result = activationTokenService.findValidToken(plaintextToken);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("shouldReturnEmptyForUsedToken")
    void shouldReturnEmptyForUsedToken() throws NoSuchAlgorithmException {
        String plaintextToken = "usedToken123";

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(plaintextToken.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(64);
        for (byte b : hashBytes) {
            hexString.append(String.format("%02x", b));
        }
        String tokenHash = hexString.toString();

        ActivationToken usedToken = ActivationToken.builder()
                .id(3L)
                .loginId(1L)
                .type("ACTIVATION")
                .tokenHash(tokenHash)
                .expiresAt(LocalDateTime.now().plusHours(1))
                .usedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now().minusHours(1))
                .build();

        when(activationTokenRepository.findByTokenHash(tokenHash)).thenReturn(Optional.of(usedToken));

        java.util.Optional<ActivationToken> result = activationTokenService.findValidToken(plaintextToken);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("shouldCreateNewTokenWhenExpiredTokenExists")
    void shouldCreateNewTokenWhenExpiredTokenExists() throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        Login login = Login.builder()
                .id(3L)
                .username("expireuser")
                .passwordHash("$2a$10$hash")
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        ActivationToken expiredToken = ActivationToken.builder()
                .id(2L)
                .loginId(3L)
                .type("ACTIVATION")
                .tokenHash("expiredhash")
                .expiresAt(LocalDateTime.now().minusHours(1))
                .usedAt(null)
                .createdAt(LocalDateTime.now().minusHours(2))
                .build();

        when(loginsRepository.findById(3L)).thenReturn(Optional.of(login));
        doAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            LocalDateTime now = invocation.getArgument(1);
            return 0;
        }).when(activationTokenRepository).invalidateUnusedTokens(anyLong(), any(LocalDateTime.class));
        when(activationTokenRepository.save(any(ActivationToken.class))).thenAnswer(invocation -> {
            ActivationToken saved = invocation.getArgument(0);
            saved.setId(13L);
            return saved;
        });

        ActivationTokenDTO result = activationTokenService.generateToken(3L);

        assertNotNull(result);
        assertEquals(13L, result.getId());
        verify(activationTokenRepository).invalidateUnusedTokens(eq(3L), any(LocalDateTime.class));
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(64);
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

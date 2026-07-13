package com.books.service;

import com.books.dto.ActivationTokenDTO;
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
}

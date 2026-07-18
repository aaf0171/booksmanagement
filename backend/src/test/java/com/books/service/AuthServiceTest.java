package com.books.service;

import com.books.dto.ActivationResponseDTO;
import com.books.dto.LoginRequestDTO;
import com.books.dto.LoginResponseDTO;
import com.books.dto.RefreshRequestDTO;
import com.books.dto.RefreshResponseDTO;
import com.books.enums.ActivationStatus;
import com.books.exception.ActivationTokenException;
import com.books.exception.LoginNotFoundException;
import com.books.exception.LoginValidationException;
import com.books.exception.PasswordMismatchException;
import com.books.model.ActivationToken;
import com.books.model.Login;
import com.books.model.Role;
import com.books.repository.LoginsRepository;
import com.books.repository.RoleRepository;
import com.books.service.ActivationTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceTest {

    @Mock
    private LoginsRepository loginsRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenGenerator jwtTokenGenerator;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private ActivationTokenService activationTokenService;

    @InjectMocks
    private AuthService authService;

    private Login activeLogin;

    @BeforeEach
    void setUp() {
        authService.setAccessTokenExpirySecondsForTest(900);

        Role borrower = Role.builder().id(1L).name("BORROWER").build();

        activeLogin = Login.builder()
                .id(1L)
                .username("jc.dusse")
                .passwordHash("$2a$10$hashedpassword")
                .enabled(true)
                .roles(Set.of(borrower))
                .lastLogin(null)
                .createdAt(LocalDateTime.now())
                .build();
    }

    // === Application service tests ===

    @Test
    @DisplayName("shouldActivateAccountSuccessfullyAndReturnSuccess")
    void shouldActivateAccountSuccessfullyAndReturnSuccess() {
        ActivationToken activationToken = ActivationToken.builder()
                .id(1L)
                .loginId(1L)
                .type("ACTIVATION")
                .tokenHash("tokenhash123")
                .expiresAt(LocalDateTime.now().plusHours(1))
                .usedAt(null)
                .createdAt(LocalDateTime.now())
                .build();

        ActivationResponseDTO validResult = new ActivationResponseDTO(ActivationStatus.SUCCESS, "Token is valid", null);
        when(activationTokenService.findTokenResult("valid-token")).thenReturn(validResult);
        when(activationTokenService.findValidToken("valid-token")).thenReturn(Optional.of(activationToken));
        when(loginsRepository.findById(1L)).thenReturn(Optional.of(activeLogin));
        when(passwordEncoder.encode("passWord123")).thenReturn("encodedhash");

        ActivationResponseDTO result = authService.activate("valid-token", "passWord123", "passWord123");

        assertEquals(ActivationStatus.SUCCESS, result.status());
        assertEquals("Account activated successfully", result.message());
        assertNull(result.email());
        verify(loginsRepository).save(activeLogin);
        verify(activationTokenService).markTokenAsUsed(1L);
    }

    @Test
    @DisplayName("shouldSetLoginEnabledToTrueOnActivation")
    void shouldSetLoginEnabledToTrueOnActivation() {
        Login disabledLogin = Login.builder()
                .id(1L)
                .username("jc.dusse")
                .passwordHash("$2a$10$hashedpassword")
                .enabled(false)
                .roles(Set.of(Role.builder().id(1L).name("BORROWER").build()))
                .lastLogin(null)
                .createdAt(LocalDateTime.now())
                .build();

        ActivationToken activationToken = ActivationToken.builder()
                .id(1L)
                .loginId(1L)
                .type("ACTIVATION")
                .tokenHash("tokenhash123")
                .expiresAt(LocalDateTime.now().plusHours(1))
                .usedAt(null)
                .createdAt(LocalDateTime.now())
                .build();

        ActivationResponseDTO validResult = new ActivationResponseDTO(ActivationStatus.SUCCESS, "Token is valid", null);
        when(activationTokenService.findTokenResult("valid-token")).thenReturn(validResult);
        when(activationTokenService.findValidToken("valid-token")).thenReturn(Optional.of(activationToken));
        when(loginsRepository.findById(1L)).thenReturn(Optional.of(disabledLogin));
        when(passwordEncoder.encode("passWord123")).thenReturn("encodedhash");

        authService.activate("valid-token", "passWord123", "passWord123");

        assertTrue(disabledLogin.getEnabled());
        verify(loginsRepository).save(disabledLogin);
    }

    @Test
    @DisplayName("shouldMarkTokenAsUsedAfterActivation")
    void shouldMarkTokenAsUsedAfterActivation() {
        ActivationToken activationToken = ActivationToken.builder()
                .id(1L)
                .loginId(1L)
                .type("ACTIVATION")
                .tokenHash("tokenhash123")
                .expiresAt(LocalDateTime.now().plusHours(1))
                .usedAt(null)
                .createdAt(LocalDateTime.now())
                .build();

        ActivationResponseDTO validResult = new ActivationResponseDTO(ActivationStatus.SUCCESS, "Token is valid", null);
        when(activationTokenService.findTokenResult("valid-token")).thenReturn(validResult);
        when(activationTokenService.findValidToken("valid-token")).thenReturn(Optional.of(activationToken));
        when(loginsRepository.findById(1L)).thenReturn(Optional.of(activeLogin));
        when(passwordEncoder.encode("passWord123")).thenReturn("encodedhash");

        authService.activate("valid-token", "passWord123", "passWord123");

        verify(activationTokenService).markTokenAsUsed(1L);
    }

    @Test
    @DisplayName("shouldReturnTokenExpiredWithEmailWhenTokenIsExpired")
    void shouldReturnTokenExpiredWithEmailWhenTokenIsExpired() {
        Login loginWithEmail = Login.builder()
                .id(1L)
                .username("utilisateur@example.com")
                .passwordHash("$2a$10$hash")
                .enabled(false)
                .createdAt(LocalDateTime.now())
                .build();

        ActivationResponseDTO expiredResult = new ActivationResponseDTO(ActivationStatus.TOKEN_EXPIRED, null, "utilisateur@example.com");
        when(activationTokenService.findTokenResult("expired-token")).thenReturn(expiredResult);

        ActivationResponseDTO result = authService.activate("expired-token", null, null);

        assertEquals(ActivationStatus.TOKEN_EXPIRED, result.status());
        assertEquals("utilisateur@example.com", result.email());
        verify(loginsRepository, never()).save(any());
        verify(activationTokenService, never()).markTokenAsUsed(anyLong());
    }

    @Test
    @DisplayName("shouldReturnTokenInvalidWithoutEmailWhenTokenDoesNotExist")
    void shouldReturnTokenInvalidWithoutEmailWhenTokenDoesNotExist() {
        ActivationResponseDTO invalidResult = new ActivationResponseDTO(ActivationStatus.TOKEN_INVALID, null, null);
        when(activationTokenService.findTokenResult("invalid-token")).thenReturn(invalidResult);

        ActivationResponseDTO result = authService.activate("invalid-token", "passWord123", "passWord123");

        assertEquals(ActivationStatus.TOKEN_INVALID, result.status());
        assertNull(result.email());
        verify(loginsRepository, never()).save(any());
        verify(activationTokenService, never()).markTokenAsUsed(anyLong());
    }

    @Test
    @DisplayName("shouldReturnAlreadyActivatedWhenTokenIsUsedAndLoginEnabled")
    void shouldReturnAlreadyActivatedWhenTokenIsUsedAndLoginEnabled() {
        ActivationResponseDTO alreadyActivatedResult = new ActivationResponseDTO(ActivationStatus.ALREADY_ACTIVATED, null, null);
        when(activationTokenService.findTokenResult("used-token")).thenReturn(alreadyActivatedResult);

        ActivationResponseDTO result = authService.activate("used-token", null, null);

        assertEquals(ActivationStatus.ALREADY_ACTIVATED, result.status());
        assertNull(result.email());
        verify(loginsRepository, never()).save(any());
        verify(activationTokenService, never()).markTokenAsUsed(anyLong());
    }

    @Test
    @DisplayName("shouldReturnTokenInvalidWhenTokenIsUsedAndLoginDisabled")
    void shouldReturnTokenInvalidWhenTokenIsUsedAndLoginDisabled() {
        ActivationResponseDTO invalidResult = new ActivationResponseDTO(ActivationStatus.TOKEN_INVALID, null, null);
        when(activationTokenService.findTokenResult("used-token")).thenReturn(invalidResult);

        ActivationResponseDTO result = authService.activate("used-token", null, null);

        assertEquals(ActivationStatus.TOKEN_INVALID, result.status());
        assertNull(result.email());
    }

    @Test
    @DisplayName("shouldThrowOnlyForTechnicalErrors")
    void shouldThrowOnlyForTechnicalErrors() {
        ActivationToken activationToken = ActivationToken.builder()
                .id(1L)
                .loginId(999L)
                .type("ACTIVATION")
                .tokenHash("tokenhash123")
                .expiresAt(LocalDateTime.now().plusHours(1))
                .usedAt(null)
                .createdAt(LocalDateTime.now())
                .build();

        ActivationResponseDTO validResult = new ActivationResponseDTO(ActivationStatus.SUCCESS, "Token is valid", null);
        when(activationTokenService.findTokenResult("valid-token")).thenReturn(validResult);
        when(activationTokenService.findValidToken("valid-token")).thenReturn(Optional.of(activationToken));
        when(loginsRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ActivationTokenException.class,
                () -> authService.activate("valid-token", "passWord123", "passWord123"));
    }

    // === Legacy tests (preserve existing functionality) ===

    @Test
    @DisplayName("shouldLoginSuccessfullyAndReturnBothTokens")
    void shouldLoginSuccessfullyAndReturnBothTokens() {
        LoginRequestDTO request = new LoginRequestDTO("jc.dusse", "secret");

        when(loginsRepository.findByUsername("jc.dusse")).thenReturn(Optional.of(activeLogin));
        when(passwordEncoder.matches("secret", "$2a$10$hashedpassword")).thenReturn(true);
        when(jwtTokenGenerator.generate(1L, "jc.dusse", List.of("BORROWER"))).thenReturn("access-token-xyz");
        when(refreshTokenService.create(1L)).thenReturn("refresh-token-abc");

        LoginResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertEquals("access-token-xyz", response.getAccessToken());
        assertEquals("refresh-token-abc", response.getRefreshToken());
        assertNotNull(response.getExpiresIn());
        assertTrue(response.getExpiresIn() > 0);
        verify(loginsRepository).save(activeLogin);
    }

    @Test
    @DisplayName("shouldReturn401WhenCredentialsAreInvalid")
    void shouldReturn401WhenCredentialsAreInvalid() {
        LoginRequestDTO request = new LoginRequestDTO("jc.dusse", "wrongpassword");

        when(loginsRepository.findByUsername("jc.dusse")).thenReturn(Optional.of(activeLogin));
        when(passwordEncoder.matches("wrongpassword", "$2a$10$hashedpassword")).thenReturn(false);

        assertThrows(LoginNotFoundException.class, () -> authService.login(request));
        verify(loginsRepository, never()).save(any());
    }

    @Test
    @DisplayName("shouldReturn401WhenAccountIsNotActivated")
    void shouldReturn401WhenAccountIsNotActivated() {
        Role borrower = Role.builder().id(1L).name("BORROWER").build();
        Login disabledLogin = Login.builder()
                .id(1L)
                .username("jc.dusse")
                .passwordHash("$2a$10$hashedpassword")
                .enabled(false)
                .roles(Set.of(borrower))
                .build();

        LoginRequestDTO request = new LoginRequestDTO("jc.dusse", "secret");

        when(loginsRepository.findByUsername("jc.dusse")).thenReturn(Optional.of(disabledLogin));

        assertThrows(LoginNotFoundException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("shouldReturn401WhenUserNotFound")
    void shouldReturn401WhenUserNotFound() {
        LoginRequestDTO request = new LoginRequestDTO("nonexistent", "secret");

        when(loginsRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(LoginNotFoundException.class, () -> authService.login(request));
    }

    @Test
    @DisplayName("shouldRefreshAccessTokenWithValidRefreshToken")
    void shouldRefreshAccessTokenWithValidRefreshToken() {
        var tokenEntity = com.books.model.RefreshToken.builder()
                .id(1L)
                .loginId(1L)
                .tokenHash("hash")
                .expiresAt(LocalDateTime.now().plusDays(10))
                .revoked(false)
                .build();

        when(refreshTokenService.isValid("refresh-token")).thenReturn(true);
        when(refreshTokenService.findValidToken("refresh-token")).thenReturn(Optional.of(tokenEntity));
        when(loginsRepository.findById(1L)).thenReturn(Optional.of(activeLogin));
        when(jwtTokenGenerator.generate(1L, "jc.dusse", List.of("BORROWER"))).thenReturn("new-access-token");

        RefreshResponseDTO response = authService.refresh("refresh-token");

        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
    }

    @Test
    @DisplayName("shouldRejectExpiredRefreshToken")
    void shouldRejectExpiredRefreshToken() {
        when(refreshTokenService.isValid("expired-token")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.refresh("expired-token"));
    }

    @Test
    @DisplayName("shouldRejectRevokedRefreshToken")
    void shouldRejectRevokedRefreshToken() {
        when(refreshTokenService.isValid("revoked-token")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.refresh("revoked-token"));
    }

    @Test
    @DisplayName("shouldRejectInvalidRefreshToken")
    void shouldRejectInvalidRefreshToken() {
        when(refreshTokenService.isValid("invalid-token")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.refresh("invalid-token"));
    }

    @Test
    @DisplayName("shouldLogoutAndRevokeRefreshToken")
    void shouldLogoutAndRevokeRefreshToken() {
        when(refreshTokenService.isValid("refresh-token")).thenReturn(true);

        assertDoesNotThrow(() -> authService.logout("refresh-token"));

        verify(refreshTokenService).revoke("refresh-token");
    }

    @Test
    @DisplayName("shouldNotReuseRevokedRefreshToken")
    void shouldNotReuseRevokedRefreshToken() {
        var tokenEntity = com.books.model.RefreshToken.builder()
                .id(1L)
                .loginId(1L)
                .tokenHash("hash")
                .expiresAt(LocalDateTime.now().plusDays(10))
                .revoked(false)
                .build();

        when(refreshTokenService.isValid("refresh-token")).thenReturn(true);
        when(refreshTokenService.findValidToken("refresh-token")).thenReturn(Optional.of(tokenEntity));
        when(loginsRepository.findById(1L)).thenReturn(Optional.of(activeLogin));
        when(jwtTokenGenerator.generate(1L, "jc.dusse", List.of("BORROWER"))).thenReturn("new-token");

        authService.refresh("refresh-token");

        when(refreshTokenService.isValid("refresh-token")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> authService.refresh("refresh-token"));
    }

    @Test
    @DisplayName("shouldThrowWhenPasswordsDoNotMatch")
    void shouldThrowWhenPasswordsDoNotMatch() {
        ActivationToken activationToken = ActivationToken.builder()
                .id(1L)
                .loginId(1L)
                .type("ACTIVATION")
                .tokenHash("tokenhash123")
                .expiresAt(LocalDateTime.now().plusHours(1))
                .usedAt(null)
                .createdAt(LocalDateTime.now())
                .build();

        ActivationResponseDTO validResult = new ActivationResponseDTO(ActivationStatus.SUCCESS, "Token is valid", null);
        when(activationTokenService.findTokenResult("valid-token")).thenReturn(validResult);
        when(activationTokenService.findValidToken("valid-token")).thenReturn(Optional.of(activationToken));
        when(loginsRepository.findById(1L)).thenReturn(Optional.of(activeLogin));

        assertThrows(PasswordMismatchException.class, () -> authService.activate("valid-token", "password1", "password2"));
    }

    @Test
    @DisplayName("shouldActivateAccountWithMatchingPasswords")
    void shouldActivateAccountWithMatchingPasswords() {
        ActivationToken activationToken = ActivationToken.builder()
                .id(1L)
                .loginId(1L)
                .type("ACTIVATION")
                .tokenHash("tokenhash123")
                .expiresAt(LocalDateTime.now().plusHours(1))
                .usedAt(null)
                .createdAt(LocalDateTime.now())
                .build();

        ActivationResponseDTO validResult = new ActivationResponseDTO(ActivationStatus.SUCCESS, "Token is valid", null);
        when(activationTokenService.findTokenResult("valid-token")).thenReturn(validResult);
        when(activationTokenService.findValidToken("valid-token")).thenReturn(Optional.of(activationToken));
        when(loginsRepository.findById(1L)).thenReturn(Optional.of(activeLogin));
        when(passwordEncoder.encode("mypassword")).thenReturn("encodedhash");

        ActivationResponseDTO result = authService.activate("valid-token", "mypassword", "mypassword");

        assertEquals(ActivationStatus.SUCCESS, result.status());
        assertEquals("Account activated successfully", result.message());
        verify(passwordEncoder).encode("mypassword");
    }
}

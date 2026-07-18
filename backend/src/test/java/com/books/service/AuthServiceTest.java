package com.books.service;

import com.books.dto.LoginRequestDTO;
import com.books.dto.LoginResponseDTO;
import com.books.dto.RefreshRequestDTO;
import com.books.dto.RefreshResponseDTO;
import com.books.exception.ActivationTokenException;
import com.books.exception.LoginNotFoundException;
import com.books.exception.LoginValidationException;
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
    @DisplayName("shouldActivateAccountWithValidToken")
    void shouldActivateAccountWithValidToken() {
        ActivationToken activationToken = ActivationToken.builder()
                .id(1L)
                .loginId(1L)
                .type("ACTIVATION")
                .tokenHash("tokenhash123")
                .expiresAt(LocalDateTime.now().plusHours(1))
                .usedAt(null)
                .createdAt(LocalDateTime.now())
                .build();

        when(activationTokenService.findValidToken("valid-token")).thenReturn(Optional.of(activationToken));
        when(loginsRepository.findById(1L)).thenReturn(Optional.of(activeLogin));

        assertDoesNotThrow(() -> authService.activate("valid-token", "passWord123", "passWord123"));
    }

    @Test
    @DisplayName("shouldSetLoginEnabledToTrue")
    void shouldSetLoginEnabledToTrue() {
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

        when(activationTokenService.findValidToken("valid-token")).thenReturn(Optional.of(activationToken));
        when(loginsRepository.findById(1L)).thenReturn(Optional.of(disabledLogin));

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

        when(activationTokenService.findValidToken("valid-token")).thenReturn(Optional.of(activationToken));
        when(loginsRepository.findById(1L)).thenReturn(Optional.of(activeLogin));

        authService.activate("valid-token", "passWord123", "passWord123");

        verify(activationTokenService).markTokenAsUsed(1L);
    }

    @Test
    @DisplayName("shouldReturn400WhenTokenIsInvalid")
    void shouldReturn400WhenTokenIsInvalid() {
        when(activationTokenService.findValidToken("invalid-token")).thenReturn(Optional.empty());

        assertThrows(ActivationTokenException.class, 
            () -> authService.activate("invalid-token", "passWord123", "passWord123"));
    }

    @Test
    @DisplayName("shouldReturn400WhenTokenIsExpired")
    void shouldReturn400WhenTokenIsExpired() {
        when(activationTokenService.findValidToken("expired-token")).thenReturn(Optional.empty());

        assertThrows(ActivationTokenException.class, 
            () -> authService.activate("expired-token", "passWord123", "passWord123"));
    }

    @Test
    @DisplayName("shouldReturn400WhenTokenIsAlreadyUsed")
    void shouldReturn400WhenTokenIsAlreadyUsed() {
        when(activationTokenService.findValidToken("used-token")).thenReturn(Optional.empty());

        assertThrows(ActivationTokenException.class, 
            () -> authService.activate("used-token", "passWord123", "passWord123"));
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

        when(activationTokenService.findValidToken("valid-token")).thenReturn(Optional.of(activationToken));
        when(loginsRepository.findById(1L)).thenReturn(Optional.of(activeLogin));

        assertThrows(ActivationTokenException.class, () -> authService.activate("valid-token", "password1", "password2"));
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

        when(activationTokenService.findValidToken("valid-token")).thenReturn(Optional.of(activationToken));
        when(loginsRepository.findById(1L)).thenReturn(Optional.of(activeLogin));
        when(passwordEncoder.encode("mypassword")).thenReturn("encodedhash");

        assertDoesNotThrow(() -> authService.activate("valid-token", "mypassword", "mypassword"));
        verify(passwordEncoder).encode("mypassword");
    }
}

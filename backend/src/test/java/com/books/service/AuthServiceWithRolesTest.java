package com.books.service;

import com.books.dto.LoginRequestDTO;
import com.books.dto.LoginResponseDTO;
import com.books.dto.RefreshResponseDTO;
import com.books.exception.LoginNotFoundException;
import com.books.model.Login;
import com.books.model.RefreshToken;
import com.books.model.Role;
import com.books.repository.LoginsRepository;
import com.books.repository.RoleRepository;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class SetOrderMatcher<T> implements org.mockito.ArgumentMatcher<List<T>> {
    private final Set<T> expected;

    public SetOrderMatcher(Set<T> expected) {
        this.expected = expected;
    }

    @Override
    public boolean matches(List<T> actual) {
        return actual != null && new HashSet<>(actual).equals(expected);
    }
}

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceWithRolesTest {

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

    @InjectMocks
    private AuthService authService;

    private Login borrowerLogin;
    private Login librarianLogin;
    private Login multiRoleLogin;
    private Login noRoleLogin;

    @BeforeEach
    void setUp() {
        authService.setAccessTokenExpirySecondsForTest(900);

        Role borrower = Role.builder().id(1L).name("BORROWER").build();
        Role librarian = Role.builder().id(2L).name("LIBRARIAN").build();
        Role admin = Role.builder().id(3L).name("ADMIN").build();

        borrowerLogin = Login.builder()
                .id(1L)
                .username("jc.dusse")
                .passwordHash("$2a$10$hashedpassword")
                .enabled(true)
                .roles(Set.of(borrower))
                .lastLogin(null)
                .createdAt(LocalDateTime.now())
                .build();

        librarianLogin = Login.builder()
                .id(2L)
                .username("librarian1")
                .passwordHash("$2a$10$hashedpassword")
                .enabled(true)
                .roles(Set.of(librarian))
                .lastLogin(null)
                .createdAt(LocalDateTime.now())
                .build();

        multiRoleLogin = Login.builder()
                .id(3L)
                .username("admin.user")
                .passwordHash("$2a$10$hashedpassword")
                .enabled(true)
                .roles(Set.of(borrower, librarian, admin))
                .lastLogin(null)
                .createdAt(LocalDateTime.now())
                .build();

        noRoleLogin = Login.builder()
                .id(4L)
                .username("novice.user")
                .passwordHash("$2a$10$hashedpassword")
                .enabled(true)
                .roles(new HashSet<>())
                .lastLogin(null)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("shouldLoginWithCorrectRolesFromDatabase")
    void shouldLoginWithCorrectRolesFromDatabase() {
        LoginRequestDTO request = new LoginRequestDTO("jc.dusse", "secret");

        when(loginsRepository.findByUsername("jc.dusse")).thenReturn(Optional.of(borrowerLogin));
        when(passwordEncoder.matches("secret", "$2a$10$hashedpassword")).thenReturn(true);
        when(jwtTokenGenerator.generate(1L, "jc.dusse", List.of("BORROWER"))).thenReturn("access-token-borrower");
        when(refreshTokenService.create(1L)).thenReturn("refresh-token-borrower");

        LoginResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertEquals("access-token-borrower", response.getAccessToken());
        assertEquals("refresh-token-borrower", response.getRefreshToken());
        verify(jwtTokenGenerator).generate(1L, "jc.dusse", List.of("BORROWER"));
    }

    @Test
    @DisplayName("shouldLoginWithMultipleRoles")
    void shouldLoginWithMultipleRoles() {
        LoginRequestDTO request = new LoginRequestDTO("admin.user", "secret");

        when(loginsRepository.findByUsername("admin.user")).thenReturn(Optional.of(multiRoleLogin));
        when(passwordEncoder.matches("secret", "$2a$10$hashedpassword")).thenReturn(true);
        when(jwtTokenGenerator.generate(anyLong(), anyString(), argThat(new SetOrderMatcher<>(Set.of("BORROWER", "LIBRARIAN", "ADMIN")))))
                .thenReturn("access-token-multi");
        when(refreshTokenService.create(3L)).thenReturn("refresh-token-multi");

        LoginResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertEquals("access-token-multi", response.getAccessToken());
    }

    @Test
    @DisplayName("shouldLoginWithEmptyRoles")
    void shouldLoginWithEmptyRoles() {
        LoginRequestDTO request = new LoginRequestDTO("novice.user", "secret");

        when(loginsRepository.findByUsername("novice.user")).thenReturn(Optional.of(noRoleLogin));
        when(passwordEncoder.matches("secret", "$2a$10$hashedpassword")).thenReturn(true);
        when(jwtTokenGenerator.generate(4L, "novice.user", List.of())).thenReturn("access-token-empty");
        when(refreshTokenService.create(4L)).thenReturn("refresh-token-empty");

        LoginResponseDTO response = authService.login(request);

        assertNotNull(response);
        assertEquals("access-token-empty", response.getAccessToken());
        verify(jwtTokenGenerator).generate(4L, "novice.user", List.of());
    }

    @Test
    @DisplayName("shouldRefreshWithCorrectRolesFromDatabase")
    void shouldRefreshWithCorrectRolesFromDatabase() {
        RefreshToken tokenEntity = RefreshToken.builder()
                .id(1L)
                .loginId(1L)
                .tokenHash("hash")
                .expiresAt(LocalDateTime.now().plusDays(10))
                .revoked(false)
                .build();

        when(refreshTokenService.isValid("refresh-token")).thenReturn(true);
        when(refreshTokenService.findValidToken("refresh-token")).thenReturn(Optional.of(tokenEntity));
        when(loginsRepository.findById(1L)).thenReturn(Optional.of(borrowerLogin));
        when(jwtTokenGenerator.generate(1L, "jc.dusse", List.of("BORROWER"))).thenReturn("new-access-token");

        RefreshResponseDTO response = authService.refresh("refresh-token");

        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        verify(jwtTokenGenerator).generate(1L, "jc.dusse", List.of("BORROWER"));
    }

    @Test
    @DisplayName("shouldRefreshWithUpdatedRoles")
    void shouldRefreshWithUpdatedRoles() {
        RefreshToken tokenEntity = RefreshToken.builder()
                .id(1L)
                .loginId(3L)
                .tokenHash("hash")
                .expiresAt(LocalDateTime.now().plusDays(10))
                .revoked(false)
                .build();

        when(refreshTokenService.isValid("refresh-token")).thenReturn(true);
        when(refreshTokenService.findValidToken("refresh-token")).thenReturn(Optional.of(tokenEntity));
        when(loginsRepository.findById(3L)).thenReturn(Optional.of(multiRoleLogin));
        when(jwtTokenGenerator.generate(anyLong(), anyString(), argThat(new SetOrderMatcher<>(Set.of("BORROWER", "LIBRARIAN", "ADMIN")))))
                .thenReturn("new-access-token-multi");

        RefreshResponseDTO response = authService.refresh("refresh-token");

        assertNotNull(response);
        assertEquals("new-access-token-multi", response.getAccessToken());
    }

    @Test
    @DisplayName("shouldReturn401WhenCredentialsAreInvalid")
    void shouldReturn401WhenCredentialsAreInvalid() {
        LoginRequestDTO request = new LoginRequestDTO("jc.dusse", "wrongpassword");

        when(loginsRepository.findByUsername("jc.dusse")).thenReturn(Optional.of(borrowerLogin));
        when(passwordEncoder.matches("wrongpassword", "$2a$10$hashedpassword")).thenReturn(false);

        assertThrows(LoginNotFoundException.class, () -> authService.login(request));
        verify(loginsRepository, never()).save(any());
    }

    @Test
    @DisplayName("shouldReturn401WhenAccountIsNotActivated")
    void shouldReturn401WhenAccountIsNotActivated() {
        Role borrower = Role.builder().id(1L).name("BORROWER").build();
        Login disabledLogin = Login.builder()
                .id(5L)
                .username("disabled.user")
                .passwordHash("$2a$10$hashedpassword")
                .enabled(false)
                .roles(Set.of(borrower))
                .build();

        LoginRequestDTO request = new LoginRequestDTO("disabled.user", "secret");

        when(loginsRepository.findByUsername("disabled.user")).thenReturn(Optional.of(disabledLogin));

        assertThrows(LoginNotFoundException.class, () -> authService.login(request));
    }
}

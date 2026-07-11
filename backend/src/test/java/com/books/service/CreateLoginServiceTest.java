package com.books.service;

import com.books.dto.CreateLoginDTO;
import com.books.dto.LoginDTO;
import com.books.exception.LoginConflictException;
import com.books.exception.LoginValidationException;
import com.books.model.Login;
import com.books.repository.LoginsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateLoginServiceTest {

    @Mock
    private LoginsRepository loginsRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private CreateLoginService createLoginService;

    private CreateLoginDTO validDto;

    @BeforeEach
    void setUp() {
        validDto = CreateLoginDTO.builder()
                .username("admin1")
                .password("securePass123")
                .build();
    }

    @Test
    @DisplayName("shouldCreateLogin")
    void shouldCreateLogin() {
        when(loginsRepository.existsByUsername("admin1")).thenReturn(false);
        when(passwordEncoder.encode("securePass123")).thenReturn("$2a$10$hashedpassword");

        Login savedLogin = Login.builder()
                .id(1L)
                .username("admin1")
                .passwordHash("$2a$10$hashedpassword")
                .enabled(true)
                .build();
        when(loginsRepository.save(any(Login.class))).thenReturn(savedLogin);

        LoginDTO result = createLoginService.create(validDto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("admin1", result.getUsername());
        assertTrue(result.getEnabled());
        verify(loginsRepository).existsByUsername("admin1");
        verify(passwordEncoder).encode("securePass123");
        verify(loginsRepository).save(any(Login.class));
    }

    @Test
    @DisplayName("shouldThrowConflictWhenUsernameAlreadyExists")
    void shouldThrowConflictWhenUsernameAlreadyExists() {
        when(loginsRepository.existsByUsername("admin1")).thenReturn(true);

        LoginConflictException ex = assertThrows(LoginConflictException.class,
                () -> createLoginService.create(validDto));

        assertTrue(ex.getMessage().contains("admin1"));
        assertTrue(ex.getMessage().contains("already exists"));
        verify(loginsRepository, never()).save(any(Login.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("shouldThrowBadRequestWhenUsernameBlank")
    void shouldThrowBadRequestWhenUsernameBlank() {
        CreateLoginDTO dto = CreateLoginDTO.builder()
                .username("")
                .password("securePass123")
                .build();

        LoginValidationException ex = assertThrows(LoginValidationException.class,
                () -> createLoginService.create(dto));

        assertEquals("Username must not be blank", ex.getMessage());
        verify(loginsRepository, never()).existsByUsername(anyString());
    }

    @Test
    @DisplayName("shouldThrowBadRequestWhenPasswordBlank")
    void shouldThrowBadRequestWhenPasswordBlank() {
        CreateLoginDTO dto = CreateLoginDTO.builder()
                .username("admin1")
                .password("")
                .build();

        LoginValidationException ex = assertThrows(LoginValidationException.class,
                () -> createLoginService.create(dto));

        assertEquals("Password must not be blank", ex.getMessage());
    }

    @Test
    @DisplayName("shouldThrowBadRequestWhenPasswordTooShort")
    void shouldThrowBadRequestWhenPasswordTooShort() {
        CreateLoginDTO dto = CreateLoginDTO.builder()
                .username("admin1")
                .password("short")
                .build();

        LoginValidationException ex = assertThrows(LoginValidationException.class,
                () -> createLoginService.create(dto));

        assertEquals("Password must be at least 8 characters", ex.getMessage());
    }

    @Test
    @DisplayName("shouldThrowBadRequestWhenUsernameTooShort")
    void shouldThrowBadRequestWhenUsernameTooShort() {
        CreateLoginDTO dto = CreateLoginDTO.builder()
                .username("ab")
                .password("securePass123")
                .build();

        LoginValidationException ex = assertThrows(LoginValidationException.class,
                () -> createLoginService.create(dto));

        assertEquals("Username must be between 3 and 100 characters", ex.getMessage());
    }
}

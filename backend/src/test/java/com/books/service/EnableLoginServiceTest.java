package com.books.service;

import com.books.dto.LoginDTO;
import com.books.exception.LoginNotFoundException;
import com.books.model.Login;
import com.books.repository.LoginsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnableLoginServiceTest {

    @Mock
    private LoginsRepository loginsRepository;

    @InjectMocks
    private EnableLoginService enableLoginService;

    private Login disabledLogin;

    @BeforeEach
    void setUp() {
        disabledLogin = Login.builder()
                .id(1L)
                .username("testuser")
                .passwordHash("$2a$10$hash")
                .enabled(false)
                .build();
    }

    @Test
    @DisplayName("shouldEnableLogin")
    void shouldEnableLogin() {
        when(loginsRepository.findById(1L)).thenReturn(Optional.of(disabledLogin));
        when(loginsRepository.save(any(Login.class))).thenAnswer(invocation -> {
            Login saved = invocation.getArgument(0);
            saved.setEnabled(true);
            return saved;
        });

        LoginDTO result = enableLoginService.enable(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertTrue(result.getEnabled());
        verify(loginsRepository).save(any(Login.class));
    }

    @Test
    @DisplayName("shouldReturnNotFoundWhenLoginDoesNotExist")
    void shouldReturnNotFoundWhenLoginDoesNotExist() {
        when(loginsRepository.findById(999L)).thenReturn(Optional.empty());

        LoginNotFoundException ex = assertThrows(LoginNotFoundException.class,
                () -> enableLoginService.enable(999L));

        assertEquals("Login not found with id: 999", ex.getMessage());
        verify(loginsRepository, never()).save(any(Login.class));
    }

    @Test
    @DisplayName("shouldReturnSuccessWhenAlreadyEnabled")
    void shouldReturnSuccessWhenAlreadyEnabled() {
        Login enabledLogin = Login.builder()
                .id(2L)
                .username("enableduser")
                .passwordHash("$2a$10$hash")
                .enabled(true)
                .build();

        when(loginsRepository.findById(2L)).thenReturn(Optional.of(enabledLogin));
        when(loginsRepository.save(any(Login.class))).thenAnswer(invocation -> {
            Login saved = invocation.getArgument(0);
            saved.setEnabled(true);
            return saved;
        });

        LoginDTO result = enableLoginService.enable(2L);

        assertNotNull(result);
        assertTrue(result.getEnabled());
        verify(loginsRepository).save(any(Login.class));
    }
}

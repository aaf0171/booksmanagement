package com.books.service;

import com.books.exception.LoginNotFoundException;
import com.books.repository.LoginsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteLoginServiceTest {

    @Mock
    private LoginsRepository loginsRepository;

    @InjectMocks
    private DeleteLoginService deleteLoginService;

    @Test
    @DisplayName("shouldDeleteLogin")
    void shouldDeleteLogin() {
        Long loginId = 1L;
        when(loginsRepository.existsById(loginId)).thenReturn(true);
        when(loginsRepository.deleteLoginByIdCustom(loginId)).thenReturn(1);

        assertDoesNotThrow(() -> deleteLoginService.delete(loginId));

        verify(loginsRepository).existsById(loginId);
        verify(loginsRepository).deleteLoginByIdCustom(loginId);
    }

    @Test
    @DisplayName("shouldReturnNotFoundWhenLoginDoesNotExist")
    void shouldReturnNotFoundWhenLoginDoesNotExist() {
        Long loginId = 999L;
        when(loginsRepository.existsById(loginId)).thenReturn(false);

        LoginNotFoundException ex = assertThrows(LoginNotFoundException.class,
                () -> deleteLoginService.delete(loginId));

        assertEquals("Login not found with id: 999", ex.getMessage());
        verify(loginsRepository).existsById(loginId);
        verify(loginsRepository, never()).deleteLoginByIdCustom(anyLong());
    }

    @Test
    @DisplayName("shouldCascadeDeleteAssociatedBorrowerOrAdministrator")
    void shouldCascadeDeleteAssociatedBorrowerOrAdministrator() {
        Long loginId = 42L;
        when(loginsRepository.existsById(loginId)).thenReturn(true);
        when(loginsRepository.deleteLoginByIdCustom(loginId)).thenReturn(1);

        assertDoesNotThrow(() -> deleteLoginService.delete(loginId));

        verify(loginsRepository).existsById(loginId);
        verify(loginsRepository).deleteLoginByIdCustom(loginId);
    }
}

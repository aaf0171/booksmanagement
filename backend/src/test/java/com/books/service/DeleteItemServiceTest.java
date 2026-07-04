package com.books.service;

import com.books.exception.ItemInUseException;
import com.books.exception.ItemNotFoundException;
import com.books.repository.ItemsRepository;
import com.books.repository.LoansRepositoryDatabase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteItemServiceTest {

    @Mock
    private ItemsRepository itemsRepository;

    @Mock
    private LoansRepositoryDatabase loansRepository;

    @InjectMocks
    private DeleteItemService deleteItemService;

    @Test
    @DisplayName("shouldDeleteItem")
    void shouldDeleteItem() {
        Long itemId = 1L;
        when(loansRepository.countActiveLoansForItem(itemId)).thenReturn(0L);
        when(itemsRepository.deleteItemByIdCustom(itemId)).thenReturn(1);

        assertDoesNotThrow(() -> deleteItemService.delete(itemId));

        verify(loansRepository).countActiveLoansForItem(itemId);
        verify(itemsRepository).deleteItemByIdCustom(itemId);
        verify(loansRepository).deleteLoansByItemId(itemId);
    }

    @Test
    @DisplayName("shouldReturnNotFoundWhenItemDoesNotExist")
    void shouldReturnNotFoundWhenItemDoesNotExist() {
        Long itemId = 999L;
        when(loansRepository.countActiveLoansForItem(itemId)).thenReturn(0L);
        when(itemsRepository.deleteItemByIdCustom(itemId)).thenReturn(0);

        ItemNotFoundException ex = assertThrows(ItemNotFoundException.class,
                () -> deleteItemService.delete(itemId));

        assertEquals("Item not found with id: 999", ex.getMessage());
        verify(loansRepository).countActiveLoansForItem(itemId);
        verify(itemsRepository).deleteItemByIdCustom(itemId);
        verify(loansRepository, never()).deleteLoansByItemId(anyLong());
    }

    @Test
    @DisplayName("shouldReturnConflictWhenActiveLoanExists")
    void shouldReturnConflictWhenActiveLoanExists() {
        Long itemId = 42L;
        when(loansRepository.countActiveLoansForItem(itemId)).thenReturn(1L);

        ItemInUseException ex = assertThrows(ItemInUseException.class,
                () -> deleteItemService.delete(itemId));

        assertTrue(ex.getMessage().contains("Cannot delete item with id 42"));
        assertTrue(ex.getMessage().contains("active loan"));
        verify(loansRepository).countActiveLoansForItem(itemId);
        verify(itemsRepository, never()).deleteItemByIdCustom(anyLong());
        verify(loansRepository, never()).deleteLoansByItemId(anyLong());
    }
}

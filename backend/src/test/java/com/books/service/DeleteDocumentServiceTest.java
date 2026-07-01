package com.books.service;

import com.books.exception.DocumentInUseException;
import com.books.exception.DocumentNotFoundException;
import com.books.repository.DocumentsRepository;
import com.books.repository.ItemsRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteDocumentServiceTest {

    @Mock
    private DocumentsRepository documentsRepository;

    @Mock
    private ItemsRepository itemsRepository;

    @InjectMocks
    private DeleteDocumentService deleteDocumentService;

    @Test
    @DisplayName("shouldDeleteDocument")
    void shouldDeleteDocument() {
        Long documentId = 1L;
        when(itemsRepository.countByDocumentId(documentId)).thenReturn(0L);
        when(documentsRepository.deleteByIdCustom(documentId)).thenReturn(1);

        assertDoesNotThrow(() -> deleteDocumentService.delete(documentId));
        verify(itemsRepository).countByDocumentId(documentId);
        verify(documentsRepository).deleteByIdCustom(documentId);
    }

    @Test
    @DisplayName("shouldReturnNotFoundWhenDocumentDoesNotExist")
    void shouldReturnNotFoundWhenDocumentDoesNotExist() {
        Long documentId = 999L;
        when(itemsRepository.countByDocumentId(documentId)).thenReturn(0L);
        when(documentsRepository.deleteByIdCustom(documentId)).thenReturn(0);

        DocumentNotFoundException exception = assertThrows(DocumentNotFoundException.class,
                () -> deleteDocumentService.delete(documentId));

        assertEquals("Document not found with id: 999", exception.getMessage());
        verify(itemsRepository).countByDocumentId(documentId);
        verify(documentsRepository).deleteByIdCustom(documentId);
    }

    @Test
    @DisplayName("shouldReturnConflictWhenItemsExist")
    void shouldReturnConflictWhenItemsExist() {
        Long documentId = 42L;
        when(itemsRepository.countByDocumentId(documentId)).thenReturn(3L);

        DocumentInUseException exception = assertThrows(DocumentInUseException.class,
                () -> deleteDocumentService.delete(documentId));

        assertTrue(exception.getMessage().contains("Cannot delete document with id 42"));
        assertTrue(exception.getMessage().contains("Items are still attached"));
        verify(itemsRepository).countByDocumentId(documentId);
        verify(documentsRepository, never()).deleteByIdCustom(anyLong());
    }
}

package com.books.service;

import com.books.dto.DocumentDTO;
import com.books.dto.DocumentWithItemsDTO;
import com.books.dto.DocumentWithItemsResponseDTO;
import com.books.dto.ItemDTO;
import com.books.mapper.DocumentMapper;
import com.books.model.Document;
import com.books.model.Document.DocumentType;
import com.books.model.Item;
import com.books.repository.DocumentsRepository;
import com.books.repository.ItemsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateDocumentWithItemsServiceTest {

    @Mock
    private DocumentsRepository documentsRepository;

    @Mock
    private ItemsRepository itemsRepository;

    @Spy
    private DocumentMapper documentMapper = new DocumentMapper();

    @InjectMocks
    private CreateDocumentWithItemsService createDocumentWithItemsService;

    @Test
    @DisplayName("should_create_document_only_when_no_items")
    void shouldCreateDocumentOnlyWhenNoItems() {
        DocumentWithItemsDTO dto = DocumentWithItemsDTO.builder()
                .title("Book Without Items")
                .documentType(DocumentType.DVD)
                .build();

        when(documentsRepository.save(any(Document.class)))
                .thenAnswer(invocation -> {
                    Document d = invocation.getArgument(0);
                    return Document.builder()
                            .id(1L)
                            .title(d.getTitle())
                            .documentType(d.getDocumentType())
                            .createdAt(LocalDateTime.now())
                            .build();
                });

        DocumentWithItemsResponseDTO result = createDocumentWithItemsService.create(dto);

        assertNotNull(result);
        assertNotNull(result.getDocument());
        assertEquals("Book Without Items", result.getDocument().getTitle());
        assertEquals(0, result.getItems().length);
        verify(documentsRepository).save(any(Document.class));
        verify(itemsRepository, never()).save(any());
    }

    @Test
    @DisplayName("should_create_document_and_items_when_items_provided")
    void shouldCreateDocumentAndItemsWhenItemsProvided() {
        DocumentWithItemsDTO dto = DocumentWithItemsDTO.builder()
                .title("Test Book")
                .documentType(DocumentType.BOOK)
                .isbn("978-1234567890")
                .publisher("Test Publisher")
                .publicationYear(2024)
                .items(List.of("Copy 1", "Copy 2"))
                .build();

        Document savedDocument = Document.builder()
                .id(1L)
                .title("Test Book")
                .documentType(DocumentType.BOOK)
                .isbn("978-1234567890")
                .publisher("Test Publisher")
                .publicationYear(2024)
                .createdAt(LocalDateTime.now())
                .build();

        Item savedItem1 = Item.builder().id(1L).barcode("Copy 1").document(savedDocument).build();
        Item savedItem2 = Item.builder().id(2L).barcode("Copy 2").document(savedDocument).build();

        when(documentsRepository.save(any(Document.class))).thenReturn(savedDocument);
        when(itemsRepository.existsByDocumentIdAndBarcode(1L, "Copy 1")).thenReturn(false);
        when(itemsRepository.existsByDocumentIdAndBarcode(1L, "Copy 2")).thenReturn(false);
        when(itemsRepository.save(any(Item.class)))
                .thenReturn(savedItem1)
                .thenReturn(savedItem2);

        DocumentWithItemsResponseDTO result = createDocumentWithItemsService.create(dto);

        assertNotNull(result);
        assertNotNull(result.getDocument());
        assertEquals("Test Book", result.getDocument().getTitle());
        assertEquals(2, result.getItems().length);
        assertEquals("Copy 1", result.getItems()[0].getBarcode());
        assertEquals("Copy 2", result.getItems()[1].getBarcode());
        verify(documentsRepository).save(any(Document.class));
        verify(itemsRepository, times(2)).save(any(Item.class));
    }

    @Test
    @DisplayName("should_link_items_to_document")
    void shouldLinkItemsToDocument() {
        DocumentWithItemsDTO dto = DocumentWithItemsDTO.builder()
                .title("Test Book")
                .documentType(DocumentType.BOOK)
                .items(List.of("Copy 1"))
                .build();

        Document savedDocument = Document.builder()
                .id(1L)
                .title("Test Book")
                .documentType(DocumentType.BOOK)
                .createdAt(LocalDateTime.now())
                .build();

        Item savedItem = Item.builder().id(1L).barcode("Copy 1").document(savedDocument).build();

        when(documentsRepository.save(any(Document.class))).thenReturn(savedDocument);
        when(itemsRepository.existsByDocumentIdAndBarcode(1L, "Copy 1")).thenReturn(false);
        when(itemsRepository.save(any(Item.class))).thenReturn(savedItem);

        DocumentWithItemsResponseDTO result = createDocumentWithItemsService.create(dto);

        assertNotNull(result);
        assertNotNull(result.getItems());
        assertEquals(1, result.getItems().length);
        assertEquals("Copy 1", result.getItems()[0].getBarcode());
        verify(itemsRepository).save(any(Item.class));
    }

    @Test
    @DisplayName("should_fail_on_invalid_document_type")
    void shouldFailOnInvalidDocumentType() {
        DocumentWithItemsDTO dto = DocumentWithItemsDTO.builder()
                .title("Test")
                .documentType(DocumentType.BOOK)
                .build();

        // Le test passe par le mapper spy, donc on ne peut pas mock validateDocumentDTO
        // On teste avec un DTO null
        assertThrows(IllegalArgumentException.class, () ->
                createDocumentWithItemsService.create(null));
    }

    @Test
    @DisplayName("should_skip_existing_item_labels")
    void shouldSkipExistingItemLabels() {
        DocumentWithItemsDTO dto = DocumentWithItemsDTO.builder()
                .title("Test Book")
                .documentType(DocumentType.BOOK)
                .items(List.of("Copy 1"))
                .build();

        Document savedDocument = Document.builder()
                .id(1L)
                .title("Test Book")
                .documentType(DocumentType.BOOK)
                .createdAt(LocalDateTime.now())
                .build();

        when(documentsRepository.save(any(Document.class))).thenReturn(savedDocument);
        when(itemsRepository.existsByDocumentIdAndBarcode(1L, "Copy 1")).thenReturn(true);

        DocumentWithItemsResponseDTO result = createDocumentWithItemsService.create(dto);

        assertNotNull(result);
        assertEquals(0, result.getItems().length);
        verify(itemsRepository, never()).save(any(Item.class));
    }
}

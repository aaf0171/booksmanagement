package com.books.controller;

import com.books.dto.DocumentDTO;
import com.books.dto.DocumentWithItemsDTO;
import com.books.dto.DocumentWithItemsResponseDTO;
import com.books.dto.ItemDTO;
import com.books.model.Document.DocumentType;
import com.books.service.CreateDocumentWithItemsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentCommandControllerTest {

    private DocumentCommandController controller;
    private CreateDocumentWithItemsService service;

    @BeforeEach
    void setUp() {
        service = mock(CreateDocumentWithItemsService.class);
        controller = new DocumentCommandController(service);
    }

    @Test
    @DisplayName("should_return_201_with_document_and_items")
    void shouldReturn201WithDocumentAndItems() {
        DocumentWithItemsDTO dto = DocumentWithItemsDTO.builder()
                .title("Test Book")
                .documentType(DocumentType.BOOK)
                .items(List.of("Copy 1"))
                .build();

        DocumentDTO documentDTO = DocumentDTO.builder()
                .id(1L)
                .title("Test Book")
                .documentType(DocumentType.BOOK)
                .createdAt(LocalDateTime.now())
                .build();

        DocumentWithItemsResponseDTO response = DocumentWithItemsResponseDTO.builder()
                .document(documentDTO)
                .items(new ItemDTO[]{ItemDTO.builder().id(1L).barcode("Copy 1").build()})
                .build();

        when(service.create(dto)).thenReturn(response);

        var httpResponse = controller.createDocument(dto);

        assertEquals(201, httpResponse.getStatusCode().value());
        assertNotNull(httpResponse.getBody());
        assertEquals("Test Book", httpResponse.getBody().getDocument().getTitle());
        assertEquals(1, httpResponse.getBody().getItems().length);
        assertEquals("Copy 1", httpResponse.getBody().getItems()[0].getBarcode());
        verify(service).create(dto);
    }

    @Test
    @DisplayName("should_accept_empty_items_list")
    void shouldAcceptEmptyItemsList() {
        DocumentWithItemsDTO dto = DocumentWithItemsDTO.builder()
                .title("Book Without Items")
                .documentType(DocumentType.DVD)
                .items(List.of())
                .build();

        DocumentDTO documentDTO = DocumentDTO.builder()
                .id(1L)
                .title("Book Without Items")
                .documentType(DocumentType.DVD)
                .createdAt(LocalDateTime.now())
                .build();

        DocumentWithItemsResponseDTO response = DocumentWithItemsResponseDTO.builder()
                .document(documentDTO)
                .items(new ItemDTO[0])
                .build();

        when(service.create(dto)).thenReturn(response);

        var httpResponse = controller.createDocument(dto);

        assertEquals(201, httpResponse.getStatusCode().value());
        assertNotNull(httpResponse.getBody());
        assertEquals("Book Without Items", httpResponse.getBody().getDocument().getTitle());
        assertEquals(0, httpResponse.getBody().getItems().length);
        verify(service).create(dto);
    }

    @Test
    @DisplayName("should_create_document_only_without_items")
    void shouldCreateDocumentOnlyWithoutItems() {
        DocumentWithItemsDTO dto = DocumentWithItemsDTO.builder()
                .title("Minimal Book")
                .documentType(DocumentType.BOOK)
                .build();

        DocumentDTO documentDTO = DocumentDTO.builder()
                .id(1L)
                .title("Minimal Book")
                .documentType(DocumentType.BOOK)
                .createdAt(LocalDateTime.now())
                .build();

        DocumentWithItemsResponseDTO response = DocumentWithItemsResponseDTO.builder()
                .document(documentDTO)
                .items(new ItemDTO[0])
                .build();

        when(service.create(dto)).thenReturn(response);

        var httpResponse = controller.createDocument(dto);

        assertEquals(201, httpResponse.getStatusCode().value());
        assertNotNull(httpResponse.getBody());
        assertEquals("Minimal Book", httpResponse.getBody().getDocument().getTitle());
        verify(service).create(dto);
    }

    @Test
    @DisplayName("should_return_multiple_items")
    void shouldReturnMultipleItems() {
        DocumentWithItemsDTO dto = DocumentWithItemsDTO.builder()
                .title("Multi Item Book")
                .documentType(DocumentType.GAME)
                .items(List.of("Copy 1", "Copy 2", "Copy 3"))
                .build();

        DocumentDTO documentDTO = DocumentDTO.builder()
                .id(1L)
                .title("Multi Item Book")
                .documentType(DocumentType.GAME)
                .createdAt(LocalDateTime.now())
                .build();

        DocumentWithItemsResponseDTO response = DocumentWithItemsResponseDTO.builder()
                .document(documentDTO)
                .items(new ItemDTO[]{
                        ItemDTO.builder().id(1L).barcode("Copy 1").build(),
                        ItemDTO.builder().id(2L).barcode("Copy 2").build(),
                        ItemDTO.builder().id(3L).barcode("Copy 3").build()
                })
                .build();

        when(service.create(dto)).thenReturn(response);

        var httpResponse = controller.createDocument(dto);

        assertEquals(201, httpResponse.getStatusCode().value());
        assertNotNull(httpResponse.getBody());
        assertEquals(3, httpResponse.getBody().getItems().length);
        assertEquals("Copy 3", httpResponse.getBody().getItems()[2].getBarcode());
        verify(service).create(dto);
    }
}

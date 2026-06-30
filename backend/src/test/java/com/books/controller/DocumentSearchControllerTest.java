package com.books.controller;

import com.books.dto.DocumentSearchDTO;
import com.books.service.DocumentSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentSearchControllerTest {

    private DocumentSearchController controller;
    private DocumentSearchService service;

    @BeforeEach
    void setUp() {
        service = mock(DocumentSearchService.class);
        controller = new DocumentSearchController(service);
    }

    @Test
    @DisplayName("should_return_200_with_results")
    void shouldReturn200WithResults() {
        DocumentSearchDTO mockDoc = createMockDocument(1L, "Test Book");
        when(service.searchDocuments("test", null, null, null, null, null, null))
                .thenReturn(List.of(mockDoc));

        var response = controller.searchDocuments("test", null, null, null, null, null, null);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("Test Book", response.getBody().get(0).getTitle());
        verify(service).searchDocuments("test", null, null, null, null, null, null);
    }

    @Test
    @DisplayName("should_return_empty_list_when_no_match")
    void shouldReturnEmptyListWhenNoMatch() {
        when(service.searchDocuments("nonexistent", null, null, null, null, null, null))
                .thenReturn(List.of());

        var response = controller.searchDocuments("nonexistent", null, null, null, null, null, null);

        assertEquals(200, response.getStatusCode().value());
        assertTrue(response.getBody().isEmpty());
        verify(service).searchDocuments("nonexistent", null, null, null, null, null, null);
    }

    @Test
    @DisplayName("should_accept_optional_filters")
    void shouldAcceptOptionalFilters() {
        DocumentSearchDTO mockDoc = createMockDocument(1L, "Harry Potter");
        when(service.searchDocuments(null, "Harry", null, "BOOK", null, null, null))
                .thenReturn(List.of(mockDoc));

        var response = controller.searchDocuments(null, "Harry", null, "BOOK", null, null, null);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("Harry Potter", response.getBody().get(0).getTitle());
        verify(service).searchDocuments(null, "Harry", null, "BOOK", null, null, null);
    }

    private DocumentSearchDTO createMockDocument(Long id, String title) {
        return new DocumentSearchDTO() {
            @Override
            public Long getId() { return id; }

            @Override
            public String getTitle() { return title; }

            @Override
            public String getSubtitle() { return null; }

            @Override
            public String getDocumentType() { return "BOOK"; }

            @Override
            public String getIsbn() { return "978-1234567890"; }

            @Override
            public String getPublisher() { return "Test Publisher"; }

            @Override
            public Integer getPublicationYear() { return 2023; }

            @Override
            public String getDescription() { return "A test book"; }

            @Override
            public LocalDateTime getCreatedAt() { return LocalDateTime.now(); }
        };
    }
}

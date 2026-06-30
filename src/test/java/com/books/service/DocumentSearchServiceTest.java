package com.books.service;

import com.books.dto.DocumentSearchDTO;
import com.books.repository.DocumentsSearchRepositoryDatabase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentSearchServiceTest {

    @Mock
    private DocumentsSearchRepositoryDatabase documentsSearchRepository;

    @InjectMocks
    private DocumentSearchService documentSearchService;

    private DocumentSearchDTO mockDocument;

    @BeforeEach
    void setUp() {
        mockDocument = new DocumentSearchDTO() {
            @Override
            public Long getId() { return 1L; }

            @Override
            public String getTitle() { return "Test Book"; }

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
            public java.time.LocalDateTime getCreatedAt() { return java.time.LocalDateTime.now(); }
        };
    }

    @Test
    @DisplayName("should_return_documents_by_fulltext_query")
    void shouldReturnDocumentsByFulltextQuery() {
        when(documentsSearchRepository.searchDocuments("test", null, null, null, null, null, null))
                .thenReturn(List.of(mockDocument));

        List<DocumentSearchDTO> results = documentSearchService.searchDocuments("test", null, null, null, null, null, null);

        assertEquals(1, results.size());
        assertEquals("Test Book", results.get(0).getTitle());
        verify(documentsSearchRepository).searchDocuments("test", null, null, null, null, null, null);
    }

    @Test
    @DisplayName("should_filter_by_title")
    void shouldFilterByTitle() {
        when(documentsSearchRepository.searchDocuments(null, "Harry", null, null, null, null, null))
                .thenReturn(List.of(mockDocument));

        List<DocumentSearchDTO> results = documentSearchService.searchDocuments(null, "Harry", null, null, null, null, null);

        assertEquals(1, results.size());
        verify(documentsSearchRepository).searchDocuments(null, "Harry", null, null, null, null, null);
    }

    @Test
    @DisplayName("should_filter_by_document_type")
    void shouldFilterByDocumentType() {
        when(documentsSearchRepository.searchDocuments(null, null, null, "BOOK", null, null, null))
                .thenReturn(List.of(mockDocument));

        List<DocumentSearchDTO> results = documentSearchService.searchDocuments(null, null, null, "BOOK", null, null, null);

        assertEquals(1, results.size());
        verify(documentsSearchRepository).searchDocuments(null, null, null, "BOOK", null, null, null);
    }

    @Test
    @DisplayName("should_return_empty_when_no_match")
    void shouldReturnEmptyWhenNoMatch() {
        when(documentsSearchRepository.searchDocuments("nonexistent", null, null, null, null, null, null))
                .thenReturn(List.of());

        List<DocumentSearchDTO> results = documentSearchService.searchDocuments("nonexistent", null, null, null, null, null, null);

        assertTrue(results.isEmpty());
        verify(documentsSearchRepository).searchDocuments("nonexistent", null, null, null, null, null, null);
    }

    @Test
    @DisplayName("should_throw_exception_when_all_params_null")
    void shouldThrowExceptionWhenAllParamsNull() {
        assertThrows(IllegalArgumentException.class, () ->
                documentSearchService.searchDocuments(null, null, null, null, null, null, null));

        verifyNoInteractions(documentsSearchRepository);
    }

    @Test
    @DisplayName("should_throw_exception_when_invalid_document_type")
    void shouldThrowExceptionWhenInvalidDocumentType() {
        assertThrows(IllegalArgumentException.class, () ->
                documentSearchService.searchDocuments(null, null, null, "INVALID_TYPE", null, null, null));

        verifyNoInteractions(documentsSearchRepository);
    }
}

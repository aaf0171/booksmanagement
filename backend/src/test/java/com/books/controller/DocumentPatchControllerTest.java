package com.books.controller;

import com.books.dto.DocumentPatchDTO;
import com.books.model.Document.DocumentType;
import com.books.service.DocumentPatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentPatchControllerTest {

    private DocumentPatchController controller;
    private DocumentPatchService service;

    @BeforeEach
    void setUp() {
        service = mock(DocumentPatchService.class);
        controller = new DocumentPatchController(service);
    }

    @Test
    @DisplayName("PATCH title - should return 204")
    void shouldPatchTitle() {
        var dto = new DocumentPatchDTO.TitlePatchDTO("New Title");
        doNothing().when(service).patchTitle(1L, dto);

        ResponseEntity<Void> response = controller.patchTitle(1L, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).patchTitle(1L, dto);
    }

    @Test
    @DisplayName("PATCH subtitle - should return 204")
    void shouldPatchSubtitle() {
        var dto = new DocumentPatchDTO.SubtitlePatchDTO("New Subtitle");
        doNothing().when(service).patchSubtitle(1L, dto);

        ResponseEntity<Void> response = controller.patchSubtitle(1L, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).patchSubtitle(1L, dto);
    }

    @Test
    @DisplayName("PATCH subtitle - should accept null value")
    void shouldPatchSubtitleToNull() {
        var dto = new DocumentPatchDTO.SubtitlePatchDTO(null);
        doNothing().when(service).patchSubtitle(1L, dto);

        ResponseEntity<Void> response = controller.patchSubtitle(1L, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).patchSubtitle(1L, dto);
    }

    @Test
    @DisplayName("PATCH document-type - should return 204")
    void shouldPatchDocumentType() {
        var dto = new DocumentPatchDTO.DocumentTypePatchDTO("DVD");
        doNothing().when(service).patchDocumentType(1L, dto);

        ResponseEntity<Void> response = controller.patchDocumentType(1L, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).patchDocumentType(1L, dto);
    }

    @Test
    @DisplayName("PATCH isbn - should return 204")
    void shouldPatchIsbn() {
        var dto = new DocumentPatchDTO.IsbnPatchDTO("978-1234567890");
        doNothing().when(service).patchIsbn(1L, dto);

        ResponseEntity<Void> response = controller.patchIsbn(1L, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).patchIsbn(1L, dto);
    }

    @Test
    @DisplayName("PATCH isbn - should accept null value")
    void shouldPatchIsbnToNull() {
        var dto = new DocumentPatchDTO.IsbnPatchDTO(null);
        doNothing().when(service).patchIsbn(1L, dto);

        ResponseEntity<Void> response = controller.patchIsbn(1L, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).patchIsbn(1L, dto);
    }

    @Test
    @DisplayName("PATCH publisher - should return 204")
    void shouldPatchPublisher() {
        var dto = new DocumentPatchDTO.PublisherPatchDTO("New Publisher");
        doNothing().when(service).patchPublisher(1L, dto);

        ResponseEntity<Void> response = controller.patchPublisher(1L, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).patchPublisher(1L, dto);
    }

    @Test
    @DisplayName("PATCH publication-year - should return 204")
    void shouldPatchPublicationYear() {
        var dto = new DocumentPatchDTO.PublicationYearPatchDTO(2024);
        doNothing().when(service).patchPublicationYear(1L, dto);

        ResponseEntity<Void> response = controller.patchPublicationYear(1L, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).patchPublicationYear(1L, dto);
    }

    @Test
    @DisplayName("PATCH publication-year - should accept null value")
    void shouldPatchPublicationYearToNull() {
        var dto = new DocumentPatchDTO.PublicationYearPatchDTO(null);
        doNothing().when(service).patchPublicationYear(1L, dto);

        ResponseEntity<Void> response = controller.patchPublicationYear(1L, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).patchPublicationYear(1L, dto);
    }

    @Test
    @DisplayName("PATCH language - should return 204")
    void shouldPatchLanguage() {
        var dto = new DocumentPatchDTO.LanguagePatchDTO("français");
        doNothing().when(service).patchLanguage(1L, dto);

        ResponseEntity<Void> response = controller.patchLanguage(1L, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).patchLanguage(1L, dto);
    }

    @Test
    @DisplayName("PATCH description - should return 204")
    void shouldPatchDescription() {
        var dto = new DocumentPatchDTO.DescriptionPatchDTO("Epic fantasy novel.");
        doNothing().when(service).patchDescription(1L, dto);

        ResponseEntity<Void> response = controller.patchDescription(1L, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).patchDescription(1L, dto);
    }

    @Test
    @DisplayName("PATCH description - should accept null value")
    void shouldPatchDescriptionToNull() {
        var dto = new DocumentPatchDTO.DescriptionPatchDTO(null);
        doNothing().when(service).patchDescription(1L, dto);

        ResponseEntity<Void> response = controller.patchDescription(1L, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).patchDescription(1L, dto);
    }

    @Test
    @DisplayName("PATCH cover-url - should return 204")
    void shouldPatchCoverUrl() {
        var dto = new DocumentPatchDTO.CoverUrlPatchDTO("https://example.com/cover.jpg");
        doNothing().when(service).patchCoverUrl(1L, dto);

        ResponseEntity<Void> response = controller.patchCoverUrl(1L, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).patchCoverUrl(1L, dto);
    }

    @Test
    @DisplayName("PATCH cover-url - should accept null value")
    void shouldPatchCoverUrlToNull() {
        var dto = new DocumentPatchDTO.CoverUrlPatchDTO(null);
        doNothing().when(service).patchCoverUrl(1L, dto);

        ResponseEntity<Void> response = controller.patchCoverUrl(1L, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).patchCoverUrl(1L, dto);
    }

    @Test
    @DisplayName("PATCH created-at - should return 204")
    void shouldPatchCreatedAt() {
        var dto = new DocumentPatchDTO.CreatedAtPatchDTO(java.time.LocalDateTime.of(2026, 6, 30, 15, 42));
        doNothing().when(service).patchCreatedAt(1L, dto);

        ResponseEntity<Void> response = controller.patchCreatedAt(1L, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).patchCreatedAt(1L, dto);
    }

    @Test
    @DisplayName("PATCH created-at - should accept null value")
    void shouldPatchCreatedAtToNull() {
        var dto = new DocumentPatchDTO.CreatedAtPatchDTO(null);
        doNothing().when(service).patchCreatedAt(1L, dto);

        ResponseEntity<Void> response = controller.patchCreatedAt(1L, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).patchCreatedAt(1L, dto);
    }
}

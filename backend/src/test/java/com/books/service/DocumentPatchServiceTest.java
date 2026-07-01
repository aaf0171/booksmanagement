package com.books.service;

import com.books.dto.DocumentPatchDTO;
import com.books.model.Document;
import com.books.model.Document.DocumentType;
import com.books.repository.DocumentsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentPatchServiceTest {

    @Mock
    private DocumentsRepository documentsRepository;

    @InjectMocks
    private DocumentPatchService documentPatchService;

    private Document existingDocument;

    @BeforeEach
    void setUp() {
        existingDocument = Document.builder()
                .id(1L)
                .title("Original Title")
                .subtitle("Original Subtitle")
                .documentType(DocumentType.BOOK)
                .isbn("9780261103573")
                .publisher("Original Publisher")
                .publicationYear(1954)
                .language("English")
                .description("Original description")
                .coverUrl("https://example.com/cover.jpg")
                .createdAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                .build();
    }

    @Test
    @DisplayName("should_update_title")
    void shouldUpdateTitle() {
        DocumentPatchDTO.TitlePatchDTO dto = new DocumentPatchDTO.TitlePatchDTO("New Title");
        when(documentsRepository.findById(1L)).thenReturn(Optional.of(existingDocument));

        documentPatchService.patchTitle(1L, dto);

        assertEquals("New Title", existingDocument.getTitle());
        verify(documentsRepository).findById(1L);
    }

    @Test
    @DisplayName("should_update_subtitle")
    void shouldUpdateSubtitle() {
        DocumentPatchDTO.SubtitlePatchDTO dto = new DocumentPatchDTO.SubtitlePatchDTO("New Subtitle");
        when(documentsRepository.findById(1L)).thenReturn(Optional.of(existingDocument));

        documentPatchService.patchSubtitle(1L, dto);

        assertEquals("New Subtitle", existingDocument.getSubtitle());
    }

    @Test
    @DisplayName("should_set_subtitle_to_null")
    void shouldSetSubtitleToNull() {
        DocumentPatchDTO.SubtitlePatchDTO dto = new DocumentPatchDTO.SubtitlePatchDTO(null);
        when(documentsRepository.findById(1L)).thenReturn(Optional.of(existingDocument));

        documentPatchService.patchSubtitle(1L, dto);

        assertNull(existingDocument.getSubtitle());
    }

    @Test
    @DisplayName("should_update_document_type")
    void shouldUpdateDocumentType() {
        DocumentPatchDTO.DocumentTypePatchDTO dto = new DocumentPatchDTO.DocumentTypePatchDTO("DVD");
        when(documentsRepository.findById(1L)).thenReturn(Optional.of(existingDocument));

        documentPatchService.patchDocumentType(1L, dto);

        assertEquals(DocumentType.DVD, existingDocument.getDocumentType());
    }

    @Test
    @DisplayName("should_throw_on_invalid_document_type")
    void shouldThrowOnInvalidDocumentType() {
        DocumentPatchDTO.DocumentTypePatchDTO dto = new DocumentPatchDTO.DocumentTypePatchDTO("INVALID");
        when(documentsRepository.findById(1L)).thenReturn(Optional.of(existingDocument));

        assertThrows(IllegalArgumentException.class, () ->
                documentPatchService.patchDocumentType(1L, dto));
    }

    @Test
    @DisplayName("should_update_isbn")
    void shouldUpdateIsbn() {
        DocumentPatchDTO.IsbnPatchDTO dto = new DocumentPatchDTO.IsbnPatchDTO("978-1234567890");
        when(documentsRepository.findById(1L)).thenReturn(Optional.of(existingDocument));

        documentPatchService.patchIsbn(1L, dto);

        assertEquals("978-1234567890", existingDocument.getIsbn());
    }

    @Test
    @DisplayName("should_set_isbn_to_null")
    void setIsbnToNull() {
        DocumentPatchDTO.IsbnPatchDTO dto = new DocumentPatchDTO.IsbnPatchDTO(null);
        when(documentsRepository.findById(1L)).thenReturn(Optional.of(existingDocument));

        documentPatchService.patchIsbn(1L, dto);

        assertNull(existingDocument.getIsbn());
    }

    @Test
    @DisplayName("should_update_publisher")
    void shouldUpdatePublisher() {
        DocumentPatchDTO.PublisherPatchDTO dto = new DocumentPatchDTO.PublisherPatchDTO("New Publisher");
        when(documentsRepository.findById(1L)).thenReturn(Optional.of(existingDocument));

        documentPatchService.patchPublisher(1L, dto);

        assertEquals("New Publisher", existingDocument.getPublisher());
    }

    @Test
    @DisplayName("should_set_publisher_to_null")
    void shouldSetPublisherToNull() {
        DocumentPatchDTO.PublisherPatchDTO dto = new DocumentPatchDTO.PublisherPatchDTO(null);
        when(documentsRepository.findById(1L)).thenReturn(Optional.of(existingDocument));

        documentPatchService.patchPublisher(1L, dto);

        assertNull(existingDocument.getPublisher());
    }

    @Test
    @DisplayName("should_update_publication_year")
    void shouldUpdatePublicationYear() {
        DocumentPatchDTO.PublicationYearPatchDTO dto = new DocumentPatchDTO.PublicationYearPatchDTO(2024);
        when(documentsRepository.findById(1L)).thenReturn(Optional.of(existingDocument));

        documentPatchService.patchPublicationYear(1L, dto);

        assertEquals(2024, existingDocument.getPublicationYear());
    }

    @Test
    @DisplayName("should_set_publication_year_to_null")
    void shouldSetPublicationYearToNull() {
        DocumentPatchDTO.PublicationYearPatchDTO dto = new DocumentPatchDTO.PublicationYearPatchDTO(null);
        when(documentsRepository.findById(1L)).thenReturn(Optional.of(existingDocument));

        documentPatchService.patchPublicationYear(1L, dto);

        assertNull(existingDocument.getPublicationYear());
    }

    @Test
    @DisplayName("should_update_language")
    void shouldUpdateLanguage() {
        DocumentPatchDTO.LanguagePatchDTO dto = new DocumentPatchDTO.LanguagePatchDTO("français");
        when(documentsRepository.findById(1L)).thenReturn(Optional.of(existingDocument));

        documentPatchService.patchLanguage(1L, dto);

        assertEquals("français", existingDocument.getLanguage());
    }

    @Test
    @DisplayName("should_set_language_to_null")
    void shouldSetLanguageToNull() {
        DocumentPatchDTO.LanguagePatchDTO dto = new DocumentPatchDTO.LanguagePatchDTO(null);
        when(documentsRepository.findById(1L)).thenReturn(Optional.of(existingDocument));

        documentPatchService.patchLanguage(1L, dto);

        assertNull(existingDocument.getLanguage());
    }

    @Test
    @DisplayName("should_update_description")
    void shouldUpdateDescription() {
        DocumentPatchDTO.DescriptionPatchDTO dto = new DocumentPatchDTO.DescriptionPatchDTO("New description");
        when(documentsRepository.findById(1L)).thenReturn(Optional.of(existingDocument));

        documentPatchService.patchDescription(1L, dto);

        assertEquals("New description", existingDocument.getDescription());
    }

    @Test
    @DisplayName("should_set_description_to_null")
    void shouldSetDescriptionToNull() {
        DocumentPatchDTO.DescriptionPatchDTO dto = new DocumentPatchDTO.DescriptionPatchDTO(null);
        when(documentsRepository.findById(1L)).thenReturn(Optional.of(existingDocument));

        documentPatchService.patchDescription(1L, dto);

        assertNull(existingDocument.getDescription());
    }

    @Test
    @DisplayName("should_update_cover_url")
    void shouldUpdateCoverUrl() {
        DocumentPatchDTO.CoverUrlPatchDTO dto = new DocumentPatchDTO.CoverUrlPatchDTO("https://new-url.com/cover.jpg");
        when(documentsRepository.findById(1L)).thenReturn(Optional.of(existingDocument));

        documentPatchService.patchCoverUrl(1L, dto);

        assertEquals("https://new-url.com/cover.jpg", existingDocument.getCoverUrl());
    }

    @Test
    @DisplayName("should_set_cover_url_to_null")
    void shouldSetCoverUrlToNull() {
        DocumentPatchDTO.CoverUrlPatchDTO dto = new DocumentPatchDTO.CoverUrlPatchDTO(null);
        when(documentsRepository.findById(1L)).thenReturn(Optional.of(existingDocument));

        documentPatchService.patchCoverUrl(1L, dto);

        assertNull(existingDocument.getCoverUrl());
    }

    @Test
    @DisplayName("should_throw_on_invalid_cover_url")
    void shouldThrowOnInvalidCoverUrl() {
        DocumentPatchDTO.CoverUrlPatchDTO dto = new DocumentPatchDTO.CoverUrlPatchDTO("not-a-url");
        when(documentsRepository.findById(1L)).thenReturn(Optional.of(existingDocument));

        assertThrows(IllegalArgumentException.class, () ->
                documentPatchService.patchCoverUrl(1L, dto));
    }

    @Test
    @DisplayName("should_update_created_at")
    void shouldUpdateCreatedAt() {
        LocalDateTime newDate = LocalDateTime.of(2026, 6, 30, 15, 42);
        DocumentPatchDTO.CreatedAtPatchDTO dto = new DocumentPatchDTO.CreatedAtPatchDTO(newDate);
        when(documentsRepository.findById(1L)).thenReturn(Optional.of(existingDocument));

        documentPatchService.patchCreatedAt(1L, dto);

        assertEquals(newDate, existingDocument.getCreatedAt());
    }

    @Test
    @DisplayName("should_set_created_at_to_null")
    void shouldSetCreatedAtToNull() {
        DocumentPatchDTO.CreatedAtPatchDTO dto = new DocumentPatchDTO.CreatedAtPatchDTO(null);
        when(documentsRepository.findById(1L)).thenReturn(Optional.of(existingDocument));

        documentPatchService.patchCreatedAt(1L, dto);

        assertNull(existingDocument.getCreatedAt());
    }

    @Test
    @DisplayName("should_throw_when_document_not_found")
    void shouldThrowWhenDocumentNotFound() {
        DocumentPatchDTO.TitlePatchDTO dto = new DocumentPatchDTO.TitlePatchDTO("New Title");
        when(documentsRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () ->
                documentPatchService.patchTitle(1L, dto));
    }
}

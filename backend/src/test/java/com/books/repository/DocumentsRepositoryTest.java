package com.books.repository;

import com.books.model.Document;
import com.books.model.Document.DocumentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DocumentsRepositoryTest {

    @Autowired
    private com.books.repository.DocumentsRepository documentsRepository;

    @Test
    @DisplayName("should_save_document")
    void shouldSaveDocument() {
        Document doc = Document.builder()
                .title("Test Document")
                .documentType(DocumentType.BOOK)
                .createdAt(LocalDateTime.now())
                .build();

        Document saved = documentsRepository.save(doc);

        assertNotNull(saved.getId());
        assertEquals("Test Document", saved.getTitle());
    }

    @Test
    @DisplayName("should_find_document_by_id")
    void shouldFindDocumentById() {
        Document doc = Document.builder()
                .title("Test Document")
                .documentType(DocumentType.DVD)
                .createdAt(LocalDateTime.now())
                .build();
        Document saved = documentsRepository.save(doc);
        documentsRepository.flush();

        var found = documentsRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Test Document", found.get().getTitle());
    }

    @Test
    @DisplayName("should_return_empty_when_document_not_found")
    void shouldReturnEmptyWhenNotFound() {
        var found = documentsRepository.findById(999L);
        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("should_update_title")
    void shouldUpdateTitle() {
        Document doc = Document.builder()
                .title("Original")
                .documentType(DocumentType.BOOK)
                .createdAt(LocalDateTime.now())
                .build();
        Document saved = documentsRepository.save(doc);
        saved.setTitle("Updated");
        documentsRepository.flush();

        var found = documentsRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Updated", found.get().getTitle());
    }

    @Test
    @DisplayName("should_update_subtitle")
    void shouldUpdateSubtitle() {
        Document doc = Document.builder()
                .title("Test")
                .subtitle("Original")
                .documentType(DocumentType.BOOK)
                .createdAt(LocalDateTime.now())
                .build();
        Document saved = documentsRepository.save(doc);
        saved.setSubtitle("Updated");
        documentsRepository.flush();

        var found = documentsRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Updated", found.get().getSubtitle());
    }

    @Test
    @DisplayName("should_update_document_type")
    void shouldUpdateDocumentType() {
        Document doc = Document.builder()
                .title("Test")
                .documentType(DocumentType.BOOK)
                .createdAt(LocalDateTime.now())
                .build();
        Document saved = documentsRepository.save(doc);
        saved.setDocumentType(DocumentType.GAME);
        documentsRepository.flush();

        var found = documentsRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(DocumentType.GAME, found.get().getDocumentType());
    }

    @Test
    @DisplayName("should_update_isbn")
    void shouldUpdateIsbn() {
        Document doc = Document.builder()
                .title("Test")
                .isbn("9780261103573")
                .documentType(DocumentType.BOOK)
                .createdAt(LocalDateTime.now())
                .build();
        Document saved = documentsRepository.save(doc);
        saved.setIsbn("978-1234567890");
        documentsRepository.flush();

        var found = documentsRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("978-1234567890", found.get().getIsbn());
    }

    @Test
    @DisplayName("should_update_publisher")
    void shouldUpdatePublisher() {
        Document doc = Document.builder()
                .title("Test")
                .publisher("Original Publisher")
                .documentType(DocumentType.BOOK)
                .createdAt(LocalDateTime.now())
                .build();
        Document saved = documentsRepository.save(doc);
        saved.setPublisher("Updated Publisher");
        documentsRepository.flush();

        var found = documentsRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Updated Publisher", found.get().getPublisher());
    }

    @Test
    @DisplayName("should_update_publication_year")
    void shouldUpdatePublicationYear() {
        Document doc = Document.builder()
                .title("Test")
                .publicationYear(1954)
                .documentType(DocumentType.BOOK)
                .createdAt(LocalDateTime.now())
                .build();
        Document saved = documentsRepository.save(doc);
        saved.setPublicationYear(2024);
        documentsRepository.flush();

        var found = documentsRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(2024, found.get().getPublicationYear());
    }

    @Test
    @DisplayName("should_update_language")
    void shouldUpdateLanguage() {
        Document doc = Document.builder()
                .title("Test")
                .language("English")
                .documentType(DocumentType.BOOK)
                .createdAt(LocalDateTime.now())
                .build();
        Document saved = documentsRepository.save(doc);
        saved.setLanguage("français");
        documentsRepository.flush();

        var found = documentsRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("français", found.get().getLanguage());
    }

    @Test
    @DisplayName("should_update_description")
    void shouldUpdateDescription() {
        Document doc = Document.builder()
                .title("Test")
                .description("Original")
                .documentType(DocumentType.BOOK)
                .createdAt(LocalDateTime.now())
                .build();
        Document saved = documentsRepository.save(doc);
        saved.setDescription("Updated");
        documentsRepository.flush();

        var found = documentsRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Updated", found.get().getDescription());
    }

    @Test
    @DisplayName("should_update_cover_url")
    void shouldUpdateCoverUrl() {
        Document doc = Document.builder()
                .title("Test")
                .coverUrl("https://example.com/old.jpg")
                .documentType(DocumentType.BOOK)
                .createdAt(LocalDateTime.now())
                .build();
        Document saved = documentsRepository.save(doc);
        saved.setCoverUrl("https://example.com/new.jpg");
        documentsRepository.flush();

        var found = documentsRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("https://example.com/new.jpg", found.get().getCoverUrl());
    }

    @Test
    @DisplayName("should_update_created_at")
    void shouldUpdateCreatedAt() {
        LocalDateTime newDate = LocalDateTime.of(2026, 6, 30, 15, 42);
        Document doc = Document.builder()
                .title("Test")
                .createdAt(LocalDateTime.of(2024, 1, 1, 0, 0))
                .documentType(DocumentType.BOOK)
                .build();
        Document saved = documentsRepository.save(doc);
        saved.setCreatedAt(newDate);
        documentsRepository.flush();

        var found = documentsRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(newDate, found.get().getCreatedAt());
    }

    @Test
    @DisplayName("should_save_nullable_fields_as_null")
    void shouldSaveNullableFieldsAsNull() {
        Document doc = Document.builder()
                .title("Test")
                .subtitle(null)
                .isbn(null)
                .publisher(null)
                .publicationYear(null)
                .language(null)
                .description(null)
                .coverUrl(null)
                .createdAt(LocalDateTime.now())
                .documentType(DocumentType.BOOK)
                .build();
        Document saved = documentsRepository.save(doc);
        documentsRepository.flush();

        var found = documentsRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertNull(found.get().getSubtitle());
        assertNull(found.get().getIsbn());
        assertNull(found.get().getPublisher());
        assertNull(found.get().getPublicationYear());
        assertNull(found.get().getLanguage());
        assertNull(found.get().getDescription());
        assertNull(found.get().getCoverUrl());
    }
}

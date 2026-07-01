package com.books.controller;

import com.books.model.Document;
import com.books.model.Document.DocumentType;
import com.books.model.Item;
import com.books.repository.DocumentsRepository;
import com.books.repository.ItemsRepository;
import com.books.service.DeleteDocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class DeleteDocumentControllerIntegrationTest {

    @Autowired
    private DocumentsRepository documentsRepository;

    @Autowired
    private ItemsRepository itemsRepository;

    @Autowired
    private DeleteDocumentService deleteDocumentService;

    private Long documentWithItemsId;

    @BeforeEach
    void setUp() {
        documentsRepository.deleteAll();
        itemsRepository.deleteAll();

        Document docWithoutItems = Document.builder()
                .title("Document Sans Items")
                .documentType(DocumentType.BOOK)
                .createdAt(java.time.LocalDateTime.now())
                .build();
        documentsRepository.save(docWithoutItems);

        Document docWithItems = Document.builder()
                .title("Document Avec Items")
                .documentType(DocumentType.DVD)
                .createdAt(java.time.LocalDateTime.now())
                .build();
        Document savedDoc = documentsRepository.save(docWithItems);
        documentWithItemsId = savedDoc.getId();

        Item item = Item.builder()
                .document(savedDoc)
                .barcode("ITEM-001")
                .build();
        itemsRepository.save(item);

        documentsRepository.flush();
        itemsRepository.flush();
    }

    @Test
    @DisplayName("DELETE existing document without items returns 204")
    void deleteExistingDocumentReturns204() {
        Document docToDel = Document.builder()
                .title("Doc a supprimer")
                .documentType(DocumentType.GAME)
                .createdAt(java.time.LocalDateTime.now())
                .build();
        Document saved = documentsRepository.save(docToDel);
        documentsRepository.flush();

        assertDoesNotThrow(() -> deleteDocumentService.delete(saved.getId()));
    }

    @Test
    @DisplayName("DELETE unknown document returns 404 via exception")
    void deleteUnknownDocumentReturns404() {
        com.books.exception.DocumentNotFoundException ex = assertThrows(
                com.books.exception.DocumentNotFoundException.class,
                () -> deleteDocumentService.delete(99999L));

        assertTrue(ex.getMessage().contains("999"));
    }

    @Test
    @DisplayName("DELETE document with items returns 409 via exception")
    void deleteDocumentWithItemsReturns409() {
        com.books.exception.DocumentInUseException ex = assertThrows(
                com.books.exception.DocumentInUseException.class,
                () -> deleteDocumentService.delete(documentWithItemsId));

        assertTrue(ex.getMessage().contains("Items are still attached"));
    }

    @Test
    @DisplayName("DELETE should actually remove document from database")
    void deleteShouldActuallyRemoveDocument() {
        Document docToDel = Document.builder()
                .title("Doc a persister")
                .documentType(DocumentType.OTHER)
                .createdAt(java.time.LocalDateTime.now())
                .build();
        Document saved = documentsRepository.save(docToDel);
        documentsRepository.flush();

        deleteDocumentService.delete(saved.getId());

        documentsRepository.flush();
        assertFalse(documentsRepository.findById(saved.getId()).isPresent());
    }

    @Test
    @DisplayName("DELETE should not affect other documents")
    void deleteShouldNotAffectOtherDocuments() {
        Document docToKeep = Document.builder()
                .title("Doc a garder")
                .documentType(DocumentType.BOOK)
                .createdAt(java.time.LocalDateTime.now())
                .build();
        Document kept = documentsRepository.save(docToKeep);
        documentsRepository.flush();

        Document docToDelete = Document.builder()
                .title("Doc a supprimer")
                .documentType(DocumentType.DVD)
                .createdAt(java.time.LocalDateTime.now())
                .build();
        Document deleted = documentsRepository.save(docToDelete);
        documentsRepository.flush();

        deleteDocumentService.delete(deleted.getId());

        documentsRepository.flush();
        assertTrue(documentsRepository.findById(kept.getId()).isPresent());
        assertFalse(documentsRepository.findById(deleted.getId()).isPresent());
    }
}

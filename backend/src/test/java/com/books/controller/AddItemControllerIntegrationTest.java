package com.books.controller;

import com.books.dto.AddItemDTO;
import com.books.model.Document;
import com.books.model.Document.DocumentType;
import com.books.model.Item;
import com.books.model.Item.PhysicalStatus;
import com.books.repository.DocumentsRepository;
import com.books.repository.ItemsRepository;
import com.books.service.AddItemToDocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AddItemControllerIntegrationTest {

    @Autowired
    private DocumentsRepository documentsRepository;

    @Autowired
    private ItemsRepository itemsRepository;

    @Autowired
    private AddItemToDocumentService addItemToDocumentService;

    private Long existingDocumentId;

    @BeforeEach
    void setUp() {
        documentsRepository.deleteAll();
        itemsRepository.deleteAll();

        Document doc = Document.builder()
                .title("Document avec items")
                .documentType(DocumentType.BOOK)
                .build();
        Document saved = documentsRepository.save(doc);
        existingDocumentId = saved.getId();

        itemsRepository.flush();
    }

    @Test
    @DisplayName("POST item returns 201 and creates item")
    void postItemReturns201() {
        AddItemDTO dto = AddItemDTO.builder()
                .barcode("ITEM-NEW-001")
                .location("Shelf A1")
                .physicalStatus("CLEAN")
                .build();

        assertDoesNotThrow(() -> addItemToDocumentService.addItem(existingDocumentId, dto));

        assertTrue(itemsRepository.existsByBarcode("ITEM-NEW-001"));
        Item found = itemsRepository.findByBarcode("ITEM-NEW-001").orElseThrow();
        assertEquals(PhysicalStatus.CLEAN, found.getPhysicalStatus());
        assertEquals("Shelf A1", found.getLocation());
    }

    @Test
    @DisplayName("POST item returns 404 when document missing")
    void postItemReturns404WhenDocumentMissing() {
        AddItemDTO dto = AddItemDTO.builder()
                .barcode("ITEM-001")
                .build();

        com.books.exception.DocumentNotFoundException ex = assertThrows(
                com.books.exception.DocumentNotFoundException.class,
                () -> addItemToDocumentService.addItem(99999L, dto));

        assertTrue(ex.getMessage().contains("999"));
    }

    @Test
    @DisplayName("POST item returns 409 when duplicate barcode")
    void postItemReturns409WhenDuplicateBarcode() {
        AddItemDTO dto1 = AddItemDTO.builder()
                .barcode("ITEM-DUP-001")
                .location("Shelf A1")
                .build();
        addItemToDocumentService.addItem(existingDocumentId, dto1);

        AddItemDTO dto2 = AddItemDTO.builder()
                .barcode("ITEM-DUP-001")
                .location("Shelf B2")
                .build();

        com.books.exception.DuplicateBarcodeException ex = assertThrows(
                com.books.exception.DuplicateBarcodeException.class,
                () -> addItemToDocumentService.addItem(existingDocumentId, dto2));

        assertTrue(ex.getMessage().contains("ITEM-DUP-001"));
        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    @DisplayName("POST item defaults status to CLEAN")
    void postItemDefaultsStatusToClean() {
        AddItemDTO dto = AddItemDTO.builder()
                .barcode("ITEM-DEFAULT")
                .build();

        addItemToDocumentService.addItem(existingDocumentId, dto);

        Item found = itemsRepository.findByBarcode("ITEM-DEFAULT").orElseThrow();
        assertEquals(PhysicalStatus.CLEAN, found.getPhysicalStatus());
    }

    @Test
    @DisplayName("POST item with all fields")
    void postItemWithAllFields() {
        java.time.LocalDate acquisitionDate = java.time.LocalDate.of(2024, 6, 15);
        AddItemDTO dto = AddItemDTO.builder()
                .barcode("ITEM-FULL")
                .location("Shelf C3")
                .acquisitionDate(acquisitionDate)
                .physicalStatus("DAMAGED")
                .build();

        addItemToDocumentService.addItem(existingDocumentId, dto);

        Item found = itemsRepository.findByBarcode("ITEM-FULL").orElseThrow();
        assertEquals("ITEM-FULL", found.getBarcode());
        assertEquals("Shelf C3", found.getLocation());
        assertEquals(acquisitionDate, found.getAcquisitionDate());
        assertEquals(PhysicalStatus.DAMAGED, found.getPhysicalStatus());
    }

    @Test
    @DisplayName("POST item links item to correct document")
    void postItemLinksToCorrectDocument() {
        AddItemDTO dto = AddItemDTO.builder()
                .barcode("ITEM-LINKED")
                .location("Shelf D1")
                .build();

        addItemToDocumentService.addItem(existingDocumentId, dto);

        Item found = itemsRepository.findByBarcode("ITEM-LINKED").orElseThrow();
        assertEquals(existingDocumentId, found.getDocument().getId());
    }

    @Test
    @DisplayName("POST item does not modify document structure")
    void postItemDoesNotModifyDocument() {
        Document docBefore = documentsRepository.findById(existingDocumentId).orElseThrow();
        String titleBefore = docBefore.getTitle();

        AddItemDTO dto = AddItemDTO.builder()
                .barcode("ITEM-NOMOD")
                .build();

        addItemToDocumentService.addItem(existingDocumentId, dto);

        Document docAfter = documentsRepository.findById(existingDocumentId).orElseThrow();
        assertEquals(titleBefore, docAfter.getTitle());
    }

    @Test
    @DisplayName("POST item with different status values")
    void postItemWithDifferentStatuses() {
        String[] statuses = {"CLEAN", "LOST", "DAMAGED", "REPAIR"};

        for (int i = 0; i < statuses.length; i++) {
            AddItemDTO dto = AddItemDTO.builder()
                    .barcode("ITEM-STATUS-" + i)
                    .physicalStatus(statuses[i])
                    .build();

            addItemToDocumentService.addItem(existingDocumentId, dto);
        }

        for (int i = 0; i < statuses.length; i++) {
            Item found = itemsRepository.findByBarcode("ITEM-STATUS-" + i).orElseThrow();
            assertEquals(PhysicalStatus.valueOf(statuses[i]), found.getPhysicalStatus());
        }
    }
}

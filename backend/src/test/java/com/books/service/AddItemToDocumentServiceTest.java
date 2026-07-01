package com.books.service;

import com.books.dto.AddItemDTO;
import com.books.dto.ItemDTO;
import com.books.exception.DuplicateBarcodeException;
import com.books.exception.DocumentNotFoundException;
import com.books.model.Document;
import com.books.model.Item;
import com.books.model.Item.PhysicalStatus;
import com.books.repository.DocumentsRepository;
import com.books.repository.ItemsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddItemToDocumentServiceTest {

    @Mock
    private DocumentsRepository documentsRepository;

    @Mock
    private ItemsRepository itemsRepository;

    @InjectMocks
    private AddItemToDocumentService addItemToDocumentService;

    private Document existingDocument;

    @BeforeEach
    void setUp() {
        existingDocument = Document.builder()
                .id(1L)
                .title("Test Document")
                .documentType(Document.DocumentType.BOOK)
                .build();
    }

    @Test
    @DisplayName("shouldCreateItemWhenDocumentExists")
    void shouldCreateItemWhenDocumentExists() {
        AddItemDTO dto = AddItemDTO.builder()
                .barcode("ITEM-001")
                .location("Shelf A3")
                .physicalStatus("CLEAN")
                .build();

        when(documentsRepository.findById(1L)).thenReturn(Optional.of(existingDocument));
        when(itemsRepository.existsByBarcode("ITEM-001")).thenReturn(false);
        when(itemsRepository.save(any(Item.class))).thenAnswer(inv -> {
            Item i = inv.getArgument(0);
            return Item.builder()
                    .id(1L)
                    .barcode(i.getBarcode())
                    .document(i.getDocument())
                    .physicalStatus(i.getPhysicalStatus())
                    .location(i.getLocation())
                    .build();
        });

        ItemDTO result = addItemToDocumentService.addItem(1L, dto);

        assertNotNull(result);
        assertEquals("ITEM-001", result.getBarcode());
        assertEquals("Shelf A3", result.getLocation());
        assertEquals("CLEAN", result.getPhysicalStatus());
        verify(itemsRepository).save(any(Item.class));
    }

    @Test
    @DisplayName("shouldDefaultStatusToClean")
    void shouldDefaultStatusToClean() {
        AddItemDTO dto = AddItemDTO.builder()
                .barcode("ITEM-002")
                .build();

        when(documentsRepository.findById(1L)).thenReturn(Optional.of(existingDocument));
        when(itemsRepository.existsByBarcode("ITEM-002")).thenReturn(false);
        when(itemsRepository.save(any(Item.class))).thenAnswer(inv -> {
            Item i = inv.getArgument(0);
            return Item.builder()
                    .id(2L)
                    .barcode(i.getBarcode())
                    .document(i.getDocument())
                    .physicalStatus(i.getPhysicalStatus())
                    .build();
        });

        ItemDTO result = addItemToDocumentService.addItem(1L, dto);

        assertNotNull(result);
        assertEquals(PhysicalStatus.CLEAN.name(), result.getPhysicalStatus());
    }

    @Test
    @DisplayName("shouldRejectDuplicateBarcode")
    void shouldRejectDuplicateBarcode() {
        AddItemDTO dto = AddItemDTO.builder()
                .barcode("ITEM-DUP")
                .build();

        when(itemsRepository.existsByBarcode("ITEM-DUP")).thenReturn(true);
        when(documentsRepository.findById(1L)).thenReturn(Optional.of(existingDocument));

        DuplicateBarcodeException ex = assertThrows(DuplicateBarcodeException.class,
                () -> addItemToDocumentService.addItem(1L, dto));

        assertTrue(ex.getMessage().contains("ITEM-DUP"));
        assertTrue(ex.getMessage().contains("already exists"));
    }

    @Test
    @DisplayName("shouldThrowNotFoundWhenDocumentMissing")
    void shouldThrowNotFoundWhenDocumentMissing() {
        AddItemDTO dto = AddItemDTO.builder()
                .barcode("ITEM-NEW")
                .build();

        when(documentsRepository.findById(999L)).thenReturn(Optional.empty());

        DocumentNotFoundException ex = assertThrows(DocumentNotFoundException.class,
                () -> addItemToDocumentService.addItem(999L, dto));

        assertTrue(ex.getMessage().contains("999"));
    }

    @Test
    @DisplayName("shouldThrowConflictWhenBarcodeAlreadyExists")
    void shouldThrowConflictWhenBarcodeAlreadyExists() {
        AddItemDTO dto = AddItemDTO.builder()
                .barcode("EXISTING-BARCODE")
                .location("Shelf B1")
                .physicalStatus("DAMAGED")
                .build();

        when(itemsRepository.existsByBarcode("EXISTING-BARCODE")).thenReturn(true);
        when(documentsRepository.findById(1L)).thenReturn(Optional.of(existingDocument));

        DuplicateBarcodeException ex = assertThrows(DuplicateBarcodeException.class,
                () -> addItemToDocumentService.addItem(1L, dto));

        assertTrue(ex.getMessage().contains("EXISTING-BARCODE"));
    }

    @Test
    @DisplayName("shouldThrowOnInvalidPhysicalStatus")
    void shouldThrowOnInvalidPhysicalStatus() {
        AddItemDTO dto = AddItemDTO.builder()
                .barcode("ITEM-BAD")
                .physicalStatus("INVALID_STATUS")
                .build();

        when(documentsRepository.findById(1L)).thenReturn(Optional.of(existingDocument));
        when(itemsRepository.existsByBarcode("ITEM-BAD")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> addItemToDocumentService.addItem(1L, dto));

        assertTrue(ex.getMessage().contains("Invalid physical status"));
    }

    @Test
    @DisplayName("shouldAcceptAllValidPhysicalStatuses")
    void shouldAcceptAllValidPhysicalStatuses() {
        String[] validStatuses = {"CLEAN", "LOST", "DAMAGED", "REPAIR"};

        for (String status : validStatuses) {
            AddItemDTO dto = AddItemDTO.builder()
                    .barcode("ITEM-" + status)
                    .physicalStatus(status)
                    .build();

            when(documentsRepository.findById(1L)).thenReturn(Optional.of(existingDocument));
            when(itemsRepository.existsByBarcode("ITEM-" + status)).thenReturn(false);
            when(itemsRepository.save(any(Item.class))).thenAnswer(inv -> {
                Item i = inv.getArgument(0);
                return Item.builder()
                        .id(10L)
                        .barcode(i.getBarcode())
                        .document(i.getDocument())
                        .physicalStatus(i.getPhysicalStatus())
                        .build();
            });

            ItemDTO result = addItemToDocumentService.addItem(1L, dto);
            assertEquals(status, result.getPhysicalStatus());

            lenient().when(documentsRepository.findById(1L)).thenReturn(Optional.of(existingDocument));
            lenient().when(itemsRepository.existsByBarcode("ITEM-" + status)).thenReturn(false);
            lenient().when(itemsRepository.save(any(Item.class))).thenAnswer(inv2 -> {
                Item i = inv2.getArgument(0);
                return Item.builder()
                        .id(10L)
                        .barcode(i.getBarcode())
                        .document(i.getDocument())
                        .physicalStatus(i.getPhysicalStatus())
                        .build();
            });
        }
    }

    @Test
    @DisplayName("shouldIncludeOptionalFields")
    void shouldIncludeOptionalFields() {
        LocalDate acquisitionDate = LocalDate.of(2024, 6, 15);
        AddItemDTO dto = AddItemDTO.builder()
                .barcode("ITEM-FULL")
                .location("Shelf C5")
                .acquisitionDate(acquisitionDate)
                .physicalStatus("LOST")
                .build();

        when(documentsRepository.findById(1L)).thenReturn(Optional.of(existingDocument));
        when(itemsRepository.existsByBarcode("ITEM-FULL")).thenReturn(false);
        when(itemsRepository.save(any(Item.class))).thenAnswer(inv -> {
            Item i = inv.getArgument(0);
            return Item.builder()
                    .id(1L)
                    .barcode(i.getBarcode())
                    .document(i.getDocument())
                    .physicalStatus(i.getPhysicalStatus())
                    .location(i.getLocation())
                    .acquisitionDate(i.getAcquisitionDate())
                    .build();
        });

        ItemDTO result = addItemToDocumentService.addItem(1L, dto);

        assertEquals("ITEM-FULL", result.getBarcode());
        assertEquals("Shelf C5", result.getLocation());
        assertEquals(acquisitionDate, result.getAcquisitionDate());
        assertEquals("LOST", result.getPhysicalStatus());
    }
}

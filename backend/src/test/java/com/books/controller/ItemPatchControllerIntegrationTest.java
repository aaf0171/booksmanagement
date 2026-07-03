package com.books.controller;

import com.books.dto.ItemPatchDTO;
import com.books.model.Document;
import com.books.model.Document.DocumentType;
import com.books.model.Item;
import com.books.model.Item.PhysicalStatus;
import com.books.repository.DocumentsRepository;
import com.books.repository.ItemsRepository;
import com.books.service.ItemPatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ItemPatchControllerIntegrationTest {

    @Autowired
    private DocumentsRepository documentsRepository;

    @Autowired
    private ItemsRepository itemsRepository;

    @Autowired
    private ItemPatchService itemPatchService;

    private Long existingItemId;

    @BeforeEach
    void setUp() {
        documentsRepository.deleteAll();
        itemsRepository.deleteAll();

        Document doc = Document.builder()
                .title("Document for patch tests")
                .documentType(DocumentType.BOOK)
                .build();
        Document savedDoc = documentsRepository.save(doc);

        Item item = Item.builder()
                .barcode("BARCODE-001")
                .physicalStatus(PhysicalStatus.CLEAN)
                .location("Shelf A1")
                .acquisitionDate(LocalDate.of(2024, 1, 15))
                .document(doc)
                .build();
        Item savedItem = itemsRepository.save(item);
        existingItemId = savedItem.getId();
    }

    @Test
    @DisplayName("PATCH barcode updates item barcode")
    void patchBarcodeUpdatesItemBarcode() {
        ItemPatchDTO.BarcodePatchDTO dto = new ItemPatchDTO.BarcodePatchDTO("NEW-BARCODE-001");
        itemPatchService.patchBarcode(existingItemId, dto);

        Item updated = itemsRepository.findById(existingItemId).orElseThrow();
        assertEquals("NEW-BARCODE-001", updated.getBarcode());
    }

    @Test
    @DisplayName("PATCH barcode throws 400 when item not found")
    void patchBarcodeThrowsWhenItemNotFound() {
        ItemPatchDTO.BarcodePatchDTO dto = new ItemPatchDTO.BarcodePatchDTO("NEW");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> itemPatchService.patchBarcode(99999L, dto));

        assertTrue(ex.getMessage().contains("99999"));
    }

    @Test
    @DisplayName("PATCH status updates item status to DAMAGED")
    void patchStatusUpdatesToDamaged() {
        ItemPatchDTO.StatusPatchDTO dto = new ItemPatchDTO.StatusPatchDTO("DAMAGED");
        itemPatchService.patchStatus(existingItemId, dto);

        Item updated = itemsRepository.findById(existingItemId).orElseThrow();
        assertEquals(PhysicalStatus.DAMAGED, updated.getPhysicalStatus());
    }

    @Test
    @DisplayName("PATCH status updates item status to LOST")
    void patchStatusUpdatesToLost() {
        ItemPatchDTO.StatusPatchDTO dto = new ItemPatchDTO.StatusPatchDTO("LOST");
        itemPatchService.patchStatus(existingItemId, dto);

        Item updated = itemsRepository.findById(existingItemId).orElseThrow();
        assertEquals(PhysicalStatus.LOST, updated.getPhysicalStatus());
    }

    @Test
    @DisplayName("PATCH status updates item status to REPAIR")
    void patchStatusUpdatesToRepair() {
        ItemPatchDTO.StatusPatchDTO dto = new ItemPatchDTO.StatusPatchDTO("REPAIR");
        itemPatchService.patchStatus(existingItemId, dto);

        Item updated = itemsRepository.findById(existingItemId).orElseThrow();
        assertEquals(PhysicalStatus.REPAIR, updated.getPhysicalStatus());
    }

    @Test
    @DisplayName("PATCH status throws on invalid value")
    void patchStatusThrowsOnInvalidValue() {
        ItemPatchDTO.StatusPatchDTO dto = new ItemPatchDTO.StatusPatchDTO("BROKEN");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> itemPatchService.patchStatus(existingItemId, dto));

        assertTrue(ex.getMessage().contains("Invalid physical status"));
    }

    @Test
    @DisplayName("PATCH status is case insensitive")
    void patchStatusIsCaseInsensitive() {
        ItemPatchDTO.StatusPatchDTO dto = new ItemPatchDTO.StatusPatchDTO("damaged");
        itemPatchService.patchStatus(existingItemId, dto);

        Item updated = itemsRepository.findById(existingItemId).orElseThrow();
        assertEquals(PhysicalStatus.DAMAGED, updated.getPhysicalStatus());
    }

    @Test
    @DisplayName("PATCH status throws when item not found")
    void patchStatusThrowsWhenItemNotFound() {
        ItemPatchDTO.StatusPatchDTO dto = new ItemPatchDTO.StatusPatchDTO("DAMAGED");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> itemPatchService.patchStatus(99999L, dto));

        assertTrue(ex.getMessage().contains("99999"));
    }

    @Test
    @DisplayName("PATCH location updates item location")
    void patchLocationUpdatesItemLocation() {
        ItemPatchDTO.LocationPatchDTO dto = new ItemPatchDTO.LocationPatchDTO("Shelf B5");
        itemPatchService.patchLocation(existingItemId, dto);

        Item updated = itemsRepository.findById(existingItemId).orElseThrow();
        assertEquals("Shelf B5", updated.getLocation());
    }

    @Test
    @DisplayName("PATCH location sets location to null")
    void patchLocationSetsToNull() {
        ItemPatchDTO.LocationPatchDTO dto = new ItemPatchDTO.LocationPatchDTO(null);
        itemPatchService.patchLocation(existingItemId, dto);

        Item updated = itemsRepository.findById(existingItemId).orElseThrow();
        assertNull(updated.getLocation());
    }

    @Test
    @DisplayName("PATCH location throws when item not found")
    void patchLocationThrowsWhenItemNotFound() {
        ItemPatchDTO.LocationPatchDTO dto = new ItemPatchDTO.LocationPatchDTO("Shelf X");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> itemPatchService.patchLocation(99999L, dto));

        assertTrue(ex.getMessage().contains("99999"));
    }

    @Test
    @DisplayName("PATCH acquisition-date updates item acquisition date")
    void patchAcquisitionDateUpdatesItemDate() {
        LocalDate newDate = LocalDate.of(2025, 8, 10);
        ItemPatchDTO.AcquisitionDatePatchDTO dto = new ItemPatchDTO.AcquisitionDatePatchDTO(newDate);
        itemPatchService.patchAcquisitionDate(existingItemId, dto);

        Item updated = itemsRepository.findById(existingItemId).orElseThrow();
        assertEquals(newDate, updated.getAcquisitionDate());
    }

    @Test
    @DisplayName("PATCH acquisition-date sets date to null")
    void patchAcquisitionDateSetsToNull() {
        ItemPatchDTO.AcquisitionDatePatchDTO dto = new ItemPatchDTO.AcquisitionDatePatchDTO(null);
        itemPatchService.patchAcquisitionDate(existingItemId, dto);

        Item updated = itemsRepository.findById(existingItemId).orElseThrow();
        assertNull(updated.getAcquisitionDate());
    }

    @Test
    @DisplayName("PATCH acquisition-date throws when item not found")
    void patchAcquisitionDateThrowsWhenItemNotFound() {
        ItemPatchDTO.AcquisitionDatePatchDTO dto = new ItemPatchDTO.AcquisitionDatePatchDTO(LocalDate.of(2025, 1, 1));
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> itemPatchService.patchAcquisitionDate(99999L, dto));

        assertTrue(ex.getMessage().contains("99999"));
    }

    @Test
    @DisplayName("PATCH does not modify unrequested fields")
    void patchDoesNotModifyUnrequestedFields() {
        Long itemId = existingItemId;

        ItemPatchDTO.StatusPatchDTO dto = new ItemPatchDTO.StatusPatchDTO("DAMAGED");
        itemPatchService.patchStatus(itemId, dto);

        Item updated = itemsRepository.findById(itemId).orElseThrow();
        assertEquals("BARCODE-001", updated.getBarcode());
        assertEquals("Shelf A1", updated.getLocation());
        assertEquals(LocalDate.of(2024, 1, 15), updated.getAcquisitionDate());
    }
}

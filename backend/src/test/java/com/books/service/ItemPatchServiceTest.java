package com.books.service;

import com.books.dto.ItemPatchDTO;
import com.books.model.Item;
import com.books.model.Item.PhysicalStatus;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemPatchServiceTest {

    @Mock
    private ItemsRepository itemsRepository;

    @InjectMocks
    private ItemPatchService itemPatchService;

    private Item existingItem;

    @BeforeEach
    void setUp() {
        existingItem = Item.builder()
                .id(1L)
                .barcode("BARCODE-001")
                .physicalStatus(PhysicalStatus.CLEAN)
                .location("Shelf A1")
                .acquisitionDate(LocalDate.of(2024, 1, 15))
                .build();
    }

    @Test
    @DisplayName("should_update_barcode")
    void shouldUpdateBarcode() {
        ItemPatchDTO.BarcodePatchDTO dto = new ItemPatchDTO.BarcodePatchDTO("BARCODE-NEW");
        when(itemsRepository.findById(1L)).thenReturn(Optional.of(existingItem));

        itemPatchService.patchBarcode(1L, dto);

        assertEquals("BARCODE-NEW", existingItem.getBarcode());
        verify(itemsRepository).findById(1L);
    }

    @Test
    @DisplayName("should_update_status")
    void shouldUpdateStatus() {
        ItemPatchDTO.StatusPatchDTO dto = new ItemPatchDTO.StatusPatchDTO("DAMAGED");
        when(itemsRepository.findById(1L)).thenReturn(Optional.of(existingItem));

        itemPatchService.patchStatus(1L, dto);

        assertEquals(PhysicalStatus.DAMAGED, existingItem.getPhysicalStatus());
    }

    @Test
    @DisplayName("should_update_status_to_clean")
    void shouldUpdateStatusToClean() {
        ItemPatchDTO.StatusPatchDTO dto = new ItemPatchDTO.StatusPatchDTO("CLEAN");
        when(itemsRepository.findById(1L)).thenReturn(Optional.of(existingItem));

        itemPatchService.patchStatus(1L, dto);

        assertEquals(PhysicalStatus.CLEAN, existingItem.getPhysicalStatus());
    }

    @Test
    @DisplayName("should_update_status_to_lost")
    void shouldUpdateStatusToLost() {
        ItemPatchDTO.StatusPatchDTO dto = new ItemPatchDTO.StatusPatchDTO("LOST");
        when(itemsRepository.findById(1L)).thenReturn(Optional.of(existingItem));

        itemPatchService.patchStatus(1L, dto);

        assertEquals(PhysicalStatus.LOST, existingItem.getPhysicalStatus());
    }

    @Test
    @DisplayName("should_update_status_to_repair")
    void shouldUpdateStatusToRepair() {
        ItemPatchDTO.StatusPatchDTO dto = new ItemPatchDTO.StatusPatchDTO("REPAIR");
        when(itemsRepository.findById(1L)).thenReturn(Optional.of(existingItem));

        itemPatchService.patchStatus(1L, dto);

        assertEquals(PhysicalStatus.REPAIR, existingItem.getPhysicalStatus());
    }

    @Test
    @DisplayName("should_accept_case_insensitive_status")
    void shouldAcceptCaseInsensitiveStatus() {
        ItemPatchDTO.StatusPatchDTO dto = new ItemPatchDTO.StatusPatchDTO("damaged");
        when(itemsRepository.findById(1L)).thenReturn(Optional.of(existingItem));

        itemPatchService.patchStatus(1L, dto);

        assertEquals(PhysicalStatus.DAMAGED, existingItem.getPhysicalStatus());
    }

    @Test
    @DisplayName("should_throw_on_invalid_status")
    void shouldThrowOnInvalidStatus() {
        ItemPatchDTO.StatusPatchDTO dto = new ItemPatchDTO.StatusPatchDTO("INVALID");
        when(itemsRepository.findById(1L)).thenReturn(Optional.of(existingItem));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> itemPatchService.patchStatus(1L, dto));

        assertTrue(ex.getMessage().contains("Invalid physical status"));
    }

    @Test
    @DisplayName("should_update_location")
    void shouldUpdateLocation() {
        ItemPatchDTO.LocationPatchDTO dto = new ItemPatchDTO.LocationPatchDTO("Shelf B5");
        when(itemsRepository.findById(1L)).thenReturn(Optional.of(existingItem));

        itemPatchService.patchLocation(1L, dto);

        assertEquals("Shelf B5", existingItem.getLocation());
    }

    @Test
    @DisplayName("should_set_location_to_null")
    void shouldSetLocationToNull() {
        ItemPatchDTO.LocationPatchDTO dto = new ItemPatchDTO.LocationPatchDTO(null);
        when(itemsRepository.findById(1L)).thenReturn(Optional.of(existingItem));

        itemPatchService.patchLocation(1L, dto);

        assertNull(existingItem.getLocation());
    }

    @Test
    @DisplayName("should_update_acquisition_date")
    void shouldUpdateAcquisitionDate() {
        LocalDate newDate = LocalDate.of(2025, 6, 20);
        ItemPatchDTO.AcquisitionDatePatchDTO dto = new ItemPatchDTO.AcquisitionDatePatchDTO(newDate);
        when(itemsRepository.findById(1L)).thenReturn(Optional.of(existingItem));

        itemPatchService.patchAcquisitionDate(1L, dto);

        assertEquals(newDate, existingItem.getAcquisitionDate());
    }

    @Test
    @DisplayName("should_set_acquisition_date_to_null")
    void shouldSetAcquisitionDateToNull() {
        ItemPatchDTO.AcquisitionDatePatchDTO dto = new ItemPatchDTO.AcquisitionDatePatchDTO(null);
        when(itemsRepository.findById(1L)).thenReturn(Optional.of(existingItem));

        itemPatchService.patchAcquisitionDate(1L, dto);

        assertNull(existingItem.getAcquisitionDate());
    }

    @Test
    @DisplayName("should_throw_when_item_not_found")
    void shouldThrowWhenItemNotFound() {
        ItemPatchDTO.BarcodePatchDTO dto = new ItemPatchDTO.BarcodePatchDTO("NEW");
        when(itemsRepository.findById(999L)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> itemPatchService.patchBarcode(999L, dto));

        assertTrue(ex.getMessage().contains("999"));
    }

    @Test
    @DisplayName("should_not_modify_unrequested_fields_when_updating_status")
    void shouldNotModifyUnrequestedFieldsWhenUpdatingStatus() {
        ItemPatchDTO.StatusPatchDTO dto = new ItemPatchDTO.StatusPatchDTO("DAMAGED");
        when(itemsRepository.findById(1L)).thenReturn(Optional.of(existingItem));

        itemPatchService.patchStatus(1L, dto);

        assertEquals("BARCODE-001", existingItem.getBarcode());
        assertEquals("Shelf A1", existingItem.getLocation());
        assertEquals(LocalDate.of(2024, 1, 15), existingItem.getAcquisitionDate());
    }
}

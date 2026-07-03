package com.books.controller;

import com.books.dto.ItemPatchDTO;
import com.books.service.ItemPatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ItemPatchControllerTest {

    private ItemPatchController controller;
    private ItemPatchService service;

    @BeforeEach
    void setUp() {
        service = mock(ItemPatchService.class);
        controller = new ItemPatchController(service);
    }

    @Test
    @DisplayName("PATCH barcode - should return 204")
    void shouldPatchBarcode() {
        var dto = new ItemPatchDTO.BarcodePatchDTO("NEW-BARCODE");
        doNothing().when(service).patchBarcode(1L, dto);

        ResponseEntity<Void> response = controller.patchBarcode(1L, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).patchBarcode(1L, dto);
    }

    @Test
    @DisplayName("PATCH status - should return 204")
    void shouldPatchStatus() {
        var dto = new ItemPatchDTO.StatusPatchDTO("DAMAGED");
        doNothing().when(service).patchStatus(1L, dto);

        ResponseEntity<Void> response = controller.patchStatus(1L, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).patchStatus(1L, dto);
    }

    @Test
    @DisplayName("PATCH location - should return 204")
    void shouldPatchLocation() {
        var dto = new ItemPatchDTO.LocationPatchDTO("Shelf B5");
        doNothing().when(service).patchLocation(1L, dto);

        ResponseEntity<Void> response = controller.patchLocation(1L, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).patchLocation(1L, dto);
    }

    @Test
    @DisplayName("PATCH location - should accept null value")
    void shouldPatchLocationToNull() {
        var dto = new ItemPatchDTO.LocationPatchDTO(null);
        doNothing().when(service).patchLocation(1L, dto);

        ResponseEntity<Void> response = controller.patchLocation(1L, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).patchLocation(1L, dto);
    }

    @Test
    @DisplayName("PATCH acquisition-date - should return 204")
    void shouldPatchAcquisitionDate() {
        var dto = new ItemPatchDTO.AcquisitionDatePatchDTO(LocalDate.of(2024, 3, 15));
        doNothing().when(service).patchAcquisitionDate(1L, dto);

        ResponseEntity<Void> response = controller.patchAcquisitionDate(1L, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).patchAcquisitionDate(1L, dto);
    }

    @Test
    @DisplayName("PATCH acquisition-date - should accept null value")
    void shouldPatchAcquisitionDateToNull() {
        var dto = new ItemPatchDTO.AcquisitionDatePatchDTO(null);
        doNothing().when(service).patchAcquisitionDate(1L, dto);

        ResponseEntity<Void> response = controller.patchAcquisitionDate(1L, dto);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(service).patchAcquisitionDate(1L, dto);
    }
}

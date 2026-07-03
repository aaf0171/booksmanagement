package com.books.controller;

import com.books.dto.ItemPatchDTO;
import com.books.service.ItemPatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemPatchController {

    private final ItemPatchService itemPatchService;

    @PatchMapping("/{id}/barcode")
    public ResponseEntity<Void> patchBarcode(
            @PathVariable Long id,
            @Valid @RequestBody ItemPatchDTO.BarcodePatchDTO dto) {
        itemPatchService.patchBarcode(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> patchStatus(
            @PathVariable Long id,
            @Valid @RequestBody ItemPatchDTO.StatusPatchDTO dto) {
        itemPatchService.patchStatus(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/location")
    public ResponseEntity<Void> patchLocation(
            @PathVariable Long id,
            @Valid @RequestBody ItemPatchDTO.LocationPatchDTO dto) {
        itemPatchService.patchLocation(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/acquisition-date")
    public ResponseEntity<Void> patchAcquisitionDate(
            @PathVariable Long id,
            @Valid @RequestBody ItemPatchDTO.AcquisitionDatePatchDTO dto) {
        itemPatchService.patchAcquisitionDate(id, dto);
        return ResponseEntity.noContent().build();
    }
}

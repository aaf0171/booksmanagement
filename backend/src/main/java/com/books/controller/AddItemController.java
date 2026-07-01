package com.books.controller;

import com.books.dto.AddItemDTO;
import com.books.dto.ItemDTO;
import com.books.service.AddItemToDocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class AddItemController {

    private final AddItemToDocumentService addItemToDocumentService;

    @PostMapping("/{documentId}/items")
    public ResponseEntity<ItemDTO> addItem(
            @PathVariable Long documentId,
            @Valid @RequestBody AddItemDTO dto) {
        ItemDTO result = addItemToDocumentService.addItem(documentId, dto);
        return ResponseEntity.status(201).body(result);
    }
}

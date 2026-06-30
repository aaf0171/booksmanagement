package com.books.controller;

import com.books.dto.DocumentWithItemsDTO;
import com.books.dto.DocumentWithItemsResponseDTO;
import com.books.service.CreateDocumentWithItemsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentCommandController {

    private final CreateDocumentWithItemsService createDocumentWithItemsService;

    @PostMapping
    public ResponseEntity<DocumentWithItemsResponseDTO> createDocument(
            @Valid @RequestBody DocumentWithItemsDTO dto) {
        DocumentWithItemsResponseDTO response = createDocumentWithItemsService.create(dto);
        return ResponseEntity.status(201).body(response);
    }
}

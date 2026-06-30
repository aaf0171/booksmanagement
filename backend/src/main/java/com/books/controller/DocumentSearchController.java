package com.books.controller;

import com.books.dto.DocumentSearchDTO;
import com.books.service.DocumentSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/documents/search")
@RequiredArgsConstructor
public class DocumentSearchController {

    private final DocumentSearchService documentSearchService;

    @GetMapping
    public ResponseEntity<List<DocumentSearchDTO>> searchDocuments(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String subtitle,
            @RequestParam(required = false) String documentType,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) Integer publicationYear) {

        List<DocumentSearchDTO> results = documentSearchService.searchDocuments(
                query, title, subtitle, documentType, isbn, publisher, publicationYear);
        return ResponseEntity.ok(results);
    }
}

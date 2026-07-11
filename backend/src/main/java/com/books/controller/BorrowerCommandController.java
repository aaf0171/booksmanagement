package com.books.controller;

import com.books.dto.CreateBorrowerDTO;
import com.books.dto.CreateBorrowerResponseDTO;
import com.books.service.CreateBorrowerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/borrowers")
@RequiredArgsConstructor
public class BorrowerCommandController {

    private final CreateBorrowerService createBorrowerService;

    @PostMapping
    public ResponseEntity<CreateBorrowerResponseDTO> createBorrower(@Valid @RequestBody CreateBorrowerDTO dto) {
        CreateBorrowerResponseDTO response = createBorrowerService.create(dto);
        return ResponseEntity.status(201).body(response);
    }
}

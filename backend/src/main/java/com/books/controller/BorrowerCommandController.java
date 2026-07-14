package com.books.controller;

import com.books.dto.CreateBorrowerDTO;
import com.books.dto.CreateBorrowerResponseDTO;
import com.books.service.CreateBorrowerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Borrower", description = "Borrower management APIs")
public class BorrowerCommandController {

    private final CreateBorrowerService createBorrowerService;

    @PostMapping
    @Operation(summary = "Create a new borrower", description = "Creates a new borrower with a login account and sends an activation email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Borrower created, activation email sent",
                    content = @Content(schema = @Schema(implementation = CreateBorrowerResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content),
            @ApiResponse(responseCode = "409", description = "Username already exists",
                    content = @Content)
    })
    public ResponseEntity<CreateBorrowerResponseDTO> createBorrower(
            @Parameter(description = "Borrower creation data", required = true)
            @Valid @RequestBody CreateBorrowerDTO dto) {
        CreateBorrowerResponseDTO response = createBorrowerService.create(dto);
        return ResponseEntity.status(201).body(response);
    }
}

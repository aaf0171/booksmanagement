package com.books.dto;

import com.books.enums.ActivationStatus;

public record ActivationResponseDTO(
    ActivationStatus status,
    String message,
    String email
) {}

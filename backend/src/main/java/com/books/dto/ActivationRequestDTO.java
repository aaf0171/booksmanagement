package com.books.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivationRequestDTO {
    @NotBlank(message = "Token is required")
    private String token;
    private String password;
    private String confirmPassword;
}

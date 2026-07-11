package com.books.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBorrowerDTO {

    @NotBlank(message = "Firstname must not be blank")
    @Size(max = 100, message = "Firstname must be at most 100 characters")
    private String firstname;

    @NotBlank(message = "Lastname must not be blank")
    @Size(max = 100, message = "Lastname must be at most 100 characters")
    private String lastname;

    @Size(max = 255, message = "Email must be at most 255 characters")
    private String email;

    @NotBlank(message = "Username must not be blank")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    private String username;
}

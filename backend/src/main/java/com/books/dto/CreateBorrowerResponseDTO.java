package com.books.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBorrowerResponseDTO {

    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String username;
    private String password;
    private Boolean loginEnabled;
    private java.time.LocalDateTime createdAt;
}

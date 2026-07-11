package com.books.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {

    private Long id;
    private String username;
    private Boolean enabled;
    private java.time.LocalDateTime createdAt;
}

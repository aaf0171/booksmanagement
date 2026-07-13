package com.books.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivationTokenDTO {

    private Long id;
    private Long loginId;
    private String type;
    private String token;
    private java.time.LocalDateTime expiresAt;
    private java.time.LocalDateTime createdAt;
}

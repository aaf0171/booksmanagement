package com.books.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "activation_tokens")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long loginId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false, unique = true)
    private String tokenHash;

    @Column(nullable = false)
    private java.time.LocalDateTime expiresAt;

    private java.time.LocalDateTime usedAt;

    @Column(nullable = false)
    private java.time.LocalDateTime createdAt;
}

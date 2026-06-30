package com.books.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String subtitle;

    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    private String isbn;

    private String publisher;

    private Integer publicationYear;

    private String language;

    private String description;

    private String coverUrl;

    private LocalDateTime createdAt;

    public enum DocumentType {
        BOOK, DVD, GAME, DEVICE, OTHER
    }
}

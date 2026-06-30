package com.books.dto;

import com.books.model.Document.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {

    private Long id;
    private String title;
    private String subtitle;
    private DocumentType documentType;
    private String isbn;
    private String publisher;
    private Integer publicationYear;
    private String language;
    private String description;
    private String coverUrl;
    private LocalDateTime createdAt;
}

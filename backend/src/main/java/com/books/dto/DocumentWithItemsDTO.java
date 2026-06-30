package com.books.dto;

import com.books.model.Document.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentWithItemsDTO {

    @NotBlank(message = "Title is required")
    private String title;

    private String subtitle;

    @NotNull(message = "Document type is required")
    private DocumentType documentType;

    private String isbn;

    private String publisher;

    private Integer publicationYear;

    private String language;

    private String description;

    private String coverUrl;

    private List<String> items;
}

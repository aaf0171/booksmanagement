package com.books.model;

import com.books.dto.DocumentWithItemsDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentWithItems {

    private String title;
    private String subtitle;
    private Document.DocumentType documentType;
    private String isbn;
    private String publisher;
    private Integer publicationYear;
    private String language;
    private String description;
    private String coverUrl;
    private List<Item> items;

    public static DocumentWithItems fromDTO(DocumentWithItemsDTO dto) {
        return DocumentWithItems.builder()
                .title(dto.getTitle())
                .subtitle(dto.getSubtitle())
                .documentType(dto.getDocumentType())
                .isbn(dto.getIsbn())
                .publisher(dto.getPublisher())
                .publicationYear(dto.getPublicationYear())
                .language(dto.getLanguage())
                .description(dto.getDescription())
                .coverUrl(dto.getCoverUrl())
                .items(dto.getItems() != null ? dto.getItems().stream()
                        .map(barcode -> Item.builder().barcode(barcode).build())
                        .toList()
                        : List.of())
                .build();
    }
}

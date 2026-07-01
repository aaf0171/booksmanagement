package com.books.mapper;

import com.books.dto.DocumentDTO;
import com.books.dto.DocumentWithItemsDTO;
import com.books.dto.DocumentWithItemsResponseDTO;
import com.books.dto.ItemDTO;
import com.books.model.Document;
import com.books.model.Document.DocumentType;
import com.books.model.DocumentWithItems;
import com.books.model.Item;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Component
public class DocumentMapper {

    private static final Set<String> VALID_DOCUMENT_TYPES =
            Set.of("BOOK", "DVD", "GAME", "DEVICE", "OTHER");

    public void validateDocumentType(DocumentType documentType) {
        if (documentType == null) {
            throw new IllegalArgumentException("Document type is required");
        }
        if (!VALID_DOCUMENT_TYPES.contains(documentType.name())) {
            throw new IllegalArgumentException("Invalid document type: " + documentType);
        }
    }

    public void validateDocumentDTO(DocumentWithItemsDTO dto) {
        if (dto == null || dto.getTitle() == null || dto.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }
        validateDocumentType(dto.getDocumentType());
    }

    public Document toDocument(DocumentWithItems command) {
        if (command == null) {
            return null;
        }
        return Document.builder()
                .title(command.getTitle())
                .subtitle(command.getSubtitle())
                .documentType(command.getDocumentType())
                .isbn(command.getIsbn())
                .publisher(command.getPublisher())
                .publicationYear(command.getPublicationYear())
                .language(command.getLanguage())
                .description(command.getDescription())
                .coverUrl(command.getCoverUrl())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public List<Item> toItemEntities(DocumentWithItems command, Document document) {
        if (command.getItems() == null || command.getItems().isEmpty()) {
            return List.of();
        }
        return command.getItems().stream()
                .map(item -> Item.builder()
                        .barcode(item.getBarcode())
                        .document(document)
                        .build())
                .toList();
    }

    public DocumentDTO toDocumentDTO(Document document) {
        if (document == null) {
            return null;
        }
        return DocumentDTO.builder()
                .id(document.getId())
                .title(document.getTitle())
                .subtitle(document.getSubtitle())
                .documentType(document.getDocumentType())
                .isbn(document.getIsbn())
                .publisher(document.getPublisher())
                .publicationYear(document.getPublicationYear())
                .language(document.getLanguage())
                .description(document.getDescription())
                .coverUrl(document.getCoverUrl())
                .createdAt(document.getCreatedAt())
                .build();
    }

    public ItemDTO toItemDTO(Item item) {
        if (item == null) {
            return null;
        }
        return ItemDTO.builder()
                .id(item.getId())
                .barcode(item.getBarcode())
                .build();
    }

    public DocumentWithItemsResponseDTO toResponse(DocumentDTO documentDTO, List<Item> items) {
        ItemDTO[] itemDTOs = items.stream()
                .map(this::toItemDTO)
                .toArray(ItemDTO[]::new);
        return DocumentWithItemsResponseDTO.builder()
                .document(documentDTO)
                .items(itemDTOs)
                .build();
    }
}

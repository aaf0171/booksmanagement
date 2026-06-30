package com.books.service;

import com.books.dto.DocumentSearchDTO;
import com.books.repository.DocumentsSearchRepositoryDatabase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class DocumentSearchService {

    private static final List<String> VALID_DOCUMENT_TYPES = Arrays.asList("BOOK", "DVD", "GAME", "DEVICE", "OTHER");

    private final DocumentsSearchRepositoryDatabase documentsSearchRepository;

    public List<DocumentSearchDTO> searchDocuments(String query, String title, String subtitle,
                                                    String documentType, String isbn,
                                                    String publisher, Integer publicationYear) {
        validateSearchParams(query, title, subtitle, documentType, isbn, publisher, publicationYear);
        return documentsSearchRepository.searchDocuments(query, title, subtitle, documentType, isbn, publisher, publicationYear);
    }

    public void validateSearchParams(String query, String title, String subtitle,
                                      String documentType, String isbn,
                                      String publisher, Integer publicationYear) {
        if (allParamsNull(query, title, subtitle, documentType, isbn, publisher, publicationYear)) {
            throw new IllegalArgumentException("At least one search parameter must be provided");
        }

        if (documentType != null && !VALID_DOCUMENT_TYPES.contains(documentType)) {
            throw new IllegalArgumentException("Invalid document type: " + documentType);
        }
    }

    private boolean allParamsNull(String query, String title, String subtitle,
                                   String documentType, String isbn,
                                   String publisher, Integer publicationYear) {
        boolean queryNull = Objects.isNull(query) || query.isEmpty();
        return queryNull
                && Objects.isNull(title)
                && Objects.isNull(subtitle)
                && Objects.isNull(documentType)
                && Objects.isNull(isbn)
                && Objects.isNull(publisher)
                && Objects.isNull(publicationYear);
    }
}

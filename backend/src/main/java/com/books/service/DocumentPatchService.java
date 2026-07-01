package com.books.service;

import com.books.dto.DocumentPatchDTO;
import com.books.model.Document;
import com.books.model.Document.DocumentType;
import com.books.repository.DocumentsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DocumentPatchService {

    private static final Set<String> VALID_DOCUMENT_TYPES =
            Set.of("BOOK", "DVD", "GAME", "DEVICE", "OTHER");

    private final DocumentsRepository documentsRepository;

    @Transactional
    public void patchTitle(Long id, DocumentPatchDTO.TitlePatchDTO dto) {
        Document doc = findDocumentOrThrow(id);
        doc.setTitle(dto.getValue());
    }

    @Transactional
    public void patchSubtitle(Long id, DocumentPatchDTO.SubtitlePatchDTO dto) {
        Document doc = findDocumentOrThrow(id);
        doc.setSubtitle(dto.getValue());
    }

    @Transactional
    public void patchDocumentType(Long id, DocumentPatchDTO.DocumentTypePatchDTO dto) {
        Document doc = findDocumentOrThrow(id);
        if (!VALID_DOCUMENT_TYPES.contains(dto.getValue().toUpperCase())) {
            throw new IllegalArgumentException("Invalid document type: " + dto.getValue());
        }
        doc.setDocumentType(DocumentType.valueOf(dto.getValue().toUpperCase()));
    }

    @Transactional
    public void patchIsbn(Long id, DocumentPatchDTO.IsbnPatchDTO dto) {
        Document doc = findDocumentOrThrow(id);
        doc.setIsbn(dto.getValue());
    }

    @Transactional
    public void patchPublisher(Long id, DocumentPatchDTO.PublisherPatchDTO dto) {
        Document doc = findDocumentOrThrow(id);
        doc.setPublisher(dto.getValue());
    }

    @Transactional
    public void patchPublicationYear(Long id, DocumentPatchDTO.PublicationYearPatchDTO dto) {
        Document doc = findDocumentOrThrow(id);
        int currentYear = Year.now().getValue();
        if (dto.getValue() != null && dto.getValue() > currentYear + 1) {
            throw new IllegalArgumentException(
                    "Publication year must not exceed " + (currentYear + 1));
        }
        doc.setPublicationYear(dto.getValue());
    }

    @Transactional
    public void patchLanguage(Long id, DocumentPatchDTO.LanguagePatchDTO dto) {
        Document doc = findDocumentOrThrow(id);
        doc.setLanguage(dto.getValue());
    }

    @Transactional
    public void patchDescription(Long id, DocumentPatchDTO.DescriptionPatchDTO dto) {
        Document doc = findDocumentOrThrow(id);
        doc.setDescription(dto.getValue());
    }

    @Transactional
    public void patchCoverUrl(Long id, DocumentPatchDTO.CoverUrlPatchDTO dto) {
        Document doc = findDocumentOrThrow(id);
        if (dto.getValue() != null && !isValidUrl(dto.getValue())) {
            throw new IllegalArgumentException("Invalid URL: " + dto.getValue());
        }
        doc.setCoverUrl(dto.getValue());
    }

    @Transactional
    public void patchCreatedAt(Long id, DocumentPatchDTO.CreatedAtPatchDTO dto) {
        Document doc = findDocumentOrThrow(id);
        doc.setCreatedAt(dto.getValue());
    }

    private Document findDocumentOrThrow(Long id) {
        return documentsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document not found with id: " + id));
    }

    private boolean isValidUrl(String urlString) {
        if (urlString == null || urlString.isBlank()) {
            return true;
        }
        try {
            new URL(urlString);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

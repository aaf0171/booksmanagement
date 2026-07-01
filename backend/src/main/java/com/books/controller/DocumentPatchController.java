package com.books.controller;

import com.books.dto.DocumentPatchDTO;
import com.books.service.DocumentPatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentPatchController {

    private final DocumentPatchService documentPatchService;

    @PatchMapping("/{id}/title")
    public ResponseEntity<Void> patchTitle(
            @PathVariable Long id,
            @Valid @RequestBody DocumentPatchDTO.TitlePatchDTO dto) {
        documentPatchService.patchTitle(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/subtitle")
    public ResponseEntity<Void> patchSubtitle(
            @PathVariable Long id,
            @Valid @RequestBody DocumentPatchDTO.SubtitlePatchDTO dto) {
        documentPatchService.patchSubtitle(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/document-type")
    public ResponseEntity<Void> patchDocumentType(
            @PathVariable Long id,
            @Valid @RequestBody DocumentPatchDTO.DocumentTypePatchDTO dto) {
        documentPatchService.patchDocumentType(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/isbn")
    public ResponseEntity<Void> patchIsbn(
            @PathVariable Long id,
            @Valid @RequestBody DocumentPatchDTO.IsbnPatchDTO dto) {
        documentPatchService.patchIsbn(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/publisher")
    public ResponseEntity<Void> patchPublisher(
            @PathVariable Long id,
            @Valid @RequestBody DocumentPatchDTO.PublisherPatchDTO dto) {
        documentPatchService.patchPublisher(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/publication-year")
    public ResponseEntity<Void> patchPublicationYear(
            @PathVariable Long id,
            @Valid @RequestBody DocumentPatchDTO.PublicationYearPatchDTO dto) {
        documentPatchService.patchPublicationYear(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/language")
    public ResponseEntity<Void> patchLanguage(
            @PathVariable Long id,
            @Valid @RequestBody DocumentPatchDTO.LanguagePatchDTO dto) {
        documentPatchService.patchLanguage(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/description")
    public ResponseEntity<Void> patchDescription(
            @PathVariable Long id,
            @Valid @RequestBody DocumentPatchDTO.DescriptionPatchDTO dto) {
        documentPatchService.patchDescription(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cover-url")
    public ResponseEntity<Void> patchCoverUrl(
            @PathVariable Long id,
            @Valid @RequestBody DocumentPatchDTO.CoverUrlPatchDTO dto) {
        documentPatchService.patchCoverUrl(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/created-at")
    public ResponseEntity<Void> patchCreatedAt(
            @PathVariable Long id,
            @Valid @RequestBody DocumentPatchDTO.CreatedAtPatchDTO dto) {
        documentPatchService.patchCreatedAt(id, dto);
        return ResponseEntity.noContent().build();
    }
}

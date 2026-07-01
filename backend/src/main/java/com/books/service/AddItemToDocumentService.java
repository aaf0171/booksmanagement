package com.books.service;

import com.books.dto.AddItemDTO;
import com.books.dto.ItemDTO;
import com.books.exception.DocumentNotFoundException;
import com.books.model.Document;
import com.books.model.Item;
import com.books.model.Item.PhysicalStatus;
import com.books.repository.DocumentsRepository;
import com.books.repository.ItemsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AddItemToDocumentService {

    private static final java.util.Set<String> VALID_STATUSES =
            java.util.Set.of("CLEAN", "LOST", "DAMAGED", "REPAIR");

    private final DocumentsRepository documentsRepository;
    private final ItemsRepository itemsRepository;

    @Transactional
    public ItemDTO addItem(Long documentId, AddItemDTO dto) {
        Document document = documentsRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));

        if (itemsRepository.existsByBarcode(dto.getBarcode())) {
            throw new com.books.exception.DuplicateBarcodeException(dto.getBarcode());
        }

        PhysicalStatus status = PhysicalStatus.CLEAN;
        if (dto.getPhysicalStatus() != null && !dto.getPhysicalStatus().isBlank()) {
            if (!VALID_STATUSES.contains(dto.getPhysicalStatus().toUpperCase())) {
                throw new IllegalArgumentException("Invalid physical status: " + dto.getPhysicalStatus());
            }
            status = PhysicalStatus.valueOf(dto.getPhysicalStatus().toUpperCase());
        }

        Item item = Item.builder()
                .document(document)
                .barcode(dto.getBarcode())
                .physicalStatus(status)
                .location(dto.getLocation())
                .acquisitionDate(dto.getAcquisitionDate())
                .build();

        Item saved = itemsRepository.save(item);

        return ItemDTO.builder()
                .id(saved.getId())
                .barcode(saved.getBarcode())
                .location(saved.getLocation())
                .acquisitionDate(saved.getAcquisitionDate())
                .physicalStatus(saved.getPhysicalStatus() != null ? saved.getPhysicalStatus().name() : null)
                .build();
    }
}

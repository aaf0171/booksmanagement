package com.books.service;

import com.books.dto.ItemPatchDTO;
import com.books.model.Item;
import com.books.model.Item.PhysicalStatus;
import com.books.repository.ItemsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemPatchService {

    private static final Set<String> VALID_STATUSES =
            Set.of("CLEAN", "LOST", "DAMAGED", "REPAIR");

    private final ItemsRepository itemsRepository;

    @Transactional
    public void patchBarcode(Long id, ItemPatchDTO.BarcodePatchDTO dto) {
        Item item = findItemOrThrow(id);
        item.setBarcode(dto.getValue());
    }

    @Transactional
    public void patchStatus(Long id, ItemPatchDTO.StatusPatchDTO dto) {
        Item item = findItemOrThrow(id);
        if (!VALID_STATUSES.contains(dto.getValue().toUpperCase())) {
            throw new IllegalArgumentException("Invalid physical status: " + dto.getValue());
        }
        item.setPhysicalStatus(PhysicalStatus.valueOf(dto.getValue().toUpperCase()));
    }

    @Transactional
    public void patchLocation(Long id, ItemPatchDTO.LocationPatchDTO dto) {
        Item item = findItemOrThrow(id);
        item.setLocation(dto.getValue());
    }

    @Transactional
    public void patchAcquisitionDate(Long id, ItemPatchDTO.AcquisitionDatePatchDTO dto) {
        Item item = findItemOrThrow(id);
        item.setAcquisitionDate(dto.getValue());
    }

    private Item findItemOrThrow(Long id) {
        return itemsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item not found with id: " + id));
    }
}

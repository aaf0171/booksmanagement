package com.books.service;

import com.books.dto.DocumentDTO;
import com.books.dto.DocumentWithItemsDTO;
import com.books.dto.DocumentWithItemsResponseDTO;
import com.books.mapper.DocumentMapper;
import com.books.model.Document;
import com.books.model.DocumentWithItems;
import com.books.model.Item;
import com.books.repository.DocumentsRepository;
import com.books.repository.ItemsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateDocumentWithItemsService {

    private final DocumentsRepository documentsRepository;
    private final ItemsRepository itemsRepository;
    private final DocumentMapper documentMapper;

    @Transactional
    public DocumentWithItemsResponseDTO create(DocumentWithItemsDTO dto) {
        documentMapper.validateDocumentDTO(dto);

        DocumentWithItems command = DocumentWithItems.fromDTO(dto);
        Document document = documentMapper.toDocument(command);
        Document savedDocument = documentsRepository.save(document);

        List<Item> items = documentMapper.toItemEntities(command, savedDocument);

        List<Item> savedItems = new ArrayList<>();
        if (!items.isEmpty()) {
            for (Item item : items) {
                if (itemsRepository.existsByDocumentIdAndLabel(savedDocument.getId(), item.getLabel())) {
                    continue;
                }
                savedItems.add(itemsRepository.save(item));
            }
        }

        DocumentDTO documentDTO = documentMapper.toDocumentDTO(savedDocument);
        return documentMapper.toResponse(documentDTO, savedItems);
    }
}

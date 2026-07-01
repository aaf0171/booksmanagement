package com.books.service;

import com.books.exception.DocumentInUseException;
import com.books.exception.DocumentNotFoundException;
import com.books.repository.DocumentsRepository;
import com.books.repository.ItemsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteDocumentService {

    private final DocumentsRepository documentsRepository;
    private final ItemsRepository itemsRepository;

    @Transactional
    public void delete(Long id) {
        if (itemsRepository.countByDocumentId(id) > 0) {
            throw new DocumentInUseException(id);
        }

        int deleted = documentsRepository.deleteByIdCustom(id);
        if (deleted == 0) {
            throw new DocumentNotFoundException(id);
        }
    }
}

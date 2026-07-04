package com.books.service;

import com.books.exception.ItemInUseException;
import com.books.exception.ItemNotFoundException;
import com.books.repository.ItemsRepository;
import com.books.repository.LoansRepositoryDatabase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteItemService {

    private final ItemsRepository itemsRepository;
    private final LoansRepositoryDatabase loansRepository;

    @Transactional
    public void delete(Long id) {
        if (loansRepository.countActiveLoansForItem(id) > 0) {
            throw new ItemInUseException(id);
        }

        int deleted = itemsRepository.deleteItemByIdCustom(id);
        if (deleted == 0) {
            throw new ItemNotFoundException(id);
        }

        loansRepository.deleteLoansByItemId(id);
    }
}

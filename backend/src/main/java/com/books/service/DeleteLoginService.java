package com.books.service;

import com.books.repository.LoginsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeleteLoginService {

    private final LoginsRepository loginsRepository;

    @Transactional
    public void delete(Long id) {
        if (!loginsRepository.existsById(id)) {
            throw new com.books.exception.LoginNotFoundException(id);
        }

        loginsRepository.deleteLoginByIdCustom(id);
    }
}

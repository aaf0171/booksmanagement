package com.books.service;

import com.books.dto.LoginDTO;
import com.books.model.Login;
import com.books.repository.LoginsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EnableLoginService {

    private final LoginsRepository loginsRepository;

    @Transactional
    public LoginDTO enable(Long id) {
        Login login = loginsRepository.findById(id)
                .orElseThrow(() -> new com.books.exception.LoginNotFoundException(id));

        login.setEnabled(true);
        Login saved = loginsRepository.save(login);

        return LoginDTO.builder()
                .id(saved.getId())
                .username(saved.getUsername())
                .enabled(saved.getEnabled())
                .lastLogin(saved.getLastLogin())
                .createdAt(saved.getCreatedAt())
                .build();
    }
}

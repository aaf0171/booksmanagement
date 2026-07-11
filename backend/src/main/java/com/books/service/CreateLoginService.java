package com.books.service;

import com.books.dto.CreateLoginDTO;
import com.books.dto.LoginDTO;
import com.books.exception.LoginConflictException;
import com.books.exception.LoginValidationException;
import com.books.repository.LoginsRepository;
import com.books.model.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateLoginService {

    private final LoginsRepository loginsRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public LoginDTO create(CreateLoginDTO dto) {
        validate(dto);

        if (loginsRepository.existsByUsername(dto.getUsername())) {
            throw new LoginConflictException(dto.getUsername());
        }

        String hashedPassword = passwordEncoder.encode(dto.getPassword());

        Login login = Login.builder()
                .username(dto.getUsername())
                .passwordHash(hashedPassword)
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .build();

        Login saved = loginsRepository.save(login);

        return LoginDTO.builder()
                .id(saved.getId())
                .username(saved.getUsername())
                .enabled(saved.getEnabled())
                .createdAt(saved.getCreatedAt())
                .build();
    }

    private void validate(CreateLoginDTO dto) {
        if (dto.getUsername() == null || dto.getUsername().isBlank()) {
            throw new LoginValidationException("Username must not be blank");
        }
        if (dto.getUsername().length() < 3) {
            throw new LoginValidationException("Username must be between 3 and 100 characters");
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new LoginValidationException("Password must not be blank");
        }
        if (dto.getPassword().length() < 8) {
            throw new LoginValidationException("Password must be at least 8 characters");
        }
    }
}

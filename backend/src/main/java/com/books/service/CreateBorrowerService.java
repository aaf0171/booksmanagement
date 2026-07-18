package com.books.service;

import com.books.dto.ActivationTokenDTO;
import com.books.dto.CreateBorrowerDTO;
import com.books.dto.CreateBorrowerResponseDTO;
import com.books.exception.LoginConflictException;
import com.books.exception.LoginValidationException;
import com.books.model.Borrower;
import com.books.model.Login;
import com.books.model.Role;
import com.books.model.RoleType;
import com.books.repository.BorrowerRepository;
import com.books.repository.LoginsRepository;
import com.books.repository.RoleRepository;
import com.books.service.ActivationTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreateBorrowerService {

    private final BorrowerRepository borrowerRepository;
    private final LoginsRepository loginsRepository;
    private final RoleRepository roleRepository;
    private final ActivationTokenService activationTokenService;
    private final ActivationEmailService activationEmailService;

    @Transactional
    public CreateBorrowerResponseDTO create(CreateBorrowerDTO dto) {
        validate(dto);

        if (loginsRepository.existsByUsername(dto.getUsername())) {
            throw new LoginConflictException(dto.getUsername());
        }

        Optional<Role> role = roleRepository.findByName(RoleType.BORROWER.name());
        Set<Role> roles = new HashSet<>();
        role.ifPresent(roles::add);

        Login login = Login.builder()
                .username(dto.getUsername())
                .passwordHash(null)
                .enabled(false)
                .createdAt(LocalDateTime.now())
                .roles(roles)
                .build();

        Login savedLogin = loginsRepository.save(login);

        Borrower borrower = Borrower.builder()
                .loginId(savedLogin.getId())
                .firstname(dto.getFirstname())
                .lastname(dto.getLastname())
                .email(dto.getEmail())
                .created_at(LocalDateTime.now())
                .build();

        Borrower savedBorrower = borrowerRepository.save(borrower);

        ActivationTokenDTO activationToken = activationTokenService.generateToken(savedLogin.getId());

        if (savedBorrower.getEmail() != null && !savedBorrower.getEmail().isBlank()) {
            try {
                activationEmailService.sendActivationEmail(savedBorrower.getEmail(), activationToken.getToken());
            } catch (Exception e) {
                log.error("Failed to send activation email to {}: {}", savedBorrower.getEmail(), e.getMessage(), e);
            }
        }

        return CreateBorrowerResponseDTO.builder()
                .id(savedBorrower.getId())
                .firstname(savedBorrower.getFirstname())
                .lastname(savedBorrower.getLastname())
                .email(savedBorrower.getEmail())
                .username(savedLogin.getUsername())
                .password(null)
                .loginEnabled(false)
                .createdAt(savedBorrower.getCreated_at())
                .build();
    }

    private void validate(CreateBorrowerDTO dto) {
        if (dto.getFirstname() == null || dto.getFirstname().isBlank()) {
            throw new LoginValidationException("Firstname must not be blank");
        }
        if (dto.getLastname() == null || dto.getLastname().isBlank()) {
            throw new LoginValidationException("Lastname must not be blank");
        }
        if (dto.getUsername() == null || dto.getUsername().isBlank()) {
            throw new LoginValidationException("Username must not be blank");
        }
        if (dto.getUsername().length() < 3) {
            throw new LoginValidationException("Username must be between 3 and 100 characters");
        }
    }
}

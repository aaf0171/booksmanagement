package com.books.controller;

import com.books.dto.CreateLoginDTO;
import com.books.dto.LoginDTO;
import com.books.exception.LoginConflictException;
import com.books.exception.LoginValidationException;
import com.books.service.CreateLoginService;
import com.books.model.Login;
import com.books.repository.LoginsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LoginCommandControllerTest {

    @Autowired
    private LoginsRepository loginsRepository;

    @Autowired
    private CreateLoginService createLoginService;

    private LoginCommandController controller;

    @BeforeEach
    void setUp() {
        loginsRepository.deleteAll();
        controller = new LoginCommandController(createLoginService);
    }

    @Test
    @DisplayName("POST_create_login_returns_201")
    void POST_create_login_returns_201() {
        CreateLoginDTO dto = CreateLoginDTO.builder()
                .username("newuser1")
                .password("securePass123")
                .build();

        ResponseEntity<LoginDTO> response = controller.createLogin(dto);

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("newuser1", response.getBody().getUsername());
        assertTrue(response.getBody().getEnabled());
        assertNotNull(response.getBody().getId());
        assertNotNull(loginsRepository.findByUsername("newuser1"));
    }

    @Test
    @DisplayName("POST_duplicate_username_returns_409")
    void POST_duplicate_username_returns_409() {
        CreateLoginDTO first = CreateLoginDTO.builder()
                .username("conflictuser")
                .password("securePass123")
                .build();

        controller.createLogin(first);

        LoginConflictException ex = assertThrows(LoginConflictException.class, () -> {
            CreateLoginDTO second = CreateLoginDTO.builder()
                    .username("conflictuser")
                    .password("anotherPass1")
                    .build();
            controller.createLogin(second);
        });

        assertTrue(ex.getMessage().contains("conflictuser"));
    }

    @Test
    @DisplayName("POST_blank_password_returns_400")
    void POST_blank_password_returns_400() {
        CreateLoginDTO dto = CreateLoginDTO.builder()
                .username("newuser2")
                .password("")
                .build();

        LoginValidationException ex = assertThrows(LoginValidationException.class,
                () -> controller.createLogin(dto));

        assertEquals("Password must not be blank", ex.getMessage());
    }

    @Test
    @DisplayName("POST_short_password_returns_400")
    void POST_short_password_returns_400() {
        CreateLoginDTO dto = CreateLoginDTO.builder()
                .username("newuser3")
                .password("short")
                .build();

        LoginValidationException ex = assertThrows(LoginValidationException.class,
                () -> controller.createLogin(dto));

        assertEquals("Password must be at least 8 characters", ex.getMessage());
    }

    @Test
    @DisplayName("POST_short_username_returns_400")
    void POST_short_username_returns_400() {
        CreateLoginDTO dto = CreateLoginDTO.builder()
                .username("ab")
                .password("securePass123")
                .build();

        LoginValidationException ex = assertThrows(LoginValidationException.class,
                () -> controller.createLogin(dto));

        assertEquals("Username must be between 3 and 100 characters", ex.getMessage());
    }
}

package com.books.controller;

import com.books.exception.LoginNotFoundException;
import com.books.model.Login;
import com.books.repository.LoginsRepository;
import com.books.service.DeleteLoginService;
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
class DeleteLoginControllerTest {

    @Autowired
    private LoginsRepository loginsRepository;

    @Autowired
    private DeleteLoginService deleteLoginService;

    private DeleteLoginController controller;

    @BeforeEach
    void setUp() {
        loginsRepository.deleteAll();
        controller = new DeleteLoginController(deleteLoginService);
    }

    @Test
    @DisplayName("DELETE_existing_login_returns_204")
    void DELETE_existing_login_returns_204() {
        Login saved = loginsRepository.save(
                Login.builder()
                        .username("delete-me")
                        .passwordHash("$2a$10$hash")
                        .enabled(true)
                        .build()
        );
        loginsRepository.flush();

        ResponseEntity<Void> response = controller.deleteLogin(saved.getId());

        assertEquals(204, response.getStatusCode().value());
        assertFalse(loginsRepository.findById(saved.getId()).isPresent());
    }

    @Test
    @DisplayName("DELETE_unknown_login_returns_404")
    void DELETE_unknown_login_returns_404() {
        LoginNotFoundException ex = assertThrows(LoginNotFoundException.class,
                () -> controller.deleteLogin(99999L));

        assertTrue(ex.getMessage().contains("999"));
    }
}

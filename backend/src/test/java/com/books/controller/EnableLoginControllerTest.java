package com.books.controller;

import com.books.dto.LoginDTO;
import com.books.exception.LoginNotFoundException;
import com.books.model.Login;
import com.books.repository.LoginsRepository;
import com.books.service.EnableLoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EnableLoginControllerTest {

    @Autowired
    private LoginsRepository loginsRepository;

    @Autowired
    private EnableLoginService enableLoginService;

    private EnableLoginController controller;

    @BeforeEach
    void setUp() {
        loginsRepository.deleteAll();
        controller = new EnableLoginController(enableLoginService);
    }

    @Test
    @DisplayName("PATCH_enable_existing_disabled_login_returns_200")
    void PATCH_enable_existing_disabled_login_returns_200() {
        Login saved = loginsRepository.save(
                Login.builder()
                        .username("enable-me")
                        .passwordHash("$2a$10$hash")
                        .enabled(false)
                        .build()
        );
        loginsRepository.flush();

        ResponseEntity<LoginDTO> response = controller.enableLogin(saved.getId());

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getEnabled());
        assertEquals("enable-me", response.getBody().getUsername());
    }

    @Test
    @DisplayName("PATCH_enable_unknown_login_returns_404")
    void PATCH_enable_unknown_login_returns_404() {
        LoginNotFoundException ex = assertThrows(LoginNotFoundException.class,
                () -> controller.enableLogin(99999L));

        assertTrue(ex.getMessage().contains("999"));
    }

    @Test
    @DisplayName("PATCH_enable_already_enabled_returns_200")
    void PATCH_enable_already_enabled_returns_200() {
        Login saved = loginsRepository.save(
                Login.builder()
                        .username("already-enabled")
                        .passwordHash("$2a$10$hash")
                        .enabled(true)
                        .build()
        );
        loginsRepository.flush();

        ResponseEntity<LoginDTO> response = controller.enableLogin(saved.getId());

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getEnabled());
    }
}

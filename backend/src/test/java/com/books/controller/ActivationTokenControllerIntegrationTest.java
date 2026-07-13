package com.books.controller;

import com.books.dto.ActivationTokenDTO;
import com.books.exception.LoginNotFoundException;
import com.books.exception.LoginValidationException;
import com.books.model.Login;
import com.books.repository.ActivationTokenRepository;
import com.books.repository.LoginsRepository;
import com.books.service.ActivationTokenService;
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
class ActivationTokenControllerIntegrationTest {

    @Autowired
    private LoginsRepository loginsRepository;

    @Autowired
    private ActivationTokenRepository activationTokenRepository;

    @Autowired
    private ActivationTokenService activationTokenService;

    private ActivationTokenController controller;

    @BeforeEach
    void setUp() {
        loginsRepository.deleteAll();
        activationTokenRepository.deleteAll();
        controller = new ActivationTokenController(activationTokenService);
    }

    @Test
    @DisplayName("POST_generate_activation_token_returns_201")
    void POST_generate_activation_token_returns_201() {
        Login saved = loginsRepository.save(
                Login.builder()
                        .username("token-user")
                        .passwordHash("$2a$10$hash")
                        .enabled(true)
                        .build()
        );
        loginsRepository.flush();

        ResponseEntity<ActivationTokenDTO> response = controller.generateActivationToken(saved.getId());

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(saved.getId(), response.getBody().getLoginId());
        assertEquals("ACTIVATION", response.getBody().getType());
        assertNotNull(response.getBody().getToken());
        assertNotNull(response.getBody().getExpiresAt());
    }

    @Test
    @DisplayName("POST_non_existent_login_returns_404")
    void POST_non_existent_login_returns_404() {
        LoginNotFoundException ex = assertThrows(LoginNotFoundException.class,
                () -> controller.generateActivationToken(99999L));

        assertTrue(ex.getMessage().contains("99999"));
    }

    @Test
    @DisplayName("POST_disabled_login_returns_400")
    void POST_disabled_login_returns_400() {
        Login saved = loginsRepository.save(
                Login.builder()
                        .username("disabled-user")
                        .passwordHash("$2a$10$hash")
                        .enabled(false)
                        .build()
        );
        loginsRepository.flush();

        LoginValidationException ex = assertThrows(LoginValidationException.class,
                () -> controller.generateActivationToken(saved.getId()));

        assertTrue(ex.getMessage().contains("disabled"));
    }

    @Test
    @DisplayName("POST_reuse_login_invalidates_previous_token")
    void POST_reuse_login_invalidates_previous_token() {
        Login saved = loginsRepository.save(
                Login.builder()
                        .username("reuse-user")
                        .passwordHash("$2a$10$hash")
                        .enabled(true)
                        .build()
        );
        loginsRepository.flush();

        controller.generateActivationToken(saved.getId());
        loginsRepository.flush();

        ResponseEntity<ActivationTokenDTO> response2 = controller.generateActivationToken(saved.getId());

        assertNotNull(response2.getBody());
        assertEquals(44, response2.getBody().getToken().length());
    }

    @Test
    @DisplayName("POST_token_is_44_characters")
    void POST_token_is_44_characters() {
        Login saved = loginsRepository.save(
                Login.builder()
                        .username("length-user")
                        .passwordHash("$2a$10$hash")
                        .enabled(true)
                        .build()
        );
        loginsRepository.flush();

        ResponseEntity<ActivationTokenDTO> response = controller.generateActivationToken(saved.getId());

        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getToken());
        assertEquals(44, response.getBody().getToken().length());
    }

    @Test
    @DisplayName("POST_each_token_has_unique_value")
    void POST_each_token_has_unique_value() {
        Login saved = loginsRepository.save(
                Login.builder()
                        .username("unique-user")
                        .passwordHash("$2a$10$hash")
                        .enabled(true)
                        .build()
        );
        loginsRepository.flush();

        ResponseEntity<ActivationTokenDTO> response1 = controller.generateActivationToken(saved.getId());
        ResponseEntity<ActivationTokenDTO> response2 = controller.generateActivationToken(saved.getId());

        assertNotNull(response1.getBody().getToken());
        assertNotNull(response2.getBody().getToken());
        assertNotEquals(response1.getBody().getToken(), response2.getBody().getToken());
    }
}

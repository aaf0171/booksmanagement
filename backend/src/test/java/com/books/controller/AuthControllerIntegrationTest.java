package com.books.controller;

import com.books.dto.*;
import com.books.exception.LoginNotFoundException;
import com.books.model.ActivationToken;
import com.books.model.Login;
import com.books.model.RefreshToken;
import com.books.model.Role;
import com.books.repository.ActivationTokenRepository;
import com.books.repository.LoginsRepository;
import com.books.repository.RoleRepository;
import com.books.service.ActivationTokenService;
import com.books.service.AuthService;
import com.books.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuthControllerIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LoginsRepository loginsRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private ActivationTokenRepository activationTokenRepository;

    @Autowired
    private ActivationTokenService activationTokenService;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    private MockMvc mockMvc;

    private Login testLogin;

    @BeforeEach
    void setUp() {
        loginsRepository.deleteAll();
        roleRepository.deleteAll();
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilter(springSecurityFilterChain)
                .build();

        Role borrower = roleRepository.save(Role.builder().name("BORROWER").build());

        testLogin = Login.builder()
                .id(null)
                .username("jc.dusse")
                .passwordHash("$2a$10$ZcX.L1QKtn4tMs.eCqQIBORsiWiv7bE3Exh3hnjeWWUHvmsCa3KRe")
                .enabled(true)
                .lastLogin(null)
                .createdAt(LocalDateTime.now())
                .roles(Set.of(borrower))
                .build();
        testLogin = loginsRepository.save(testLogin);
    }

    @Test
    @DisplayName("POST_login_valid_credentials_returns_200_with_tokens")
    void POST_login_valid_credentials_returns_200_with_tokens() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("username", "jc.dusse");
        body.put("password", "password123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.expiresIn").value(900))
                .andReturn();

        LoginResponseDTO response = objectMapper.readValue(
                result.getResponse().getContentAsString(), LoginResponseDTO.class);

        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        assertEquals(900L, response.getExpiresIn());
    }

    @Test
    @DisplayName("POST_login_invalid_credentials_returns_401")
    void POST_login_invalid_credentials_returns_401() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("username", "jc.dusse");
        body.put("password", "wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST_login_inactive_account_returns_401")
    void POST_login_inactive_account_returns_401() throws Exception {
        Role borrower = roleRepository.findByName("BORROWER").orElseGet(() -> roleRepository.save(Role.builder().name("BORROWER").build()));
        Login disabledLogin = Login.builder()
                .username("disabled.user")
                .passwordHash("$2a$10$ZcX.L1QKtn4tMs.eCqQIBORsiWiv7bE3Exh3hnjeWWUHvmsCa3KRe")
                .enabled(false)
                .roles(Set.of(borrower))
                .build();
        disabledLogin = loginsRepository.save(disabledLogin);

        Map<String, String> body = new HashMap<>();
        body.put("username", "disabled.user");
        body.put("password", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST_login_tokens_not_null")
    void POST_login_tokens_not_null() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("username", "jc.dusse");
        body.put("password", "password123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn();

        LoginResponseDTO response = objectMapper.readValue(
                result.getResponse().getContentAsString(), LoginResponseDTO.class);

        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
    }

    @Test
    @DisplayName("POST_login_accessToken_is_jwt")
    void POST_login_accessToken_is_jwt() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("username", "jc.dusse");
        body.put("password", "password123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponseDTO response = objectMapper.readValue(
                result.getResponse().getContentAsString(), LoginResponseDTO.class);

        String token = response.getAccessToken();
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);
    }

    @Test
    @DisplayName("POST_login_refreshToken_is_44_characters")
    void POST_login_refreshToken_is_44_characters() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("username", "jc.dusse");
        body.put("password", "password123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponseDTO response = objectMapper.readValue(
                result.getResponse().getContentAsString(), LoginResponseDTO.class);

        String refreshToken = response.getRefreshToken();
        assertTrue(refreshToken.length() >= 40,
                "Refresh token should be base64 encoded 32 bytes = 44 chars, got: " + refreshToken.length());
    }

    @Test
    @DisplayName("POST_refresh_valid_token_returns_200_with_new_accessToken")
    void POST_refresh_valid_token_returns_200_with_new_accessToken() throws Exception {
        String[] tokens = doLogin();
        String refreshToken = tokens[0];
        String accessToken = tokens[1];

        Map<String, String> body = new HashMap<>();
        body.put("refreshToken", refreshToken);

        MvcResult result = mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.expiresIn").value(900))
                .andReturn();

        RefreshResponseDTO response = objectMapper.readValue(
                result.getResponse().getContentAsString(), RefreshResponseDTO.class);

        assertNotNull(response.getAccessToken());
        assertNotEquals(accessToken, response.getAccessToken());
    }

    @Test
    @DisplayName("POST_refresh_expired_token_returns_401")
    void POST_refresh_expired_token_returns_401() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("refreshToken", "invalid-expired-token");

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST_refresh_revoked_token_returns_401")
    void POST_refresh_revoked_token_returns_401() throws Exception {
        String[] tokens = doLogin();
        String refreshToken = tokens[0];

        refreshTokenService.revoke(refreshToken);

        Map<String, String> body = new HashMap<>();
        body.put("refreshToken", refreshToken);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST_logout_revokes_refresh_token")
    void POST_logout_revokes_refresh_token() throws Exception {
        String[] tokens = doLogin();
        String refreshToken = tokens[0];

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refreshToken", refreshToken))))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST_logout_revoked_token_returns_401")
    void POST_logout_revoked_token_returns_401() throws Exception {
        String[] tokens = doLogin();
        String refreshToken = tokens[0];

        refreshTokenService.revoke(refreshToken);

        Map<String, String> body = new HashMap<>();
        body.put("refreshToken", refreshToken);

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET_protected_endpoint_without_token_returns_401")
    void GET_protected_endpoint_without_token_returns_401() throws Exception {
        mockMvc.perform(get("/api/documents/search"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("GET_protected_endpoint_with_valid_token_returns_200")
    void GET_protected_endpoint_with_valid_token_returns_200() throws Exception {
        String[] tokens = doLogin();
        String accessToken = tokens[1];

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sub").isNotEmpty())
                .andExpect(jsonPath("$.login").isNotEmpty())
                .andExpect(jsonPath("$.roles").isArray());
    }

    private String[] doLogin() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("username", "jc.dusse");
        body.put("password", "password123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andReturn();

        LoginResponseDTO response = objectMapper.readValue(
                result.getResponse().getContentAsString(), LoginResponseDTO.class);

        return new String[]{response.getRefreshToken(), response.getAccessToken()};
    }

    // === Activation controller integration tests ===

    @Test
    @DisplayName("POST_activate_valid_token_returns_200_with_SUCCESS")
    void POST_activate_valid_token_returns_200_with_SUCCESS() throws Exception {
        Role borrower = roleRepository.findByName("BORROWER").orElseGet(() -> roleRepository.save(Role.builder().name("BORROWER").build()));
        Login disabledLogin = Login.builder()
                .username("activate-test-user")
                .passwordHash("$2a$10$ZcX.L1QKtn4tMs.eCqQIBORsiWiv7bE3Exh3hnjeWWUHvmsCa3KRe")
                .enabled(false)
                .roles(Set.of(borrower))
                .build();
        disabledLogin = loginsRepository.save(disabledLogin);

        String[] tokenAndHash = generateActivationToken(disabledLogin.getId());
        String plaintextToken = tokenAndHash[0];

        String password = "passWorD123!";
        String confirmPassword = "passWorD123!";

        Map<String, String> body = new HashMap<>();
        body.put("token", plaintextToken);
        body.put("password", password);
        body.put("confirmPassword", confirmPassword);

        mockMvc.perform(post("/api/auth/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("Account activated successfully"))
                .andExpect(jsonPath("$.email").isEmpty());
    }

    @Test
    @DisplayName("POST_activate_login_enabled_set_to_true")
    void POST_activate_login_enabled_set_to_true() throws Exception {
        Role borrower = roleRepository.findByName("BORROWER").orElseGet(() -> roleRepository.save(Role.builder().name("BORROWER").build()));
        Login disabledLogin = Login.builder()
                .username("enable-test-user")
                .passwordHash("$2a$10$ZcX.L1QKtn4tMs.eCqQIBORsiWiv7bE3Exh3hnjeWWUHvmsCa3KRe")
                .enabled(false)
                .roles(Set.of(borrower))
                .build();
        disabledLogin = loginsRepository.save(disabledLogin);

        String[] tokenAndHash = generateActivationToken(disabledLogin.getId());
        String plaintextToken = tokenAndHash[0];

        assertFalse(disabledLogin.getEnabled());

        String password = "passWorD123!";
        String confirmPassword = "passWorD123!";

        Map<String, String> body = new HashMap<>();
        body.put("token", plaintextToken);
        body.put("password", password);
        body.put("confirmPassword", confirmPassword);

        mockMvc.perform(post("/api/auth/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        Login updatedLogin = loginsRepository.findById(disabledLogin.getId()).orElseThrow();
        assertTrue(updatedLogin.getEnabled());
    }

    @Test
    @DisplayName("POST_activate_token_marked_as_used")
    void POST_activate_token_marked_as_used() throws Exception {
        Role borrower = roleRepository.findByName("BORROWER").orElseGet(() -> roleRepository.save(Role.builder().name("BORROWER").build()));
        Login disabledLogin = Login.builder()
                .username("mark-used-user")
                .passwordHash("$2a$10$ZcX.L1QKtn4tMs.eCqQIBORsiWiv7bE3Exh3hnjeWWUHvmsCa3KRe")
                .enabled(false)
                .roles(Set.of(borrower))
                .build();
        disabledLogin = loginsRepository.save(disabledLogin);

        String[] tokenAndHash = generateActivationToken(disabledLogin.getId());
        String plaintextToken = tokenAndHash[0];
        String tokenHash = tokenAndHash[1];

        String password = "passWorD123!";
        String confirmPassword = "passWorD123!";

        Map<String, String> body = new HashMap<>();
        body.put("token", plaintextToken);
        body.put("password", password);
        body.put("confirmPassword", confirmPassword);

        mockMvc.perform(post("/api/auth/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        entityManager.flush();
        entityManager.clear();

        ActivationToken token = activationTokenRepository.findByTokenHash(tokenHash).orElseThrow();
        assertNotNull(token.getUsedAt());
    }

    @Test
    @DisplayName("POST_activate_expired_token_returns_410_with_email")
    void POST_activate_expired_token_returns_410_with_email() throws Exception {
        Role borrower = roleRepository.findByName("BORROWER").orElseGet(() -> roleRepository.save(Role.builder().name("BORROWER").build()));
        Login disabledLogin = Login.builder()
                .username("expired-token-user@example.com")
                .passwordHash("$2a$10$ZcX.L1QKtn4tMs.eCqQIBORsiWiv7bE3Exh3hnjeWWUHvmsCa3KRe")
                .enabled(false)
                .roles(Set.of(borrower))
                .build();
        disabledLogin = loginsRepository.save(disabledLogin);

        String[] tokenAndHash = generateExpiredActivationToken(disabledLogin.getId());
        String plaintextToken = tokenAndHash[0];

        Map<String, String> body = new HashMap<>();
        body.put("token", plaintextToken);

        mockMvc.perform(post("/api/auth/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isGone())
                .andExpect(jsonPath("$.status").value("TOKEN_EXPIRED"))
                .andExpect(jsonPath("$.email").value("expired-token-user@example.com"));
    }

    @Test
    @DisplayName("POST_activate_expired_token_without_user_returns_410_without_email")
    void POST_activate_expired_token_without_user_returns_410_without_email() throws Exception {
        // Create expired token but with a non-existent loginId
        String plaintextToken = "expiredTokenForTestNoUser1234567890abcdefghijklmn==";

        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(plaintextToken.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(64);
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            String tokenHash = hexString.toString();

            ActivationToken expiredToken = ActivationToken.builder()
                    .loginId(99999L)
                    .type("ACTIVATION")
                    .tokenHash(tokenHash)
                    .expiresAt(LocalDateTime.now().minusHours(1))
                    .usedAt(null)
                    .createdAt(LocalDateTime.now().minusHours(2))
                    .build();
            activationTokenRepository.save(expiredToken);

            Map<String, String> body = new HashMap<>();
            body.put("token", plaintextToken);

            mockMvc.perform(post("/api/auth/activate")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(body)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value("TOKEN_INVALID"))
                    .andExpect(jsonPath("$.email").isEmpty());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate expired token", e);
        }
    }

    @Test
    @DisplayName("POST_activate_invalid_token_returns_400_without_email")
    void POST_activate_invalid_token_returns_400_without_email() throws Exception {
        String password = "passWorD123!";
        String confirmPassword = "passWorD123!";

        Map<String, String> body = new HashMap<>();
        body.put("token", "nonexistent-token-value-xyz");
        body.put("password", password);
        body.put("confirmPassword", confirmPassword);

        mockMvc.perform(post("/api/auth/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("TOKEN_INVALID"))
                .andExpect(jsonPath("$.email").isEmpty());
    }

    @Test
    @DisplayName("POST_activate_already_activated_returns_200_with_ALREADY_ACTIVATED")
    void POST_activate_already_activated_returns_200_with_ALREADY_ACTIVATED() throws Exception {
        Role borrower = roleRepository.findByName("BORROWER").orElseGet(() -> roleRepository.save(Role.builder().name("BORROWER").build()));
        Login disabledLogin = Login.builder()
                .username("used-token-user")
                .passwordHash("$2a$10$ZcX.L1QKtn4tMs.eCqQIBORsiWiv7bE3Exh3hnjeWWUHvmsCa3KRe")
                .enabled(false)
                .roles(Set.of(borrower))
                .build();
        disabledLogin = loginsRepository.save(disabledLogin);

        String[] tokenAndHash = generateActivationToken(disabledLogin.getId());
        String plaintextToken = tokenAndHash[0];
        String storedHash = tokenAndHash[1];

        activationTokenRepository.markAsUsed(activationTokenRepository.findByTokenHash(storedHash).orElseThrow().getId());
        entityManager.flush();
        entityManager.clear();

        // Re-enable the login to simulate already-activated account
        Login updatedLogin = loginsRepository.findById(disabledLogin.getId()).orElseThrow();
        updatedLogin.setEnabled(true);
        loginsRepository.save(updatedLogin);
        entityManager.flush();
        entityManager.clear();

        Map<String, String> body = new HashMap<>();
        body.put("token", plaintextToken);

        mockMvc.perform(post("/api/auth/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ALREADY_ACTIVATED"))
                .andExpect(jsonPath("$.email").isEmpty());
    }

    @Test
    @DisplayName("POST_activate_without_token_returns_400")
    void POST_activate_without_token_returns_400() throws Exception {
        mockMvc.perform(post("/api/auth/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST_activate_no_user_data_leaked_for_invalid_token")
    void POST_activate_no_user_data_leaked_for_invalid_token() throws Exception {
        String password = "passWorD123!";
        String confirmPassword = "passWorD123!";

        Map<String, String> body = new HashMap<>();
        body.put("token", "nonexistent-token-value-xyz");
        body.put("password", password);
        body.put("confirmPassword", confirmPassword);

        mockMvc.perform(post("/api/auth/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("TOKEN_INVALID"))
                .andExpect(jsonPath("$.email").isEmpty())
                .andExpect(jsonPath("$.message").isEmpty());
    }

    @Test
    @DisplayName("POST_activate_mismatched_passwords_returns_422")
    void POST_activate_mismatched_passwords_returns_422() throws Exception {
        Role borrower = roleRepository.findByName("BORROWER").orElseGet(() -> roleRepository.save(Role.builder().name("BORROWER").build()));
        Login disabledLogin = Login.builder()
                .username("password-mismatch-user")
                .passwordHash("$2a$10$ZcX.L1QKtn4tMs.eCqQIBORsiWiv7bE3Exh3hnjeWWUHvmsCa3KRe")
                .enabled(false)
                .roles(Set.of(borrower))
                .build();
        disabledLogin = loginsRepository.save(disabledLogin);

        String[] tokenAndHash = generateActivationToken(disabledLogin.getId());
        String plaintextToken = tokenAndHash[0];

        Map<String, String> body = new HashMap<>();
        body.put("token", plaintextToken);
        body.put("password", "password1");
        body.put("confirmPassword", "password2");

        mockMvc.perform(post("/api/auth/activate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorType").value("PASSWORDS_MISMATCH"));
    }

    private String[] generateActivationToken(Long loginId) {
        String[] result = new String[2];
        String plaintextToken = activationTokenService.generateToken(loginId).getToken();
        activationTokenRepository.flush();
        ActivationToken saved = activationTokenRepository.findByLoginId(loginId).stream()
                .findFirst()
                .orElseThrow();
        result[0] = plaintextToken;
        result[1] = saved.getTokenHash();
        return result;
    }

    private String[] generateExpiredActivationToken(Long loginId) {
        String[] result = new String[2];
        String plaintextToken = "expiredTokenForTest1234567890abcdefghijklmnop==";

        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(plaintextToken.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(64);
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            String tokenHash = hexString.toString();

            ActivationToken expiredToken = ActivationToken.builder()
                    .loginId(loginId)
                    .type("ACTIVATION")
                    .tokenHash(tokenHash)
                    .expiresAt(LocalDateTime.now().minusHours(1))
                    .usedAt(null)
                    .createdAt(LocalDateTime.now().minusHours(2))
                    .build();
            activationTokenRepository.save(expiredToken);

            result[0] = plaintextToken;
            result[1] = tokenHash;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate expired token", e);
        }
        return result;
    }

    private String hashToken(String token) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(token.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(64);
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to hash token", e);
        }
    }
}

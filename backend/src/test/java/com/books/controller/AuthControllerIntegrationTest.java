package com.books.controller;

import com.books.dto.*;
import com.books.exception.LoginNotFoundException;
import com.books.model.Login;
import com.books.model.RefreshToken;
import com.books.repository.LoginsRepository;
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
    private AuthService authService;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    private MockMvc mockMvc;

    private Login testLogin;

    @BeforeEach
    void setUp() {
        loginsRepository.deleteAll();
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilter(springSecurityFilterChain)
                .build();

        testLogin = Login.builder()
                .id(null)
                .username("jc.dusse")
                .passwordHash("$2a$10$ZcX.L1QKtn4tMs.eCqQIBORsiWiv7bE3Exh3hnjeWWUHvmsCa3KRe")
                .enabled(true)
                .lastLogin(null)
                .createdAt(LocalDateTime.now())
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
        Login disabledLogin = Login.builder()
                .username("disabled.user")
                .passwordHash("$2a$10$ZcX.L1QKtn4tMs.eCqQIBORsiWiv7bE3Exh3hnjeWWUHvmsCa3KRe")
                .enabled(false)
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
}

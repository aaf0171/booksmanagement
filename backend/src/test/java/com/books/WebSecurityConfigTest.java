package com.books;

import com.books.model.Login;
import com.books.model.Role;
import com.books.repository.LoginsRepository;
import com.books.repository.RoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class WebSecurityConfigTest {
    @Autowired
    private WebApplicationContext context;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private LoginsRepository loginsRepository;

    @Autowired
    private RoleRepository roleRepository;
    
    private MockMvc mockMvc;
    
    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        loginsRepository.deleteAll();
        roleRepository.deleteAll();
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        
        Role borrower = roleRepository.save(Role.builder().name("BORROWER").build());
        
        Login testLogin = Login.builder()
                .id(null)
                .username("jc.dusse")
                .passwordHash("$2a$10$ZcX.L1QKtn4tMs.eCqQIBORsiWiv7bE3Exh3hnjeWWUHvmsCa3KRe")
                .enabled(true)
                .lastLogin(null)
                .createdAt(LocalDateTime.now())
                .roles(Set.of(borrower))
                .build();
        testLogin = loginsRepository.save(testLogin);
        System.out.println("Saved login with id: " + testLogin.getId());
    }
    
    @Test
    void debugLoginAndMe() throws Exception {
        // First login to get a token
        Map<String, String> loginBody = new HashMap<>();
        loginBody.put("username", "jc.dusse");
        loginBody.put("password", "password123");
        
        String loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginBody)))
                .andReturn()
                .getResponse()
                .getContentAsString();
        int loginStatus = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginBody)))
                .andReturn()
                .getResponse()
                .getStatus();
        System.out.println("Login status: " + loginStatus);
        System.out.println("Login response: " + loginResult);
        
        // Parse token from response
        JsonNode jsonNode = objectMapper.readTree(loginResult);
        String accessToken = jsonNode.get("accessToken").asText();
        
        System.out.println("Access token: " + accessToken);
        System.out.println("Token parts: " + accessToken.split("\\.").length);
        
        // Now try /api/auth/me with the token
        String meResult = mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println("Me response: " + meResult);
        int meStatus = mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andReturn()
                .getResponse()
                .getStatus();
        System.out.println("Me status: " + meStatus);
    }
}

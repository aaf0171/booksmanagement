package com.books.controller;

import com.books.dto.BorrowerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext
class BorrowersControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM borrowers");
        jdbcTemplate.update("INSERT INTO borrowers (name, surname) VALUES (?, ?)", "Jean", "Dupont");
        jdbcTemplate.update("INSERT INTO borrowers (name, surname) VALUES (?, ?)", "Marie", "Martin");
    }

    @Test
    void shouldReturnListOfBorrowerDTO() {
        RestTemplate template = new RestTemplate();
        String url = "http://localhost:" + port + "/borrowers/findAll";
        ResponseEntity<BorrowerDTO[]> response = template.getForEntity(url, BorrowerDTO[].class);

        assertNotNull(response);
        assertNotNull(response.getBody());

        assertEquals(2, response.getBody().length);

        BorrowerDTO first = response.getBody()[0];
        assertNotNull(first.getId());
        assertNotNull(first.getName());
        assertNotNull(first.getSurname());
        assertEquals("Jean", first.getName());
        assertEquals("Dupont", first.getSurname());

        BorrowerDTO second = response.getBody()[1];
        assertEquals("Marie", second.getName());
        assertEquals("Martin", second.getSurname());
    }
}

package com.books.controller;

import com.books.model.Loan;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoansControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Test
    void shouldReturnActiveLoans() {
        RestTemplate template = new RestTemplate();
        String url = "http://localhost:" + port + "/loans/findAllActiveLoans";
        ResponseEntity<Loan[]> response = template.getForEntity(url, Loan[].class);

        assertNotNull(response);
        assertNotNull(response.getBody());
    }
}

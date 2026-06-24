package com.books.controller;

import com.books.dto.BookDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BooksControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Test
    void shouldReturnListOfBookDTO() {
        RestTemplate template = new RestTemplate();
        String url = "http://localhost:" + port + "/books/findAll";
        ResponseEntity<BookDTO[]> response = template.getForEntity(url, BookDTO[].class);

        assertNotNull(response);
        assertNotNull(response.getBody());

        BookDTO firstBook = response.getBody()[0];
        assertNotNull(firstBook.getTitle());
    }
}

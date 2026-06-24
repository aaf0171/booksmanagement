package com.books.repository;

import com.books.model.Book;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
@Profile("!test")
@RequiredArgsConstructor
public class BooksRepositoryFile implements BookRepository {

    private final ObjectMapper objectMapper;

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        try {
            File file = new File("src/main/java/com/books/datas/books.json");
            JsonNode rootNode = objectMapper.readTree(file);
            JsonNode booksNode = rootNode.get("books");

            if (booksNode != null && booksNode.isArray()) {
                for (JsonNode bookNode : booksNode) {
                    Book book = new Book();
                    book.setTitle(bookNode.has("title") ? bookNode.get("title").asText() : "");
                    book.setIsbn(bookNode.has("isbn") ? bookNode.get("isbn").asText() : "");
                    books.add(book);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading books.json", e);
        }
        return books;
    }
}

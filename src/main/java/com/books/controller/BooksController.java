package com.books.controller;

import com.books.dto.BookDTO;
import com.books.service.BooksService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BooksController {

    private final BooksService booksService;

    @GetMapping("/")
    public String helloWorld() {
        return "Hello World!";
    }

    @GetMapping("/books/findAll")
    public List<BookDTO> findAll() {
        return booksService.findAll();
    }
}

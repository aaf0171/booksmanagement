package com.books.service;

import com.books.dto.BookDTO;
import com.books.model.Book;
import com.books.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BooksService {

    private final BookRepository bookRepository;

    public List<BookDTO> findAll() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(book -> new BookDTO(book.getId(), book.getTitle()))
                .toList();
    }
}

package com.books.repository;

import com.books.model.Book;

import java.util.List;

public interface BookRepository {

    List<Book> findAll();
}

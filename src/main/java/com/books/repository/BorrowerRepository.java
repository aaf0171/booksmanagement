package com.books.repository;

import com.books.model.Borrower;

import java.util.List;

public interface BorrowerRepository {
    List<Borrower> findAll();
}

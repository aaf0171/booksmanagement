package com.books.service;

import com.books.model.Borrower;
import com.books.repository.BorrowerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowersService {

    private final BorrowerRepository borrowerRepository;

    public List<Borrower> findAll() {
        return borrowerRepository.findAll();
    }
}

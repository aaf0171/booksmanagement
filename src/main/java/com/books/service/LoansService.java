package com.books.service;

import com.books.model.Loans;
import com.books.repository.LoansRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoansService {

    private final LoansRepository loansRepository;

    public List<Loans> findAllActiveLoans() {
        return loansRepository.findAllActiveLoans();
    }
}

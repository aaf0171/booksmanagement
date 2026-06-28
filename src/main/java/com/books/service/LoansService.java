package com.books.service;

import com.books.dto.ActiveLoanDTO;
import com.books.repository.LoansRepositoryDatabase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoansService {

    private final LoansRepositoryDatabase loansRepository;

    public List<ActiveLoanDTO> findAllActiveLoans() {
        return loansRepository.findActiveLoans();
    }
}

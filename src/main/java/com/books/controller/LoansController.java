package com.books.controller;

import com.books.model.Loans;
import com.books.service.LoansService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LoansController {

    private final LoansService loansService;

    @GetMapping("/loans/findAllActiveLoans")
    public List<Loans> findAllActiveLoans() {
        return loansService.findAllActiveLoans();
    }
}

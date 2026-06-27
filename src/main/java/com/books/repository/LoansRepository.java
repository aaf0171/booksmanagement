package com.books.repository;

import com.books.model.Loans;
import java.util.List;

public interface LoansRepository {
    List<Loans> findAllActiveLoans();
}

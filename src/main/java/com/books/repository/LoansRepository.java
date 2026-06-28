package com.books.repository;

import com.books.dto.ActiveLoanDTO;
import java.util.List;

public interface LoansRepository {
    List<ActiveLoanDTO> findAllActiveLoans();
}

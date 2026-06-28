package com.books.repository;
import com.books.model.Loan;
import com.books.dto.ActiveLoanDTO;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Primary
@Transactional(readOnly = true)
public interface LoansRepositoryDatabase extends JpaRepository<Loan, Long> {

    @Query(value="""
        SELECT
            l.id AS id,
            b.title AS bookTitle,
            br.name AS borrowerName,
            l.due_date AS dueDate

        FROM loans l

        JOIN books b 
            ON b.id = l.book_id

        JOIN borrowers br 
            ON br.id = l.borrower_id

        WHERE l.return_date IS NULL

    """, nativeQuery = true)
    List<ActiveLoanDTO> findActiveLoans();
}
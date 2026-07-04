package com.books.repository;

import com.books.dto.ActiveLoanDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoansRepositoryDatabase extends JpaRepository<com.books.model.Loan, Long> {

    @Query(value = """
        SELECT l.id AS id,
               l.item_id AS itemId,
               l.borrower_id AS borrowerId,
               CONCAT(br.firstname, ' ', br.lastname) AS borrowerName,
               d.title AS documentTitle,
               l.loan_date AS loanDate,
               l.due_date AS dueDate
        FROM loans l
        JOIN borrowers br ON l.borrower_id = br.id
        JOIN items i ON l.item_id = i.id
        JOIN documents d ON i.document_id = d.id
        WHERE l.return_date IS NULL
        """,
        nativeQuery = true)
    List<ActiveLoanDTO> findActiveLoans();

    @Query(value = """
        SELECT COUNT(*)
        FROM loans l
        WHERE l.item_id = :itemId
          AND l.return_date IS NULL
        """,
        nativeQuery = true)
    Long countActiveLoansForItem(@Param("itemId") Long itemId);

    @Modifying(clearAutomatically = true)
    @Query(value = "DELETE FROM loans WHERE item_id = :itemId", nativeQuery = true)
    void deleteLoansByItemId(@Param("itemId") Long itemId);
}

package com.books.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "loans")
@Getter
@Setter
public class Loans {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long bookId;

    private Long borrowerId;

    private LocalDate loanDate;

    private Integer loanPeriodDays;

    private LocalDate dueDate;

    private LocalDate returnDate;

    private String status;

    public Loans() {}

    public Loans(Long bookId, Long borrowerId, LocalDate loanDate, Integer loanPeriodDays, LocalDate dueDate, LocalDate returnDate, String status) {
        this.bookId = bookId;
        this.borrowerId = borrowerId;
        this.loanDate = loanDate;
        this.loanPeriodDays = loanPeriodDays;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
    }
}

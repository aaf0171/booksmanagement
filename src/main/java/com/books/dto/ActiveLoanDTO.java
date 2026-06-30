package com.books.dto;

import java.time.LocalDateTime;

public interface ActiveLoanDTO {
    Long getId();
    Long getItemId();
    Long getBorrowerId();
    String getBorrowerName();
    String getDocumentTitle();
    LocalDateTime getLoanDate();
    LocalDateTime getDueDate();
}

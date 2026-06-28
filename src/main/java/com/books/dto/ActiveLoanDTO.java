package com.books.dto;
import java.time.LocalDate;

public interface ActiveLoanDTO {

    Long getId();

    String getBookTitle();

    String getBorrowerName();

    LocalDate getDueDate();

}

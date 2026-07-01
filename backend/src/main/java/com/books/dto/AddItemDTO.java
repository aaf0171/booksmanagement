package com.books.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddItemDTO {

    private String barcode;
    private String location;
    private LocalDate acquisitionDate;
    private String physicalStatus;
}

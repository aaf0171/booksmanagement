package com.books.controller;

import com.books.dto.BorrowerDTO;
import com.books.service.BorrowersService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BorrowersController {

    private final BorrowersService borrowersService;

    @GetMapping("/borrowers/findAll")
    public List<BorrowerDTO> findAll() {
        return borrowersService.findAll().stream()
                .map(b -> new BorrowerDTO(b.getId(), b.getName(), b.getSurname()))
                .toList();
    }
}

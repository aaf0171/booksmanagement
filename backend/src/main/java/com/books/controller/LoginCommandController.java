package com.books.controller;

import com.books.dto.CreateLoginDTO;
import com.books.dto.LoginDTO;
import com.books.service.CreateLoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logins")
@RequiredArgsConstructor
public class LoginCommandController {

    private final CreateLoginService createLoginService;

    @PostMapping
    public ResponseEntity<LoginDTO> createLogin(@Valid @RequestBody CreateLoginDTO dto) {
        LoginDTO response = createLoginService.create(dto);
        return ResponseEntity.status(201).body(response);
    }
}

package com.books.controller;

import com.books.dto.ActivationTokenDTO;
import com.books.service.ActivationTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logins")
@RequiredArgsConstructor
public class ActivationTokenController {

    private final ActivationTokenService activationTokenService;

    @PostMapping("/{id}/activation-token")
    public ResponseEntity<ActivationTokenDTO> generateActivationToken(@PathVariable Long id) {
        ActivationTokenDTO response = activationTokenService.generateToken(id);
        return ResponseEntity.status(201).body(response);
    }
}

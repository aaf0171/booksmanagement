package com.books.controller;

import com.books.dto.LoginDTO;
import com.books.service.DisableLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logins")
@RequiredArgsConstructor
public class DisableLoginController {

    private final DisableLoginService disableLoginService;

    @PatchMapping("/{id}/disable")
    public ResponseEntity<LoginDTO> disableLogin(@PathVariable Long id) {
        LoginDTO response = disableLoginService.disable(id);
        return ResponseEntity.ok().body(response);
    }
}

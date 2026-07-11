package com.books.controller;

import com.books.dto.LoginDTO;
import com.books.service.EnableLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logins")
@RequiredArgsConstructor
public class EnableLoginController {

    private final EnableLoginService enableLoginService;

    @PatchMapping("/{id}/enable")
    public ResponseEntity<LoginDTO> enableLogin(@PathVariable Long id) {
        LoginDTO response = enableLoginService.enable(id);
        return ResponseEntity.ok().body(response);
    }
}

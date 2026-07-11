package com.books.controller;

import com.books.service.DeleteLoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logins")
@RequiredArgsConstructor
public class DeleteLoginController {

    private final DeleteLoginService deleteLoginService;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLogin(@PathVariable Long id) {
        deleteLoginService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

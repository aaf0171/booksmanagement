package com.books.controller;

import com.books.service.DeleteItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class DeleteItemController {

    private final DeleteItemService deleteItemService;

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        deleteItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

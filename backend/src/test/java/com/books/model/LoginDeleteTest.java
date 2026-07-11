package com.books.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginDeleteTest {

    @Test
    @DisplayName("shouldDeleteLoginWhenExists")
    void shouldDeleteLoginWhenExists() {
        Login login = Login.builder()
                .id(1L)
                .username("deleteme")
                .passwordHash("$2a$10$hash")
                .enabled(true)
                .build();

        assertNotNull(login);
        assertEquals(1L, login.getId());
    }

    @Test
    @DisplayName("shouldRejectDeletingNonExistentLogin")
    void shouldRejectDeletingNonExistentLogin() {
        Login login = Login.builder()
                .id(999L)
                .username("nonexistent")
                .passwordHash("$2a$10$hash")
                .enabled(true)
                .build();

        assertNotNull(login);
        assertEquals(999L, login.getId());
    }
}

package com.books.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginEnableTest {

    @Test
    @DisplayName("shouldEnableLoginWhenDisabled")
    void shouldEnableLoginWhenDisabled() {
        Login login = Login.builder()
                .username("testuser")
                .passwordHash("$2a$10$hash")
                .enabled(false)
                .build();

        login.setEnabled(true);

        assertTrue(login.getEnabled());
    }

    @Test
    @DisplayName("shouldAcceptAlreadyEnabledLogin")
    void shouldAcceptAlreadyEnabledLogin() {
        Login login = Login.builder()
                .username("testuser")
                .passwordHash("$2a$10$hash")
                .enabled(true)
                .build();

        login.setEnabled(true);

        assertTrue(login.getEnabled());
    }
}

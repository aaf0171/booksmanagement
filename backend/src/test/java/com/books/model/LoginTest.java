package com.books.model;

import com.books.exception.LoginValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginTest {

    @Test
    @DisplayName("shouldHashPasswordWithBCrypt")
    void shouldHashPasswordWithBCrypt() {
        Login login = Login.builder()
                .username("testuser")
                .passwordHash("$2a$10$dummyhashvaluefortestingpasswordhashing0000000000000000000000")
                .enabled(true)
                .build();

        assertNotNull(login.getPasswordHash());
        assertTrue(login.getPasswordHash().startsWith("$2a$"));
        assertEquals("testuser", login.getUsername());
    }

    @Test
    @DisplayName("shouldAcceptNullUsernameAtEntityLevel")
    void shouldAcceptNullUsernameAtEntityLevel() {
        Login login = Login.builder()
                .username(null)
                .passwordHash("$2a$10$hash")
                .build();

        assertNotNull(login);
        assertNull(login.getUsername());
    }

    @Test
    @DisplayName("shouldAcceptEmptyPasswordAtEntityLevel")
    void shouldAcceptEmptyPasswordAtEntityLevel() {
        Login login = Login.builder()
                .username("testuser")
                .passwordHash("")
                .build();

        assertNotNull(login);
        assertEquals("", login.getPasswordHash());
    }

    @Test
    @DisplayName("shouldAcceptShortPasswordAtEntityLevel")
    void shouldAcceptShortPasswordAtEntityLevel() {
        Login login = Login.builder()
                .username("testuser")
                .passwordHash("short")
                .build();

        assertNotNull(login);
        assertEquals("short", login.getPasswordHash());
    }

    @Test
    @DisplayName("shouldAcceptShortUsernameAtEntityLevel")
    void shouldAcceptShortUsernameAtEntityLevel() {
        Login login = Login.builder()
                .username("ab")
                .passwordHash("$2a$10$hashvalue")
                .build();

        assertNotNull(login);
        assertEquals("ab", login.getUsername());
    }

    @Test
    @DisplayName("shouldCreateLoginWithValidData")
    void shouldCreateLoginWithValidData() {
        Login login = Login.builder()
                .username("admin1")
                .passwordHash("$2a$10$dummyhashvaluefortesting000000000000000000000000000000")
                .enabled(true)
                .build();

        assertNotNull(login);
        assertEquals("admin1", login.getUsername());
        assertTrue(login.getEnabled());
        assertNull(login.getLastLogin());
    }
}

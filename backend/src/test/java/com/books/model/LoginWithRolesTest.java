package com.books.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LoginWithRolesTest {

    @Test
    @DisplayName("shouldCreateLoginWithRoles")
    void shouldCreateLoginWithRoles() {
        Role borrower = Role.builder().id(1L).name("BORROWER").build();
        Role librarian = Role.builder().id(2L).name("LIBRARIAN").build();

        Login login = Login.builder()
                .id(1L)
                .username("testuser")
                .passwordHash("$2a$10$hash")
                .enabled(true)
                .roles(Set.of(borrower, librarian))
                .build();

        assertEquals(2, login.getRoles().size());
        assertTrue(login.getRoles().contains(borrower));
        assertTrue(login.getRoles().contains(librarian));
    }

    @Test
    @DisplayName("shouldCreateLoginWithEmptyRoles")
    void shouldCreateLoginWithEmptyRoles() {
        Login login = Login.builder()
                .id(1L)
                .username("testuser")
                .passwordHash("$2a$10$hash")
                .enabled(true)
                .build();

        assertNotNull(login.getRoles());
        assertTrue(login.getRoles().isEmpty());
    }

    @Test
    @DisplayName("shouldAddRoleToLogin")
    void shouldAddRoleToLogin() {
        Role borrower = Role.builder().id(1L).name("BORROWER").build();

        Login login = Login.builder()
                .id(1L)
                .username("testuser")
                .passwordHash("$2a$10$hash")
                .enabled(true)
                .build();

        login.getRoles().add(borrower);

        assertEquals(1, login.getRoles().size());
        assertTrue(login.getRoles().contains(borrower));
    }
}

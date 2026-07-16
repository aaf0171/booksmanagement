package com.books.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    @DisplayName("shouldCreateRoleWithIdAndName")
    void shouldCreateRoleWithIdAndName() {
        Role role = Role.builder()
                .id(1L)
                .name("BORROWER")
                .build();

        assertEquals(1L, role.getId());
        assertEquals("BORROWER", role.getName());
    }

    @Test
    @DisplayName("shouldCreateRoleWithoutId")
    void shouldCreateRoleWithoutId() {
        Role role = Role.builder()
                .name("LIBRARIAN")
                .build();

        assertNull(role.getId());
        assertEquals("LIBRARIAN", role.getName());
    }

    @Test
    @DisplayName("shouldCreateRoleWithDifferentNames")
    void shouldCreateRoleWithDifferentNames() {
        Role borrower = Role.builder().name("BORROWER").build();
        Role librarian = Role.builder().name("LIBRARIAN").build();
        Role admin = Role.builder().name("ADMIN").build();

        assertEquals("BORROWER", borrower.getName());
        assertEquals("LIBRARIAN", librarian.getName());
        assertEquals("ADMIN", admin.getName());
    }
}

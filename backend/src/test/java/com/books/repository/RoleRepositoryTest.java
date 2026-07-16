package com.books.repository;

import com.books.model.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    @DisplayName("shouldSaveRole")
    void shouldSaveRole() {
        Role role = Role.builder()
                .name("TEST_ROLE")
                .build();

        Role saved = roleRepository.save(role);

        assertNotNull(saved.getId());
        assertEquals("TEST_ROLE", saved.getName());
        assertTrue(saved.getId() > 0);
    }

    @Test
    @DisplayName("shouldFindRoleByName")
    void shouldFindRoleByName() {
        Role saved = roleRepository.save(Role.builder().name("FINDABLE_ROLE").build());
        roleRepository.flush();

        Optional<Role> found = roleRepository.findByName("FINDABLE_ROLE");

        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals("FINDABLE_ROLE", found.get().getName());
    }

    @Test
    @DisplayName("shouldFindAllRoles")
    void shouldFindAllRoles() {
        roleRepository.save(Role.builder().name("ROLE_A").build());
        roleRepository.save(Role.builder().name("ROLE_B").build());
        roleRepository.flush();

        List<Role> roles = roleRepository.findAll();

        assertTrue(roles.size() >= 2);
    }

    @Test
    @DisplayName("shouldReturnEmptyWhenRoleNotFound")
    void shouldReturnEmptyWhenRoleNotFound() {
        Optional<Role> found = roleRepository.findByName("NONEXISTENT_ROLE");

        assertFalse(found.isPresent());
    }
}

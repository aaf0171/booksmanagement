package com.books.repository;

import com.books.model.Login;
import com.books.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LoginsRepositoryWithRolesTest {

    @Autowired
    private LoginsRepository loginsRepository;

    @Autowired
    private RoleRepository roleRepository;

    private Role borrowerRole;
    private Role librarianRole;
    private Login testLogin;

    @BeforeEach
    void setUp() {
        roleRepository.deleteAll();
        loginsRepository.deleteAll();

        borrowerRole = roleRepository.save(Role.builder().name("BORROWER").build());
        librarianRole = roleRepository.save(Role.builder().name("LIBRARIAN").build());
    }

    @Test
    @DisplayName("shouldSaveLoginWithRoles")
    void shouldSaveLoginWithRoles() {
        testLogin = Login.builder()
                .username("login-with-roles")
                .passwordHash("$2a$10$dummyhashvalueforrepository0000000000000000000000000")
                .enabled(true)
                .roles(new HashSet<>(Arrays.asList(borrowerRole, librarianRole)))
                .build();

        Login saved = loginsRepository.save(testLogin);

        assertNotNull(saved.getId());
        loginsRepository.flush();

        Login found = loginsRepository.findById(saved.getId()).orElseThrow();
        assertEquals(2, found.getRoles().size());
        assertTrue(found.getRoles().contains(borrowerRole));
        assertTrue(found.getRoles().contains(librarianRole));
    }

    @Test
    @DisplayName("shouldFindLoginWithRolesFetched")
    void shouldFindLoginWithRolesFetched() {
        testLogin = Login.builder()
                .username("findable-roles-login")
                .passwordHash("$2a$10$dummyhashvalueforrepository0000000000000000000000000")
                .enabled(true)
                .roles(new HashSet<>(Collections.singletonList(borrowerRole)))
                .build();

        Login saved = loginsRepository.save(testLogin);
        loginsRepository.flush();

        Login found = loginsRepository.findById(saved.getId()).orElseThrow();

        assertNotNull(found.getRoles());
        assertFalse(found.getRoles().isEmpty());
        assertEquals(1, found.getRoles().size());
        assertEquals("BORROWER", found.getRoles().iterator().next().getName());
    }

    @Test
    @DisplayName("shouldPersistLoginRoleMapping")
    void shouldPersistLoginRoleMapping() {
        Login login1 = Login.builder()
                .username("login1")
                .passwordHash("$2a$10$dummyhashvalueforrepository0000000000000000000000000")
                .enabled(true)
                .roles(new HashSet<>(Collections.singletonList(borrowerRole)))
                .build();

        Login login2 = Login.builder()
                .username("login2")
                .passwordHash("$2a$10$dummyhashvalueforrepository0000000000000000000000000")
                .enabled(true)
                .roles(new HashSet<>(Arrays.asList(borrowerRole, librarianRole)))
                .build();

        loginsRepository.save(login1);
        loginsRepository.save(login2);
        loginsRepository.flush();

        Login found1 = loginsRepository.findByUsername("login1").orElseThrow();
        Login found2 = loginsRepository.findByUsername("login2").orElseThrow();

        assertEquals(1, found1.getRoles().size());
        assertEquals(2, found2.getRoles().size());

        assertTrue(found1.getRoles().contains(borrowerRole));
        assertTrue(found2.getRoles().contains(borrowerRole));
        assertTrue(found2.getRoles().contains(librarianRole));
    }
}

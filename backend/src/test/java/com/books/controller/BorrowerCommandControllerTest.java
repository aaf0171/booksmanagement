package com.books.controller;

import com.books.dto.CreateBorrowerDTO;
import com.books.dto.CreateBorrowerResponseDTO;
import com.books.exception.LoginConflictException;
import com.books.exception.LoginValidationException;
import com.books.model.Borrower;
import com.books.model.Login;
import com.books.repository.BorrowerRepository;
import com.books.repository.LoginsRepository;
import com.books.service.CreateBorrowerService;
import com.books.service.ActivationEmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BorrowerCommandControllerTest {

    @Autowired
    private BorrowerRepository borrowerRepository;

    @Autowired
    private LoginsRepository loginsRepository;

    @Autowired
    private CreateBorrowerService createBorrowerService;

    @Autowired
    private ActivationEmailService activationEmailService;

    private BorrowerCommandController controller;

    @BeforeEach
    void setUp() {
        loginsRepository.deleteAll();
        borrowerRepository.deleteAll();
        controller = new BorrowerCommandController(createBorrowerService);
    }

    @Test
    @DisplayName("POST_create_borrower_returns_201")
    void POST_create_borrower_returns_201() {
        CreateBorrowerDTO dto = CreateBorrowerDTO.builder()
                .firstname("Jane")
                .lastname("Smith")
                .email("jane@example.com")
                .username("janesmith")
                .build();

        ResponseEntity<CreateBorrowerResponseDTO> response = controller.createBorrower(dto);

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("Jane", response.getBody().getFirstname());
        assertEquals("janesmith", response.getBody().getUsername());
        assertNotNull(response.getBody().getPassword());
        assertFalse(response.getBody().getLoginEnabled());
    }

    @Test
    @DisplayName("POST_create_borrower_triggers_activation_email")
    void POST_create_borrower_triggers_activation_email() {
        CreateBorrowerDTO dto = CreateBorrowerDTO.builder()
                .firstname("Alice")
                .lastname("Dupont")
                .email("alice.dupont@example.com")
                .username("alicedupont")
                .build();

        ResponseEntity<CreateBorrowerResponseDTO> response = controller.createBorrower(dto);

        assertEquals(201, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("alice.dupont@example.com", response.getBody().getEmail());

        Login login = loginsRepository.findByUsername("alicedupont").orElseThrow();
        assertFalse(login.getEnabled());

        Borrower borrower = borrowerRepository.findById(response.getBody().getId()).orElseThrow();
        assertNotNull(borrower);
        assertEquals("alice.dupont@example.com", borrower.getEmail());
    }

    @Test
    @DisplayName("POST_create_borrower_invalid_data_no_email_sent")
    void POST_create_borrower_invalid_data_no_email_sent() {
        CreateBorrowerDTO dto = CreateBorrowerDTO.builder()
                .firstname("")
                .lastname("Doe")
                .username("johndoe")
                .build();

        LoginValidationException ex = assertThrows(LoginValidationException.class,
                () -> controller.createBorrower(dto));

        assertEquals("Firstname must not be blank", ex.getMessage());

        assertEquals(0, borrowerRepository.count());
        assertEquals(0, loginsRepository.count());
    }

    @Test
    @DisplayName("POST_duplicate_username_returns_409")
    void POST_duplicate_username_returns_409() {
        CreateBorrowerDTO first = CreateBorrowerDTO.builder()
                .firstname("John")
                .lastname("Doe")
                .username("dupuser")
                .build();

        controller.createBorrower(first);

        LoginConflictException ex = assertThrows(LoginConflictException.class, () -> {
            CreateBorrowerDTO second = CreateBorrowerDTO.builder()
                    .firstname("Jane")
                    .lastname("Smith")
                    .username("dupuser")
                    .build();
            controller.createBorrower(second);
        });

        assertTrue(ex.getMessage().contains("dupuser"));
    }

    @Test
    @DisplayName("POST_blank_firstname_returns_400")
    void POST_blank_firstname_returns_400() {
        CreateBorrowerDTO dto = CreateBorrowerDTO.builder()
                .firstname("")
                .lastname("Doe")
                .username("blankfirst")
                .build();

        LoginValidationException ex = assertThrows(LoginValidationException.class,
                () -> controller.createBorrower(dto));

        assertEquals("Firstname must not be blank", ex.getMessage());
    }

    @Test
    @DisplayName("POST_blank_lastname_returns_400")
    void POST_blank_lastname_returns_400() {
        CreateBorrowerDTO dto = CreateBorrowerDTO.builder()
                .firstname("John")
                .lastname("")
                .username("blanklast")
                .build();

        LoginValidationException ex = assertThrows(LoginValidationException.class,
                () -> controller.createBorrower(dto));

        assertEquals("Lastname must not be blank", ex.getMessage());
    }

    @Test
    @DisplayName("POST_blank_username_returns_400")
    void POST_blank_username_returns_400() {
        CreateBorrowerDTO dto = CreateBorrowerDTO.builder()
                .firstname("John")
                .lastname("Doe")
                .username("")
                .build();

        LoginValidationException ex = assertThrows(LoginValidationException.class,
                () -> controller.createBorrower(dto));

        assertEquals("Username must not be blank", ex.getMessage());
    }

    @Test
    @DisplayName("POST_short_username_returns_400")
    void POST_short_username_returns_400() {
        CreateBorrowerDTO dto = CreateBorrowerDTO.builder()
                .firstname("John")
                .lastname("Doe")
                .username("ab")
                .build();

        LoginValidationException ex = assertThrows(LoginValidationException.class,
                () -> controller.createBorrower(dto));

        assertEquals("Username must be between 3 and 100 characters", ex.getMessage());
    }

    @Test
    @DisplayName("POST_response_contains_raw_password")
    void POST_response_contains_raw_password() {
        CreateBorrowerDTO dto = CreateBorrowerDTO.builder()
                .firstname("John")
                .lastname("Doe")
                .username("passwordtest")
                .build();

        ResponseEntity<CreateBorrowerResponseDTO> response = controller.createBorrower(dto);

        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getPassword());
        assertFalse(response.getBody().getPassword().isBlank());
    }

    @Test
    @DisplayName("POST_response_login_disabled")
    void POST_response_login_disabled() {
        CreateBorrowerDTO dto = CreateBorrowerDTO.builder()
                .firstname("John")
                .lastname("Doe")
                .username("disabledtest")
                .build();

        ResponseEntity<CreateBorrowerResponseDTO> response = controller.createBorrower(dto);

        assertNotNull(response.getBody());
        assertFalse(response.getBody().getLoginEnabled());
    }
}

package com.books.service;

import com.books.dto.ActivationTokenDTO;
import com.books.dto.CreateBorrowerDTO;
import com.books.dto.CreateBorrowerResponseDTO;
import com.books.exception.LoginConflictException;
import com.books.exception.LoginValidationException;
import com.books.model.Borrower;
import com.books.model.Login;
import com.books.repository.BorrowerRepository;
import com.books.repository.LoginsRepository;
import com.books.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateBorrowerServiceTest {

    @Mock
    private BorrowerRepository borrowerRepository;

    @Mock
    private LoginsRepository loginsRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private ActivationTokenService activationTokenService;

    @Mock
    private ActivationEmailService activationEmailService;

    @InjectMocks
    private CreateBorrowerService createBorrowerService;

    private CreateBorrowerDTO validDto;

    @BeforeEach
    void setUp() {
        validDto = CreateBorrowerDTO.builder()
                .firstname("John")
                .lastname("Doe")
                .email("john@example.com")
                .username("johndoe")
                .build();
    }

    @Test
    @DisplayName("shouldCreateBorrowerWithLogin")
    void shouldCreateBorrowerWithLogin() {
        when(loginsRepository.existsByUsername("johndoe")).thenReturn(false);

        Login savedLogin = Login.builder().id(1L).username("johndoe").passwordHash(null).build();
        when(loginsRepository.save(any(Login.class))).thenReturn(savedLogin);

        Borrower savedBorrower = Borrower.builder().id(1L).login_id(1L).firstname("John").lastname("Doe").build();
        when(borrowerRepository.save(any(Borrower.class))).thenReturn(savedBorrower);

        ActivationTokenDTO tokenDto = ActivationTokenDTO.builder()
                .id(1L)
                .loginId(1L)
                .token("token123")
                .type("ACTIVATION")
                .build();
        when(activationTokenService.generateToken(1L)).thenReturn(tokenDto);

        CreateBorrowerResponseDTO result = createBorrowerService.create(validDto);

        assertNotNull(result);
        assertEquals("John", result.getFirstname());
        assertEquals("johndoe", result.getUsername());
        assertNull(result.getPassword());
        assertFalse(result.getLoginEnabled());
        verify(loginsRepository).save(any(Login.class));
        verify(borrowerRepository).save(any(Borrower.class));
    }

    @Test
    @DisplayName("shouldSendActivationEmailAfterBorrowerCreation")
    void shouldSendActivationEmailAfterBorrowerCreation() {
        when(loginsRepository.existsByUsername("johndoe")).thenReturn(false);

        Login savedLogin = Login.builder().id(1L).username("johndoe").passwordHash(null).build();
        when(loginsRepository.save(any(Login.class))).thenReturn(savedLogin);

        Borrower savedBorrower = Borrower.builder()
                .id(1L)
                .login_id(1L)
                .firstname("John")
                .lastname("Doe")
                .email("john@example.com")
                .build();
        when(borrowerRepository.save(any(Borrower.class))).thenReturn(savedBorrower);

        ActivationTokenDTO tokenDto = ActivationTokenDTO.builder()
                .id(1L)
                .loginId(1L)
                .token("token123")
                .type("ACTIVATION")
                .build();
        when(activationTokenService.generateToken(1L)).thenReturn(tokenDto);

        CreateBorrowerResponseDTO result = createBorrowerService.create(validDto);

        assertNotNull(result);
    verify(activationEmailService).sendActivationEmail("john@example.com", "token123");
    }

    @Test
    @DisplayName("shouldNotSendEmailWhenBorrowerCreationFails")
    void shouldNotSendEmailWhenBorrowerCreationFails() {
        when(loginsRepository.existsByUsername("johndoe")).thenReturn(false);

        Login savedLogin = Login.builder().id(1L).username("johndoe").passwordHash(null).build();
        when(loginsRepository.save(any(Login.class))).thenReturn(savedLogin);

        when(borrowerRepository.save(any(Borrower.class))).thenThrow(new RuntimeException("DB error"));

  assertThrows(RuntimeException.class, () -> createBorrowerService.create(validDto));

        verify(activationEmailService, never()).sendActivationEmail(any(), any());
    }

    @Test
    @DisplayName("shouldNotSendEmailWhenLoginCreationFails")
    void shouldNotSendEmailWhenLoginCreationFails() {
        when(loginsRepository.existsByUsername("johndoe")).thenReturn(false);

        when(loginsRepository.save(any(Login.class))).thenThrow(new RuntimeException("DB error"));

   assertThrows(RuntimeException.class, () -> createBorrowerService.create(validDto));

        verify(activationEmailService, never()).sendActivationEmail(any(), any());
    }

    @Test
    @DisplayName("shouldLogErrorWhenEmailSendingFails")
    void shouldLogErrorWhenEmailSendingFails() {
        when(loginsRepository.existsByUsername("johndoe")).thenReturn(false);

        Login savedLogin = Login.builder().id(1L).username("johndoe").passwordHash(null).build();
        when(loginsRepository.save(any(Login.class))).thenReturn(savedLogin);

        Borrower savedBorrower = Borrower.builder()
                .id(1L)
                .login_id(1L)
                .firstname("John")
                .lastname("Doe")
                .email("john@example.com")
                .build();
        when(borrowerRepository.save(any(Borrower.class))).thenReturn(savedBorrower);

        ActivationTokenDTO tokenDto = ActivationTokenDTO.builder()
                .id(1L)
                .loginId(1L)
                .token("token123")
                .type("ACTIVATION")
                .build();
        when(activationTokenService.generateToken(1L)).thenReturn(tokenDto);

        doThrow(new RuntimeException("SMTP error")).when(activationEmailService).sendActivationEmail(any(), any());

        CreateBorrowerResponseDTO result = createBorrowerService.create(validDto);

      assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    @DisplayName("shouldNotRollbackBorrowerOnEmailFailure")
    void shouldNotRollbackBorrowerOnEmailFailure() {
        when(loginsRepository.existsByUsername("johndoe")).thenReturn(false);

        Login savedLogin = Login.builder().id(1L).username("johndoe").passwordHash(null).build();
        when(loginsRepository.save(any(Login.class))).thenReturn(savedLogin);

        Borrower savedBorrower = Borrower.builder()
                .id(1L)
                .login_id(1L)
                .firstname("John")
                .lastname("Doe")
                .email("john@example.com")
                .build();
        when(borrowerRepository.save(any(Borrower.class))).thenReturn(savedBorrower);

        ActivationTokenDTO tokenDto = ActivationTokenDTO.builder()
                .id(1L)
                .loginId(1L)
                .token("token123")
                .type("ACTIVATION")
                .build();
        when(activationTokenService.generateToken(1L)).thenReturn(tokenDto);

        doThrow(new RuntimeException("SMTP error")).when(activationEmailService).sendActivationEmail(any(), any());

        CreateBorrowerResponseDTO result = createBorrowerService.create(validDto);

        assertNotNull(result);
        assertEquals(1L, savedBorrower.getId());

     verify(borrowerRepository).save(any(Borrower.class));
    }

    @Test
    @DisplayName("shouldNotSendEmailWhenEmailFieldIsNull")
    void shouldNotSendEmailWhenEmailFieldIsNull() {
        CreateBorrowerDTO dto = CreateBorrowerDTO.builder()
                .firstname("John")
                .lastname("Doe")
                .email(null)
                .username("johndoe")
                .build();

        when(loginsRepository.existsByUsername("johndoe")).thenReturn(false);

        Login savedLogin = Login.builder().id(1L).username("johndoe").passwordHash(null).build();
        when(loginsRepository.save(any(Login.class))).thenReturn(savedLogin);

        Borrower savedBorrower = Borrower.builder()
                .id(1L)
                .login_id(1L)
                .firstname("John")
                .lastname("Doe")
                .email(null)
                .build();
        when(borrowerRepository.save(any(Borrower.class))).thenReturn(savedBorrower);

        ActivationTokenDTO tokenDto = ActivationTokenDTO.builder()
                .id(1L)
                .loginId(1L)
                .token("token123")
                .type("ACTIVATION")
                .build();
        when(activationTokenService.generateToken(1L)).thenReturn(tokenDto);

        CreateBorrowerResponseDTO result = createBorrowerService.create(dto);

    assertNotNull(result);
        verify(activationEmailService, never()).sendActivationEmail(any(), any());
    }

    @Test
    @DisplayName("shouldDisableLoginByDefault")
    void shouldDisableLoginByDefault() {
        when(loginsRepository.existsByUsername("johndoe")).thenReturn(false);

        Login savedLogin = Login.builder().id(1L).username("johndoe").passwordHash(null).build();
        when(loginsRepository.save(any(Login.class))).thenReturn(savedLogin);

        Borrower savedBorrower = Borrower.builder().id(1L).login_id(1L).build();
        when(borrowerRepository.save(any(Borrower.class))).thenReturn(savedBorrower);

        CreateBorrowerResponseDTO result = createBorrowerService.create(validDto);

       assertFalse(result.getLoginEnabled());
        assertNull(result.getPassword());
    }

    @Test
    @DisplayName("shouldReturnNullPasswordInResponse")
    void shouldReturnNullPasswordInResponse() {
        when(loginsRepository.existsByUsername("johndoe")).thenReturn(false);

        Login savedLogin = Login.builder().id(1L).build();
        when(loginsRepository.save(any(Login.class))).thenReturn(savedLogin);

        Borrower savedBorrower = Borrower.builder().id(1L).build();
        when(borrowerRepository.save(any(Borrower.class))).thenReturn(savedBorrower);

        ActivationTokenDTO tokenDto = ActivationTokenDTO.builder()
                .id(1L)
                .loginId(1L)
                .token("token123")
                .type("ACTIVATION")
                .build();
        when(activationTokenService.generateToken(1L)).thenReturn(tokenDto);

        CreateBorrowerResponseDTO result = createBorrowerService.create(validDto);

      assertNull(result.getPassword());
    }

    @Test
    @DisplayName("shouldThrowConflictWhenUsernameAlreadyExists")
    void shouldThrowConflictWhenUsernameAlreadyExists() {
        when(loginsRepository.existsByUsername("johndoe")).thenReturn(true);

        LoginConflictException ex = assertThrows(LoginConflictException.class,
                () -> createBorrowerService.create(validDto));

        assertTrue(ex.getMessage().contains("johndoe"));
    }

    @Test
    @DisplayName("shouldThrowBadRequestWhenFirstnameBlank")
    void shouldThrowBadRequestWhenFirstnameBlank() {
        CreateBorrowerDTO dto = CreateBorrowerDTO.builder()
                .firstname("")
                .lastname("Doe")
                .username("johndoe")
                .build();

        LoginValidationException ex = assertThrows(LoginValidationException.class,
                () -> createBorrowerService.create(dto));

        assertEquals("Firstname must not be blank", ex.getMessage());
    }

    @Test
    @DisplayName("shouldThrowBadRequestWhenLastnameBlank")
    void shouldThrowBadRequestWhenLastnameBlank() {
        CreateBorrowerDTO dto = CreateBorrowerDTO.builder()
                .firstname("John")
                .lastname("")
                .username("johndoe")
                .build();

        LoginValidationException ex = assertThrows(LoginValidationException.class,
                () -> createBorrowerService.create(dto));

        assertEquals("Lastname must not be blank", ex.getMessage());
    }

    @Test
    @DisplayName("shouldThrowBadRequestWhenUsernameBlank")
    void shouldThrowBadRequestWhenUsernameBlank() {
        CreateBorrowerDTO dto = CreateBorrowerDTO.builder()
                .firstname("John")
                .lastname("Doe")
                .username("")
                .build();

        LoginValidationException ex = assertThrows(LoginValidationException.class,
                () -> createBorrowerService.create(dto));

        assertEquals("Username must not be blank", ex.getMessage());
    }

    @Test
    @DisplayName("shouldThrowBadRequestWhenUsernameTooShort")
    void shouldThrowBadRequestWhenUsernameTooShort() {
        CreateBorrowerDTO dto = CreateBorrowerDTO.builder()
                .firstname("John")
                .lastname("Doe")
                .username("jo")
                .build();

        LoginValidationException ex = assertThrows(LoginValidationException.class,
                () -> createBorrowerService.create(dto));

        assertEquals("Username must be between 3 and 100 characters", ex.getMessage());
    }

    @Test
    @DisplayName("shouldRollbackOnTransactionFailure")
    void shouldRollbackOnTransactionFailure() {
        when(loginsRepository.existsByUsername("johndoe")).thenReturn(false);

        Login savedLogin = Login.builder().id(1L).username("johndoe").passwordHash(null).build();
        when(loginsRepository.save(any(Login.class))).thenReturn(savedLogin);
        when(borrowerRepository.save(any(Borrower.class))).thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> createBorrowerService.create(validDto));

        assertTrue(ex.getMessage().contains("DB error"));
    }
}

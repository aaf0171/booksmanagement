package com.books.dto;

import com.books.enums.ActivationStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActivationResponseDTOTest {

    @Test
    @DisplayName("shouldCreateResponseWithAllFields")
    void shouldCreateResponseWithAllFields() {
        ActivationResponseDTO dto = new ActivationResponseDTO(ActivationStatus.SUCCESS, "Account activated successfully", null);

        assertEquals(ActivationStatus.SUCCESS, dto.status());
        assertEquals("Account activated successfully", dto.message());
        assertNull(dto.email());
    }

    @Test
    @DisplayName("shouldCreateTokenExpiredResponseWithEmail")
    void shouldCreateTokenExpiredResponseWithEmail() {
        ActivationResponseDTO dto = new ActivationResponseDTO(ActivationStatus.TOKEN_EXPIRED, null, "utilisateur@example.com");

        assertEquals(ActivationStatus.TOKEN_EXPIRED, dto.status());
        assertNull(dto.message());
        assertEquals("utilisateur@example.com", dto.email());
    }

    @Test
    @DisplayName("shouldCreateTokenInvalidResponseWithoutEmail")
    void shouldCreateTokenInvalidResponseWithoutEmail() {
        ActivationResponseDTO dto = new ActivationResponseDTO(ActivationStatus.TOKEN_INVALID, null, null);

        assertEquals(ActivationStatus.TOKEN_INVALID, dto.status());
        assertNull(dto.message());
        assertNull(dto.email());
    }

    @Test
    @DisplayName("shouldCreateAlreadyActivatedResponseWithoutEmail")
    void shouldCreateAlreadyActivatedResponseWithoutEmail() {
        ActivationResponseDTO dto = new ActivationResponseDTO(ActivationStatus.ALREADY_ACTIVATED, null, null);

        assertEquals(ActivationStatus.ALREADY_ACTIVATED, dto.status());
        assertNull(dto.message());
        assertNull(dto.email());
    }

    @Test
    @DisplayName("shouldNotExposeEmailForInvalidToken")
    void shouldNotExposeEmailForInvalidToken() {
        ActivationResponseDTO dto = new ActivationResponseDTO(ActivationStatus.TOKEN_INVALID, null, null);

        assertNull(dto.email());
    }

    @Test
    @DisplayName("shouldExposeEmailOnlyForTokenExpired")
    void shouldExposeEmailOnlyForTokenExpired() {
        ActivationResponseDTO expiredDto = new ActivationResponseDTO(ActivationStatus.TOKEN_EXPIRED, null, "user@example.com");
        ActivationResponseDTO invalidDto = new ActivationResponseDTO(ActivationStatus.TOKEN_INVALID, null, null);

        assertEquals("user@example.com", expiredDto.email());
        assertNull(invalidDto.email());
    }

    @Test
    @DisplayName("shouldReturnNullEmailForSuccess")
    void shouldReturnNullEmailForSuccess() {
        ActivationResponseDTO dto = new ActivationResponseDTO(ActivationStatus.SUCCESS, "Account activated successfully", null);

        assertNull(dto.email());
    }

    @Test
    @DisplayName("shouldReturnNullEmailForAlreadyActivated")
    void shouldReturnNullEmailForAlreadyActivated() {
        ActivationResponseDTO dto = new ActivationResponseDTO(ActivationStatus.ALREADY_ACTIVATED, null, null);

        assertNull(dto.email());
    }
}

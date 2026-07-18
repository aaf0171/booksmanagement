package com.books.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActivationStatusTest {

    @Test
    @DisplayName("shouldHaveAllExpectedValues")
    void shouldHaveAllExpectedValues() {
        ActivationStatus[] expectedValues = {
            ActivationStatus.SUCCESS,
            ActivationStatus.TOKEN_EXPIRED,
            ActivationStatus.TOKEN_INVALID,
            ActivationStatus.ALREADY_ACTIVATED
        };

        ActivationStatus[] actualValues = ActivationStatus.values();
        assertEquals(expectedValues.length, actualValues.length);

        for (ActivationStatus expected : expectedValues) {
            assertDoesNotThrow(() -> ActivationStatus.valueOf(expected.name()));
        }
    }

    @Test
    @DisplayName("shouldReturnCorrectNumberOfValues")
    void shouldReturnCorrectNumberOfValues() {
        assertEquals(4, ActivationStatus.values().length);
    }

    @Test
    @DisplayName("shouldReturnEnumFromName")
    void shouldReturnEnumFromName() {
        assertEquals(ActivationStatus.SUCCESS, ActivationStatus.valueOf("SUCCESS"));
        assertEquals(ActivationStatus.TOKEN_EXPIRED, ActivationStatus.valueOf("TOKEN_EXPIRED"));
        assertEquals(ActivationStatus.TOKEN_INVALID, ActivationStatus.valueOf("TOKEN_INVALID"));
        assertEquals(ActivationStatus.ALREADY_ACTIVATED, ActivationStatus.valueOf("ALREADY_ACTIVATED"));
    }
}

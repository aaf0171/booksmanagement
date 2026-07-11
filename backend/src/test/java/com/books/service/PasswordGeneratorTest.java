package com.books.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorTest {

    private final PasswordGenerator passwordGenerator = new PasswordGenerator();

    @Test
    @DisplayName("shouldGeneratePasswordWith16Characters")
    void shouldGeneratePasswordWith16Characters() {
        String password = passwordGenerator.generate();

        assertEquals(16, password.length());
    }

    @Test
    @DisplayName("shouldGeneratePasswordWithUppercaseLetter")
    void shouldGeneratePasswordWithUppercaseLetter() {
        String password = passwordGenerator.generate();

        assertTrue(password.chars().anyMatch(Character::isUpperCase));
    }

    @Test
    @DisplayName("shouldGeneratePasswordWithLowercaseLetter")
    void shouldGeneratePasswordWithLowercaseLetter() {
        String password = passwordGenerator.generate();

        assertTrue(password.chars().anyMatch(Character::isLowerCase));
    }

    @Test
    @DisplayName("shouldGeneratePasswordWithDigit")
    void shouldGeneratePasswordWithDigit() {
        String password = passwordGenerator.generate();

        assertTrue(password.chars().anyMatch(Character::isDigit));
    }

    @Test
    @DisplayName("shouldGeneratePasswordWithSpecialCharacter")
    void shouldGeneratePasswordWithSpecialCharacter() {
        String password = passwordGenerator.generate();

        String specialChars = "!@#$%^&*";
        assertTrue(password.chars().anyMatch(c -> specialChars.indexOf(c) >= 0));
    }

    @Test
    @DisplayName("shouldGenerateDifferentPasswords")
    void shouldGenerateDifferentPasswords() {
        String password1 = passwordGenerator.generate();
        String password2 = passwordGenerator.generate();

        assertNotEquals(password1, password2);
    }
}

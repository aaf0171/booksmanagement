package com.books.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class PasswordGenerator {

    private static final int PASSWORD_LENGTH = 16;
    private static final String UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*";
    private static final String ALL_CHARS = UPPER_CASE + LOWER_CASE + DIGITS + SPECIAL_CHARS;

    private final SecureRandom secureRandom;

    public PasswordGenerator() {
        this.secureRandom = new SecureRandom();
    }

    public String generate() {
        List<Character> passwordChars = new ArrayList<>();

        passwordChars.add(UPPER_CASE.charAt(secureRandom.nextInt(UPPER_CASE.length())));
        passwordChars.add(LOWER_CASE.charAt(secureRandom.nextInt(LOWER_CASE.length())));
        passwordChars.add(DIGITS.charAt(secureRandom.nextInt(DIGITS.length())));
        passwordChars.add(SPECIAL_CHARS.charAt(secureRandom.nextInt(SPECIAL_CHARS.length())));

        for (int i = 0; i < PASSWORD_LENGTH - 4; i++) {
            passwordChars.add(ALL_CHARS.charAt(secureRandom.nextInt(ALL_CHARS.length())));
        }

        Collections.shuffle(passwordChars, secureRandom);

        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);
        for (char c : passwordChars) {
            password.append(c);
        }

        return password.toString();
    }
}

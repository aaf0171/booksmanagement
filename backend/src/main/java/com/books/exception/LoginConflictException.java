package com.books.exception;

public class LoginConflictException extends RuntimeException {

    public LoginConflictException(String username) {
        super("Login conflict: username '" + username + "' already exists");
    }
}

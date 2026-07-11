package com.books.exception;

public class LoginNotFoundException extends RuntimeException {

    public LoginNotFoundException(Long id) {
        super("Login not found with id: " + id);
    }
}

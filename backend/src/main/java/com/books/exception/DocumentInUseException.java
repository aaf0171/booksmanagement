package com.books.exception;

public class DocumentInUseException extends RuntimeException {

    public DocumentInUseException(Long id) {
        super("Cannot delete document with id " + id + ": one or more Items are still attached.");
    }
}

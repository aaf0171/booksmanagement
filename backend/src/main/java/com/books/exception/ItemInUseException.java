package com.books.exception;

public class ItemInUseException extends RuntimeException {

    public ItemInUseException(Long id) {
        super("Cannot delete item with id " + id + ": it has an active loan.");
    }
}

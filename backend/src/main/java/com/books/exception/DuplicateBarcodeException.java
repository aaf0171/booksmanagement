package com.books.exception;

public class DuplicateBarcodeException extends RuntimeException {

    public DuplicateBarcodeException(String barcode) {
        super("An item with barcode '" + barcode + "' already exists.");
    }
}

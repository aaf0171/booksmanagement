package com.books.dto;

public class BookDTO {
    private final Long id;
    private final String title;

    public BookDTO(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
}

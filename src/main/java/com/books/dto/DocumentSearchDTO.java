package com.books.dto;

import java.time.Year;

public interface DocumentSearchDTO {

    Long getId();

    String getTitle();

    String getSubtitle();

    String getDocumentType();

    String getIsbn();

    String getPublisher();

    Integer getPublicationYear();

    String getDescription();

    java.time.LocalDateTime getCreatedAt();
}

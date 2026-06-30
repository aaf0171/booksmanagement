package com.books.repository;

import com.books.dto.DocumentSearchDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentsSearchRepositoryDatabase extends JpaRepository<com.books.model.Document, Long> {

    @Query(value = """
        SELECT d.id AS id,
               d.title AS title,
               d.subtitle AS subtitle,
               d.document_type AS documentType,
               d.isbn AS isbn,
               d.publisher AS publisher,
               d.publication_year AS publicationYear,
               d.description AS description,
               d.created_at AS createdAt
        FROM documents d
        WHERE (MATCH(d.title, d.subtitle, d.description, d.publisher) AGAINST (:query IN NATURAL LANGUAGE MODE)
               OR :query IS NULL OR :query = '')
          AND (:title IS NULL OR d.title LIKE CONCAT('%', :title, '%'))
          AND (:subtitle IS NULL OR d.subtitle LIKE CONCAT('%', :subtitle, '%'))
          AND (:documentType IS NULL OR d.document_type = :documentType)
          AND (:isbn IS NULL OR d.isbn = :isbn)
          AND (:publisher IS NULL OR d.publisher LIKE CONCAT('%', :publisher, '%'))
          AND (:publicationYear IS NULL OR d.publication_year = :publicationYear)
        """,
        nativeQuery = true)
    List<DocumentSearchDTO> searchDocuments(
            @Param("query") String query,
            @Param("title") String title,
            @Param("subtitle") String subtitle,
            @Param("documentType") String documentType,
            @Param("isbn") String isbn,
            @Param("publisher") String publisher,
            @Param("publicationYear") Integer publicationYear
    );

    @Query(value = """
        SELECT d.id AS id,
               d.title AS title,
               d.subtitle AS subtitle,
               d.document_type AS documentType,
               d.isbn AS isbn,
               d.publisher AS publisher,
               d.publication_year AS publicationYear,
               d.description AS description,
               d.created_at AS createdAt
        FROM documents d
        WHERE d.title LIKE CONCAT('%', :title, '%')
        """,
        nativeQuery = true)
    List<DocumentSearchDTO> searchByTitle(@Param("title") String title);

    @Query(value = """
        SELECT d.id AS id,
               d.title AS title,
               d.subtitle AS subtitle,
               d.document_type AS documentType,
               d.isbn AS isbn,
               d.publisher AS publisher,
               d.publication_year AS publicationYear,
               d.description AS description,
               d.created_at AS createdAt
        FROM documents d
        WHERE d.publication_year = :publicationYear
        """,
        nativeQuery = true)
    List<DocumentSearchDTO> searchByPublicationYear(@Param("publicationYear") Integer publicationYear);

    @Query(value = """
        SELECT d.id AS id,
               d.title AS title,
               d.subtitle AS subtitle,
               d.document_type AS documentType,
               d.isbn AS isbn,
               d.publisher AS publisher,
               d.publication_year AS publicationYear,
               d.description AS description,
               d.created_at AS createdAt
        FROM documents d
        WHERE d.publisher LIKE CONCAT('%', :publisher, '%')
        """,
        nativeQuery = true)
    List<DocumentSearchDTO> searchByPublisher(@Param("publisher") String publisher);
}

package com.books.repository;

import com.books.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentsRepository extends JpaRepository<Document, Long> {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Document d WHERE d.id = :id")
    int deleteByIdCustom(@Param("id") Long id);
}

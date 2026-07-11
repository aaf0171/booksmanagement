package com.books.repository;

import com.books.model.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoginsRepository extends JpaRepository<Login, Long> {

    Optional<Login> findByUsername(String username);

    boolean existsByUsername(String username);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM Login l WHERE l.id = :id")
    int deleteLoginByIdCustom(@Param("id") Long id);
}

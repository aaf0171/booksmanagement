package com.books.repository;

import com.books.model.ActivationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ActivationTokenRepository extends JpaRepository<ActivationToken, Long> {

    @Query("SELECT a FROM ActivationToken a WHERE a.loginId = :loginId AND a.usedAt IS NULL AND a.expiresAt > :now AND a.type = 'ACTIVATION'")
    Optional<ActivationToken> findActiveByLoginId(@Param("loginId") Long loginId, @Param("now") LocalDateTime now);

    @Query("SELECT a FROM ActivationToken a WHERE a.tokenHash = :tokenHash")
    Optional<ActivationToken> findByTokenHash(@Param("tokenHash") String tokenHash);

    @Query("SELECT a FROM ActivationToken a WHERE a.loginId = :loginId AND a.type = 'ACTIVATION'")
    List<ActivationToken> findByLoginId(@Param("loginId") Long loginId);

    @Modifying
    @Query("UPDATE ActivationToken a SET a.usedAt = NOW() WHERE a.id = :id")
    int markAsUsed(@Param("id") Long id);

    @Modifying
    @Query("UPDATE ActivationToken a SET a.usedAt = NOW() " +
           "WHERE a.loginId = :loginId AND a.usedAt IS NULL AND a.expiresAt > :now AND a.type = 'ACTIVATION'")
    int invalidateUnusedTokens(@Param("loginId") Long loginId, @Param("now") LocalDateTime now);
}

package com.books.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtTokenGenerator {

    private final SecretKey key;
    private final long accessTokenExpirySeconds;

    public JwtTokenGenerator(
            @Value("${auth.jwt.secret-key}") String secretKey,
            @Value("${auth.jwt.access-token-expiry-seconds:900}") long accessTokenExpirySeconds) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirySeconds = accessTokenExpirySeconds;
    }

    public String generate(Long loginId, String username, List<String> roles) {
        long now = System.currentTimeMillis();
        long expiry = now + (accessTokenExpirySeconds * 1000);

        return Jwts.builder()
                .subject(String.valueOf(loginId))
                .id(UUID.randomUUID().toString())
                .claim("login", username)
                .claim("roles", roles)
                .issuedAt(new Date(now))
                .expiration(new Date(expiry))
                .signWith(key)
                .compact();
    }

    public Claims parseClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }
}

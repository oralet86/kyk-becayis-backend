package com.sazark.kykbecayis.user;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;


@Component
public class JwtService {
    @Getter
    private final String jwtSecret;
    private Key key;

    public static final int JWT_LIFESPAN_SECOND = (7 * 24 * 60 * 60); // 1 week

    public JwtService(@Value("${jwt.secret}") String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(String email) {
        // 1 week in ms
        long expirationMs = JWT_LIFESPAN_SECOND * 1000; // 1 week in ms
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public JwtValidationResult validateToken(String token) {
        if (token == null || token.isBlank()) {
            return JwtValidationResult.EMPTY;
        }
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return JwtValidationResult.VALID;
        } catch (ExpiredJwtException e) {
            return JwtValidationResult.EXPIRED;
        } catch (JwtException e) {
            return JwtValidationResult.INVALID;
        }
    }

    public enum JwtValidationResult {
        VALID,
        EXPIRED,
        INVALID,
        EMPTY
    }
}

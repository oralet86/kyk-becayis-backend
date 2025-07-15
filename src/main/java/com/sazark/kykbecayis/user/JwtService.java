package com.sazark.kykbecayis.user;

import com.sazark.kykbecayis.core.config.EnvConfig;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;


@Component
public class JwtService {
    public static final int JWT_LIFESPAN_SECOND = (7 * 24 * 60 * 60); // 1 week
    private final Key key = Keys.hmacShaKeyFor(EnvConfig.getJWT_SECRET().getBytes());

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

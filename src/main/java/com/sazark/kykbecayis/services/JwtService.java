package com.sazark.kykbecayis.services;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    // Secret key (should be at least 256 bits for HS256)
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Token validity in milliseconds (e.g., 1 hour)
    private final long jwtExpirationMs = 3600000;

    // Generate JWT token with subject (e.g., user id or username)
    public String generateToken(String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key)
                .compact();
    }

    // Validate the token (throws if invalid)
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Token invalid
            return false;
        }
    }

    // Get subject (e.g. user id) from token
    public String getSubjectFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}

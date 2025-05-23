package com.sazark.kykbecayis.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;


@Component
public class JwtService {
    public static final int JWT_LIFESPAN_SECOND = (7*24*60*60); // 1 week
    private final String secret = "Y2VzK3N1OHF2cGpMbWxrT2tJYk9hmjFvYWR4cGh0a3U=";

    private final Key key = Keys.hmacShaKeyFor(secret.getBytes());

    public String generateToken(String uid) {
        // 1 week in ms
        long expirationMs = JWT_LIFESPAN_SECOND * 1000; // 1 week in ms
        return Jwts.builder()
                .setSubject(uid)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUID(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}

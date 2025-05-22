package com.sazark.kykbecayis.services;

import com.sazark.kykbecayis.auth.JwtService;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    public void setUp() {
        jwtService = new JwtService();
    }

    @Test
    public void generateToken_returnsValidToken() {
        String uid = "test-user";
        String token = jwtService.generateToken(uid);

        assertThat(token).isNotNull();
        assertThat(jwtService.isTokenValid(token)).isTrue();
        assertThat(jwtService.extractUID(token)).isEqualTo(uid);
    }

    @Test
    public void extractUID_returnsCorrectUID() {
        String uid = "sample-uid";
        String token = jwtService.generateToken(uid);

        String extracted = jwtService.extractUID(token);

        assertThat(extracted).isEqualTo(uid);
    }

    @Test
    public void isTokenValid_returnsFalse_forMalformedToken() {
        String invalidToken = "this.is.not.a.jwt";

        boolean isValid = jwtService.isTokenValid(invalidToken);

        assertThat(isValid).isFalse();
    }

    @Test
    public void isTokenValid_returnsFalse_forExpiredToken() {
        String uid = "expired-user";
        String secret = "your-256-bit-secret-your-256-bit-secret";
        Key key = Keys.hmacShaKeyFor(secret.getBytes());

        // Manually create expired token
        String expiredToken = io.jsonwebtoken.Jwts.builder()
                .setSubject(uid)
                .setIssuedAt(new Date(System.currentTimeMillis() - 20000))
                .setExpiration(new Date(System.currentTimeMillis() - 10000)) // already expired
                .signWith(key, io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();

        assertThat(jwtService.isTokenValid(expiredToken)).isFalse();
    }

    @Test
    public void extractUID_throwsException_forInvalidToken() {
        String malformedToken = "invalid.token.payload";

        assertThrows(io.jsonwebtoken.JwtException.class, () -> {
            jwtService.extractUID(malformedToken);
        });
    }
}

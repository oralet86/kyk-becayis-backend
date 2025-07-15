package com.sazark.kykbecayis.services;

import com.sazark.kykbecayis.core.config.EnvConfig;
import com.sazark.kykbecayis.user.JwtService;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Key;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JwtServiceTest {

    private final String email = "test@test.edu.tr";
    private JwtService jwtService;

    @BeforeEach
    public void setUp() {
        jwtService = new JwtService();
    }

    @Test
    public void generateToken_returnsValidToken() {
        String token = jwtService.generateToken(email);
        assertThat(token).isNotNull();
        assertThat(jwtService.validateToken(token)).isEqualTo(JwtService.JwtValidationResult.VALID);
        assertThat(jwtService.extractEmail(token)).isEqualTo(email);
    }

    @Test
    public void extractID_returnsCorrectEmail() {
        String token = jwtService.generateToken(email);
        String extracted = jwtService.extractEmail(token);
        assertThat(extracted).isEqualTo(email);
    }

    @Test
    public void validateTokenValid_returnsFalse_forMalformedToken() {
        String invalidToken = "this.is.not.a.jwt";
        JwtService.JwtValidationResult isValid = jwtService.validateToken(invalidToken);
        assertThat(isValid).isEqualTo(JwtService.JwtValidationResult.INVALID);
    }

    @Test
    public void validateTokenValid_returnsFalse_forExpiredToken() {
        String email = "expired@test.edu.tr";
        String secret = EnvConfig.getJWT_SECRET();
        Key key = Keys.hmacShaKeyFor(secret.getBytes());

        // Manually create expired token
        String expiredToken = io.jsonwebtoken.Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis() - 20000))
                .setExpiration(new Date(System.currentTimeMillis() - 10000)) // already expired
                .signWith(key, io.jsonwebtoken.SignatureAlgorithm.HS256)
                .compact();

        assertThat(jwtService.validateToken(expiredToken)).isEqualTo(JwtService.JwtValidationResult.EXPIRED);
    }

    @Test
    public void extractEmail_throwsException_forInvalidToken() {
        String malformedToken = "invalid.token.payload";

        assertThrows(io.jsonwebtoken.JwtException.class, () -> {
            jwtService.extractEmail(malformedToken);
        });
    }
}

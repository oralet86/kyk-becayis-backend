package com.sazark.kykbecayis.core.exceptions;

public class JwtAuthException extends RuntimeException {
    public JwtAuthException(String message) {
        super(message);
    }
}

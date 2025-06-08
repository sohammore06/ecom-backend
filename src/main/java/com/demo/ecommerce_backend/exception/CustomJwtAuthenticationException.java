package com.demo.ecommerce_backend.exception;

public class CustomJwtAuthenticationException extends RuntimeException {
    public CustomJwtAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomJwtAuthenticationException(String message) {
        super(message);
    }
}

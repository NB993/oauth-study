package com.pio.oauth.exception;

import io.jsonwebtoken.ExpiredJwtException;
import java.util.Map;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ExpiredJwtException.class)
    public Map<String, String> handleBusinessException(ExpiredJwtException e) {
        return Map.of("message", e.getMessage());
    }
}

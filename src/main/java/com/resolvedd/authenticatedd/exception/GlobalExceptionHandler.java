package com.resolvedd.authenticatedd.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.resolvedd.authenticatedd.constants.ExceptionConstants.EXPIRED_TOKEN_MESSAGE;
import static com.resolvedd.authenticatedd.constants.ExceptionConstants.INVALID_TOKEN_MESSAGE;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidToken(InvalidTokenException ex) {
        return new ResponseEntity<>(ErrorResponse.create(ex, HttpStatus.UNAUTHORIZED, ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(JwtException ex) {
        return new ResponseEntity<>(ErrorResponse.create(ex, HttpStatus.UNAUTHORIZED, ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        return new ResponseEntity<>(ErrorResponse.create(ex, HttpStatus.UNAUTHORIZED, ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MissingCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleMissingCredentials(MissingCredentialsException ex) {
        return new ResponseEntity<>(ErrorResponse.create(ex, HttpStatus.UNAUTHORIZED, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleExistingUser(UserAlreadyExistsException ex) {
        return new ResponseEntity<>(ErrorResponse.create(ex, HttpStatus.UNAUTHORIZED, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
package com.recommend.server.controller;

import com.recommend.server.exception.*;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    private ResponseEntity<String> validationFail(ConstraintViolationException ex) {
        String errors = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(LocationNotFound.class)
    private ResponseEntity<String> LocationFail(LocationNotFound locationNotFound) {
        return ResponseEntity.badRequest().body(locationNotFound.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExists.class)
    private ResponseEntity<String> EmailAlreadyExists(EmailAlreadyExists emailAlreadyExists) {
        return ResponseEntity.badRequest().body(emailAlreadyExists.getMessage());
    }

    @ExceptionHandler(BadCredentials.class)
    private ResponseEntity<String> BadCredentials(BadCredentials badCredentials) {
        return ResponseEntity.badRequest().body(badCredentials.getMessage());
    }

    @ExceptionHandler(TokenInvalid.class)
    private ResponseEntity<String> TokenInvalid(TokenInvalid tokenInvalid) {
        return ResponseEntity.badRequest().body(tokenInvalid.getMessage());
    }

    @ExceptionHandler(LocationNotProvided.class)
    private ResponseEntity<String> LocationNotProvided(LocationNotProvided locationNotProvided) {
        return ResponseEntity.badRequest().body(locationNotProvided.getMessage());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    private ResponseEntity<String> ExpiredJwt(ExpiredJwtException ex) {
        return ResponseEntity.status(401).body("Token expired");
    }

    @ExceptionHandler(CollegeNotFound.class)
    private ResponseEntity<String> handleCollegeNotFound(CollegeNotFound ex) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(Empty.class)
    private ResponseEntity<String> handleEmpty(Empty data) {
        return ResponseEntity.badRequest().body(data.getMessage());
    }
}

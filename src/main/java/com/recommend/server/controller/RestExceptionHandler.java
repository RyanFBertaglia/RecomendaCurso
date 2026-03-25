package com.recommend.server.controller;

import com.recommend.server.exception.BadCredentials;
import com.recommend.server.exception.EmailAlreadyExists;
import com.recommend.server.exception.LocationNotFound;
import com.recommend.server.exception.TokenInvalid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

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
}

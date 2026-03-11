package com.recommend.server.controller;

import com.recommend.server.exception.LocationNotFound;
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
}

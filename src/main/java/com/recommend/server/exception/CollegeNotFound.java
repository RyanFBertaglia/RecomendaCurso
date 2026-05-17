package com.recommend.server.exception;

public class CollegeNotFound extends RuntimeException {
    public CollegeNotFound(String message) {
        super(message);
    }
}

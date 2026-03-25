package com.recommend.server.exception;

public class BadCredentials extends RuntimeException {
    public BadCredentials(String message) {
        super(message);
    }
    public BadCredentials() {
        super("Email or password is incorrect");
    }
}

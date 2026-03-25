package com.recommend.server.exception;

public class EmailAlreadyExists extends RuntimeException {
    public EmailAlreadyExists() {
        super("This email is already in use");
    }
}

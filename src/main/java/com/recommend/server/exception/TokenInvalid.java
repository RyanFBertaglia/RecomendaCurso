package com.recommend.server.exception;

public class TokenInvalid extends RuntimeException {
    public TokenInvalid(String message) {
        super(message);
    }
}

package com.recommend.server.exception;

public class LocationNotProvided extends RuntimeException {
    public LocationNotProvided(String message) {
        super(message);
    }
}

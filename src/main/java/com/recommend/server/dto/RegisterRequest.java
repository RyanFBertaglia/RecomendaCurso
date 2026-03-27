package com.recommend.server.dto;

public record RegisterRequest(String name, String email, String password, Coordinates locale) {
}

package com.recommend.server.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Size(min = 2, message = "Name must have at least 2 characters") String name,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6, message = "Password must have at least 6 characters") String password,
        Coordinates locale) {
}

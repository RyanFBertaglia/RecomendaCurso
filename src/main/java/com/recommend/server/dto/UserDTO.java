package com.recommend.server.dto;

import com.recommend.server.model.Capelinho;

public record UserDTO(String name, String email, Coordinates locale, Integer capelinho) {
}

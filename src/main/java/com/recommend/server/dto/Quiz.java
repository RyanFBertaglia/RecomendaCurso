package com.recommend.server.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class Quiz {
    @NotNull(message = "Disc profile cannot be null")
    @NotEmpty(message = "Disc profile cannot be empty")
    private Map<Character, Double> discProfile;
}

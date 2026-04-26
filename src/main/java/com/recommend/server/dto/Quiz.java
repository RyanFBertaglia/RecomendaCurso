package com.recommend.server.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
public class Quiz {
    @NotEmpty(message = "Abilities cannot be empty")
    private List<String> abilities;
    private List<String> userCantBe;
}

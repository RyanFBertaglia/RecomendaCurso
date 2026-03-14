package com.recommend.server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
@AllArgsConstructor
public class Quiz {
    private List<String> abilities;
    private List<String> userCantBe;
}

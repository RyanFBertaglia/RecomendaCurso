package com.recommend.server.dto;

import java.util.Map;

public record CourseDTO(String name, Map<Character, Double> discWeights, String description) {
}

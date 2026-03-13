package com.recommend.server.dto;

import java.util.List;

public record CourseDTO(String name, List<String> abilities, List<String> cantBe, String description) {
}

package com.recommend.server.dto;

import java.util.List;

public record CollegeDTO(
        String name,
        String description,
        Coordinates locale,
        List<CourseImpDTO> courses
) {}
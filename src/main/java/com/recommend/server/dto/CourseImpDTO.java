package com.recommend.server.dto;

import java.util.List;
import java.util.Map;

public record CourseImpDTO(
        String name,
        Integer courseId,
        Integer collegeId,
        Map<String, Object> note,
        String details,
        Double fees,
        Coordinates locale
) {
}

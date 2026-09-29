package com.recommend.server.dto;

import com.recommend.server.model.Course;
import com.recommend.server.model.History;

import java.util.Date;
import java.util.Map;

public record HistoryDTO(Integer id, CourseInfo course, Date accessedAt) {

    public record CourseInfo(Integer id, String name, String description, Map<Character, Double> discWeights) {
        public static CourseInfo from(Course course) {
            if (course == null) return null;
            return new CourseInfo(course.getId(), course.getName(), course.getDescription(), course.getDiscWeights());
        }
    }

    public static HistoryDTO from(History history) {
        return new HistoryDTO(history.getId(), CourseInfo.from(history.getCourse()), history.getAccessedAt());
    }
}
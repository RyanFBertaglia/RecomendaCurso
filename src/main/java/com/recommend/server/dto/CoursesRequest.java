package com.recommend.server.dto;

import java.util.List;

public record CoursesRequest(List<Integer> courses, Double distance, Coordinates location) {
}

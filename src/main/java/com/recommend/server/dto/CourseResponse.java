package com.recommend.server.dto;

import com.recommend.server.model.Course;

public record CourseResponse(int incidence, Course course) {

    public Course getCourse() {
        return course;
    }

}

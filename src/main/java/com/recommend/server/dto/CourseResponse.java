package com.recommend.server.dto;

import com.recommend.server.model.Course;
import lombok.Getter;

@Getter
public class CourseResponse {
    private int incidencia;
    private Course curso;
    public CourseResponse(int incidencia, Course curso) {
        this.incidencia = incidencia;
        this.curso = curso;
    }

    public Course getCourse() { return curso; }

}

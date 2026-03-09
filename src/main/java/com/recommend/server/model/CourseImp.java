package com.recommend.server.model;

import com.recommend.server.dto.Coordinates;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.jackson.autoconfigure.JacksonProperties.Json;

@Data
@NoArgsConstructor
public class CourseImp {
    private Integer id;
    private String name;
    private Integer idCourse;
    private Integer idCollege;
    private Json note;
    private String details;
    private Double fees;
    private Coordinates locale;
}
package com.recommend.server.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.recommend.server.dto.Coordinates;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class College {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "college")
    @JsonManagedReference("college-courses")
    private List<CourseImp> courses;
    private Coordinates locale;
    private String description;
    private String image;
}

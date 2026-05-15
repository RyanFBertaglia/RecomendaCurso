package com.recommend.server.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private List<String> abilities;
    private List<String> cantBe;
    private String description;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference("course-courseImps")
    private List<CourseImp> courseImps;

    public int compare(List<String> abilities) {
        return (int) abilities.stream()
                .filter(this.abilities::contains)
                .count();
    }
    public Course(String name, List<String> abilities) {
        this.name = name;
        this.abilities = abilities;
    }
}
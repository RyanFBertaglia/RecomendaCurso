package com.recommend.server.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "disc_weights", columnDefinition = "json")
    private Map<Character, Double> discWeights;

    private String description;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference("course-courseImps")
    private List<CourseImp> courseImps;

    public double dotProduct(Map<Character, Double> profile) {
        if (discWeights == null) return 0.0;
        double score = 0.0;
        for (Map.Entry<Character, Double> entry : discWeights.entrySet()) {
            double profileValue = profile.getOrDefault(entry.getKey(), 0.0);
            score += entry.getValue() * profileValue;
        }
        return score;
    }

    public Course(String name, Map<Character, Double> discWeights) {
        this.name = name;
        this.discWeights = discWeights;
    }
}

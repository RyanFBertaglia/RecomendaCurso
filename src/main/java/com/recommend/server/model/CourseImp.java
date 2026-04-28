package com.recommend.server.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.recommend.server.dto.Coordinates;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Data
@NoArgsConstructor
@Entity
public class CourseImp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_course", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "id_college", nullable = false)
    @JsonBackReference
    private College college;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "note", columnDefinition = "json")
    private Map<String, Object> note;

    private String details;
    private Double fees;

    @Embedded
    private Coordinates locale;
}
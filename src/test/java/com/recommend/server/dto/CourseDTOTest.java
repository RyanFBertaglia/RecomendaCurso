package com.recommend.server.dto;

import org.junit.jupiter.api.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CourseDTOTest {

    @Test @Order(1)
    @DisplayName("CourseDTO deve ser criado com discWeights")
    void testCourseDTOWithDiscWeights() {
        CourseDTO dto = new CourseDTO(
                "Ciência da Computação",
                Map.of('D', 0.6, 'I', 0.3, 'S', 0.4, 'C', 0.9),
                "Focado no desenvolvimento de tecnologias, algoritmos e soluções de software.",
                "EXATAS"
        );

        assertEquals("Ciência da Computação", dto.name());
        assertEquals(4, dto.discWeights().size());
        assertEquals(0.9, dto.discWeights().get('C'), 1e-9);
        assertEquals("Focado no desenvolvimento de tecnologias, algoritmos e soluções de software.", dto.description());
    }

    @Test @Order(2)
    @DisplayName("Quiz deve ser criado com discProfile")
    void testQuizWithDiscProfile() {
        Quiz quiz = new Quiz(Map.of('D', 1.0, 'I', 0.5, 'S', 0.8, 'C', 0.2));

        assertEquals(4, quiz.getDiscProfile().size());
        assertEquals(1.0, quiz.getDiscProfile().get('D'), 1e-9);
    }

    @Test @Order(3)
    @DisplayName("Fafyl deve armazenar score double e course")
    void testFafylWithDoubleScore() {
        com.recommend.server.model.Course course = new com.recommend.server.model.Course();
        course.setName("Medicina");

        Fafyl fafyl = new Fafyl(1.23, course);

        assertEquals(1.23, fafyl.score(), 1e-9);
        assertEquals("Medicina", fafyl.course().getName());
    }
}

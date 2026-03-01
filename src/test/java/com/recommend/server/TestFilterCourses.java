package com.recommend.server;

import com.recommend.server.dto.CourseResponse;
import com.recommend.server.model.Course;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@SpringBootTest
public class TestFilterCourses {
    @Test
    void testFilterCourses() {
        List<Course> courses = new ArrayList<>();
        courses.add(new Course("Química", Arrays.asList("Matemática", "Curioso", "Sério")));
        courses.add(new Course("Direito", Arrays.asList("Focado", "Sério", "Falar em público")));
        courses.add(new Course("Artes", Arrays.asList("Criatividade", "Descontraido")));
        List<String> abilities = Arrays.asList("Matemática", "Focado", "Descontraido", "Trabalho remoto", "Criatividade");
        List<String> userCantBe = Arrays.asList("Sério", "Falar em público");

        List<CourseResponse> possible = courses.stream()
                .filter(course -> course.hab().stream().noneMatch(userCantBe::contains))
                .map(curso -> {
                    int score = curso.compare(abilities);
                    return new CourseResponse(score, curso);
                })
                .filter(resp -> resp.incidence() > 0)
                .sorted(Comparator.comparingInt(CourseResponse::incidence).reversed())
                .toList();

        System.out.println("Recommended courses:");
        possible.forEach(c -> System.out.println(c.getCourse().nome() + " - Afinidade: " + c.incidence()));
    }

}


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
        List<Course> cursos = new ArrayList<>();
        cursos.add(new Course("Química", Arrays.asList("Matemática")));
        cursos.add(new Course("Direito", Arrays.asList("Focado", "Sério")));
        cursos.add(new Course("Artes", Arrays.asList("Criatividade", "Descontraido")));
        List<String> habUsuario = Arrays.asList("Matemática", "Focado", "Descontraido", "Trabalho remoto", "Criatividade");
        List<String> nPode = Arrays.asList("Sério", "Não fala em público");

        List<CourseResponse> possivel = cursos.stream()
                .filter(curso -> curso.hab().stream().noneMatch(nPode::contains))
                .map(curso -> {
                    int score = curso.compare(habUsuario);
                    return new CourseResponse(score, curso);
                })
                .filter(resp -> resp.getIncidencia() > 0)
                .sorted(Comparator.comparingInt(CourseResponse::getIncidencia).reversed())
                .toList();

        System.out.println("Recommended courses:");
        possivel.forEach(c -> System.out.println(c.getCourse().nome() + " - Afinidade: " + c.getIncidencia()));
    }

}


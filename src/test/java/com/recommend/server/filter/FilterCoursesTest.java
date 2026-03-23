package com.recommend.server.filter;

import com.recommend.server.dto.Fafyl;
import com.recommend.server.dto.Quiz;
import com.recommend.server.model.Course;
import com.recommend.server.repository.CourseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FilterCoursesTest {

    @Mock
    CourseRepository courseRepository;

    @Test
    void testFilterCourses() {

        List<Course> mockCourses = List.of(
                new Course("Design", Arrays.asList("Criatividade", "Empatia")),
                new Course("Engenharia", Arrays.asList("Matemática", "Precisão")),
                new Course("História da Arte", Arrays.asList("História da Arte", "Criatividade"))
        );

        when(courseRepository.count()).thenReturn((long) mockCourses.size());
        when(courseRepository.streamAll()).thenReturn(mockCourses.stream());

        Quiz quiz = new Quiz(
                Arrays.asList("Memorização", "Empatia", "Ergonomia", "Sustentabilidade", "História da Arte"),
                Arrays.asList("Averso a normas", "Impreciso", "Escuta Ativa", "Desorganizado financeiramente")
        );

        assertTrue(courseRepository.count() > 0);

        try (Stream<Course> stream = courseRepository.streamAll()) {

            List<Fafyl> courses = stream
                    .filter(course -> course.getAbilities().stream().noneMatch(quiz.getUserCantBe()::contains))
                    .map(curso -> new Fafyl(curso.compare(quiz.getAbilities()), curso))
                    .filter(resp -> resp.incidence() > 0)
                    .sorted(Comparator.comparingInt(Fafyl::incidence).reversed())
                    .toList();

            assertFalse(courses.isEmpty());

            courses.forEach(c ->
                    System.out.println(c.course().getName() + " - Afinidade: " + c.incidence())
            );

            verify(courseRepository).count();
            verify(courseRepository).streamAll();
        }
    }
}
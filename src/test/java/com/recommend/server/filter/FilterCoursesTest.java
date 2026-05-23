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

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FilterCoursesTest {

    @Mock
    CourseRepository courseRepository;

    @Test
    void testFilterCoursesByDiscScore() {
        List<Course> mockCourses = List.of(
                buildCourse("Design", Map.of('D', 0.2, 'I', 0.8, 'S', 0.5, 'C', 1.0)),
                buildCourse("Engenharia", Map.of('D', 0.5, 'I', 1.0, 'S', 0.2, 'C', 0.8)),
                buildCourse("História da Arte", Map.of('D', 0.3, 'I', 0.6, 'S', 0.7, 'C', 0.4))
        );

        when(courseRepository.count()).thenReturn((long) mockCourses.size());
        when(courseRepository.streamAll()).thenReturn(mockCourses.stream());

        Quiz quiz = new Quiz(Map.of('D', 0.0, 'I', 1.0, 'S', 0.0, 'C', 0.0));

        assertTrue(courseRepository.count() > 0);

        try (Stream<Course> stream = courseRepository.streamAll()) {
            List<Fafyl> courses = stream
                    .map(curso -> new Fafyl(curso.dotProduct(quiz.getDiscProfile()), curso))
                    .filter(resp -> resp.score() > 0)
                    .sorted((a, b) -> Double.compare(b.score(), a.score()))
                    .toList();

            assertFalse(courses.isEmpty());

            courses.forEach(c ->
                    System.out.println(c.course().getName() + " - Score: " + c.score())
            );

            verify(courseRepository).count();
            verify(courseRepository).streamAll();
        }
    }

    private Course buildCourse(String name, Map<Character, Double> discWeights) {
        Course c = new Course();
        c.setName(name);
        c.setDiscWeights(discWeights);
        return c;
    }
}

package com.recommend.server.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recommend.server.dto.*;
import com.recommend.server.model.Course;
import com.recommend.server.repository.CourseImpRepository;
import com.recommend.server.repository.CourseRepository;
import com.recommend.server.service.FafylService;
import com.recommend.server.service.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
public class GeneralTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseImpRepository courseImpRepository;

    @Mock
    private Location location;

    @InjectMocks
    private FafylService fafylService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(fafylService, "objectMapper", new ObjectMapper());
    }

    @Test
    void shouldReturnRankedCoursesBasedOnDiscProfile() {
        Course course1 = buildCourse(1, "Engenharia de Software", Map.of('D', 0.5, 'I', 1.0, 'S', 0.2, 'C', 0.8));
        Course course2 = buildCourse(2, "Design Gráfico",        Map.of('D', 0.2, 'I', 0.8, 'S', 0.5, 'C', 1.0));
        Course course3 = buildCourse(3, "Medicina",              Map.of('D', 0.8, 'I', 0.2, 'S', 1.0, 'C', 0.5));

        when(courseRepository.streamAll())
                .thenReturn(Stream.of(course1, course2, course3));

        Quiz quiz = new Quiz(Map.of('D', 1.0, 'I', 0.0, 'S', 0.0, 'C', 0.0));

        List<Fafyl> result = fafylService.findAllFafyl(quiz);

        assertThat(result).isNotEmpty();
        assertThat(result).allMatch(f -> f.score() > 0);
        assertThat(result.get(0).course().getName()).isEqualTo("Medicina");

        IO.println(result);
    }

    @Test
    void shouldReturnInDistance() {
        Coordinates user = new Coordinates(-22.9059, -47.0590);

        CourseImpRepository.CourseImpProjection proj1 = buildProjection(
                1, 10, "Engenharia de Software - UNICAMP",
                -22.8173, -47.0696, "{\"turno\":\"integral\"}", "Detalhes A", 5000.0
        );
        CourseImpRepository.CourseImpProjection proj2 = buildProjection(
                2, 20, "Medicina - PUC",
                -22.9950, -47.1189, "{\"turno\":\"noturno\"}", "Detalhes B", 8000.0
        );
        CourseImpRepository.CourseImpProjection proj3 = buildProjection(
                3, 30, "Design - FAM",
                null, null, null, "Detalhes C", 3000.0
        );

        when(courseImpRepository.findNearbyCourses(
                eq(List.of(1, 2, 3)),
                eq(user.lat()),
                eq(user.lon()),
                anyDouble()
        )).thenReturn(List.of(proj1, proj2, proj3));

        when(location.distance(new Coordinates(-22.8173, -47.0696), user)).thenReturn(5000.0);
        when(location.distance(new Coordinates(-22.9950, -47.1189), user)).thenReturn(200000.0);

        CoursesRequest coursesRequest = new CoursesRequest(
                List.of(1, 2, 3),
                100000.0,
                user
        );

        List<CourseImpDTO> courses = fafylService.findInDistance(
                coursesRequest.courses(),
                coursesRequest.distance(),
                coursesRequest.location()
        );

        assertThat(courses).hasSize(1);
        assertThat(courses.getFirst().name()).isEqualTo("Engenharia de Software - UNICAMP");

        IO.println(courses);
    }

    private Course buildCourse(int id, String name, Map<Character, Double> discWeights) {
        Course c = new Course();
        c.setId(id);
        c.setName(name);
        c.setDiscWeights(discWeights);
        return c;
    }

    private CourseImpRepository.CourseImpProjection buildProjection(
            int courseId, int collegeId, String name,
            Double lat, Double lon,
            String note, String details, Double fees) {

        CourseImpRepository.CourseImpProjection mock =
                Mockito.mock(CourseImpRepository.CourseImpProjection.class);

        when(mock.getCourseId()).thenReturn(courseId);
        when(mock.getCollegeId()).thenReturn(collegeId);
        when(mock.getName()).thenReturn(name);
        when(mock.getLat()).thenReturn(lat);
        when(mock.getLon()).thenReturn(lon);
        when(mock.getNote()).thenReturn(note);
        when(mock.getDetails()).thenReturn(details);
        when(mock.getFees()).thenReturn(fees);

        return mock;
    }
}

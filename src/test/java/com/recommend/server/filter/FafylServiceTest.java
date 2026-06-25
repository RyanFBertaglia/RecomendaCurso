package com.recommend.server.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recommend.server.dto.Coordinates;
import com.recommend.server.dto.CourseImpDTO;
import com.recommend.server.dto.Fafyl;
import com.recommend.server.dto.Quiz;
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
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FafylServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseImpRepository courseImpRepository;

    @Mock
    private Location location;

    @InjectMocks
    private FafylService fafylService;

    @Mock
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnRankedCoursesBasedOnDiscProfile() {
        Course c1 = buildCourse("Direito", 1.0, 0.5, 0.8, 0.2);
        Course c2 = buildCourse("Medicina", 0.8, 0.2, 1.0, 0.5);
        Course c3 = buildCourse("Engenharia", 0.5, 1.0, 0.2, 0.8);

        Quiz quiz = new Quiz(Map.of('D', 1.0, 'I', 0.0, 'S', 0.0, 'C', 0.0));

        when(courseRepository.streamAll())
                .thenReturn(Stream.of(c1, c2, c3));

        List<Fafyl> result = fafylService.findAllFafyl(quiz);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).course().getName()).isEqualTo("Direito");
        assertThat(result.get(0).score()).isGreaterThan(result.get(1).score());
    }

    @Test
    void shouldRemoveCoursesWithZeroScore() {
        Course c1 = buildCourse("Zero", 0.0, 0.0, 0.0, 0.0);

        Quiz quiz = new Quiz(Map.of('D', 1.0, 'I', 0.0, 'S', 0.0, 'C', 0.0));

        when(courseRepository.streamAll())
                .thenReturn(Stream.of(c1));

        List<Fafyl> result = fafylService.findAllFafyl(quiz);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldSortCoursesByScoreDescending() {
        Course c1 = buildCourse("Alto D", 1.0, 0.0, 0.0, 0.0);
        Course c2 = buildCourse("Baixo D", 0.3, 0.0, 0.0, 0.0);

        Quiz quiz = new Quiz(Map.of('D', 1.0, 'I', 0.0, 'S', 0.0, 'C', 0.0));

        when(courseRepository.streamAll())
                .thenReturn(Stream.of(c2, c1));

        List<Fafyl> result = fafylService.findAllFafyl(quiz);

        assertThat(result.get(0).score())
                .isGreaterThan(result.get(1).score());
    }

    @Test
    void shouldThrowRuntimeExceptionWhenRepositoryFails() {
        Quiz quiz = new Quiz(Map.of('D', 1.0, 'I', 0.0, 'S', 0.0, 'C', 0.0));

        when(courseRepository.streamAll())
                .thenThrow(new RuntimeException("db error"));

        assertThatThrownBy(() -> fafylService.findAllFafyl(quiz))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void shouldReturnCoursesWithinDistance() {
        List<Integer> ids = List.of(1, 2);
        Coordinates user = new Coordinates(-22.9059, -47.0590);

        CourseImpRepository.CourseImpProjection p1 = mock(CourseImpRepository.CourseImpProjection.class);
        CourseImpRepository.CourseImpProjection p2 = mock(CourseImpRepository.CourseImpProjection.class);

        when(p1.getName()).thenReturn("Curso 1");
        when(p1.getCourseId()).thenReturn(1);
        when(p1.getCollegeId()).thenReturn(1);
        when(p1.getLat()).thenReturn(-22.90);
        when(p1.getLon()).thenReturn(-47.05);
        when(p1.getNote()).thenReturn("{}");
        when(p1.getDetails()).thenReturn("Detalhes");
        when(p1.getFees()).thenReturn(1000.0);

        when(p2.getName()).thenReturn("Curso 2");
        when(p2.getCourseId()).thenReturn(2);
        when(p2.getCollegeId()).thenReturn(2);
        when(p2.getLat()).thenReturn(-30.00);
        when(p2.getLon()).thenReturn(-50.00);
        when(p2.getNote()).thenReturn("{}");
        when(p2.getDetails()).thenReturn("Detalhes");
        when(p2.getFees()).thenReturn(2000.0);

        when(courseImpRepository.findNearbyCourses(
                eq(ids),
                anyDouble(),
                anyDouble(),
                anyDouble()
        )).thenReturn(List.of(p1, p2));

        List<CourseImpDTO> result =
                fafylService.findInDistance(ids, 10000.0, user);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).courseId()).isEqualTo(1);

        verify(courseImpRepository).findNearbyCourses(
                eq(ids),
                eq(user.lat()),
                eq(user.lon()),
                anyDouble()
        );
    }

    @Test
    void shouldReturnAllCourseImplementationsWhenDistanceNotProvided() {
        CourseImpDTO dto1 = new CourseImpDTO("Curso 1", 1, 10, null, "Detalhes", 1000.0, null);
        CourseImpDTO dto2 = new CourseImpDTO("Curso 2", 2, 20, null, "Detalhes", 2000.0, null);

        when(courseImpRepository.findAllCourseImpDTO(any()))
                .thenReturn(List.of(dto1, dto2));

        List<CourseImpDTO> result = fafylService.findWithoutDistance(List.of(1, 2));
        assertThat(result).hasSize(2);
        verify(courseImpRepository).findAllCourseImpDTO(any());
    }

    private Course buildCourse(String name, double d, double i, double s, double c) {
        Course course = new Course();
        course.setName(name);
        course.setDiscWeights(Map.of('D', d, 'I', i, 'S', s, 'C', c));
        return course;
    }
}

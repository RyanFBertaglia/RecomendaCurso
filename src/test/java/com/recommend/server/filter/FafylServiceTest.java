package com.recommend.server.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.recommend.server.dto.Coordinates;
import com.recommend.server.dto.CourseImpDTO;
import com.recommend.server.dto.Fafyl;
import com.recommend.server.dto.Quiz;
import com.recommend.server.model.Course;
import com.recommend.server.model.CourseImp;
import com.recommend.server.repository.CourseImpRepository;
import com.recommend.server.repository.CourseRepository;
import com.recommend.server.service.FafylService;
import com.recommend.server.service.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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
    void shouldReturnRankedCoursesBasedOnAbilities() {

        Course c1 = mock(Course.class);
        Course c2 = mock(Course.class);
        Course c3 = mock(Course.class);

        when(c1.getAbilities()).thenReturn(List.of("Empatia", "Memorização"));
        when(c2.getAbilities()).thenReturn(List.of("Matemática"));
        when(c3.getAbilities()).thenReturn(List.of("Organização"));

        when(c1.compare(any())).thenReturn(2);
        when(c2.compare(any())).thenReturn(1);
        when(c3.compare(any())).thenReturn(0);

        Quiz quiz = new Quiz(
                List.of("Empatia", "Memorização"),
                List.of()
        );

        when(courseRepository.streamAll())
                .thenReturn(Stream.of(c1, c2, c3));

        List<Fafyl> result = fafylService.findAllFafyl(quiz);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).incidence()).isEqualTo(2);
        assertThat(result.get(1).incidence()).isEqualTo(1);
    }

    @Test
    void shouldFilterCoursesBasedOnCantBe() {

        Course c1 = mock(Course.class);

        when(c1.getAbilities()).thenReturn(List.of("Empatia"));

        Quiz quiz = new Quiz(
                List.of("Empatia"),
                List.of("Empatia")
        );

        when(courseRepository.streamAll())
                .thenReturn(Stream.of(c1));

        List<Fafyl> result = fafylService.findAllFafyl(quiz);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldRemoveCoursesWithZeroIncidence() {

        Course c1 = mock(Course.class);

        when(c1.getAbilities()).thenReturn(List.of("Empatia"));
        when(c1.compare(any())).thenReturn(0);

        Quiz quiz = new Quiz(
                List.of("Empatia"),
                List.of()
        );

        when(courseRepository.streamAll())
                .thenReturn(Stream.of(c1));

        List<Fafyl> result = fafylService.findAllFafyl(quiz);

        assertThat(result).isEmpty();
    }

    @Test
    void shouldSortCoursesByIncidenceDescending() {

        Course c1 = mock(Course.class);
        Course c2 = mock(Course.class);

        when(c1.getAbilities()).thenReturn(List.of("Empatia"));
        when(c2.getAbilities()).thenReturn(List.of("Memorização"));

        when(c1.compare(any())).thenReturn(2);
        when(c2.compare(any())).thenReturn(1);

        Quiz quiz = new Quiz(
                List.of("Empatia", "Memorização"),
                List.of()
        );

        when(courseRepository.streamAll())
                .thenReturn(Stream.of(c2, c1));

        List<Fafyl> result = fafylService.findAllFafyl(quiz);

        assertThat(result.get(0).incidence())
                .isGreaterThan(result.get(1).incidence());
    }

    @Test
    void shouldThrowRuntimeExceptionWhenRepositoryFails() {

        Quiz quiz = new Quiz(
                List.of("Empatia"),
                List.of()
        );

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

        when(location.distance(any(), eq(user)))
                .thenReturn(5000.0)   // p1 dentro
                .thenReturn(20000.0); // p2 fora

        List<CourseImpDTO> result =
                fafylService.findInDistance(ids, 10000.0, user);

        assertThat(result).hasSize(1);
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

        when(courseImpRepository.findAllCourseImpDTO())
                .thenReturn(List.of(dto1, dto2));

        List<CourseImpDTO> result = fafylService.findWithoutDistance(List.of(1, 2));
        assertThat(result).hasSize(2);
        verify(courseImpRepository).findAllCourseImpDTO();
    }
}
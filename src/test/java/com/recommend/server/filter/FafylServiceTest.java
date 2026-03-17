package com.recommend.server.filter;


import com.recommend.server.dto.Coordinates;
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
        Coordinates user = new Coordinates(10.0, 10.0);

        CourseImp imp1 = mock(CourseImp.class);
        CourseImp imp2 = mock(CourseImp.class);

        List<CourseImp> dbCourses = List.of(imp1, imp2);

        when(courseImpRepository.findByCourseIdIn(ids))
                .thenReturn(dbCourses);

        when(location.filterByLocation(dbCourses, 10.0, user))
                .thenReturn(List.of(imp1));

        List<CourseImp> result =
                fafylService.findInDistance(ids, 10.0, user);

        assertThat(result).hasSize(1);

        verify(courseImpRepository).findByCourseIdIn(ids);
        verify(location).filterByLocation(dbCourses, 10.0, user);
    }

    @Test
    void shouldReturnAllCourseImplementationsWhenDistanceNotProvided() {

        CourseImp imp1 = mock(CourseImp.class);
        CourseImp imp2 = mock(CourseImp.class);

        when(courseImpRepository.findAll())
                .thenReturn(List.of(imp1, imp2));

        List<CourseImp> result =
                fafylService.findInDistance(List.of(1,2));

        assertThat(result).hasSize(2);

        verify(courseImpRepository).findAll();
    }
}
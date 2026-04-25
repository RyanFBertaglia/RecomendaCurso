package com.recommend.server.service;

import com.recommend.server.dto.CollegeDTO;
import com.recommend.server.dto.Coordinates;
import com.recommend.server.dto.CourseImpDTO;
import com.recommend.server.dto.CourseDTO;
import com.recommend.server.model.Course;
import com.recommend.server.model.CourseImp;
import com.recommend.server.model.College;
import com.recommend.server.repository.CourseRepository;
import com.recommend.server.repository.CollegeRepository;
import com.recommend.server.repository.CourseImpRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataServiceTransactionTest {
    @Mock
    CourseRepository courseRepository;

    @Mock
    CollegeRepository collegeRepository;

    @Mock
    CourseImpRepository courseImpRepository;

    @InjectMocks
    DataService dataService;

    @Test
    void insertCoursesShouldUseTransactionalAnnotation() {
        List<CourseDTO> courses = List.of(
                new CourseDTO("Course 1", List.of("skill1"), List.of("cant1"), "Description 1")
        );

        when(courseRepository.saveAll(any())).thenReturn(List.of());

        dataService.insertCourses(courses);

        verify(courseRepository).saveAll(any());
    }

    @Test
    void insertCoursesImpShouldUseTransactionalAnnotation() {
        Course course = new Course();
        course.setId(1);
        course.setName("Course 1");

        College college = new College();
        college.setId(1);
        college.setName("College 1");

        CourseImpDTO dto = new CourseImpDTO(
                "Course 1", 1, 1, null, "Details", 100.0, new Coordinates(1.0, 1.0)
        );

        when(courseRepository.findById(1)).thenReturn(Optional.of(course));
        when(collegeRepository.findById(1)).thenReturn(Optional.of(college));
        when(courseImpRepository.saveAll(any())).thenReturn(List.of());

        dataService.insertCoursesImp(List.of(dto));

        verify(courseRepository).findById(1);
        verify(collegeRepository).findById(1);
    }

    @Test
    void insertCollegesShouldUseTransactionalAnnotation() {
        List<CollegeDTO> colleges = List.of(
                new CollegeDTO("College 1", "Description", null, List.of(), null)
        );

        when(collegeRepository.save(any())).thenAnswer(invocation -> {
            College c = invocation.getArgument(0);
            c.setId(1);
            return c;
        });

        dataService.insertColleges(colleges);

        verify(collegeRepository).save(any(College.class));
    }
}
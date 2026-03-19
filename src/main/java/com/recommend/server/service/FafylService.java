package com.recommend.server.service;

import com.recommend.server.dto.Coordinates;
import com.recommend.server.dto.Fafyl;
import com.recommend.server.dto.Quiz;
import com.recommend.server.model.Course;
import com.recommend.server.model.CourseImp;
import com.recommend.server.repository.CourseImpRepository;
import com.recommend.server.repository.CourseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FafylService {

    private final CourseRepository courseRepository;
    private final CourseImpRepository courseImpRepository;
    private final Location location;

    public FafylService(CourseRepository courseRepository, CourseImpRepository courseImpRepository, Location location) {
        this.courseRepository = courseRepository;
        this.courseImpRepository = courseImpRepository;
        this.location = location;
    }

    @Transactional
    public List<Fafyl> findAllFafyl(Quiz quiz) {
        try (Stream<Course> stream = courseRepository.streamAll()) {
            return stream
                    .filter(course -> course.getAbilities().stream().noneMatch(quiz.getUserCantBe()::contains))
                    .map(curso -> new Fafyl(curso.compare(quiz.getAbilities()), curso))
                    .filter(resp -> resp.incidence() > 0)
                    .sorted(Comparator.comparingInt(Fafyl::incidence).reversed())
                    .toList();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public List<CourseImp> findInDistance(List<Integer> courses, Double distance, Coordinates user) {
        List<CourseImp> all = courseImpRepository.findByCourseIdIn(courses);
        return location.filterByLocation(all, distance, user);
    }

    public List<CourseImp> findInDistance(List<Integer> courses) {
        return courseImpRepository.findAll();
    }
}

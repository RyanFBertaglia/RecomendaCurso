package com.recommend.server.service;

import com.recommend.server.dto.Fafyl;
import com.recommend.server.dto.Quiz;
import com.recommend.server.model.Course;
import com.recommend.server.repository.CourseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class FafylService {

    @Autowired
    CourseRepository courseRepository;

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
}

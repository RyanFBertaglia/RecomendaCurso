package com.recommend.server.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recommend.server.dto.Coordinates;
import com.recommend.server.dto.CourseImpDTO;
import com.recommend.server.dto.Fafyl;
import com.recommend.server.dto.Quiz;
import com.recommend.server.model.Course;
import com.recommend.server.repository.CourseImpRepository;
import com.recommend.server.repository.CourseRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Service
public class FafylService {

    private final CourseRepository courseRepository;
    private final CourseImpRepository courseImpRepository;
    private final Location location;
    private final ObjectMapper objectMapper;

    public FafylService(CourseRepository courseRepository, CourseImpRepository courseImpRepository,
                        Location location, ObjectMapper objectMapper) {
        this.courseRepository = courseRepository;
        this.courseImpRepository = courseImpRepository;
        this.location = location;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public List<Fafyl> findAllFafyl(Quiz quiz) {
        return findAllFafyl(quiz, PageRequest.of(0, Integer.MAX_VALUE)).getContent();
    }

    @Transactional
    @Async
    public Page<Fafyl> findAllFafyl(Quiz quiz, Pageable pageable) {
        try (Stream<Course> stream = courseRepository.streamAll()) {
            List<Fafyl> all = stream
                    .filter(course -> course.getAbilities().stream().noneMatch(quiz.getUserCantBe()::contains))
                    .map(curso -> new Fafyl(curso.compare(quiz.getAbilities()), curso))
                    .filter(resp -> resp.incidence() > 0)
                    .sorted(Comparator.comparingInt(Fafyl::incidence).reversed())
                    .toList();

            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), all.size());

            if (start >= all.size()) {
                return new PageImpl<>(List.of(), pageable, all.size());
            }

            return new PageImpl<>(all.subList(start, end), pageable, all.size());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


//    public List<CourseImp> findInDistance(List<Integer> courses, Double distance, Coordinates user) {
//        List<CourseImp> all = courseImpRepository.findByCourseIdIn(courses);
//        return location.filterByLocation(all, distance, user);
//    }

    public List<CourseImpDTO> findInDistance(List<Integer> courses,
                                             Double maxDistance,
                                             Coordinates user) {
        final double MARGIN_FACTOR = 1.3;
        double expandedDistance = maxDistance * MARGIN_FACTOR;

        // Haversine in the database, no College loading
        List<CourseImpRepository.CourseImpProjection> candidates = courseImpRepository.findNearbyCourses(
                courses,
                user.lat(),
                user.lon(),
                expandedDistance
        );

        // DTO e filter with API
        return candidates.stream()
                .map(this::toDTO)
                .filter(dto -> dto.locale() != null)
                .filter(dto -> location.distance(dto.locale(), user) <= maxDistance)
                .toList();
    }

    public List<CourseImpDTO> findWithoutDistance(List<Integer> courses) {
        return courseImpRepository.findAllCourseImpDTO();
    }

    private CourseImpDTO toDTO(CourseImpRepository.CourseImpProjection p) {
        Coordinates locale = (p.getLat() != null && p.getLon() != null)
                ? new Coordinates(p.getLat(), p.getLon())
                : null;

        Map<String, Object> note = parseNote(p.getNote());

        return new CourseImpDTO(
                p.getName(),
                p.getCourseId(),
                p.getCollegeId(),
                note,
                p.getDetails(),
                p.getFees(),
                locale
        );
    }

    private Map<String, Object> parseNote(String raw) {
        if (raw == null || raw.isBlank()) return null;
        try {
            return objectMapper.readValue(raw, new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException("Erro ao desserializar o campo note: " + raw, e);
        }
    }
}

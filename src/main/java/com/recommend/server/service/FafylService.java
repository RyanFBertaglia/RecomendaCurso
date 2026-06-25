package com.recommend.server.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recommend.server.dto.Coordinates;
import com.recommend.server.dto.CourseImpDTO;
import com.recommend.server.dto.Fafyl;
import com.recommend.server.dto.Quiz;
import com.recommend.server.exception.Empty;
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
                    .map(course -> new Fafyl(course.dotProduct(quiz.getDiscProfile()), course))
                    .filter(fafyl -> fafyl.score() > 0)
                    .sorted(Comparator.comparingDouble(Fafyl::score).reversed())
                    .toList();

            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), all.size());

            if (start >= all.size()) {
                return new PageImpl<>(List.of(), pageable, all.size());
            }

            return new PageImpl<>(all.subList(start, end), pageable, all.size());

        } catch(NullPointerException e) {
            throw new Empty("Nenhum curso encontrado");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<CourseImpDTO> findInDistance(List<Integer> courses,
                                             Double maxDistance,
                                             Coordinates user) {
        List<CourseImpRepository.CourseImpProjection> candidates = courseImpRepository.findNearbyCourses(
                courses, user.lat(), user.lon(), maxDistance
        );

        return candidates.stream()
                .map(this::toDTO)
                .filter(dto -> dto.locale() != null)
                .toList();
    }

    public List<CourseImpDTO> findInDistanceFiltered(List<Integer> courses,
                                                      Double maxDistance,
                                                      Coordinates user,
                                                      Double minFees,
                                                      Double maxFees,
                                                      String period) {
        List<CourseImpRepository.CourseImpProjection> candidates = courseImpRepository.findNearbyCoursesFiltered(
                courses, user.lat(), user.lon(), maxDistance, minFees, maxFees
        );

        Stream<CourseImpDTO> stream = candidates.stream()
                .map(this::toDTO)
                .filter(dto -> dto.locale() != null);

        if (period != null && !period.isBlank()) {
            stream = stream.filter(dto -> {
                if (dto.details() == null) return false;
                try {
                    Map<String, Object> details = objectMapper.readValue(dto.details(), new TypeReference<>() {});
                    String horario = (String) details.get("horario");
                    return period.equals(horario);
                } catch (Exception e) {
                    return false;
                }
            });
        }

        return stream.toList();
    }

    public List<CourseImpDTO> findWithoutDistance(List<Integer> courses) {
        return courseImpRepository.findAllCourseImpDTO(courses != null && !courses.isEmpty() ? courses : null);
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
